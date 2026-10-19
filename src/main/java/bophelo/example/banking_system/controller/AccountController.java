package bophelo.example.banking_system.controller;

import bophelo.example.banking_system.dto.request.AccountNamingRequest;
import bophelo.example.banking_system.dto.request.PaymentRequest;
import bophelo.example.banking_system.dto.response.AccountsResponse;
import bophelo.example.banking_system.dto.response.DepositResponse;
import bophelo.example.banking_system.dto.request.TransactionRequest;
import bophelo.example.banking_system.dto.request.TransferRequest;
import bophelo.example.banking_system.dto.response.TransactionHistoryResponse;
import bophelo.example.banking_system.dto.response.WithdrawResponse;
import bophelo.example.banking_system.service.AccountService;
import bophelo.example.banking_system.service.CustomerAccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(CustomerAccountService customerAccountService, AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<DepositResponse> depositMoney(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody TransactionRequest depositRequest){
        DepositResponse depositResponse = accountService.depositMoney(userDetails.getUsername(), depositRequest);
        return ResponseEntity.ok(depositResponse);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<WithdrawResponse> withdrawMoney(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody TransactionRequest withdrawRequest){
        WithdrawResponse withdrawMoney = accountService.withdrawMoney(userDetails.getUsername(), withdrawRequest);
        return ResponseEntity.ok(withdrawMoney);
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transferMoney(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody TransferRequest transferRequest){
        String result = accountService.transferMoney(userDetails.getUsername(), transferRequest);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/pay")
    public ResponseEntity<String> pay(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody PaymentRequest request){
        String result = accountService.pay(userDetails.getUsername(), request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/open-savings-account")
    public String openSavingsAccount(@AuthenticationPrincipal UserDetails userDetails,@RequestBody AccountNamingRequest request){
        return accountService.openSavingsAccount(userDetails.getUsername(), request);
    }

    @GetMapping("my-accounts")
    public List<AccountsResponse> getAllAccountsAndActiveAccounts( @AuthenticationPrincipal UserDetails userDetails){
        return accountService.getAllAccountsAndActiveAccounts(userDetails.getUsername());
    }

    @GetMapping("/{accountNumber}/transactions")
    public List<TransactionHistoryResponse> getTransactionHistory(Authentication authentication, @PathVariable String accountNumber) {
        return accountService.getTransactionHistory(authentication.getName(), accountNumber);
    }

    @PatchMapping("/close-account/{accountNumber}")
    public ResponseEntity<String> closeAccount(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String accountNumber){
        String result = accountService.closeAccount(userDetails.getUsername(), accountNumber);
        return ResponseEntity.ok(result);
    }
}
