package com.hackathon.bankingapp.services.transaction.impl;

import com.hackathon.bankingapp.entities.Account;
import com.hackathon.bankingapp.entities.Asset;
import com.hackathon.bankingapp.entities.AssetTransaction;
import com.hackathon.bankingapp.repositories.AssetRepository;
import com.hackathon.bankingapp.repositories.AssetTransactionRepository;
import com.hackathon.bankingapp.services.email.EmailService;
import com.hackathon.bankingapp.services.transaction.AssetMailingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import static com.hackathon.bankingapp.utils.Constants.*;

@RequiredArgsConstructor
@Service
public class AssetMailingServiceImpl implements AssetMailingService {

    private final AssetRepository assetRepository;
    private final AssetTransactionRepository assetTransactionRepository;
    private final EmailService emailService;


    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public void sendAssetPurchaseMessage(Asset asset, Account account,
                                         AssetTransaction assetTransaction) {


        String initialMailPart = String.format(Locale.US, MAIL_PURCHASE_ASSET_INITIAL,
                asset.getAssetAmount(), asset.getAssetSymbol(),
                assetTransaction.getTransactionValue(),
                asset.getAssetSymbol(), asset.getAssetAmount());

        AssetsAndTotalWorth assetsAndTotalWorth = getAssetsAndTotalWorth(account);

        String finalMailPart = String.format(Locale.US,
                MAIL_PURCHASE_ASSET_END, account.getBalance(), assetsAndTotalWorth.totalWorth());

        String completeMailBody = initialMailPart + assetsAndTotalWorth.allAssetsString() + finalMailPart;

        emailService.sendSimpleMessage(account.getUser().getEmail(),
                "Investment Purchase Confirmation", completeMailBody);

    }


    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public void sendAssetSellMessage(Asset asset, Account account, AssetTransaction assetTransaction) {
        BigDecimal operationProfits = assetTransaction.getTransactionValue().subtract(
                asset.getAveragePriceBought().multiply(assetTransaction.getAmount()));


        String initialMailPart = String.format(Locale.US, MAIL_SALE_ASSET_INITIAL,
                assetTransaction.getAmount(), asset.getAssetSymbol(), operationProfits,
                asset.getAssetSymbol(), asset.getAssetAmount());

        AssetsAndTotalWorth assetsAndTotalWorth = getAssetsAndTotalWorth(account);

        String finalMailPart = String.format(Locale.US,
                MAIL_PURCHASE_ASSET_END, account.getBalance(), assetsAndTotalWorth.totalWorth());

        String completeMailBody = initialMailPart + assetsAndTotalWorth.allAssetsString() + finalMailPart;

        emailService.sendSimpleMessage(account.getUser().getEmail(),
                "Investment Sale Confirmation", completeMailBody);
    }

    private AssetsAndTotalWorth getAssetsAndTotalWorth(Account account) {
        StringBuilder allAssetsString = new StringBuilder();
        BigDecimal totalWorth = account.getBalance();

        List<Asset> allUserAssets = assetRepository.findByAccount(account);
        for (Asset accountAsset : allUserAssets) {
            AssetSummary assetSummary = obtainAssetAverages(accountAsset);
            totalWorth = totalWorth.add(assetSummary.assetWorth);

            String assetLine = assetSummary.description();
            allAssetsString.append(assetLine).append("\n");
        }
        return new AssetsAndTotalWorth(allAssetsString, totalWorth);
    }

    private record AssetsAndTotalWorth(StringBuilder allAssetsString, BigDecimal totalWorth) {
    }

    private AssetSummary obtainAssetAverages(Asset asset) {

        BigDecimal finalPrice = asset.getAveragePriceBought().multiply(asset.getAssetAmount());

        String assetDescription = String.format(Locale.US, ASSET_SUMMARY_LINE,
                asset.getAssetSymbol(), asset.getAssetAmount(), finalPrice);

        return new AssetSummary(assetDescription, finalPrice);
    }

    private record AssetSummary(String description, BigDecimal assetWorth) {
    }
}
