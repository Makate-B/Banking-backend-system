package bophelo.example.banking_system.service;

import bophelo.example.banking_system.dto.request.AccountNamingRequest;
import bophelo.example.banking_system.dto.request.PaymentRequest;
import bophelo.example.banking_system.dto.request.TransactionRequest;
import bophelo.example.banking_system.dto.request.TransferRequest;
import bophelo.example.banking_system.dto.response.AccountsResponse;
import bophelo.example.banking_system.dto.response.DepositResponse;
import bophelo.example.banking_system.dto.response.TransactionHistoryResponse;
import bophelo.example.banking_system.dto.response.WithdrawResponse;
import bophelo.example.banking_system.entity.Account;
import bophelo.example.banking_system.entity.BankTransaction;
import bophelo.example.banking_system.entity.Customer;
import bophelo.example.banking_system.enums.AccountStatus;
import bophelo.example.banking_system.enums.AccountType;
import bophelo.example.banking_system.enums.CustomerStatus;
import bophelo.example.banking_system.enums.TransactionType;
import bophelo.example.banking_system.exception.BadRequestException;
import bophelo.example.banking_system.exception.ResourceNotFoundException;
import bophelo.example.banking_system.mapper.AccountMapper;
import bophelo.example.banking_system.mapper.TransactionMapper;
import bophelo.example.banking_system.repository.AccountRepository;
import bophelo.example.banking_system.repository.BankTransactionRepository;
import bophelo.example.banking_system.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionMapper transactionMapper;
    private final AccountMapper accountMapper;
    private final CustomerRepository customerRepository;
    private final BankTransactionRepository bankTransactionRepository;

    @Transactional
    public DepositResponse depositMoney(String username, TransactionRequest transactionRequest) {

        validateTransactionData(transactionRequest);

        Account account = getAccountForUser(
                transactionRequest.accountNumber(), username);

        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new BadRequestException("Account is not Active");
        }

        BigDecimal balanceBefore = account.getBalance();
        BigDecimal balanceAfter = balanceBefore.add(transactionRequest.amount());

        account.setBalance(balanceAfter);

        Account savedAccount = accountRepository.save(account);

        recordTransaction(
                savedAccount,
                TransactionType.DEPOSIT,
                transactionRequest.amount(),
                balanceBefore,
                balanceAfter,
                null,
                username,
                UUID.randomUUID().toString(),
                "Money deposited"
        );

        return transactionMapper.depositResponse(savedAccount);
    }

    @Transactional
    public WithdrawResponse withdrawMoney(String username, TransactionRequest transactionRequest) {

        validateTransactionData(transactionRequest);

        Account account = getAccountForUser(
                transactionRequest.accountNumber(), username);

        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new BadRequestException("Account is not Active");
        }

        if (transactionRequest.amount().compareTo(account.getBalance()) > 0) {
            throw new BadRequestException("Insufficient Balance");
        }

        BigDecimal balanceBefore = account.getBalance();
        BigDecimal balanceAfter = balanceBefore.subtract(transactionRequest.amount());

        account.setBalance(balanceAfter);

        Account savedAccount = accountRepository.save(account);

        recordTransaction(
                savedAccount,
                TransactionType.WITHDRAWAL,
                transactionRequest.amount(),
                balanceBefore,
                balanceAfter,
                null,
                username,
                UUID.randomUUID().toString(),
                "Money withdrawn"
        );

        return transactionMapper.withdrawResponse(savedAccount);
    }

    @Transactional
    public String transferMoney(String username, TransferRequest transferRequest) {

        Account senderAccount = accountRepository
                .findByAccountNumber(transferRequest.senderAccountNumber())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Sender account does not exist"));

        Account receiverAccount = accountRepository
                .findByAccountNumber(transferRequest.receiverAccountNumber())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Receiver account does not exist"));

        if (!senderAccount.getCustomer().getAppUser().getUsername().equals(username)
                || !receiverAccount.getCustomer().getAppUser().getUsername().equals(username)) {
            throw new AccessDeniedException(
                    "You are not allowed to access another user's accounts");
        }

        if (senderAccount.getId().equals(receiverAccount.getId())) {
            throw new BadRequestException("Cannot transfer to the same account");
        }

        if (senderAccount.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new BadRequestException("Sender Account is not active");
        }

        if (receiverAccount.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new BadRequestException("Receiver Account is not active");
        }

        BigDecimal amount = transferRequest.amount();

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Transfer amount must be greater than zero");
        }

        BigDecimal senderBefore = senderAccount.getBalance();
        BigDecimal receiverBefore = receiverAccount.getBalance();

        if (amount.compareTo(senderBefore) > 0) {
            throw new BadRequestException("Insufficient funds");
        }

        BigDecimal senderAfter = senderBefore.subtract(amount);
        BigDecimal receiverAfter = receiverBefore.add(amount);

        senderAccount.setBalance(senderAfter);
        receiverAccount.setBalance(receiverAfter);

        accountRepository.save(senderAccount);
        accountRepository.save(receiverAccount);

        String reference = UUID.randomUUID().toString();

        recordTransaction(
                senderAccount,
                TransactionType.TRANSFER_OUT,
                amount,
                senderBefore,
                senderAfter,
                receiverAccount.getAccountNumber(),
                username,
                reference,
                "Internal transfer sent"
        );

        recordTransaction(
                receiverAccount,
                TransactionType.TRANSFER_IN,
                amount,
                receiverBefore,
                receiverAfter,
                senderAccount.getAccountNumber(),
                username,
                reference,
                "Internal transfer received"
        );

        return "Money transferred successfully";
    }

    @Transactional
    public String pay(String username, PaymentRequest paymentRequest) {

        Account senderAccount = accountRepository
                .findByAccountNumber(paymentRequest.senderAccountNumber())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Sender account does not exist"));

        if (!senderAccount.getCustomer().getAppUser().getUsername().equals(username)) {
            throw new AccessDeniedException(
                    "You are not allowed to access another user's account");
        }

        Account receiverAccount = accountRepository
                .findByAccountNumber(paymentRequest.receiverAccountNumber())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Recipient account does not exist"));

        if (senderAccount.getId().equals(receiverAccount.getId())) {
            throw new BadRequestException("You cannot pay your own account");
        }

        if (senderAccount.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new BadRequestException("Sender account is not active");
        }

        if (receiverAccount.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new BadRequestException("Recipient account is not active");
        }

        BigDecimal amount = paymentRequest.amount();

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException(
                    "Payment amount must be greater than zero");
        }

        BigDecimal senderBefore = senderAccount.getBalance();
        BigDecimal receiverBefore = receiverAccount.getBalance();

        if (amount.compareTo(senderBefore) > 0) {
            throw new BadRequestException("Insufficient funds");
        }

        BigDecimal senderAfter = senderBefore.subtract(amount);
        BigDecimal receiverAfter = receiverBefore.add(amount);

        senderAccount.setBalance(senderAfter);
        receiverAccount.setBalance(receiverAfter);

        accountRepository.save(senderAccount);
        accountRepository.save(receiverAccount);

        String reference = UUID.randomUUID().toString();

        recordTransaction(
                senderAccount,
                TransactionType.PAYMENT_OUT,
                amount,
                senderBefore,
                senderAfter,
                receiverAccount.getAccountNumber(),
                username,
                reference,
                "Payment sent"
        );

        recordTransaction(
                receiverAccount,
                TransactionType.PAYMENT_IN,
                amount,
                receiverBefore,
                receiverAfter,
                senderAccount.getAccountNumber(),
                username,
                reference,
                "Payment received"
        );

        return "Payment successful";
    }

    public String openSavingsAccount(String username, AccountNamingRequest request) {

        Customer customer = customerRepository.findCustomerByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer not found"));

        if (customer.getCustomerStatus() != CustomerStatus.ACTIVE) {
            throw new BadRequestException("Customer is Not Active");
        }

        long savingsAccountCount = accountRepository
                .countByCustomerAndAccountType(customer, AccountType.SAVINGS);

        if (savingsAccountCount >= 5) {
            throw new BadRequestException(
                    "Cannot have more than 5 savings account");
        }

        Account account = new Account();

        if (request.name() == null || request.name().isBlank()) {
            account.setName("Savings");
        } else {
            account.setName(request.name().trim());
        }

        account.setAccountType(AccountType.SAVINGS);
        account.setAccountStatus(AccountStatus.ACTIVE);
        account.setAccountNumber(generateAccountNumber());
        account.setCustomer(customer);
        account.setBalance(BigDecimal.ZERO);

        accountRepository.save(account);

        return "Savings account opened Successfully";
    }

    public List<AccountsResponse> getAllAccountsAndActiveAccounts(String username) {

        List<Account> accounts = accountRepository
                .findAccountsByUsernameAndAccountStatus(
                        username, AccountStatus.ACTIVE);

        return accounts.stream()
                .map(accountMapper::toResponse)
                .toList();
    }

    public String closeAccount(String username, String accountNumber) {

        Account account = getAccountForUser(accountNumber, username);

        if (account.getAccountType() == AccountType.CURRENT || account.getAccountType() == AccountType.BUSINESS) {
            throw new BadRequestException(
                    "Main Account cannot be closed, Requires Admin. Visit branch");
        }

        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new BadRequestException("Account has already been closed");
        }

        if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new BadRequestException(
                    "Account has money, Transfer first");
        }

        account.setClosedAt(LocalDate.now());
        account.setAccountStatus(AccountStatus.CLOSED);

        accountRepository.save(account);

        return "Account has been closed successfully";
    }

    public List<TransactionHistoryResponse> getTransactionHistory(String username, String accountNumber) {

        Account account = getAccountForUser(accountNumber, username);

        return bankTransactionRepository
                .findTransactionsByAccountNumberAndUsername(
                        account.getAccountNumber(), username)
                .stream()
                .map(transaction -> new TransactionHistoryResponse(
                        transaction.getReference(),
                        transaction.getAccount().getAccountNumber(),
                        transaction.getType(),
                        transaction.getAmount(),
                        transaction.getBalanceBefore(),
                        transaction.getBalanceAfter(),
                        transaction.getRelatedAccountNumber(),
                        transaction.getInitiatedBy(),
                        transaction.getCreatedAt(),
                        transaction.getDescription()
                ))
                .toList();
    }

    private void validateTransactionData(TransactionRequest transactionRequest) {

        if (transactionRequest.amount() == null) {
            throw new BadRequestException("Amount is required");
        }

        if (transactionRequest.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Enter valid amount");
        }
    }

    private void recordTransaction(
            Account account,
            TransactionType type,
            BigDecimal amount,
            BigDecimal balanceBefore,
            BigDecimal balanceAfter,
            String relatedAccountNumber,
            String username,
            String reference,
            String description) {

        BankTransaction transaction = new BankTransaction();

        transaction.setReference(reference);
        transaction.setAccount(account);
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setBalanceBefore(balanceBefore);
        transaction.setBalanceAfter(balanceAfter);
        transaction.setRelatedAccountNumber(relatedAccountNumber);
        transaction.setInitiatedBy(username);
        transaction.setCreatedAt(Instant.now());
        transaction.setDescription(description);

        bankTransactionRepository.save(transaction);
    }

    private String generateAccountNumber() {

        String accountNumber;

        do {
            accountNumber = String.valueOf(
                    ThreadLocalRandom.current().nextLong(
                            1_000_000_000L, 10_000_000_000L));
        } while (accountRepository.existsByAccountNumber(accountNumber));

        return accountNumber;
    }

    private Account getAccountForUser(String accountNumber, String authenticatedUsername) {

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Account number was not found"));

        String accountOwnerUsername = account.getCustomer()
                .getAppUser()
                .getUsername();

        if (!authenticatedUsername.equals(accountOwnerUsername)) {
            throw new AccessDeniedException("You cannot access another user's account");
        }

        return account;
    }
}