package com.hackathon.bankingapp.services.transaction.impl;

import com.hackathon.bankingapp.dto.request.transaction.AssetPurchaseRequest;
import com.hackathon.bankingapp.dto.request.transaction.AssetSaleRequest;
import com.hackathon.bankingapp.entities.*;
import com.hackathon.bankingapp.exceptions.ApiException;
import com.hackathon.bankingapp.repositories.AccountRepository;
import com.hackathon.bankingapp.repositories.AssetRepository;
import com.hackathon.bankingapp.repositories.AssetTransactionRepository;
import com.hackathon.bankingapp.repositories.TransactionRepository;
import com.hackathon.bankingapp.services.customer.AccountService;
import com.hackathon.bankingapp.services.transaction.AssetMailingService;
import com.hackathon.bankingapp.services.transaction.AssetService;
import com.hackathon.bankingapp.services.transaction.MarketPricesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssetServiceImpl implements AssetService {

    private final MarketPricesService marketPricesService;
    private final AccountService accountService;
    private final AssetRepository assetRepository;
    private final AccountRepository accountRepository;
    private final AssetTransactionRepository assetTransactionRepository;
    private final AssetMailingService assetMailingService;
    private final TransactionRepository transactionRepository;

    private record AssetInvestmentResult(Asset asset, AssetTransaction assetTransaction) {
    }


    @Override
    @Transactional
    public void buyAsset(AssetPurchaseRequest transactionRequest) {
        Account account = accountService.getUserAccount();
        validatePin(account, transactionRequest.pin());

        AssetInvestmentResult assetPurchaseResult = buyAsset(transactionRequest.amount(),
                transactionRequest.assetSymbol(), account);

        assetMailingService.sendAssetPurchaseMessage(assetPurchaseResult.asset(),
                account, assetPurchaseResult.assetTransaction());
    }

    @Override
    public void buyAsset(Account account, String assetSymbol, BigDecimal amount) {
        buyAsset(amount, assetSymbol, account);
    }

    @Override
    @Transactional
    public void sellAsset(AssetSaleRequest saleRequest) {
        Account account = accountService.getUserAccount();
        validatePin(account, saleRequest.pin());

        AssetInvestmentResult result = sellAsset(saleRequest.quantity(), saleRequest.assetSymbol(), account);
        assetMailingService.sendAssetSellMessage(result.asset(), account, result.assetTransaction());

    }

    @Override
    @Transactional
    public void sellAsset(Account account, String assetSymbol, BigDecimal quantity) {
        sellAsset(quantity, assetSymbol, account);
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
                .map(asset -> asset.getAssetAmount().multiply(asset.getAveragePurchasedPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return assetsWorth.add(account.getBalance()).setScale(12, RoundingMode.HALF_UP)
                .stripTrailingZeros();
    }

    private AssetInvestmentResult buyAsset(BigDecimal amount, String assetSymbol, Account account) {
        BigDecimal newBalance = account.getBalance().subtract(amount);
        boolean insufficientBalance = newBalance.compareTo(BigDecimal.ZERO) < 0;
        if (insufficientBalance) {
            throw new ApiException("Internal error occurred while purchasing the asset.",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        BigDecimal assetPrice = marketPricesService.getAssetPrice(assetSymbol);

        BigDecimal assetQuantity = amount
                .divide(assetPrice, 16, RoundingMode.HALF_UP);

        Asset asset = saveAsset(assetSymbol, account, assetQuantity, assetPrice);

        AssetTransaction assetTransaction = saveAssetTransaction(amount, assetQuantity, assetPrice, asset);

        account.setBalance(newBalance);
        accountRepository.save(account);
        saveAccountTransaction(amount, TransactionType.ASSET_PURCHASE, account);

        return new AssetInvestmentResult(asset, assetTransaction);
    }

    private AssetInvestmentResult sellAsset(BigDecimal quantity, String assetSymbol, Account account) {
        ApiException errorSellingAsset = new ApiException("Internal error occurred while selling the asset.", HttpStatus.INTERNAL_SERVER_ERROR);

        Asset asset = updateAssetQuantity(quantity, assetSymbol, account, errorSellingAsset);

        BigDecimal assetPrice = marketPricesService.getAssetPrice(assetSymbol);
        BigDecimal transactionValue = assetPrice.multiply(quantity);

        account.setBalance(account.getBalance().add(transactionValue));
        accountRepository.save(account);

        AssetTransaction assetTransaction = createSellAssetTransaction(
                quantity, asset, transactionValue, assetPrice);


        saveAccountTransaction(transactionValue, TransactionType.ASSET_SELL, account);
        return new AssetInvestmentResult(asset, assetTransaction);
    }


    private Asset updateAssetQuantity(BigDecimal quantity, String assetSymbol,
                                      Account account, ApiException errorSellingAsset) {

        Asset asset = assetRepository.findByAssetSymbolAndAccount(assetSymbol, account)
                .orElseThrow(() -> errorSellingAsset);

        BigDecimal newAssetQuantity = asset.getAssetAmount().subtract(quantity);
        boolean insufficientQuantity = newAssetQuantity.compareTo(BigDecimal.ZERO) < 0;
        if (insufficientQuantity) {
            log.error("Insufficient quantity to sell actual: {}, intended to sell: {}",
                    asset.getAssetAmount(), quantity);
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

    private Asset saveAsset(String assetSymbol, Account account,
                            BigDecimal assetQuantity, BigDecimal assetPrice) {

        Optional<Asset> assetOpt = assetRepository.findByAssetSymbolAndAccount(
                assetSymbol, account);

        Asset asset;
        if (assetOpt.isPresent()) {
            asset = assetOpt.get();

            BigDecimal newAssetAmount = asset.getAssetAmount().add(assetQuantity);
            asset.setAssetAmount(newAssetAmount);

            BigDecimal averagePurchasedPrice = asset.getAveragePurchasedPrice();
            BigDecimal purchasedUnits = asset.getTotalPurchasedAssets();

            averagePurchasedPrice = ((averagePurchasedPrice.multiply(purchasedUnits))
                    .add(assetQuantity.multiply(assetPrice)))
                    .divide(newAssetAmount, 16, RoundingMode.HALF_UP);

            asset.setAveragePurchasedPrice(averagePurchasedPrice);
            asset.setTotalPurchasedAssets(purchasedUnits.add(assetQuantity));

        } else {
            asset = Asset.builder()
                    .assetSymbol(assetSymbol)
                    .assetAmount(assetQuantity)
                    .totalPurchasedAssets(assetQuantity)
                    .averagePurchasedPrice(assetPrice)
                    .account(account)
                    .build();
        }
        return assetRepository.save(asset);
    }

    private AssetTransaction saveAssetTransaction(BigDecimal amount,
                                                  BigDecimal assetQuantity, BigDecimal assetPrice,
                                                  Asset asset) {
        AssetTransaction assetTransaction = AssetTransaction.builder()
                .transactionType(AssetTransactionType.PURCHASE)
                .amount(assetQuantity)
                .price(assetPrice)
                .asset(asset)
                .transactionValue(amount)
                .build();

        return assetTransactionRepository.save(assetTransaction);
    }

    private AssetTransaction createSellAssetTransaction(BigDecimal quantity, Asset asset,
                                                        BigDecimal transactionValue, BigDecimal assetPrice) {

        AssetTransaction assetTransaction = AssetTransaction.builder()
                .asset(asset)
                .transactionValue(transactionValue)
                .price(assetPrice)
                .amount(quantity)
                .transactionType(AssetTransactionType.SELL)
                .build();

        return assetTransactionRepository.save(assetTransaction);
    }


    private void saveAccountTransaction(BigDecimal amount, TransactionType transactionType, Account account) {

        Transaction transaction = Transaction.builder()
                .amount(amount)
                .transactionType(transactionType)
                .transactionDate(Instant.now())
                .sourceAccount(account)
                .build();
        transactionRepository.save(transaction);
    }
}
