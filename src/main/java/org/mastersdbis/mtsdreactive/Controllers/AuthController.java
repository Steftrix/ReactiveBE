package org.mastersdbis.mtsdreactive.Controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mastersdbis.mtsdreactive.DTO.UserDTO;
import org.mastersdbis.mtsdreactive.Entities.User;
import org.mastersdbis.mtsdreactive.Security.JwtUtil;
import org.mastersdbis.mtsdreactive.Services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public Mono<ResponseEntity<String>> register(@RequestBody @Valid UserDTO dto) {
        return userService.findByUsername(dto.getUsername())
                .flatMap(existing ->
                        Mono.<ResponseEntity<String>>just(
                                ResponseEntity.badRequest().body("Username already taken.")))
                .switchIfEmpty(
                        userService.findByEmail(dto.getEmail())
                                .flatMap(existing ->
                                        Mono.<ResponseEntity<String>>just(
                                                ResponseEntity.badRequest().body("Email already in use.")))
                                .switchIfEmpty(Mono.defer(() -> {
                                    User user = new User();
                                    user.setUsername(dto.getUsername());
                                    user.setPassword(dto.getPassword());
                                    user.setEmail(dto.getEmail());
                                    user.setPhoneNumber(dto.getPhoneNumber());
                                    return userService.addUser(user)
                                            .map(saved -> ResponseEntity
                                                    .status(HttpStatus.CREATED)
                                                    .<String>body("User registered successfully."));
                                }))
                )
                .onErrorResume(IllegalArgumentException.class, e ->
                        Mono.just(ResponseEntity.badRequest().body(e.getMessage())));
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<String>> login(@RequestBody UserDTO dto) {
        return userService.findByUsername(dto.getUsername())
                .filter(user -> passwordEncoder.matches(dto.getPassword(), user.getPassword()))
                .map(user -> {
                    String token = jwtUtil.generateToken(user.getUsername());
                    return ResponseEntity.ok(token);
                })
                .defaultIfEmpty(ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid username or password."));
    }
}