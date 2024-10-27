package com.hackathon.bankingapp.services.transaction.impl;

import com.hackathon.bankingapp.dto.request.transaction.AssetTransactionRequest;
import com.hackathon.bankingapp.entities.Account;
import com.hackathon.bankingapp.entities.Asset;
import com.hackathon.bankingapp.exceptions.ApiException;
import com.hackathon.bankingapp.repositories.AccountRepository;
import com.hackathon.bankingapp.repositories.AssetRepository;
import com.hackathon.bankingapp.services.customer.AccountService;
import com.hackathon.bankingapp.services.transaction.AssetService;
import com.hackathon.bankingapp.services.transaction.MarketPricesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {

    private final MarketPricesService marketPricesService;
    private final AccountService accountService;
    private final AssetRepository assetRepository;
    private final AccountRepository accountRepository;


    @Override
    @Transactional
    public void buyAsset(AssetTransactionRequest assetTransaction) {
        Account account = accountService.getUserAccount();

        BigDecimal newBalance = account.getBalance().subtract(assetTransaction.amount());
        if (newBalance.compareTo(assetTransaction.amount()) < 0) {
            throw new ApiException("Internal error occurred while purchasing the asset.",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        BigDecimal assetPrice = marketPricesService.getAssetPrice(assetTransaction.assetSymbol());

        BigDecimal assetQuantity = assetTransaction.amount()
                .divide(assetPrice, 16, RoundingMode.HALF_UP);

        Asset asset = Asset.builder()
                .assetSymbol(assetTransaction.assetSymbol())
                .price(assetPrice)
                .amount(assetQuantity)
                .account(account)
                .build();

        assetRepository.save(asset);
        account.setBalance(newBalance);
        accountRepository.save(account);
    }
}
