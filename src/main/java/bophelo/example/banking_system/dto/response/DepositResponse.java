package bophelo.example.banking_system.dto.response;

import java.math.BigDecimal;

public record DepositResponse(

        String accountNumber,
        BigDecimal balance,
        String message
) {}
