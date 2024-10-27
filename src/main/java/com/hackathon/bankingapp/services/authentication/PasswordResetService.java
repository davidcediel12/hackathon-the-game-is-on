package com.hackathon.bankingapp.services.authentication;

import com.hackathon.bankingapp.dto.request.authentication.ResetPasswordRequest;
import com.hackathon.bankingapp.dto.request.authentication.ResetTokenRequest;
import com.hackathon.bankingapp.dto.response.authentication.ResetTokenResponse;

public interface PasswordResetService {


    void sendOtp(String email);

    ResetTokenResponse getResetToken(ResetTokenRequest resetTokenRequest);

    void resetPassword(ResetPasswordRequest resetPasswordRequest);
}
