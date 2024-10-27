package com.hackathon.bankingapp.services.transaction.impl;

import com.hackathon.bankingapp.dto.request.transaction.PaymentSubscriptionRequest;
import com.hackathon.bankingapp.services.customer.AccountService;
import com.hackathon.bankingapp.services.transaction.InvestmentBotService;
import com.hackathon.bankingapp.services.transaction.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;


@Service
@Slf4j
public class InvestmentBotServiceImpl implements InvestmentBotService {

    private final ThreadPoolTaskScheduler taskScheduler;
    private final Map<Long, ScheduledFuture<?>> scheduledTasks;
    private final TransactionService transactionService;
    private final AccountService accountService;

    public InvestmentBotServiceImpl(ThreadPoolTaskScheduler taskScheduler,
                                          TransactionService transactionService,
                                          AccountService accountService) {
        this.taskScheduler = taskScheduler;
        this.transactionService = transactionService;
        this.scheduledTasks = new HashMap<>();
        this.accountService = accountService;
    }


    @Override
    public void startInvestmentBot(String pin) {

    }

    public void schedulePayment(Long accountId, Runnable task, long delay) {


        ScheduledFuture<?> periodicPayment = scheduledTasks.get(accountId);

        if (periodicPayment != null && !periodicPayment.isDone()) {
            periodicPayment.cancel(true);
            scheduledTasks.remove(accountId);
        }
        periodicPayment = taskScheduler.scheduleWithFixedDelay(
                task, Instant.now(), Duration.ofSeconds(delay));

        scheduledTasks.put(accountId, periodicPayment);
    }

    public void cancelTask(Long accountId) {
        if (scheduledTasks.get(accountId) != null) {
            scheduledTasks.get(accountId).cancel(true);
            scheduledTasks.remove(accountId);
        }
    }

    private void withDrawMoneyPeriodically(Long accountId, PaymentSubscriptionRequest subscriptionRequest) {

        log.info("Scheduled task for account {}, paying {}", accountId, subscriptionRequest.amount());

        try {
            transactionService.performAutomaticPayment(accountId, subscriptionRequest.amount());
        } catch (RuntimeException e) {
            log.warn("Cancelling task due to a withdrawal error");
            cancelTask(accountId);
        }
    }


}
