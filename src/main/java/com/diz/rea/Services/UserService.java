package com.diz.rea.Services;

import com.diz.rea.Entities.Provider;
import com.diz.rea.Entities.User;
import com.diz.rea.Repositories.ProviderRepository;
import com.diz.rea.Repositories.UserRepository;
import com.diz.rea.Repositories.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final ProviderRepository providerRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public Mono<User> addUser(User user) {
        validatePassword(user.getPassword());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setDateCreated(LocalDateTime.now());
        user.setDateUpdated(LocalDateTime.now());
        return userRepository.save(user)
                .flatMap(saved -> userRoleRepository.save(
                                new UserRole(saved.getId(), "CLIENT"))
                        .thenReturn(saved));
    }

    public Mono<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Mono<Provider> findProviderByUserId(Integer userId) {
        return providerRepository.findByUserId(userId);
    }

    public Mono<User> updateUser(User user) {
        user.setDateUpdated(LocalDateTime.now());
        return userRepository.save(user);
    }

    // Key pattern: load user then load roles reactively
    public Mono<User> findByUsernameWithRoles(String username) {
        return userRepository.findByUsername(username)
                .flatMap(user -> userRoleRepository.findByUserId(user.getId())
                        .collectList()
                        .map(roles -> {
                            user.setRoles(roles.stream()
                                    .map(UserRole::getRole)
                                    .collect(Collectors.toSet()));
                            return user;
                        }));
    }
}