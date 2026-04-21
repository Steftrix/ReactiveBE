package org.mastersdbis.mtsdreactive.Security;

import org.mastersdbis.mtsdreactive.Repositories.UserRepository;
import org.mastersdbis.mtsdreactive.Repositories.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ReactiveUserDetailsServiceImpl implements ReactiveUserDetailsService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(
                        new UsernameNotFoundException("User not found: " + username)))
                .flatMap(user ->
                        userRoleRepository.findByUserId(user.getId())
                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRole()))
                                .collectList()
                                .map(authorities -> (UserDetails)
                                        org.springframework.security.core.userdetails.User
                                                .withUsername(user.getUsername())
                                                .password(user.getPassword())
                                                .authorities(authorities)
                                                .build()
                                )
                );
    }
}