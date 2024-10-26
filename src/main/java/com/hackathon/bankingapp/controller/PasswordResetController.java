package com.hackathon.bankingapp.controller;


import com.hackathon.bankingapp.dto.request.OtpRequest;
import com.hackathon.bankingapp.dto.request.ResetTokenRequest;
import com.hackathon.bankingapp.dto.response.OtpResponse;
import com.hackathon.bankingapp.dto.response.ResetTokenResponse;
import com.hackathon.bankingapp.services.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/password-reset")
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/send-otp")
    public ResponseEntity<OtpResponse> sendOtp(@RequestBody @Valid OtpRequest otpRequest) {
        passwordResetService.sendOtp(otpRequest.identifier());

        String successMessage = "OTP sent successfully to: " + otpRequest.identifier();
        return ResponseEntity.ok(new OtpResponse(successMessage));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ResetTokenResponse> getResetToken(@Valid @RequestBody ResetTokenRequest otpRequest) {

        return ResponseEntity.ok(passwordResetService.getResetToken(otpRequest));
    }
}
