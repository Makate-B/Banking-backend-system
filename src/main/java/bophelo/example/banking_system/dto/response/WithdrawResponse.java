package bophelo.example.banking_system.dto.response;

import java.math.BigDecimal;

public record WithdrawResponse(

        String accountNumber,
        BigDecimal balance,
        String message

) {}
