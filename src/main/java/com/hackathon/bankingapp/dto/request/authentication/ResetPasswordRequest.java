package com.hackathon.bankingapp.dto.request.authentication;

import com.hackathon.bankingapp.utils.Password;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(@NotBlank @Email String identifier,
                                   @NotBlank String resetToken,
                                   @NotBlank @Password String newPassword) {
}
