package com.mastersdbis.mtsdreactive.Controllers;

import com.mastersdbis.mtsdreactive.Services.BookingService;
import com.mastersdbis.mtsdreactive.Services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;
    private final ServiceService serviceService;

    @PostMapping
    public Mono<ResponseEntity<String>> addBooking(@RequestBody BookingDTO dto) {
        return userService.findByUsername(dto.getUsername())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")))
                .flatMap(user -> serviceService.findById(dto.getServiceId())
                        .switchIfEmpty(Mono.error(
                                new IllegalArgumentException("Service not found")))
                        .flatMap(service -> bookingService.addBooking(
                                user.getId(), service.getId(),
                                dto.getPrice(), dto.getDueDate(), dto.getDueTime())))
                .map(saved -> ResponseEntity.ok("Booking added"))
                .onErrorResume(IllegalArgumentException.class, e ->
                        Mono.just(ResponseEntity.badRequest().body(e.getMessage())))
                .onErrorResume(IllegalStateException.class, e ->
                        Mono.just(ResponseEntity.badRequest().body(e.getMessage())));
    }

    @GetMapping("/user/{username}")
    public Mono<ResponseEntity<List<BookingDTO>>> getByUser(
            @PathVariable String username) {
        return userService.findByUsername(username)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Not found")))
                .flatMap(user -> bookingService.findByUserId(user.getId())
                        .map(BookingDTO::fromBooking)
                        .collectList())
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(
                        ResponseEntity.notFound().<List<BookingDTO>>build()));
    }
}
