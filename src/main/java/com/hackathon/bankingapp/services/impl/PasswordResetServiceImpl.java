package com.hackathon.bankingapp.services.impl;

import com.hackathon.bankingapp.dto.request.ResetPasswordRequest;
import com.hackathon.bankingapp.dto.request.ResetTokenRequest;
import com.hackathon.bankingapp.dto.response.ResetTokenResponse;
import com.hackathon.bankingapp.entities.OtpCode;
import com.hackathon.bankingapp.entities.ResetToken;
import com.hackathon.bankingapp.entities.User;
import com.hackathon.bankingapp.exceptions.ApiException;
import com.hackathon.bankingapp.repositories.OtpCodeRepository;
import com.hackathon.bankingapp.repositories.ResetTokenRepository;
import com.hackathon.bankingapp.repositories.UserRepository;
import com.hackathon.bankingapp.services.OtpGeneratorService;
import com.hackathon.bankingapp.services.PasswordResetService;
import com.hackathon.bankingapp.services.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private static final String MESSAGE_PREFIX = "OTP:";
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final OtpGeneratorService otpGeneratorService;
    private final OtpCodeRepository otpCodeRepository;
    private final ResetTokenRepository resetTokenRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void sendOtp(String email) {

        if(!userRepository.existsByEmailIgnoreCase(email)) {
            return;
        }

        String otpCode = otpGeneratorService.generateOtp();
        String otpMessage = MESSAGE_PREFIX + otpCode;

         emailService.sendSimpleMessage(email, otpMessage, otpMessage);

        saveOtp(email, otpCode);
    }

    @Override
    @Transactional
    public ResetTokenResponse getResetToken(ResetTokenRequest resetTokenRequest) {

        User user = getUserReference(resetTokenRequest.identifier());
        getAndUpdateOtpCode(resetTokenRequest);

        String resetToken = createResetToken(user);

        return new ResetTokenResponse(resetToken);
    }

    private User getUserReference(String email) {
        User user = userRepository.getReferenceByEmailIgnoreCase(email);

        if(user == null) {
            throw new ApiException("User not found", HttpStatus.BAD_REQUEST);
        }
        return user;
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        String userEmail = resetPasswordRequest.identifier();
        User user = getUserReference(userEmail);

        ResetToken resetToken = resetTokenRepository.findUnusedTokenByUserEmail(
                userEmail, resetPasswordRequest.resetToken())
                .orElseThrow(() -> new ApiException("Invalid reset token", HttpStatus.BAD_REQUEST));

        user.setPassword(passwordEncoder.encode(resetPasswordRequest.newPassword()));
        userRepository.save(user);

        resetToken.setUsed(Boolean.TRUE);
        resetTokenRepository.save(resetToken);
    }

    private String createResetToken(User user) {

        String resetToken = UUID.randomUUID().toString();

        ResetToken resetTokenEntity = ResetToken.builder()
                .token(resetToken)
                .used(Boolean.FALSE)
                .user(user)
                .build();

        resetTokenRepository.save(resetTokenEntity);
        return resetToken;
    }

    private void getAndUpdateOtpCode(ResetTokenRequest resetTokenRequest) {
        OtpCode otpCode =  otpCodeRepository.findUnusedOtpCodeByUserEmail(
                resetTokenRequest.identifier(), resetTokenRequest.otp())
                .orElseThrow(() -> new ApiException("Invalid OTP", HttpStatus.BAD_REQUEST));

        otpCode.setUsed(true);
        otpCodeRepository.save(otpCode);
    }

    private void saveOtp(String email, String otpCode) {
        OtpCode otpCodeEntity = OtpCode.builder()
                .code(otpCode)
                .user(userRepository.getReferenceByEmailIgnoreCase(email))
                .used(Boolean.FALSE)
                .build();

        otpCodeRepository.save(otpCodeEntity);
    }
}
