package org.mastersdbis.mtsdreactive.Services;

import lombok.RequiredArgsConstructor;
import org.mastersdbis.mtsdreactive.Entities.Service;
import org.mastersdbis.mtsdreactive.Repositories.ServiceRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;

    // ----------------------------------------------------------------
    // Queries
    // ----------------------------------------------------------------

    public Mono<Service> findById(Integer id) {
        return serviceRepository.findById(id);
    }

    public Flux<Service> findAll() {
        return serviceRepository.findAll();
    }

    public Flux<Service> findByActiveTrue() {
        return serviceRepository.findByActiveTrue();
    }

    public Flux<Service> findByProviderId(Integer providerId) {
        return serviceRepository.findByProviderId(providerId);
    }

    public Flux<Service> findByProviderIdAndActiveTrue(Integer providerId) {
        return serviceRepository.findByProviderIdAndActiveTrue(providerId);
    }

    public Flux<Service> findByDomain(String domain) {
        return serviceRepository.findByDomainAndActiveTrue(domain);
    }

    public Flux<Service> findBySubdomain(String subdomain) {
        return serviceRepository.findBySubdomainAndActiveTrue(subdomain);
    }

    public Flux<Service> findByRegion(String region) {
        return serviceRepository.findByRegionAndActiveTrue(region);
    }

    public Flux<Service> searchServices(String searchTerm) {
        return serviceRepository.searchServices(searchTerm);
    }

    // ----------------------------------------------------------------
    // Mutations
    // ----------------------------------------------------------------

    public Mono<Service> saveService(Service service) {
        if (service.getDateCreated() == null) {
            service.setDateCreated(LocalDateTime.now());
        }
        service.setDateUpdated(LocalDateTime.now());
        return serviceRepository.save(service);
    }

    public Mono<Void> deleteService(Integer serviceId) {
        return serviceRepository.deleteById(serviceId);
    }
}