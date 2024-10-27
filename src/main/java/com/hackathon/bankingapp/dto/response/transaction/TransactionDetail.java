package com.hackathon.bankingapp.dto.response.transaction;

import java.math.BigDecimal;

public record TransactionDetail(Long id, BigDecimal amount,
                                String transactionType, long transactionDate,
                                String sourceAccountNumber, String targetAccountNumber) {
}
