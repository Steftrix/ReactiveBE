package org.mastersdbis.mtsdreactive.Services;

import lombok.RequiredArgsConstructor;
import org.mastersdbis.mtsdreactive.Entities.Booking;
import org.mastersdbis.mtsdreactive.Entities.Payment;
import org.mastersdbis.mtsdreactive.Repositories.BookingRepository;
import org.mastersdbis.mtsdreactive.Repositories.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    // ----------------------------------------------------------------
    // Queries
    // ----------------------------------------------------------------

    public Mono<Payment> findById(Integer id) {
        return paymentRepository.findById(id);
    }

    public Mono<Payment> findByBookingId(Integer bookingId) {
        return paymentRepository.findByBookingId(bookingId);
    }

    public Flux<Payment> findByUserId(Integer userId) {
        return paymentRepository.findByUserId(userId);
    }

    public Flux<Payment> findByUserIdAndStatus(Integer userId, String status) {
        return paymentRepository.findByUserIdAndStatus(userId, status);
    }

    public Flux<Payment> findByPaymentState(String state) {
        return paymentRepository.findByPaymentState(state);
    }

    public Flux<Payment> findByServiceId(Integer serviceId) {
        return paymentRepository.findByServiceId(serviceId);
    }

    public Flux<Payment> findByProviderId(Integer providerId) {
        return paymentRepository.findByProviderId(providerId);
    }

    // ----------------------------------------------------------------
    // Mutations
    // ----------------------------------------------------------------

    public Mono<Payment> savePayment(Payment payment) {
        payment.setDateUpdated(LocalDateTime.now());
        return paymentRepository.save(payment);
    }

    public Mono<Void> deletePayment(Integer paymentId) {
        return paymentRepository.deleteById(paymentId);
    }

    /**
     * Processes a payment and transitions the booking to ACTIVE
     * if payment is ACCEPTED or PENDING.
     * Deletes the booking if payment is in any other state.
     */
    @Transactional
    public Mono<Payment> processPayment(Integer bookingId, Payment payment) {
        payment.setBookingId(bookingId);
        payment.setPaymentDate(LocalDate.now());
        payment.setDateCreated(LocalDateTime.now());
        payment.setDateUpdated(LocalDateTime.now());

        return bookingRepository.findById(bookingId)
            .switchIfEmpty(Mono.error(
                new IllegalArgumentException("Booking not found: " + bookingId)))
            .flatMap(booking -> {
                if ("ACCEPTED".equals(payment.getPaymentState())
                        || "PENDING".equals(payment.getPaymentState())) {
                    booking.setBookingState("ACTIVE");
                    booking.setDateUpdated(LocalDateTime.now());
                    return bookingRepository.save(booking)
                        .then(paymentRepository.save(payment));
                } else {
                    return bookingRepository.delete(booking)
                        .then(paymentRepository.save(payment));
                }
            });
    }

    /**
     * Creates a REVERTED payment entry for a booking.
     * Does not delete the original payment — creates a new one
     * with REVERTED state, mirroring the imperative app's behaviour.
     */
    @Transactional
    public Mono<Payment> revertPayment(Integer bookingId) {
        return paymentRepository.findByBookingId(bookingId)
            .switchIfEmpty(Mono.error(
                new IllegalStateException("No payment found for booking: " + bookingId)))
            .flatMap(original -> {
                Payment refund = new Payment();
                refund.setBookingId(original.getBookingId());
                refund.setAmount(original.getAmount());
                refund.setPaymentMethod(original.getPaymentMethod());
                refund.setPaymentState("REVERTED");
                refund.setPaymentDate(LocalDate.now());
                refund.setDateCreated(LocalDateTime.now());
                refund.setDateUpdated(LocalDateTime.now());
                return paymentRepository.save(refund);
            });
    }
}