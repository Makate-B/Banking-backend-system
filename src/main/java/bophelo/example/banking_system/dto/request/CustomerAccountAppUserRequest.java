package bophelo.example.banking_system.dto.request;

import bophelo.example.banking_system.enums.AccountType;
import bophelo.example.banking_system.enums.EmploymentStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public record CustomerAccountAppUserRequest(

        @NotBlank(message = "Full name is required")
        String fullName,

        @NotBlank(message = "ID number is required")
        @Pattern(regexp = "\\d{13}", message = "ID number must be 13 digits")
        String idNumber,

        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "0\\d{9}", message = "Phone number must start with 0 and contain 10 digits")
        String phoneNumber,

        @Email(message = "Enter valid email address")
        @NotBlank(message = "Email address is required")
        String emailAddress,

        @NotEmpty(message = "At least one home Address is required")
        @Size(max = 2, message = "You can provide at most two addresses")
        List<@Valid AddressRequest> addresses,

        @NotNull(message = "Employment status is required")
        EmploymentStatus employmentStatus,

        @NotNull(message = "Account type is required")
        AccountType accountType,

        @NotBlank(message = "Username is required")
        @Size(min = 8, message = "Username must be at least 8 characters")
        String username,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password

){}
