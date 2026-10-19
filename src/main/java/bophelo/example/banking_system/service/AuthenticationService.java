package bophelo.example.banking_system.service;

import bophelo.example.banking_system.dto.request.LoginRequest;
import bophelo.example.banking_system.dto.response.AuthenticationResponse;
import bophelo.example.banking_system.entity.AppUser;
import bophelo.example.banking_system.entity.Customer;
import bophelo.example.banking_system.enums.CustomerStatus;
import bophelo.example.banking_system.exception.BadRequestException;
import bophelo.example.banking_system.exception.ResourceNotFoundException;
import bophelo.example.banking_system.repository.AppUserRepository;
import bophelo.example.banking_system.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthenticationResponse login(LoginRequest request){

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());

        String token = jwtService.generateToken(userDetails);

        return new AuthenticationResponse("Bearer", token);

   }
}
