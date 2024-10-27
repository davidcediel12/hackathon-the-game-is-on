package com.hackathon.bankingapp.services.transaction;

import com.hackathon.bankingapp.dto.request.transaction.TransactionRequest;
import com.hackathon.bankingapp.dto.request.transaction.TransferRequest;
import com.hackathon.bankingapp.dto.response.transaction.TransactionDetail;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {

    void depositMoney(TransactionRequest transactionRequest);

    void withdrawMoney(TransactionRequest transactionRequest);

    void performAutomaticPayment(Long accountId, BigDecimal amount);

    void transferMoney(TransferRequest transferRequest);

    List<TransactionDetail> getTransactions();
}
