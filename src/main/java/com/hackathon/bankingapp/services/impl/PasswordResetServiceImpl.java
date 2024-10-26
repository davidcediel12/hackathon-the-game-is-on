package com.hackathon.bankingapp.services.impl;

import com.hackathon.bankingapp.entities.OtpCode;
import com.hackathon.bankingapp.repositories.OtpCodeRepository;
import com.hackathon.bankingapp.repositories.UserRepository;
import com.hackathon.bankingapp.services.OtpGeneratorService;
import com.hackathon.bankingapp.services.PasswordResetService;
import com.hackathon.bankingapp.services.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private static final String MESSAGE_PREFIX = "OTP:";
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final OtpGeneratorService otpGeneratorService;
    private final OtpCodeRepository otpCodeRepository;

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

    private void saveOtp(String email, String otpCode) {
        OtpCode otpCodeEntity = OtpCode.builder()
                .code(otpCode)
                .user(userRepository.getReferenceByEmailIgnoreCase(email))
                .used(Boolean.FALSE)
                .build();

        otpCodeRepository.save(otpCodeEntity);
    }
}
