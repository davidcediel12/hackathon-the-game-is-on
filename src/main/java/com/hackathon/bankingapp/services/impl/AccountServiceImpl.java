package com.hackathon.bankingapp.services.impl;

import com.hackathon.bankingapp.dto.request.AssignPinRequest;
import com.hackathon.bankingapp.dto.response.AccountDetailResponse;
import com.hackathon.bankingapp.entities.Account;
import com.hackathon.bankingapp.entities.User;
import com.hackathon.bankingapp.exceptions.ApiException;
import com.hackathon.bankingapp.repositories.AccountRepository;
import com.hackathon.bankingapp.services.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
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
        Account account = getUserAccount();
        return new AccountDetailResponse(account.getAccountId(), account.getBalance());
    }


    @Override
    public void assignPin(AssignPinRequest assignPinRequest) {

        User user = validateUserPasswordAndGetUser(assignPinRequest.password());
        Account account = user.getAccount();

        if(account.getPin() != null){
            throw new ApiException("Pin already exists", HttpStatus.BAD_REQUEST);
        }

        account.setPin(assignPinRequest.pin());
        accountRepository.save(account);
    }


    private  Account getUserAccount() {
        User user = getLoggedInUser();
        return user.getAccount();
    }

    private User validateUserPasswordAndGetUser(String password) {
        User user = getLoggedInUser();

        if(!passwordEncoder.matches(password,user.getPassword())){
            throw new ApiException("Invalid password", HttpStatus.BAD_REQUEST);
        }

        return user;
    }

    private User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }
}
