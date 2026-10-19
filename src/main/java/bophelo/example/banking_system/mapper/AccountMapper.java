package bophelo.example.banking_system.mapper;

import bophelo.example.banking_system.dto.response.AccountsResponse;
import bophelo.example.banking_system.entity.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public AccountsResponse toResponse(Account account){

        return new AccountsResponse(account.getName(), account.getAccountNumber(), account.getAccountType(), account.getBalance());

    }


}
