package bophelo.example.banking_system.dto.response;

import bophelo.example.banking_system.enums.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionHistoryResponse(
        String reference,
        String accountNumber,
        TransactionType type,
        BigDecimal amount,
        BigDecimal balanceBefore,
        BigDecimal balanceAfter,
        String relatedAccountNumber,
        String initiatedBy,
        Instant createdAt,
        String description
) {}
