package bophelo.example.banking_system.repository;

import bophelo.example.banking_system.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
