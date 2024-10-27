package com.hackathon.bankingapp.dto.response.dashboard;

import java.math.BigDecimal;

public record AccountDetailResponse(String accountNumber, BigDecimal balance) {
}
