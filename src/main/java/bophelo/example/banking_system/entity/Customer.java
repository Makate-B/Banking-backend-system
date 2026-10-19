package bophelo.example.banking_system.entity;

import bophelo.example.banking_system.enums.*;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(unique = true, nullable = false, updatable = false)
    private String idNumber;

    @Column(unique = true, nullable = false)
    private String phoneNumber;

    @Column(unique = true, nullable = false)
    private String emailAddress;

    @OneToMany(mappedBy = "customer")
    private List<CustomerAddress> customerAddresses = new ArrayList<>();

    @OneToOne(mappedBy = "customer")
    private AppUser appUser;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EmploymentStatus employmentStatus;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CustomerStatus customerStatus;

    @OneToMany(mappedBy = "customer", fetch = FetchType.EAGER)
    private List<Account> accounts = new ArrayList<>();

    public Customer(String fullName, String idNumber, String phoneNumber,
                    String emailAddress, EmploymentStatus employmentStatus) {
        this.fullName = fullName;
        this.idNumber = idNumber;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.employmentStatus = employmentStatus;
    }

}
