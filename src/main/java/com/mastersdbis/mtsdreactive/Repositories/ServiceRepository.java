package com.mastersdbis.mtsdreactive.Repositories;

import com.mastersdbis.mtsdreactive.Entities.Service;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ServiceRepository extends ReactiveCrudRepository<Service, Integer> {

    Flux<Service> findByProviderIdAndActiveTrue(Integer providerId);

    Flux<Service> findByProviderId(Integer providerId);

    Flux<Service> findByDomainAndActiveTrue(String domain);

    Flux<Service> findBySubdomainAndActiveTrue(String subdomain);

    Flux<Service> findByRegionAndActiveTrue(String region);

    Flux<Service> findByActiveTrue();

    @Query("""
        SELECT * FROM service
        WHERE active = true
        AND (
            LOWER(name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
            OR LOWER(description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
        )
    """)
    Flux<Service> searchServices(String searchTerm);
}
