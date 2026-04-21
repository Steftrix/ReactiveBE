package org.mastersdbis.mtsdreactive.Controllers;

import lombok.RequiredArgsConstructor;
import org.mastersdbis.mtsdreactive.DTO.PaymentDTO;
import org.mastersdbis.mtsdreactive.Services.PaymentService;
import org.mastersdbis.mtsdreactive.Services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final UserService userService;

    @PostMapping("/process")
    public Mono<ResponseEntity<String>> processPayment(@RequestBody PaymentDTO dto) {
        return paymentService.processPayment(dto.getBookingId(), dto.toPayment())
            .map(p -> ResponseEntity.ok("Payment processed successfully."))
            .onErrorResume(IllegalArgumentException.class, e ->
                Mono.just(ResponseEntity.badRequest().body(e.getMessage())));
    }

    @PostMapping("/revert")
    public Mono<ResponseEntity<String>> revertPayment(@RequestBody PaymentDTO dto) {
        return paymentService.revertPayment(dto.getBookingId())
            .map(p -> ResponseEntity.ok("Payment reverted."))
            .onErrorResume(e ->
                Mono.just(ResponseEntity.badRequest().body(e.getMessage())));
    }

    @PutMapping("/update")
    public Mono<ResponseEntity<Void>> updatePayment(@RequestBody PaymentDTO dto) {
        return paymentService.savePayment(dto.toPayment())
            .then(Mono.just(ResponseEntity.ok().<Void>build()));
    }

    @DeleteMapping("/delete/{id}")
    public Mono<ResponseEntity<Void>> deletePayment(@PathVariable Integer id) {
        return paymentService.findById(id)
            .flatMap(p -> paymentService.deletePayment(p.getId()))
            .then(Mono.just(ResponseEntity.ok().<Void>build()))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<PaymentDTO>> findById(@PathVariable Integer id) {
        return paymentService.findById(id)
            .map(p -> ResponseEntity.ok(PaymentDTO.fromPayment(p)))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/byBooking/{bookingId}")
    public Mono<ResponseEntity<PaymentDTO>> findByBooking(@PathVariable Integer bookingId) {
        return paymentService.findByBookingId(bookingId)
            .map(p -> ResponseEntity.ok(PaymentDTO.fromPayment(p)))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/byUser/{userId}")
    public Mono<ResponseEntity<List<PaymentDTO>>> findByUser(@PathVariable Integer userId) {
        return paymentService.findByUserId(userId)
            .map(PaymentDTO::fromPayment)
            .collectList()
            .map(ResponseEntity::ok);
    }

    @GetMapping("/byUserAndStatus/{userId}/{status}")
    public Mono<ResponseEntity<List<PaymentDTO>>> findByUserAndStatus(
            @PathVariable Integer userId,
            @PathVariable String status) {
        return paymentService.findByUserIdAndStatus(userId, status)
            .map(PaymentDTO::fromPayment)
            .collectList()
            .map(ResponseEntity::ok);
    }

    @GetMapping("/byState/{state}")
    public Mono<ResponseEntity<List<PaymentDTO>>> findByState(@PathVariable String state) {
        return paymentService.findByPaymentState(state)
            .map(PaymentDTO::fromPayment)
            .collectList()
            .map(ResponseEntity::ok);
    }

    @GetMapping("/service/{serviceId}")
    public Mono<ResponseEntity<List<PaymentDTO>>> findByService(@PathVariable Integer serviceId) {
        return paymentService.findByServiceId(serviceId)
            .map(PaymentDTO::fromPayment)
            .collectList()
            .map(ResponseEntity::ok);
    }

    @GetMapping("/provider/{providerId}")
    public Mono<ResponseEntity<List<PaymentDTO>>> findByProvider(@PathVariable Integer providerId) {
        return paymentService.findByProviderId(providerId)
            .map(PaymentDTO::fromPayment)
            .collectList()
            .map(ResponseEntity::ok);
    }
}