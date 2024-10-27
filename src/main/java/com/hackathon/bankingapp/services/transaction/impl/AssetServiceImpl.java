package com.hackathon.bankingapp.services.transaction.impl;

import com.hackathon.bankingapp.dto.request.transaction.AssetTransactionRequest;
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
    public void buyAsset(AssetTransactionRequest transactionRequest) {
        Account account = accountService.getUserAccount();
        validatePin(account, transactionRequest.pin());

        BigDecimal newBalance = account.getBalance().subtract(transactionRequest.amount());
        if (newBalance.compareTo(transactionRequest.amount()) < 0) {
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

    private void validatePin(Account account, String pin) {
        if (!Objects.equals(account.getPin(), pin)) {
            throw ApiException.invalidPin();
        }
    }

    private Asset saveAsset(AssetTransactionRequest transactionRequest, Account account,
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

    private AssetTransaction saveAssetTransaction(AssetTransactionRequest transactionRequest,
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
