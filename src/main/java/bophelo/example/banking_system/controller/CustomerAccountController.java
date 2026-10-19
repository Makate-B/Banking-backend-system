package bophelo.example.banking_system.controller;

import bophelo.example.banking_system.dto.request.CustomerAccountAppUserRequest;
import bophelo.example.banking_system.dto.response.CustomerAccountAppUserResponse;
import bophelo.example.banking_system.service.CustomerAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerAccountController {

    private final CustomerAccountService customerAccountService;

    @PostMapping("/open-account")
    public CustomerAccountAppUserResponse registerCustomerAndOpenAccount(@RequestBody @Valid CustomerAccountAppUserRequest request){
        return customerAccountService.registerCustomerAndOpenAccount(request);
    }

    @PostMapping("/deactivate/{idNumber}")
    public ResponseEntity<String> deactivateCustomerAndTheirAccounts(@PathVariable String idNumber){
        String result = customerAccountService.deactivateCustomerAndCloseAllAccounts(idNumber);
        return ResponseEntity.ok(result);
    }


}
