package com.hackathon.bankingapp.services.transaction;

import com.hackathon.bankingapp.entities.Account;
import com.hackathon.bankingapp.entities.Asset;
import com.hackathon.bankingapp.entities.AssetTransaction;

public interface AssetMailingService {
    void sendAssetPurchaseMessage(Asset asset, Account account,
                                  AssetTransaction assetTransaction);

    void sendAssetSellMessage(Asset asset, Account account, AssetTransaction assetTransaction);
}
