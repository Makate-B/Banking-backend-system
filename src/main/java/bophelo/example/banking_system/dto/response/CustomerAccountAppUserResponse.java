package bophelo.example.banking_system.dto.response;

import bophelo.example.banking_system.enums.AccountStatus;
import bophelo.example.banking_system.enums.AccountType;

import java.math.BigDecimal;

public record CustomerAccountAppUserResponse(

        String fullName,
        String accountNumber,
        AccountType accountType,
        BigDecimal balance,
        String username,
        String message

) {}
