package com.mastersdbis.mtsdreactive.Repositories;

import com.mastersdbis.mtsdreactive.Entities.User;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User, Integer> {

    Mono<User> findByUsername(String username);

    Mono<User> findByEmail(String email);

    @Query("SELECT * FROM users WHERE LOWER(username) LIKE LOWER(CONCAT('%', :pattern, '%'))")
    Flux<User> searchByUsernamePattern(String pattern);

    @Modifying
    @Query("UPDATE users SET password = :password, date_updated = NOW() WHERE id = :id")
    Mono<Void> updateUserPassword(Integer id, String password);

    @Modifying
    @Query("UPDATE users SET rating = :rating, date_updated = NOW() WHERE id = :id")
    Mono<Void> updateUserRating(Integer id, Double rating);
}
