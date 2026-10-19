package bophelo.example.banking_system.entity;

import bophelo.example.banking_system.enums.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "app_users")
public class AppUser {

    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private boolean isLocked;

    @Column(nullable = false)
    private boolean isEnabled;

    @OneToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    public AppUser(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
