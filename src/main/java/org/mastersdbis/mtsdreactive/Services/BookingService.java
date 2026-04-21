package org.mastersdbis.mtsdreactive.Services;

import lombok.RequiredArgsConstructor;
import org.mastersdbis.mtsdreactive.Entities.Booking;
import org.mastersdbis.mtsdreactive.Repositories.BookingRepository;
import org.mastersdbis.mtsdreactive.Repositories.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;

    // ----------------------------------------------------------------
    // Queries
    // ----------------------------------------------------------------

    public Mono<Booking> findById(Integer id) {
        return bookingRepository.findById(id);
    }

    public Flux<Booking> findAll() {
        return bookingRepository.findAll();
    }

    public Flux<Booking> findByUserId(Integer userId) {
        return bookingRepository.findByUserId(userId);
    }

    public Flux<Booking> findByServiceId(Integer serviceId) {
        return bookingRepository.findByServiceId(serviceId);
    }

    public Flux<Booking> findByProviderId(Integer providerId) {
        return bookingRepository.findByServiceProviderId(providerId);
    }

    public Flux<Booking> findByProviderIdAndState(Integer providerId, String state) {
        return bookingRepository.findByServiceProviderIdAndState(providerId, state);
    }

    public Flux<Booking> findByPeriod(LocalDate start, LocalDate end) {
        return bookingRepository.findByDueDateBetween(start, end);
    }

    public Flux<Booking> findByUserIdAndState(Integer userId, String state) {
        return bookingRepository.findByUserIdAndBookingState(userId, state);
    }

    public Flux<Booking> findByDueDateAndTime(LocalDate date, LocalTime time) {
        return bookingRepository.findByDueDateAndDueTime(date, time);
    }

    // ----------------------------------------------------------------
    // Mutations
    // ----------------------------------------------------------------

    public Mono<Booking> save(Booking booking) {
        booking.setDateUpdated(LocalDateTime.now());
        return bookingRepository.save(booking);
    }

    @Transactional
    public Mono<Booking> addBooking(Integer userId, Integer serviceId,
                                    Double price, LocalDate dueDate,
                                    LocalTime dueTime, String deliveryAddress) {
        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setServiceId(serviceId);
        booking.setPrice(price);
        booking.setDueDate(dueDate);
        booking.setDueTime(dueTime);
        booking.setDeliveryAddress(deliveryAddress);
        booking.setBookingState("PENDING_PAYMENT");
        booking.setDateCreated(LocalDateTime.now());
        booking.setDateUpdated(LocalDateTime.now());
        return bookingRepository.save(booking);
    }

    @Transactional
    public Mono<Booking> cancelBooking(Integer bookingId) {
        return bookingRepository.findById(bookingId)
                .switchIfEmpty(Mono.error(
                        new IllegalArgumentException("Booking not found: " + bookingId)))
                .flatMap(booking -> {
                    long hours = Duration.between(
                            booking.getDateCreated(), LocalDateTime.now()).toHours();
                    if (hours >= 24) {
                        return Mono.error(new IllegalStateException(
                                "Booking can only be cancelled within 24 hours of creation."));
                    }
                    return paymentRepository.findByBookingId(bookingId)
                            .switchIfEmpty(Mono.error(
                                    new IllegalStateException("No payment found for this booking.")))
                            .flatMap(payment -> {
                                payment.setPaymentState("REVERTED");
                                payment.setDateUpdated(LocalDateTime.now());
                                return paymentRepository.save(payment);
                            })
                            .then(Mono.defer(() -> {
                                booking.setBookingState("CANCELED");
                                booking.setDueDate(LocalDate.now());
                                booking.setDateUpdated(LocalDateTime.now());
                                return bookingRepository.save(booking);
                            }));
                });
    }

    public Mono<Booking> completeBooking(Integer bookingId) {
        return bookingRepository.findById(bookingId)
                .switchIfEmpty(Mono.error(
                        new IllegalArgumentException("Booking not found: " + bookingId)))
                .flatMap(booking -> {
                    if (!"ACTIVE".equals(booking.getBookingState())) {
                        return Mono.error(new IllegalStateException(
                                "Booking can only be completed if it is ACTIVE."));
                    }
                    booking.setBookingState("COMPLETED");
                    booking.setDateUpdated(LocalDateTime.now());
                    return bookingRepository.save(booking);
                });
    }

    public Mono<Booking> updateBooking(Booking booking) {
        booking.setDateUpdated(LocalDateTime.now());
        return bookingRepository.save(booking);
    }
}