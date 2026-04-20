package com.mastersdbis.mtsdreactive.Repositories;

import com.mastersdbis.mtsdreactive.Entities.UserRole;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRoleRepository extends ReactiveCrudRepository<UserRole, Integer> {

    @Query("SELECT * FROM user_roles WHERE user_id = :userId")
    Flux<UserRole> findByUserId(Integer userId);

    @Modifying
    @Query("DELETE FROM user_roles WHERE user_id = :userId")
    Mono<Void> deleteByUserId(Integer userId);

    @Query("INSERT INTO user_roles (user_id, roles) VALUES (:userId, :role) ON CONFLICT DO NOTHING")
    @Modifying
    Mono<Void> addRole(Integer userId, String role);
}