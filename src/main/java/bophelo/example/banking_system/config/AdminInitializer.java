
package bophelo.example.banking_system.config;

import bophelo.example.banking_system.entity.AppUser;
import bophelo.example.banking_system.enums.Role;
import bophelo.example.banking_system.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer {

    @Bean
    CommandLineRunner createAdmin(
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.admin.username}") String adminUsername,
            @Value("${app.admin.password}") String adminPassword) {

        return args -> {
            if (appUserRepository.findByUsername(adminUsername).isPresent()) {
                return;
            }

            AppUser admin = new AppUser();
            admin.setUsername(adminUsername);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ADMIN);
            admin.setLocked(false);
            admin.setEnabled(true);

            appUserRepository.save(admin);
        };
    }
}
