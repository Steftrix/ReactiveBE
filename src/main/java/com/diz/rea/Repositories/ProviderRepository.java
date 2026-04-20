package com.diz.rea.Repositories;

import com.diz.rea.Entities.Provider;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProviderRepository extends ReactiveCrudRepository<Provider, Integer> {
    Mono<Provider> findByUserId(Integer userId);
    Flux<Provider> findAll();
}