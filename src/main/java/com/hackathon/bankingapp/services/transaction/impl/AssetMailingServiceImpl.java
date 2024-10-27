package com.hackathon.bankingapp.services.transaction.impl;

import com.hackathon.bankingapp.entities.Account;
import com.hackathon.bankingapp.entities.Asset;
import com.hackathon.bankingapp.entities.AssetTransaction;
import com.hackathon.bankingapp.entities.AssetTransactionType;
import com.hackathon.bankingapp.repositories.AssetRepository;
import com.hackathon.bankingapp.repositories.AssetTransactionRepository;
import com.hackathon.bankingapp.services.email.EmailService;
import com.hackathon.bankingapp.services.transaction.AssetMailingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import static com.hackathon.bankingapp.utils.Constants.*;

@RequiredArgsConstructor
@Service
public class AssetMailingServiceImpl implements AssetMailingService {

    private final AssetRepository assetRepository;
    private final AssetTransactionRepository assetTransactionRepository;
    private final EmailService emailService;


    @Override
    public void sendAssetPurchaseMessage(Asset asset, Account account,
                                         AssetTransaction assetTransaction) {


        String initialMailPart = String.format(Locale.US, MAIL_PURCHASE_ASSET_INITIAL,
                asset.getAssetAmount(), asset.getAssetSymbol(),
                assetTransaction.getTransactionValue(),
                asset.getAssetSymbol(), asset.getAssetAmount());

        StringBuilder allAssetsString = new StringBuilder();
        BigDecimal totalWorth = account.getBalance();

        List<Asset> allUserAssets = assetRepository.findByAccount(account);
        for (Asset accountAsset : allUserAssets) {
            AssetSummary assetSummary = obtainAssetAverages(accountAsset);
            totalWorth = totalWorth.add(assetSummary.assetWorth);

            String assetLine = assetSummary.description();
            allAssetsString.append(assetLine).append("\n");
        }

        String finalMailPart = String.format(Locale.US,
                MAIL_PURCHASE_ASSET_END.formatted(account.getBalance(), totalWorth));

        String completeMailBody = initialMailPart + allAssetsString + finalMailPart;

        emailService.sendSimpleMessage(account.getUser().getEmail(),
                "Investment Sale Confirmation", completeMailBody);

    }

    private AssetSummary obtainAssetAverages(Asset asset) {


        List<AssetTransaction> assetTransactions = assetTransactionRepository.findByAsset(asset);

        AtomicReference<BigDecimal> averagePrice = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal> totalAssetBought = new AtomicReference<>(BigDecimal.ZERO);

        assetTransactions.stream()
                .filter(transaction -> transaction.getTransactionType().equals(AssetTransactionType.PURCHASE))
                .forEach(transaction -> {
                    totalAssetBought.updateAndGet(assetAmount -> assetAmount.add(transaction.getAmount()));
                    averagePrice.updateAndGet(amountSpend -> amountSpend.add(
                            transaction.getAmount().multiply(transaction.getPrice())));
                });

        averagePrice.updateAndGet(price -> price.divide(
                totalAssetBought.get(), 16, RoundingMode.HALF_UP));

        BigDecimal finalPrice = averagePrice.get().multiply(asset.getAssetAmount());

        String assetDescription = String.format(Locale.US, ASSET_SUMMARY_LINE,
                asset.getAssetSymbol(), asset.getAssetAmount(), finalPrice);

        return new AssetSummary(assetDescription, finalPrice);
    }

    record AssetSummary(String description, BigDecimal assetWorth) {
    }
}
