package com.diz.rea.Repositories;

import reactor.core.publisher.Mono;

public interface UserRoleRepository extends ReactiveCrudRepository<UserRole, Integer> {
    Flux<UserRole> findByUserId(Integer userId);
    Mono<Void> deleteByUserId(Integer userId);
}