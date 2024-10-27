package com.hackathon.bankingapp.services.transaction.impl;

import com.hackathon.bankingapp.dto.request.transaction.TransactionRequest;
import com.hackathon.bankingapp.dto.request.transaction.TransferRequest;
import com.hackathon.bankingapp.dto.response.transaction.TransactionDetail;
import com.hackathon.bankingapp.entities.Account;
import com.hackathon.bankingapp.entities.Transaction;
import com.hackathon.bankingapp.entities.TransactionType;
import com.hackathon.bankingapp.exceptions.ApiException;
import com.hackathon.bankingapp.repositories.AccountRepository;
import com.hackathon.bankingapp.repositories.TransactionRepository;
import com.hackathon.bankingapp.services.customer.AccountService;
import com.hackathon.bankingapp.services.transaction.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public void depositMoney(TransactionRequest transactionRequest) {
        Account account = getAccountAndValidatePin(transactionRequest.pin());
        addMoney(account, transactionRequest.amount());
        saveTransaction(transactionRequest.amount(), account, null, TransactionType.CASH_DEPOSIT);
    }


    @Override
    @Transactional
    public void withdrawMoney(TransactionRequest transactionRequest) {
        Account account = getAccountAndValidatePin(transactionRequest.pin());

        subtractMoney(transactionRequest.amount(), account);
        saveTransaction(transactionRequest.amount(), account, null, TransactionType.CASH_WITHDRAWAL);
    }


    @Override
    @Transactional
    public void transferMoney(TransferRequest transferRequest) {

        Account account = getAccountAndValidatePin(transferRequest.pin());

        Account destinationAccount = accountRepository.findByAccountId(transferRequest.targetAccountNumber())
                .orElseThrow(() -> new ApiException("Destination account not found", HttpStatus.BAD_REQUEST));

        subtractMoney(transferRequest.amount(), account);
        addMoney(destinationAccount, transferRequest.amount());

        saveTransaction(transferRequest.amount(), account, destinationAccount, TransactionType.CASH_TRANSFER);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDetail> getTransactions() {

        Account account = accountService.getUserAccount();

        return transactionRepository.findBySourceAccount(account).stream()
                .map(Transaction::toDetail)
                .toList();
    }

    private void saveTransaction(BigDecimal amount, Account account, Account destinationAccount,
                                 TransactionType transactionType) {

        Transaction transaction = Transaction.builder()
                .sourceAccount(account)
                .targetAccount(destinationAccount)
                .amount(amount)
                .transactionType(transactionType)
                .transactionDate(Instant.now())
                .build();

        transactionRepository.save(transaction);
    }

    private void addMoney(Account account, BigDecimal amount) {
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
    }

    private void subtractMoney(BigDecimal value, Account account) {
        BigDecimal newBalance = account.getBalance().subtract(value);
        boolean isNegativeBalance = newBalance.compareTo(BigDecimal.ZERO) < 0;

        if (isNegativeBalance) {
            throw new ApiException("Insufficient balance", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        account.setBalance(newBalance);
        accountRepository.save(account);
    }

    private Account getAccountAndValidatePin(String pin) {
        Account account = accountService.getUserAccount();

        if (!Objects.equals(pin, account.getPin())) {
            throw ApiException.invalidPin();
        }
        return account;
    }
}
