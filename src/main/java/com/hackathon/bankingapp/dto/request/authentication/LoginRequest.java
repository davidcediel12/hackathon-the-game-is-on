package com.hackathon.bankingapp.dto.request.authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank @Email String identifier,
                           @NotBlank String password) {
}
