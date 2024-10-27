package com.hackathon.bankingapp.services.transaction;

import com.hackathon.bankingapp.dto.request.transaction.AssetPurchaseRequest;
import com.hackathon.bankingapp.dto.request.transaction.AssetSaleRequest;

public interface AssetService {

    void buyAsset(AssetPurchaseRequest transactionRequest);
    void sellAsset(AssetSaleRequest transactionRequest);
}
