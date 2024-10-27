package com.hackathon.bankingapp.services.transaction;

import com.hackathon.bankingapp.dto.request.account.TransactionRequest;

public interface TransactionService {

    void depositMoney(TransactionRequest transactionRequest);

    void withdrawMoney(TransactionRequest transactionRequest);
}
