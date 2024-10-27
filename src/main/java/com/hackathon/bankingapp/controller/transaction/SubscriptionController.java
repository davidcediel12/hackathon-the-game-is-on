package com.hackathon.bankingapp.controller.transaction;

import com.hackathon.bankingapp.dto.request.transaction.PaymentSubscriptionRequest;
import com.hackathon.bankingapp.services.transaction.PaymentSubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user-actions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final PaymentSubscriptionService paymentSubscriptionService;


    @PostMapping("/subscribe")
    public ResponseEntity<String> createPaymentSubscription(@Valid @RequestBody
                                                            PaymentSubscriptionRequest paymentSubscriptionRequest) {

        paymentSubscriptionService.scheduleSubscription(paymentSubscriptionRequest);
        return ResponseEntity.ok().body("Subscription created successfully.");
    }


}
