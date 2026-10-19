package bophelo.example.banking_system.repository;

import bophelo.example.banking_system.entity.Account;
import bophelo.example.banking_system.entity.Customer;
import bophelo.example.banking_system.enums.AccountStatus;
import bophelo.example.banking_system.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("""
           SELECT a
           FROM Account a
           WHERE a.accountNumber = :accountNumber
              AND a.customer.appUser.username = :username
    """)
    Optional<Account> findByAccountNumberAndUsername(@Param("accountNumber") String accountNumber, @Param("username") String username);

    Optional<Account> findByAccountNumber(String accountNumber);


    @Query("""
            SELECT a
            FROM Account a
            WHERE a.customer.idNumber = :idNumber
            """)
    List<Account> findAccountByCustomerIdNumber(@Param("idNumber") String idNumber);

    long countByCustomerAndAccountType(Customer customer, AccountType accountType);

    @Query("""
            SELECT a
            FROM Account a
            WHERE a.customer.appUser.username = :username
               AND a.accountStatus = :accountStatus
            """)
    List<Account> findAccountsByUsernameAndAccountStatus(@Param("username") String username, @Param("accountStatus") AccountStatus accountStatus);

    boolean existsByAccountNumber(String accountNumber);

}
