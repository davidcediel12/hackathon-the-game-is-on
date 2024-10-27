package com.hackathon.bankingapp.services.transaction.impl;

import com.hackathon.bankingapp.dto.request.transaction.AssetPurchaseRequest;
import com.hackathon.bankingapp.dto.request.transaction.AssetSaleRequest;
import com.hackathon.bankingapp.entities.Account;
import com.hackathon.bankingapp.entities.Asset;
import com.hackathon.bankingapp.entities.AssetTransaction;
import com.hackathon.bankingapp.entities.AssetTransactionType;
import com.hackathon.bankingapp.exceptions.ApiException;
import com.hackathon.bankingapp.repositories.AccountRepository;
import com.hackathon.bankingapp.repositories.AssetRepository;
import com.hackathon.bankingapp.repositories.AssetTransactionRepository;
import com.hackathon.bankingapp.services.customer.AccountService;
import com.hackathon.bankingapp.services.transaction.AssetMailingService;
import com.hackathon.bankingapp.services.transaction.AssetService;
import com.hackathon.bankingapp.services.transaction.MarketPricesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {

    private final MarketPricesService marketPricesService;
    private final AccountService accountService;
    private final AssetRepository assetRepository;
    private final AccountRepository accountRepository;
    private final AssetTransactionRepository assetTransactionRepository;
    private final AssetMailingService assetMailingService;


    @Override
    @Transactional
    public void buyAsset(AssetPurchaseRequest transactionRequest) {
        Account account = accountService.getUserAccount();
        validatePin(account, transactionRequest.pin());

        BigDecimal newBalance = account.getBalance().subtract(transactionRequest.amount());
        boolean insufficientBalance = newBalance.compareTo(BigDecimal.ZERO) < 0;
        if (insufficientBalance) {
            throw new ApiException("Internal error occurred while purchasing the asset.",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        BigDecimal assetPrice = marketPricesService.getAssetPrice(transactionRequest.assetSymbol());

        BigDecimal assetQuantity = transactionRequest.amount()
                .divide(assetPrice, 16, RoundingMode.HALF_UP);

        Asset asset = saveAsset(transactionRequest, account, assetQuantity, assetPrice);

        AssetTransaction assetTransaction = saveAssetTransaction(transactionRequest, assetQuantity, assetPrice, asset);

        account.setBalance(newBalance);
        accountRepository.save(account);
        assetMailingService.sendAssetPurchaseMessage(asset, account, assetTransaction);
    }

    @Override
    @Transactional
    public void sellAsset(AssetSaleRequest saleRequest) {
        Account account = accountService.getUserAccount();
        validatePin(account, saleRequest.pin());

        ApiException errorSellingAsset = new ApiException("Internal error occurred while selling the asset.", HttpStatus.INTERNAL_SERVER_ERROR);

        Asset asset = updateAssetQuantity(saleRequest, account, errorSellingAsset);

        BigDecimal assetPrice = marketPricesService.getAssetPrice(saleRequest.assetSymbol());
        BigDecimal transactionValue = assetPrice.multiply(saleRequest.quantity());

        account.setBalance(account.getBalance().add(transactionValue));
        accountRepository.save(account);

        AssetTransaction assetTransaction = createSellAssetTransaction(saleRequest, asset, transactionValue, assetPrice);
        assetMailingService.sendAssetSellMessage(asset, account, assetTransaction);

    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> getAssets() {

        Account account = accountService.getUserAccount();
        Map<String, BigDecimal> assets = new HashMap<>();

        assetRepository.findByAccount(account)
                .forEach(asset -> assets.put(asset.getAssetSymbol(), asset.getAssetAmount()));

        return assets;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getNetWorth() {
        Account account = accountService.getUserAccount();

        BigDecimal assetsWorth = assetRepository.findByAccount(account).stream()
                .map(asset -> asset.getAssetAmount().multiply(asset.getAveragePriceBought()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return assetsWorth.add(account.getBalance()).setScale(12, RoundingMode.HALF_UP);
    }

    private AssetTransaction createSellAssetTransaction(AssetSaleRequest saleRequest, Asset asset,
                                                        BigDecimal transactionValue, BigDecimal assetPrice) {

        AssetTransaction assetTransaction = AssetTransaction.builder()
                .asset(asset)
                .transactionValue(transactionValue)
                .price(assetPrice)
                .amount(saleRequest.quantity())
                .transactionType(AssetTransactionType.SELL)
                .build();

        return assetTransactionRepository.save(assetTransaction);
    }

    private Asset updateAssetQuantity(AssetSaleRequest saleRequest, Account account, ApiException errorSellingAsset) {
        Asset asset = assetRepository.findByAssetSymbolAndAccount(saleRequest.assetSymbol(), account)
                .orElseThrow(() -> errorSellingAsset);

        BigDecimal newAssetQuantity = asset.getAssetAmount().subtract(saleRequest.quantity());
        if (newAssetQuantity.compareTo(BigDecimal.ZERO) < 0) {
            throw errorSellingAsset;
        }

        asset.setAssetAmount(newAssetQuantity);
        assetRepository.save(asset);
        return asset;
    }

    private void validatePin(Account account, String pin) {
        if (!Objects.equals(account.getPin(), pin)) {
            throw ApiException.invalidPin();
        }
    }

    private Asset saveAsset(AssetPurchaseRequest transactionRequest, Account account,
                            BigDecimal assetQuantity, BigDecimal assetPrice) {

        Optional<Asset> assetOpt = assetRepository.findByAssetSymbolAndAccount(
                transactionRequest.assetSymbol(), account);

        Asset asset;
        if (assetOpt.isPresent()) {
            asset = assetOpt.get();

            BigDecimal newAssetAmount = asset.getAssetAmount().add(assetQuantity);
            asset.setAssetAmount(newAssetAmount);

            BigDecimal averagePriceBought = asset.getAveragePriceBought();
            BigDecimal unitsBought = asset.getTotalAssetBought();

            averagePriceBought = ((averagePriceBought.multiply(unitsBought)).add(assetQuantity.multiply(assetPrice)))
                    .divide(newAssetAmount, 16, RoundingMode.HALF_UP);

            asset.setAveragePriceBought(averagePriceBought);
            asset.setTotalAssetBought(asset.getTotalAssetBought().add(assetQuantity));

        } else {
            asset = Asset.builder()
                    .assetSymbol(transactionRequest.assetSymbol())
                    .assetAmount(assetQuantity)
                    .totalAssetBought(assetQuantity)
                    .averagePriceBought(assetPrice)
                    .account(account)
                    .build();
        }
        return assetRepository.save(asset);
    }

    private AssetTransaction saveAssetTransaction(AssetPurchaseRequest transactionRequest,
                                                  BigDecimal assetQuantity, BigDecimal assetPrice,
                                                  Asset asset) {
        AssetTransaction assetTransaction = AssetTransaction.builder()
                .transactionType(AssetTransactionType.PURCHASE)
                .amount(assetQuantity)
                .price(assetPrice)
                .asset(asset)
                .transactionValue(transactionRequest.amount())
                .build();

        return assetTransactionRepository.save(assetTransaction);
    }
}
