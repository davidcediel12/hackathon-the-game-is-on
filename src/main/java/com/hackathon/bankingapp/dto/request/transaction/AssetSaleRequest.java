package com.hackathon.bankingapp.dto.request.transaction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record AssetSaleRequest(@NotBlank String assetSymbol,
                               @NotBlank @Pattern(regexp = "\\d{4}") String pin,
                               @NotNull @Positive BigDecimal quantity) {
}
