package org.mastersdbis.mtsdreactive.Controllers;

import lombok.RequiredArgsConstructor;
import org.mastersdbis.mtsdreactive.DTO.ServiceDTO;
import org.mastersdbis.mtsdreactive.Entities.Service;
import org.mastersdbis.mtsdreactive.Services.ServiceService;
import org.mastersdbis.mtsdreactive.Services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;
    private final UserService userService;

    @PostMapping
    public Mono<ResponseEntity<String>> saveService(@RequestBody ServiceDTO dto) {
        return userService.findByUsername(dto.getUsername())
            .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found.")))
            .flatMap(user -> userService.findProviderByUserId(user.getId())
                .switchIfEmpty(Mono.error(
                    new IllegalArgumentException("User is not a provider.")))
                .flatMap(provider -> {
                    Service service = new Service();
                    service.setProviderId(provider.getId());
                    service.setName(dto.getName());
                    service.setDescription(dto.getDescription());
                    service.setDomain(dto.getDomain());
                    service.setSubdomain(dto.getSubdomain());
                    service.setPrice(dto.getPrice());
                    service.setRegion(dto.getRegion());
                    service.setActive(true);
                    service.setAcceptedPaymentMethods(dto.getAcceptedPaymentMethods());
                    service.setServiceType(dto.getServiceType());
                    service.setMinimumBookingTime(dto.getMinimumBookingTime());
                    return serviceService.saveService(service);
                }))
            .map(s -> ResponseEntity.ok("Service saved successfully."))
            .onErrorResume(IllegalArgumentException.class, e ->
                Mono.just(ResponseEntity.badRequest().body(e.getMessage())));
    }

    @GetMapping("/id/{id}")
    public Mono<ResponseEntity<ServiceDTO>> getById(@PathVariable Integer id) {
        return serviceService.findById(id)
            .map(s -> ResponseEntity.ok(ServiceDTO.fromService(s)))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/active")
    public Mono<ResponseEntity<List<ServiceDTO>>> getActiveServices() {
        return serviceService.findByActiveTrue()
            .map(ServiceDTO::fromService)
            .collectList()
            .map(ResponseEntity::ok);
    }

    @GetMapping("/search")
    public Mono<ResponseEntity<List<ServiceDTO>>> searchServices(@RequestParam String searchTerm) {
        return serviceService.searchServices(searchTerm)
            .map(ServiceDTO::fromService)
            .collectList()
            .map(ResponseEntity::ok);
    }

    @GetMapping("/provider/{username}")
    public Mono<ResponseEntity<List<ServiceDTO>>> getByProvider(@PathVariable String username) {
        return userService.findByUsername(username)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found.")))
            .flatMap(user -> userService.findProviderByUserId(user.getId())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Not a provider.")))
                .flatMap(provider -> serviceService.findByProviderId(provider.getId())
                    .map(s -> {
                        ServiceDTO dto = ServiceDTO.fromService(s);
                        dto.setUsername(username);
                        return dto;
                    })
                    .collectList()))
            .map(ResponseEntity::ok)
            .onErrorResume(IllegalArgumentException.class, e ->
                Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .<List<ServiceDTO>>build()));
    }

    @GetMapping("/domain/{domain}")
    public Mono<ResponseEntity<List<ServiceDTO>>> getByDomain(@PathVariable String domain) {
        return serviceService.findByDomain(domain)
            .map(ServiceDTO::fromService)
            .collectList()
            .map(ResponseEntity::ok);
    }

    @GetMapping("/subdomain/{subdomain}")
    public Mono<ResponseEntity<List<ServiceDTO>>> getBySubdomain(@PathVariable String subdomain) {
        return serviceService.findBySubdomain(subdomain)
            .map(ServiceDTO::fromService)
            .collectList()
            .map(ResponseEntity::ok);
    }

    @GetMapping("/region/{region}")
    public Mono<ResponseEntity<List<ServiceDTO>>> getByRegion(@PathVariable String region) {
        return serviceService.findByRegion(region)
            .map(ServiceDTO::fromService)
            .collectList()
            .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Map<String, String>>> updateService(
            @PathVariable Integer id,
            @RequestBody ServiceDTO dto) {
        return serviceService.findById(id)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Service not found.")))
            .flatMap(service -> {
                if (dto.getName() != null)              service.setName(dto.getName());
                if (dto.getDescription() != null)       service.setDescription(dto.getDescription());
                if (dto.getDomain() != null)            service.setDomain(dto.getDomain());
                if (dto.getSubdomain() != null)         service.setSubdomain(dto.getSubdomain());
                if (dto.getPrice() != null)             service.setPrice(dto.getPrice());
                if (dto.getRegion() != null)            service.setRegion(dto.getRegion());
                if (dto.getActive() != null)            service.setActive(dto.getActive());
                if (dto.getMinimumBookingTime() != null) service.setMinimumBookingTime(dto.getMinimumBookingTime());
                return serviceService.saveService(service);
            })
            .map(s -> ResponseEntity.ok(Map.of("message", "Service updated successfully.")))
            .onErrorResume(IllegalArgumentException.class, e ->
                Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()))));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Map<String, String>>> deleteService(@PathVariable Integer id) {
        return serviceService.findById(id)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Service not found.")))
            .flatMap(service -> serviceService.deleteService(service.getId()))
            .then(Mono.just(ResponseEntity.ok(Map.of("message", "Service deleted successfully."))))
            .onErrorResume(IllegalArgumentException.class, e ->
                Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()))));
    }
}