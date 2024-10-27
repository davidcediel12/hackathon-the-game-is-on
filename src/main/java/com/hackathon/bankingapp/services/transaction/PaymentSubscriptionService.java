package com.hackathon.bankingapp.services.transaction;

import com.hackathon.bankingapp.dto.request.transaction.PaymentSubscriptionRequest;

public interface PaymentSubscriptionService {

    void scheduleSubscription(PaymentSubscriptionRequest paymentSubscriptionRequest);
}
