package bophelo.example.banking_system.service;

import bophelo.example.banking_system.dto.request.AddressRequest;
import bophelo.example.banking_system.dto.request.CustomerAccountAppUserRequest;
import bophelo.example.banking_system.dto.request.TransactionRequest;
import bophelo.example.banking_system.dto.request.TransferRequest;
import bophelo.example.banking_system.dto.response.AccountsResponse;
import bophelo.example.banking_system.dto.response.CustomerAccountAppUserResponse;
import bophelo.example.banking_system.dto.response.DepositResponse;
import bophelo.example.banking_system.dto.response.WithdrawResponse;
import bophelo.example.banking_system.entity.*;
import bophelo.example.banking_system.enums.*;
import bophelo.example.banking_system.enums.AccountType;
import bophelo.example.banking_system.enums.CustomerStatus;
import bophelo.example.banking_system.exception.BadRequestException;
import bophelo.example.banking_system.exception.DuplicateResourceException;
import bophelo.example.banking_system.exception.ResourceNotFoundException;
import bophelo.example.banking_system.helper.CustomerAccountAppUserEntities;
import bophelo.example.banking_system.mapper.AccountMapper;
import bophelo.example.banking_system.mapper.CustomerAccountAppUserMapper;
import bophelo.example.banking_system.mapper.TransactionMapper;
import bophelo.example.banking_system.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class CustomerAccountService {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final CustomerAccountAppUserMapper customerAccountAppUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final AppUserRepository appUserRepository;
    private final AddressRepository addressRepository;
    private final CustomerAddressRepository customerAddressRepository;

    @Transactional
    public CustomerAccountAppUserResponse registerCustomerAndOpenAccount(CustomerAccountAppUserRequest request){

        validateUniqueData(request);

        CustomerAccountAppUserEntities entities = customerAccountAppUserMapper.toCustomerAccountAppUserEntities(request);

        //Customer

        Customer customer = entities.customer();

        customer.setCustomerStatus(CustomerStatus.ACTIVE);

        Customer savedCustomer = customerRepository.save(customer);

        //Addresses

        List<CustomerAddress> customerAddresses = new ArrayList<>();

        for (AddressRequest addressRequest : request.addresses()) {

            Address address = customerAccountAppUserMapper
                    .toAddress(addressRequest);

            Address savedAddress = addressRepository.save(address);

            CustomerAddress customerAddress = new CustomerAddress();
            customerAddress.setCustomer(savedCustomer);
            customerAddress.setAddress(savedAddress);

            customerAddresses.add(customerAddress);
        }

        customerAddressRepository.saveAll(customerAddresses);

        //Account

        Account account = entities.account();

        if(request.accountType().equals(AccountType.SAVINGS)){
            throw new BadRequestException("Choose CURRENT OR BUSINESS");
        }

        account.setName("Main Account");
        account.setAccountNumber(generateAccountNumber());
        account.setAccountStatus(AccountStatus.ACTIVE);
        account.setBalance(BigDecimal.ZERO);
        account.setCustomer(savedCustomer);

        Account savedAccount = accountRepository.save(account);

        //AppUser

        AppUser appUser = entities.appUser();

        if(appUserRepository.existsByUsername(request.username())){
            throw new DuplicateResourceException("Username exists");
        }

        appUser.setPassword(passwordEncoder.encode(request.password()));
        appUser.setRole(Role.CUSTOMER);
        appUser.setEnabled(true);
        appUser.setLocked(false);
        appUser.setCustomer(savedCustomer);

        AppUser savedAppUser = appUserRepository.save(appUser);

        return customerAccountAppUserMapper.toResponse(savedCustomer, savedAccount,savedAppUser);

    }

    @Transactional
    public String deactivateCustomerAndCloseAllAccounts(String idNumber){

        Customer customer = customerRepository.findCustomerByIdNumber(idNumber).orElseThrow(()-> new ResourceNotFoundException("Customer was not found"));

        if(customer.getCustomerStatus() == CustomerStatus.UNACTIVE){
            throw new BadRequestException("Customer already deactivated");
        }

        List<Account> accounts = accountRepository.findAccountByCustomerIdNumber(idNumber);

        for(Account account: accounts){
            if (account.getBalance().compareTo(BigDecimal.ZERO) > 0){
                throw new BadRequestException("Account Number " + account.getAccountNumber() + " has R" + account.getBalance());
            }
        }

        for(Account account: accounts){
            account.setClosedAt(LocalDate.now());
            account.setAccountStatus(AccountStatus.CLOSED);
        }

        AppUser appUser = appUserRepository.findAppUserByCustomerIdNumber(idNumber).orElseThrow(()-> new ResourceNotFoundException("Account was not found"));

        appUser.setLocked(true);
        appUser.setEnabled(false);

        customer.setCustomerStatus(CustomerStatus.UNACTIVE);

        accountRepository.saveAll(accounts);
        customerRepository.save(customer);

        return "Deactivated customer and closed their accounts successfully";
    }

    private void validateUniqueData(CustomerAccountAppUserRequest request){

        if (customerRepository.existsByIdNumber(request.idNumber())){
            throw new DuplicateResourceException("ID Number already exists");
        }

        if (customerRepository.existsByPhoneNumber(request.phoneNumber())){
            throw new DuplicateResourceException("Phone Number already exists");
        }

        if (customerRepository.existsByEmailAddress(request.emailAddress())){
            throw new DuplicateResourceException("Email Address already exists");
        }

    }

    private String generateAccountNumber(){

        String accountNumber;

        do {
            accountNumber = String.valueOf(ThreadLocalRandom.current().nextLong(1_000_000_000L, 1_000_000_0000L));
        }while (
                accountRepository.existsByAccountNumber(accountNumber)
        );

        return accountNumber;
    }
}
