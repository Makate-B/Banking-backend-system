package bophelo.example.banking_system.mapper;

import bophelo.example.banking_system.dto.request.AddressRequest;
import bophelo.example.banking_system.dto.request.CustomerAccountAppUserRequest;
import bophelo.example.banking_system.dto.response.CustomerAccountAppUserResponse;
import bophelo.example.banking_system.entity.Account;
import bophelo.example.banking_system.entity.Address;
import bophelo.example.banking_system.entity.AppUser;
import bophelo.example.banking_system.entity.Customer;
import bophelo.example.banking_system.helper.CustomerAccountAppUserEntities;
import org.springframework.stereotype.Component;

@Component
public class CustomerAccountAppUserMapper {

    public CustomerAccountAppUserEntities toCustomerAccountAppUserEntities(CustomerAccountAppUserRequest request){


        Customer customer = new Customer(request.fullName(), request.idNumber(),
                                         request.phoneNumber(), request.emailAddress(),
                                         request.employmentStatus());

        Account account = new Account(request.accountType());

        AppUser appUser = new AppUser(request.username(), request.password());

        return new CustomerAccountAppUserEntities(customer, account, appUser);

    }


    public Address toAddress(AddressRequest request) {
        return new Address(
                request.streetAddress(),
                request.suburb(),
                request.city(),
                request.province(),
                request.postalCode()
        );
    }


    public CustomerAccountAppUserResponse toResponse(Customer customer, Account account, AppUser appUser){

        return new CustomerAccountAppUserResponse(customer.getFullName(), account.getAccountNumber(),
                                                  account.getAccountType(),account.getBalance(),
                                                  appUser.getUsername(), "Account Opened Successfully, You may login.");
    }

}
