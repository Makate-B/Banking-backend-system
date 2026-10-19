
package bophelo.example.banking_system.repository;

import bophelo.example.banking_system.entity.BankTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BankTransactionRepository
        extends JpaRepository<BankTransaction, Long> {


    @Query("""
            SELECT t
            FROM BankTransaction t
            WHERE t.account.accountNumber = :accountNumber
              AND t.account.customer.appUser.username = :username
             ORDER BY t.createdAt DESC
            """)
    List<BankTransaction> findTransactionsByAccountNumberAndUsername(
            @Param("accountNumber") String accountNumber,
            @Param("username") String username
    );
}
