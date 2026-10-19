package bophelo.example.banking_system.entity;

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
@Table(name = "addresses")
public class Address {

    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String streetAddress;

    @Column(nullable = false)
    private String suburb;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String province;

    @Column(nullable = false)
    private String postalCode;

    @OneToMany(mappedBy = "address")
    private List<CustomerAddress> customerAddresses = new ArrayList<>();

    public Address(String streetAddress, String suburb, String city, String province, String postalCode) {
        this.streetAddress = streetAddress;
        this.suburb = suburb;
        this.city = city;
        this.province = province;
        this.postalCode = postalCode;
    }
}
