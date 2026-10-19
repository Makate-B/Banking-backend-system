package bophelo.example.banking_system.mapper;

import bophelo.example.banking_system.dto.response.DepositResponse;
import bophelo.example.banking_system.dto.response.WithdrawResponse;
import bophelo.example.banking_system.entity.Account;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public WithdrawResponse withdrawResponse(Account account){

        return new WithdrawResponse(
                account.getAccountNumber(),
                account.getBalance(),
                "Withdrawn money successfully");
    }

    public DepositResponse depositResponse(Account account){

        return new DepositResponse(
                account.getAccountNumber(),
                account.getBalance(),
                "Deposited money successfully");
    }

}
