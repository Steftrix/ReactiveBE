package com.mastersdbis.mtsdreactive.Repositories;

import com.mastersdbis.mtsdreactive.Entities.Provider;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProviderRepository extends ReactiveCrudRepository<Provider, Integer> {

    // provider.id == user.id (shared PK via @MapsId in JPA world)
    Mono<Provider> findById(Integer id);

    @Query("SELECT * FROM provider WHERE validationstatus = 'APPROVED'")
    Flux<Provider> findAllApproved();

    @Query("SELECT * FROM provider WHERE validationstatus = 'PENDING'")
    Flux<Provider> findAllPending();
}