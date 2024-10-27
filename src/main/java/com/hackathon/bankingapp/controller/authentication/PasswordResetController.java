package com.hackathon.bankingapp.controller.authentication;


import com.hackathon.bankingapp.dto.request.authentication.OtpRequest;
import com.hackathon.bankingapp.dto.request.authentication.ResetPasswordRequest;
import com.hackathon.bankingapp.dto.request.authentication.ResetTokenRequest;
import com.hackathon.bankingapp.dto.response.GenericResponse;
import com.hackathon.bankingapp.dto.response.authentication.ResetTokenResponse;
import com.hackathon.bankingapp.services.authentication.PasswordResetService;
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
    public ResponseEntity<GenericResponse> sendOtp(@RequestBody @Valid OtpRequest otpRequest) {
        passwordResetService.sendOtp(otpRequest.identifier());

        String successMessage = "OTP sent successfully to: " + otpRequest.identifier();
        return ResponseEntity.ok(new GenericResponse(successMessage));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ResetTokenResponse> getResetToken(@Valid @RequestBody ResetTokenRequest otpRequest) {

        return ResponseEntity.ok(passwordResetService.getResetToken(otpRequest));
    }

    @PostMapping
    public ResponseEntity<GenericResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {

        passwordResetService.resetPassword(resetPasswordRequest);
        return ResponseEntity.ok(new GenericResponse("Password reset successfully"));
    }
}
