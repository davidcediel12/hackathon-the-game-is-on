package com.hackathon.bankingapp.services.transaction.impl;

import com.hackathon.bankingapp.dto.request.account.TransactionRequest;
import com.hackathon.bankingapp.dto.request.account.TransferRequest;
import com.hackathon.bankingapp.entities.Account;
import com.hackathon.bankingapp.exceptions.ApiException;
import com.hackathon.bankingapp.repositories.AccountRepository;
import com.hackathon.bankingapp.services.customer.AccountService;
import com.hackathon.bankingapp.services.transaction.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final AccountService accountService;
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public void depositMoney(TransactionRequest transactionRequest) {
        Account account = getAccountAndValidatePin(transactionRequest.pin());
        addMoney(account, transactionRequest.amount());
    }


    @Override
    @Transactional
    public void withdrawMoney(TransactionRequest transactionRequest) {
        Account account = getAccountAndValidatePin(transactionRequest.pin());

        subtractMoney(transactionRequest.amount(), account);
    }


    @Override
    @Transactional
    public void transferMoney(TransferRequest transferRequest) {

        Account account = getAccountAndValidatePin(transferRequest.pin());

        Account destinationAccount = accountRepository.findByAccountId(transferRequest.targetAccountNumber())
                .orElseThrow(() -> new ApiException("Destination account not found", HttpStatus.BAD_REQUEST));

        subtractMoney(transferRequest.amount(), account);
        addMoney(destinationAccount, transferRequest.amount());
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
            throw new ApiException("Invalid PIN", HttpStatus.FORBIDDEN);
        }
        return account;
    }
}
