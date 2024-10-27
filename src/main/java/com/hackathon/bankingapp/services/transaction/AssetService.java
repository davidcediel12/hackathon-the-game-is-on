package com.hackathon.bankingapp.services.transaction;

import com.hackathon.bankingapp.dto.request.transaction.AssetPurchaseRequest;
import com.hackathon.bankingapp.dto.request.transaction.AssetSaleRequest;
import com.hackathon.bankingapp.entities.Account;

import java.math.BigDecimal;
import java.util.Map;

public interface AssetService {

    void buyAsset(AssetPurchaseRequest transactionRequest);

    void buyAsset(Account account, String assetSymbol, BigDecimal amount);

    void sellAsset(AssetSaleRequest transactionRequest);

    void sellAsset(Account account, String assetSymbol, BigDecimal quantity);

    Map<String, BigDecimal> getAssets();

    BigDecimal getNetWorth();

}
