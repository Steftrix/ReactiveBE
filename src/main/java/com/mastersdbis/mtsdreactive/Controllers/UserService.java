package com.mastersdbis.mtsdreactive.Controllers;

import lombok.RequiredArgsConstructor;
import com.mastersdbis.mtsdreactive.Entities.Provider;
import com.mastersdbis.mtsdreactive.Entities.User;
import com.mastersdbis.mtsdreactive.Entities.UserRole;
import com.mastersdbis.mtsdreactive.Repositories.ProviderRepository;
import com.mastersdbis.mtsdreactive.Repositories.UserRepository;
import com.mastersdbis.mtsdreactive.Repositories.UserRoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final ProviderRepository providerRepository;
    private final PasswordEncoder passwordEncoder;

    // ----------------------------------------------------------------
    // Core user loading — roles resolved reactively from user_roles
    // ----------------------------------------------------------------

    /**
     * Loads user and eagerly attaches their roles.
     * Used by security layer and any place where roles are needed.
     */
    public Mono<User> findByUsernameWithRoles(String username) {
        return userRepository.findByUsername(username)
                .flatMap(user ->
                        userRoleRepository.findByUserId(user.getId())
                                .collectList()
                                .map(roles -> {
                                    roles.forEach(r -> user.getRoles().add(r.getRole()));
                                    return user;
                                })
                );
    }

    public Mono<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Mono<User> findById(Integer id) {
        return userRepository.findById(id);
    }

    public Mono<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Flux<User> findAll() {
        return userRepository.findAll();
    }

    public Flux<User> searchByUsernamePattern(String pattern) {
        return userRepository.searchByUsernamePattern(pattern);
    }

    // ----------------------------------------------------------------
    // User mutations
    // ----------------------------------------------------------------

    @Transactional
    public Mono<User> addUser(User user) {
        validatePassword(user.getPassword());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setDateCreated(LocalDateTime.now());
        user.setDateUpdated(LocalDateTime.now());

        return userRepository.save(user)
                .flatMap(saved ->
                        userRoleRepository.save(new UserRole(saved.getId(), "CLIENT"))
                                .thenReturn(saved)
                );
    }

    public Mono<User> updateUser(User user) {
        user.setDateUpdated(LocalDateTime.now());
        return userRepository.save(user);
    }

    public Mono<Void> updateUserPassword(Integer userId, String rawPassword) {
        validatePassword(rawPassword);
        String encoded = passwordEncoder.encode(rawPassword);
        return userRepository.updateUserPassword(userId, encoded);
    }

    public Mono<Void> deleteUser(Integer userId) {
        return userRoleRepository.deleteByUserId(userId)
                .then(userRepository.deleteById(userId));
    }

    // ----------------------------------------------------------------
    // Provider operations
    // ----------------------------------------------------------------

    public Mono<Provider> findProviderById(Integer id) {
        return providerRepository.findById(id);
    }

    public Mono<Provider> findProviderByUserId(Integer userId) {
        return providerRepository.findById(userId);
    }

    public Flux<Provider> findAllApprovedProviders() {
        return providerRepository.findAllApproved();
    }

    @Transactional
    public Mono<Provider> addProvider(Provider provider) {
        provider.setValidationStatus("PENDING");
        provider.setDateCreated(LocalDateTime.now());
        provider.setDateUpdated(LocalDateTime.now());
        return providerRepository.save(provider);
    }

    public Mono<Provider> updateProvider(Provider provider) {
        provider.setDateUpdated(LocalDateTime.now());
        return providerRepository.save(provider);
    }

    // ----------------------------------------------------------------
    // Password validation (mirrors imperative app rules)
    // ----------------------------------------------------------------

    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters.");
        }
        if (!Pattern.compile("[A-Z]").matcher(password).find()) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter.");
        }
        if (!Pattern.compile("[0-9]").matcher(password).find()) {
            throw new IllegalArgumentException("Password must contain at least one digit.");
        }
        if (!Pattern.compile("[^a-zA-Z0-9]").matcher(password).find()) {
            throw new IllegalArgumentException("Password must contain at least one special character.");
        }
    }
}
