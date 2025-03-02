package com.ing.hubs.broker_api.util;

import com.ing.hubs.broker_api.entity.Customer;
import com.ing.hubs.broker_api.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthorizationUtil {

    private final CustomerRepository customerRepository;

    public void validateUserAccess(Long customerId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        if (!customer.getId().equals(customerId)) {
            throw new AccessDeniedException("You are not authorized to perform this action.");
        }
    }
}
