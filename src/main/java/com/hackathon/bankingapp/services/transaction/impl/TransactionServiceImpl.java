package com.hackathon.bankingapp.services.transaction.impl;

import com.hackathon.bankingapp.dto.request.account.TransactionRequest;
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
        Account account = accountService.getUserAccount();

        validatePin(transactionRequest, account);

        account.setBalance(account.getBalance().add(transactionRequest.amount()));
        accountRepository.save(account);
    }

    @Override
    @Transactional
    public void withdrawMoney(TransactionRequest transactionRequest) {
        Account account = accountService.getUserAccount();

        validatePin(transactionRequest, account);

        BigDecimal newBalance = account.getBalance().subtract(transactionRequest.amount());
        boolean isNegativeBalance = newBalance.compareTo(BigDecimal.ZERO) < 0;

        if(isNegativeBalance) {
            throw new ApiException("Insufficient balance", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        account.setBalance(newBalance);
        accountRepository.save(account);
    }

    private static void validatePin(TransactionRequest transactionRequest, Account account) {
        if(!Objects.equals(transactionRequest.pin(), account.getPin())) {
            throw new ApiException("Invalid PIN", HttpStatus.FORBIDDEN  );
        }
    }


}
