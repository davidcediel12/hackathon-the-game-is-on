package com.hackathon.bankingapp.controller;

import com.hackathon.bankingapp.dto.response.AccountDetailResponse;
import com.hackathon.bankingapp.dto.response.UserDetailsResponse;
import com.hackathon.bankingapp.services.AccountService;
import com.hackathon.bankingapp.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final UserService userService;
    private final AccountService accountService;

    @GetMapping("/user")
    public ResponseEntity<UserDetailsResponse> getUserInfo() {
        return ResponseEntity.ok(userService.getLoggedInUserDetails());
    }


    @GetMapping("/account")
    public ResponseEntity<AccountDetailResponse> getAccountDetail() {
        return ResponseEntity.ok(accountService.getLoggedInUserAccount());
    }
}
