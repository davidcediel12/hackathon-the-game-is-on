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
import com.hackathon.bankingapp.services.transaction.AssetService;
import com.hackathon.bankingapp.services.transaction.MarketPricesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {

    private final MarketPricesService marketPricesService;
    private final AccountService accountService;
    private final AssetRepository assetRepository;
    private final AccountRepository accountRepository;
    private final AssetTransactionRepository assetTransactionRepository;


    @Override
    @Transactional
    public void buyAsset(AssetTransactionRequest transactionRequest) {
        Account account = accountService.getUserAccount();

        BigDecimal newBalance = account.getBalance().subtract(transactionRequest.amount());
        if (newBalance.compareTo(transactionRequest.amount()) < 0) {
            throw new ApiException("Internal error occurred while purchasing the asset.",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        BigDecimal assetPrice = marketPricesService.getAssetPrice(transactionRequest.assetSymbol());

        BigDecimal assetQuantity = transactionRequest.amount()
                .divide(assetPrice, 16, RoundingMode.HALF_UP);

        saveAsset(transactionRequest, account, assetQuantity);

        saveAssetTransaction(transactionRequest, assetQuantity, assetPrice);

        account.setBalance(newBalance);
        accountRepository.save(account);
    }

    private void saveAsset(AssetTransactionRequest transactionRequest, Account account, BigDecimal assetQuantity) {
        Optional<Asset> assetOpt = assetRepository.findByAssetSymbolAndAccount(
                transactionRequest.assetSymbol(), account);

        Asset asset;
        if (assetOpt.isPresent()) {
            asset = assetOpt.get();

            BigDecimal newAssetAmount = asset.getAssetAmount().add(assetQuantity);
            asset.setAssetAmount(newAssetAmount);
        } else {
            asset = Asset.builder()
                    .assetSymbol(transactionRequest.assetSymbol())
                    .assetAmount(assetQuantity)
                    .account(account)
                    .build();
        }
        assetRepository.save(asset);
    }

    private void saveAssetTransaction(AssetTransactionRequest transactionRequest, BigDecimal assetQuantity, BigDecimal assetPrice) {
        AssetTransaction assetTransaction = AssetTransaction.builder()
                .transactionType(AssetTransactionType.PURCHASE)
                .amount(assetQuantity)
                .price(assetPrice)
                .transactionValue(transactionRequest.amount())
                .build();

        assetTransactionRepository.save(assetTransaction);
    }
}
