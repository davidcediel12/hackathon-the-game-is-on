package com.hackathon.bankingapp.services.transaction;

import com.hackathon.bankingapp.dto.request.transaction.AssetTransactionRequest;

public interface AssetService {

    void buyAsset(AssetTransactionRequest assetTransaction);
}
