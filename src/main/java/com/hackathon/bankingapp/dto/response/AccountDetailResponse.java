package com.hackathon.bankingapp.dto.response;

import java.math.BigDecimal;

public record AccountDetailResponse(String accountNumber, BigDecimal balance) {
}
