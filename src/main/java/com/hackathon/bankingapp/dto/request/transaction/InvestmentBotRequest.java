package com.hackathon.bankingapp.dto.request.transaction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record InvestmentBotRequest (@NotBlank @Pattern(regexp = "\\d{4}") String pin){
}
