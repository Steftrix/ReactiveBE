package org.mastersdbis.mtsdreactive.Controllers;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.mastersdbis.mtsdreactive.DTO.ProviderDTO;
import org.mastersdbis.mtsdreactive.DTO.ProviderUpdateDTO;
import org.mastersdbis.mtsdreactive.DTO.UserDTO;
import org.mastersdbis.mtsdreactive.DTO.UserUpdateDTO;
import org.mastersdbis.mtsdreactive.Entities.Provider;
import org.mastersdbis.mtsdreactive.Services.AdminService;
import org.mastersdbis.mtsdreactive.Services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AdminService adminService;

    // ----------------------------------------------------------------
    // User CRUD
    // ----------------------------------------------------------------

    @GetMapping("/{username}")
    public Mono<ResponseEntity<UserDTO>> getUserByUsername(@PathVariable String username) {
        return userService.findByUsername(username)
            .map(u -> ResponseEntity.ok(
                new UserDTO(u.getUsername(), null, u.getEmail(), u.getPhoneNumber())))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Mono<ResponseEntity<Object>> searchUsers(@RequestParam String usernamePattern) {
        if (usernamePattern.length() < 3) {
            return Mono.just(ResponseEntity.badRequest()
                .body("Search pattern must be at least 3 characters."));
        }
        return userService.searchByUsernamePattern(usernamePattern)
            .map(u -> new UserDTO(u.getUsername(), null, u.getEmail(), u.getPhoneNumber()))
            .collectList()
            .map(list -> list.isEmpty()
                ? ResponseEntity.ok().body((Object) "No users found.")
                : ResponseEntity.ok().body((Object) list));
    }

    @GetMapping("/all")
    public Mono<ResponseEntity<List<UserDTO>>> findAllUsers() {
        return userService.findAll()
            .map(u -> new UserDTO(u.getUsername(), null, u.getEmail(), u.getPhoneNumber()))
            .collectList()
            .map(ResponseEntity::ok);
    }

    @PutMapping("/{username}")
    public Mono<ResponseEntity<String>> updateUser(
            @PathVariable String username,
            @RequestBody @Valid UserUpdateDTO dto) {
        return userService.findByUsername(username)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found.")))
            .flatMap(user -> {
                if (dto.getUsername() != null)    user.setUsername(dto.getUsername());
                if (dto.getEmail() != null)       user.setEmail(dto.getEmail());
                if (dto.getPhoneNumber() != null) user.setPhoneNumber(dto.getPhoneNumber());
                if (dto.getAddress() != null)     user.setAddress(dto.getAddress());
                return userService.updateUser(user);
            })
            .map(u -> ResponseEntity.ok("User updated successfully."))
            .onErrorResume(IllegalArgumentException.class, e ->
                Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage())));
    }

    @PutMapping("/{username}/password")
    public Mono<ResponseEntity<String>> updatePassword(
            @PathVariable String username,
            @RequestBody PasswordRequest req) {
        return userService.findByUsername(username)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found.")))
            .flatMap(user -> userService.updateUserPassword(user.getId(), req.getPassword()))
            .then(Mono.just(ResponseEntity.ok("Password updated successfully.")))
            .onErrorResume(IllegalArgumentException.class, e ->
                Mono.just(ResponseEntity.badRequest().body(e.getMessage())));
    }

    @GetMapping("/id")
    public Mono<ResponseEntity<Object>> getCurrentUserId(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Not authenticated."));
        }
        return userService.findByUsername(auth.getName())
            .map(u -> ResponseEntity.ok((Object) u.getId()))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // ----------------------------------------------------------------
    // Provider endpoints
    // ----------------------------------------------------------------

    @PostMapping("/addProvider")
    public Mono<ResponseEntity<String>> addProvider(@RequestBody @Valid ProviderDTO dto) {
        Provider provider = new Provider();
        provider.setId(dto.getUserId());
        provider.setCif(dto.getCif());
        provider.setCompanyName(dto.getCompanyName());
        provider.setCompanyAdress(dto.getCompanyAdress());
        provider.setServiceDomain(dto.getServiceDomain());
        provider.setBankIBAN(dto.getBankIBAN());

        return userService.addProvider(provider)
            .map(p -> ResponseEntity.status(HttpStatus.CREATED)
                .body("Provider added successfully."))
            .onErrorResume(e ->
                Mono.just(ResponseEntity.badRequest().body(e.getMessage())));
    }

    @PutMapping("/providers/{username}")
    public Mono<ResponseEntity<String>> updateProvider(
            @PathVariable String username,
            @RequestBody ProviderUpdateDTO dto) {
        return userService.findByUsername(username)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found.")))
            .flatMap(user -> userService.findProviderByUserId(user.getId())
                .switchIfEmpty(Mono.error(
                    new IllegalArgumentException("User is not a provider.")))
                .flatMap(provider -> {
                    if (dto.getCompanyName() != null)  provider.setCompanyName(dto.getCompanyName());
                    if (dto.getCompanyAdress() != null) provider.setCompanyAdress(dto.getCompanyAdress());
                    if (dto.getCif() != null)          provider.setCif(dto.getCif());
                    if (dto.getServiceDomain() != null) provider.setServiceDomain(dto.getServiceDomain());
                    if (dto.getBankIBAN() != null)     provider.setBankIBAN(dto.getBankIBAN());
                    return userService.updateProvider(provider);
                }))
            .map(p -> ResponseEntity.ok("Provider updated successfully."))
            .onErrorResume(IllegalArgumentException.class, e ->
                Mono.just(ResponseEntity.badRequest().body(e.getMessage())));
    }

    @GetMapping("/providers/{username}")
    public Mono<ResponseEntity<Object>> findProvider(@PathVariable String username) {
        return userService.findByUsername(username)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found.")))
            .flatMap(user -> userService.findProviderByUserId(user.getId())
                .switchIfEmpty(Mono.error(
                    new IllegalArgumentException("No provider associated with this user.")))
                .map(ProviderDTO::fromProvider))
            .map(dto -> ResponseEntity.ok((Object) dto))
            .onErrorResume(IllegalArgumentException.class, e ->
                Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage())));
    }

    @GetMapping("/check-provider")
    public Mono<ResponseEntity<Boolean>> isProvider(@RequestParam String username) {
        return userService.findByUsername(username)
            .flatMap(user -> userService.findProviderByUserId(user.getId()).hasElement())
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.ok(false));
    }

    @GetMapping("/providers/all")
    public Mono<ResponseEntity<List<ProviderDTO>>> findAllApprovedProviders() {
        return userService.findAllApprovedProviders()
            .map(ProviderDTO::fromProvider)
            .collectList()
            .map(ResponseEntity::ok);
    }

    @PutMapping("/providers/{providerId}/validate")
    public Mono<ResponseEntity<String>> validateProvider(
            @PathVariable Integer providerId,
            Authentication auth) {
        return userService.findByUsername(auth.getName())
            .flatMap(admin -> adminService.validateProvider(providerId, admin.getId()))
            .map(p -> ResponseEntity.ok("Provider validated successfully."))
            .onErrorResume(IllegalArgumentException.class, e ->
                Mono.just(ResponseEntity.badRequest().body(e.getMessage())));
    }

    @PutMapping("/providers/{providerId}/deny")
    public Mono<ResponseEntity<String>> denyProvider(@PathVariable Integer providerId) {
        return adminService.denyProvider(providerId)
            .map(p -> ResponseEntity.ok("Provider denied successfully."))
            .onErrorResume(IllegalArgumentException.class, e ->
                Mono.just(ResponseEntity.badRequest().body(e.getMessage())));
    }

    // ----------------------------------------------------------------
    // Inner class for password request body
    // ----------------------------------------------------------------

    @Getter
    @Setter
    public static class PasswordRequest {
        private String password;
    }
}