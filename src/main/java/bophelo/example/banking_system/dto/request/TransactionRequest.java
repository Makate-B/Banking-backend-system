package bophelo.example.banking_system.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransactionRequest(

        @NotBlank(message = "Account Number is required")
        String accountNumber,

        @Min(value = 1, message = "Amount must be greater than zero")
        @NotNull(message = "Amount is required")
        BigDecimal amount

) {}
