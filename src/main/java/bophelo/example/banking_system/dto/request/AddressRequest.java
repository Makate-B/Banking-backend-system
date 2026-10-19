package bophelo.example.banking_system.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AddressRequest(

        @NotBlank(message = "Street Address is required")
        String streetAddress,

        @NotBlank(message = "Suburb is required")
        String suburb,

        @NotBlank(message = "City is required")
        String city,

        @NotBlank(message = "Province is required")
        String province,

        @NotBlank(message = "Postal code is required")
        @Pattern(regexp = "\\d{4}", message = "Postal code must be 4 digits and must be digits only")
        String postalCode


) {
}
