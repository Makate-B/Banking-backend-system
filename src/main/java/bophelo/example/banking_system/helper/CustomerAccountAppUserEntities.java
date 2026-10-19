package bophelo.example.banking_system.helper;

import bophelo.example.banking_system.entity.Account;
import bophelo.example.banking_system.entity.Address;
import bophelo.example.banking_system.entity.AppUser;
import bophelo.example.banking_system.entity.Customer;

public record CustomerAccountAppUserEntities(Customer customer, Account account, AppUser appUser) {

}
