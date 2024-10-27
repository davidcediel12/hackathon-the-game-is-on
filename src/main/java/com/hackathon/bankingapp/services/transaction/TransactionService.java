package com.hackathon.bankingapp.services.transaction;

import com.hackathon.bankingapp.dto.request.account.DepositRequest;

public interface TransactionService {

    void depositMoney(DepositRequest depositRequest);
}
