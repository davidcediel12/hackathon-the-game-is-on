package com.hackathon.bankingapp.services.impl;

import com.hackathon.bankingapp.dto.response.AccountDetailResponse;
import com.hackathon.bankingapp.entities.Account;
import com.hackathon.bankingapp.entities.User;
import com.hackathon.bankingapp.repositories.AccountRepository;
import com.hackathon.bankingapp.services.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public Account createAccount(User user) {
        String accountId = UUID.randomUUID().toString();

        Account account = Account.builder()
                .accountId(accountId)
                .balance(BigDecimal.ZERO)
                .user(user)
                .build();


        return accountRepository.save(account);
    }

    @Override
    public AccountDetailResponse getLoggedInUserAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Account account = user.getAccount();
        return new AccountDetailResponse(account.getAccountId(), account.getBalance());
    }
}
