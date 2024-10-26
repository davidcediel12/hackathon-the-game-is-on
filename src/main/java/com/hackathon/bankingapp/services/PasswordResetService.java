package com.hackathon.bankingapp.services;

import com.hackathon.bankingapp.dto.request.ResetPasswordRequest;
import com.hackathon.bankingapp.dto.request.ResetTokenRequest;
import com.hackathon.bankingapp.dto.response.ResetTokenResponse;

public interface PasswordResetService {


    void sendOtp(String email);

    ResetTokenResponse getResetToken(ResetTokenRequest resetTokenRequest);

    void resetPassword(ResetPasswordRequest resetPasswordRequest);
}
