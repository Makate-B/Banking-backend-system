package bophelo.example.banking_system.repository;

import bophelo.example.banking_system.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByUsername(String username);

    boolean existsByUsername(String username);

    @Query("""
            SELECT au
            FROM AppUser au
            WHERE au.customer.idNumber = :idNumber
            """)
    Optional<AppUser> findAppUserByCustomerIdNumber(@Param("idNumber") String idNumber);
}
