package bophelo.example.banking_system.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Digits;
import java.math.BigDecimal;

public record PaymentRequest(

        @NotBlank
        String senderAccountNumber,

        @NotBlank
        String receiverAccountNumber,

        @NotNull
        @DecimalMin(value = "0.01", message = "Payment amount must be greater than zero")
        @Digits(integer = 15, fraction = 2)
        BigDecimal amount

) {}
