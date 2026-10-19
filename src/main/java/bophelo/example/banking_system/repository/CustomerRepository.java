package bophelo.example.banking_system.repository;

import bophelo.example.banking_system.entity.Customer;
import bophelo.example.banking_system.enums.CustomerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findCustomerByIdNumber(String idNumber);

    boolean existsByIdNumber(String idNumber);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByEmailAddress(String emailAddress);

    @Query("""
    SELECT c
    FROM Customer c
    JOIN AppUser u ON u.customer = c
    WHERE u.username = :username
    """)
    Optional<Customer> findCustomerByUsername(@Param("username") String username);
}
