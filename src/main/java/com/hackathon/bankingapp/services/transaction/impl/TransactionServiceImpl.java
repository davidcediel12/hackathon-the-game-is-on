package com.hackathon.bankingapp.services.transaction.impl;

import com.hackathon.bankingapp.dto.request.account.DepositRequest;
import com.hackathon.bankingapp.entities.Account;
import com.hackathon.bankingapp.exceptions.ApiException;
import com.hackathon.bankingapp.repositories.AccountRepository;
import com.hackathon.bankingapp.services.customer.AccountService;
import com.hackathon.bankingapp.services.transaction.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final AccountService accountService;
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public void depositMoney(DepositRequest depositRequest) {
        Account account = accountService.getUserAccount();

        if(!Objects.equals(depositRequest.pin(), account.getPin())) {
            throw new ApiException("Invalid PIN", HttpStatus.FORBIDDEN  );
        }

        account.setBalance(account.getBalance().add(depositRequest.amount()));
        accountRepository.save(account);
    }
}
