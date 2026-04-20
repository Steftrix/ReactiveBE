package com.mastersdbis.mtsdreactive.Controllers;

import lombok.RequiredArgsConstructor;
import com.mastersdbis.mtsdreactive.Entities.Provider;
import com.mastersdbis.mtsdreactive.Entities.UserRole;
import com.mastersdbis.mtsdreactive.Repositories.ProviderRepository;
import com.mastersdbis.mtsdreactive.Repositories.UserRepository;
import com.mastersdbis.mtsdreactive.Repositories.UserRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final ProviderRepository providerRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    @Transactional
    public Mono<Provider> validateProvider(Integer providerId, Integer adminUserId) {
        return providerRepository.findById(providerId)
            .switchIfEmpty(Mono.error(
                new IllegalArgumentException("Provider not found: " + providerId)))
            .flatMap(provider -> {
                if (!"PENDING".equals(provider.getValidationStatus())) {
                    return Mono.error(new IllegalArgumentException(
                        "Provider can only be validated if status is PENDING"));
                }
                provider.setValidationStatus("APPROVED");
                provider.setApprovedById(adminUserId);
                provider.setDateUpdated(LocalDateTime.now());
                return providerRepository.save(provider);
            })
            .flatMap(savedProvider ->
                // Grant PROVIDER role to the user
                userRoleRepository.save(
                    new UserRole(savedProvider.getId(), "PROVIDER"))
                    .thenReturn(savedProvider)
            );
    }

    @Transactional
    public Mono<Provider> denyProvider(Integer providerId) {
        return providerRepository.findById(providerId)
            .switchIfEmpty(Mono.error(
                new IllegalArgumentException("Provider not found: " + providerId)))
            .flatMap(provider -> {
                if (!"PENDING".equals(provider.getValidationStatus())) {
                    return Mono.error(new IllegalArgumentException(
                        "Provider can only be denied if status is PENDING"));
                }
                provider.setValidationStatus("DENIED");
                provider.setDateUpdated(LocalDateTime.now());
                return providerRepository.save(provider);
            });
    }

    @Transactional
    public Mono<Void> makeAdmin(Integer userId) {
        return userRoleRepository.save(new UserRole(userId, "ADMIN"))
            .then(userRoleRepository.save(new UserRole(userId, "PROVIDER")))
            .then();
    }
}