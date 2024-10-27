package com.hackathon.bankingapp.controller.transaction;

import jakarta.validation.constraints.Pattern;

public record InvestmentBotRequest (@Pattern(regexp = "\\d{4}") String pin){
}
