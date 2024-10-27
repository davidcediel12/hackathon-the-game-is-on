package com.hackathon.bankingapp.dto.request.account;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record DepositRequest(@NotBlank @Pattern(regexp = "\\d{4}") String pin,
                             @NotNull @PositiveOrZero BigDecimal amount) {
}
