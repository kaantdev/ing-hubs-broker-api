package com.ing.hubs.broker_api.config;

import com.ing.hubs.broker_api.entity.Customer;
import com.ing.hubs.broker_api.enums.Role;
import com.ing.hubs.broker_api.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataLoader {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner loadData() {
        return args -> {
            if (customerRepository.count() == 0) { // Eğer kullanıcı yoksa ekleyelim
                Customer admin = new Customer();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123")); // Şifre bcrypt ile hashleniyor
                admin.setRole(Role.ROLE_ADMIN);
                customerRepository.save(admin);

                Customer user = new Customer();
                user.setUsername("user");
                user.setPassword(passwordEncoder.encode("user123"));
                user.setRole(Role.ROLE_USER);
                customerRepository.save(user);

                System.out.println("Dummy kullanıcılar eklendi: admin / user");
            }
        };
    }
}