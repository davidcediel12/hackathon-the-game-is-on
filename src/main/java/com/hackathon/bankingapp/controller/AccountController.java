package com.hackathon.bankingapp.controller;


import com.hackathon.bankingapp.dto.response.AccountDetailResponse;
import com.hackathon.bankingapp.services.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class AccountController {


    private final AccountService accountService;


    @GetMapping("/account")
    public ResponseEntity<AccountDetailResponse> getAccountDetail() {
        return ResponseEntity.ok(accountService.getLoggedInUserAccount());
    }
}
