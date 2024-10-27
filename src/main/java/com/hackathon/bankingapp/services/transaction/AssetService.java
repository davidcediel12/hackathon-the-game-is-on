package com.hackathon.bankingapp.services.transaction;

import com.hackathon.bankingapp.dto.request.transaction.AssetPurchaseRequest;
import com.hackathon.bankingapp.dto.request.transaction.AssetSaleRequest;

import java.math.BigDecimal;
import java.util.Map;

public interface AssetService {

    void buyAsset(AssetPurchaseRequest transactionRequest);
    void sellAsset(AssetSaleRequest transactionRequest);

    Map<String, BigDecimal> getAssets();

    BigDecimal getNetWorth();

}
