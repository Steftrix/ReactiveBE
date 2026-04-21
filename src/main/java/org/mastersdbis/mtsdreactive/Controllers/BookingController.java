package org.mastersdbis.mtsdreactive.Controllers;

import lombok.RequiredArgsConstructor;
import org.mastersdbis.mtsdreactive.DTO.BookingDTO;
import org.mastersdbis.mtsdreactive.Services.BookingService;
import org.mastersdbis.mtsdreactive.Services.ServiceService;
import org.mastersdbis.mtsdreactive.Services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final ServiceService serviceService;
    private final UserService userService;

    @PostMapping
    public Mono<ResponseEntity<String>> addBooking(@RequestBody BookingDTO dto) {
        return userService.findByUsername(dto.getUsername())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found.")))
                .flatMap(user -> serviceService.findById(dto.getServiceId())
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Service not found.")))
                        .flatMap(service -> bookingService.addBooking(
                                user.getId(),
                                service.getId(),
                                dto.getPrice(),
                                dto.getDueDate(),
                                dto.getDueTime(),
                                dto.getDeliveryAddress()
                        )))
                .map(b -> ResponseEntity.ok("Booking added successfully."))
                .onErrorResume(IllegalArgumentException.class, e ->
                        Mono.just(ResponseEntity.badRequest().body(e.getMessage())))
                .onErrorResume(IllegalStateException.class, e ->
                        Mono.just(ResponseEntity.badRequest().body(e.getMessage())));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<String>> updateBooking(
            @PathVariable Integer id,
            @RequestBody BookingDTO dto) {
        return bookingService.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Booking not found.")))
                .flatMap(booking -> {
                    if (dto.getDueDate() != null)         booking.setDueDate(dto.getDueDate());
                    if (dto.getDueTime() != null)         booking.setDueTime(dto.getDueTime());
                    if (dto.getDeliveryAddress() != null) booking.setDeliveryAddress(dto.getDeliveryAddress());
                    if (dto.getPrice() != null)           booking.setPrice(dto.getPrice());
                    if (dto.getBookingState() != null)    booking.setBookingState(dto.getBookingState());
                    return bookingService.updateBooking(booking);
                })
                .map(b -> ResponseEntity.ok("Booking updated successfully."))
                .onErrorResume(IllegalArgumentException.class, e ->
                        Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage())));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<String>> cancelBooking(@PathVariable Integer id) {
        return bookingService.cancelBooking(id)
                .map(b -> ResponseEntity.ok("Booking cancelled successfully."))
                .onErrorResume(IllegalArgumentException.class, e ->
                        Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage())))
                .onErrorResume(IllegalStateException.class, e ->
                        Mono.just(ResponseEntity.badRequest().body(e.getMessage())));
    }

    @PutMapping("/{id}/complete")
    public Mono<ResponseEntity<Map<String, String>>> completeBooking(@PathVariable Integer id) {
        return bookingService.completeBooking(id)
                .map(b -> ResponseEntity.ok(Map.of("message", "Booking completed successfully.")))
                .onErrorResume(IllegalArgumentException.class, e ->
                        Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(Map.of("error", e.getMessage()))))
                .onErrorResume(IllegalStateException.class, e ->
                        Mono.just(ResponseEntity.badRequest().body(Map.of("error", e.getMessage()))));
    }

    @GetMapping("/{bookingId}")
    public Mono<ResponseEntity<BookingDTO>> getById(@PathVariable Integer bookingId) {
        return bookingService.findById(bookingId)
                .map(b -> ResponseEntity.ok(BookingDTO.fromBooking(b)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{username}")
    public Mono<ResponseEntity<List<BookingDTO>>> getByUser(@PathVariable String username) {
        return userService.findByUsername(username)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found.")))
                .flatMap(user -> bookingService.findByUserId(user.getId())
                        .map(b -> {
                            BookingDTO dto = BookingDTO.fromBooking(b);
                            dto.setUsername(username);
                            return dto;
                        })
                        .collectList())
                .map(ResponseEntity::ok)
                .onErrorResume(IllegalArgumentException.class, e ->
                        Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .<List<BookingDTO>>build()));
    }

    @GetMapping("/service/{serviceId}")
    public Mono<ResponseEntity<List<BookingDTO>>> getByService(@PathVariable Integer serviceId) {
        return bookingService.findByServiceId(serviceId)
                .map(BookingDTO::fromBooking)
                .collectList()
                .map(ResponseEntity::ok);
    }

    @GetMapping("/provider/{username}")
    public Mono<ResponseEntity<List<BookingDTO>>> getByProvider(@PathVariable String username) {
        return userService.findByUsername(username)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found.")))
                .flatMap(user -> userService.findProviderByUserId(user.getId())
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Not a provider.")))
                        .flatMap(provider -> bookingService.findByProviderId(provider.getId())
                                .map(BookingDTO::fromBooking)
                                .collectList()))
                .map(ResponseEntity::ok)
                .onErrorResume(IllegalArgumentException.class, e ->
                        Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .<List<BookingDTO>>build()));
    }
}