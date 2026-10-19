package bophelo.example.banking_system.dto.response;

import bophelo.example.banking_system.enums.AccountType;
import java.math.BigDecimal;

public record AccountsResponse(

        String name,
        String accountNumber,
        AccountType accountType,
        BigDecimal balance

) {}
