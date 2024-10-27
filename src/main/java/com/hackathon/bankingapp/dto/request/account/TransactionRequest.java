package com.hackathon.bankingapp.dto.request.account;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record TransactionRequest(@NotBlank @Pattern(regexp = "\\d{4}") String pin,
                                 @NotNull @Positive BigDecimal amount) {
}
