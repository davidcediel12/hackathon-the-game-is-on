package com.hackathon.bankingapp.dto.request.authentication;

import com.hackathon.bankingapp.utils.Password;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdatePinRequest(@NotBlank @Pattern(regexp = "\\d{4}") String oldPin,
                               @NotBlank @Pattern(regexp = "\\d{4}") String newPin,
                               @Password String password) {
}
