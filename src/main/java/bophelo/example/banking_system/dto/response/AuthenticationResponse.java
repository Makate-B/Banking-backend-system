package bophelo.example.banking_system.dto.response;

public record AuthenticationResponse(
        String type,
        String token
) {
}
