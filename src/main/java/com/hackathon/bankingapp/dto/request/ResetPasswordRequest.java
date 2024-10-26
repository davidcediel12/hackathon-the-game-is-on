package com.hackathon.bankingapp.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(@NotBlank @Email String identifier,
                                   @NotBlank String resetToken,
                                   @NotBlank String newPassword) {
}
