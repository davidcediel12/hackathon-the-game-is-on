package com.hackathon.bankingapp.services.transaction;

import com.hackathon.bankingapp.dto.request.account.TransactionRequest;
import com.hackathon.bankingapp.dto.request.account.TransferRequest;
import com.hackathon.bankingapp.dto.response.transaction.TransactionDetail;

import java.util.List;

public interface TransactionService {

    void depositMoney(TransactionRequest transactionRequest);

    void withdrawMoney(TransactionRequest transactionRequest);

    void transferMoney(TransferRequest transferRequest);

    List<TransactionDetail> getTransactions();
}
