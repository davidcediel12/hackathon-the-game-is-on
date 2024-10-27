package com.hackathon.bankingapp.services.transaction.impl;

import com.hackathon.bankingapp.entities.Account;
import com.hackathon.bankingapp.entities.Asset;
import com.hackathon.bankingapp.exceptions.ApiException;
import com.hackathon.bankingapp.repositories.AccountRepository;
import com.hackathon.bankingapp.repositories.AssetRepository;
import com.hackathon.bankingapp.services.customer.AccountService;
import com.hackathon.bankingapp.services.transaction.AssetService;
import com.hackathon.bankingapp.services.transaction.InvestmentBotService;
import com.hackathon.bankingapp.services.transaction.MarketPricesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;


@Service
@Slf4j
public class InvestmentBotServiceImpl implements InvestmentBotService {

    private final ThreadPoolTaskScheduler taskScheduler;
    private final Map<Long, ScheduledFuture<?>> scheduledTasks;
    private final AccountService accountService;
    private final MarketPricesService marketPricesService;
    private final AssetRepository assetRepository;
    private final AccountRepository accountRepository;
    private final AssetService assetService;

    public InvestmentBotServiceImpl(ThreadPoolTaskScheduler taskScheduler, AccountService accountService,
                                    MarketPricesService marketPricesService, AssetRepository assetRepository,
                                    AccountRepository accountRepository, AssetService assetService) {

        this.taskScheduler = taskScheduler;
        this.marketPricesService = marketPricesService;
        this.assetService = assetService;
        this.scheduledTasks = new HashMap<>();
        this.accountService = accountService;
        this.assetRepository = assetRepository;
        this.accountRepository = accountRepository;
    }


    @Override
    public void startInvestmentBot(String pin) {
        Long accountId = accountService.getUserAccount().getId();
        Runnable task = () -> {
            try {
                this.analyzeAssets(accountId);
            } catch (RuntimeException e) {
                log.warn("Stopping bot task due tue an error ", e);
                cancelTask(accountId);
            }
        };

        schedulePayment(accountId, task, 10);
    }

    public void schedulePayment(Long accountId, Runnable task, long delay) {


        ScheduledFuture<?> botAnalyzer = scheduledTasks.get(accountId);

        if (botAnalyzer != null && !botAnalyzer.isDone()) {
            botAnalyzer.cancel(true);
            scheduledTasks.remove(accountId);
        }
        botAnalyzer = taskScheduler.scheduleWithFixedDelay(
                task, Instant.now(), Duration.ofSeconds(delay));

        scheduledTasks.put(accountId, botAnalyzer);
    }

    public void cancelTask(Long accountId) {
        if (scheduledTasks.get(accountId) != null) {
            scheduledTasks.get(accountId).cancel(true);
            scheduledTasks.remove(accountId);
        }
    }

    private void analyzeAssets(Long accountId) {

        log.info("Analyzing assets for account {}", accountId);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ApiException("Account not found", HttpStatus.INTERNAL_SERVER_ERROR));

        List<Asset> assets = assetRepository.findByAccount(account);
        Map<String, BigDecimal> currentAssetPrices = marketPricesService.getAssetsPrice();


        for (Asset asset : assets) {

            if (!currentAssetPrices.containsKey(asset.getAssetSymbol())) {
                continue;
            }

            BigDecimal currentAssetPrice = currentAssetPrices.get(asset.getAssetSymbol());

            BigDecimal averagePriceBought = asset.getAveragePriceBought();

            BigDecimal percentagePriceChange = (currentAssetPrice.subtract(averagePriceBought))
                    .divide(averagePriceBought, 16, RoundingMode.HALF_UP);

            BigDecimal percentageChangeThreshold = BigDecimal.valueOf(0.1);


            boolean changeMeetThreshold = percentagePriceChange.abs().compareTo(percentageChangeThreshold) >= 0;
            if (changeMeetThreshold) {
                boolean isCheaper = percentagePriceChange.compareTo(BigDecimal.ZERO) <= 0;
                if (isCheaper) {
                    buyAsset(asset, account, assets, percentagePriceChange, currentAssetPrice);
                } else { // More expensive

                    BigDecimal assetQuantity = asset.getAssetAmount();
                    BigDecimal assetToSell = assetQuantity.multiply(
                            max(percentagePriceChange, BigDecimal.valueOf(0.3)));

                    assetService.sellAsset(account, asset.getAssetSymbol(), assetToSell);
                    log.info("Sell {} of {} at price {} (avg price bought {}), profit percentage {}",
                            asset.getAssetAmount(), asset.getAssetSymbol(), currentAssetPrice,
                            asset.getAveragePriceBought(), percentagePriceChange);
                }
            } else {
                log.info("The percentage change between the avg price {} and actual price {} is not worth it to exchange ({})",
                        asset.getAveragePriceBought(), currentAssetPrice, percentagePriceChange);
            }
        }
    }

    private void buyAsset(Asset asset, Account account, List<Asset> assets,
                          BigDecimal percentagePriceChange, BigDecimal currentAssetPrice) {

        BigDecimal availableAmountPerAsset = account.getBalance().divide(
                BigDecimal.valueOf(assets.size()), 12, RoundingMode.HALF_UP);

        BigDecimal amountPercentageToSpend = max(BigDecimal.valueOf(0.3), percentagePriceChange.abs());

        BigDecimal amountToBuy = availableAmountPerAsset.multiply(amountPercentageToSpend);

        assetService.buyAsset(account, asset.getAssetSymbol(), amountToBuy);
        log.info("Bot: Buy {} of {} at price {} (average price bought {}), profit percentage: {}",
                amountToBuy, asset.getAssetSymbol(), currentAssetPrice, asset.getAveragePriceBought(), percentagePriceChange);
    }

    private BigDecimal max(BigDecimal x, BigDecimal y) {
        if (x.compareTo(y) > 0) {
            return x;
        }
        return y;
    }


}
