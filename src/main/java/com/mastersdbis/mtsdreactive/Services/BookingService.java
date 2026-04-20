package com.mastersdbis.mtsdreactive.Services;

import com.mastersdbis.mtsdreactive.Entities.Booking;
import com.mastersdbis.mtsdreactive.Repositories.BookingRepository;
import com.mastersdbis.mtsdreactive.Repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.*;


@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;

    public Mono<Booking> addBooking(Integer userId, Integer serviceId,
                                    Double price, LocalDate dueDate, LocalTime time) {
        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setServiceId(serviceId);
        booking.setPrice(price);
        booking.setDueDate(dueDate);
        booking.setDueTime(time);
        booking.setBookingState("PENDING_PAYMENT");
        booking.setDateCreated(LocalDateTime.now());
        booking.setDateUpdated(LocalDateTime.now());
        return bookingRepository.save(booking);
    }

    public Mono<Booking> cancelBooking(Integer bookingId) {
        return bookingRepository.findById(bookingId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Booking not found")))
                .flatMap(booking -> {
                    long hours = Duration.between(booking.getDateCreated(),
                            LocalDateTime.now()).toHours();
                    if (hours >= 24) {
                        return Mono.error(new IllegalStateException(
                                "Booking can only be cancelled within 24 hours"));
                    }
                    return paymentRepository.findByBookingId(bookingId)
                            .switchIfEmpty(Mono.error(
                                    new IllegalStateException("No payment found")))
                            .flatMap(payment -> {
                                payment.setPaymentState("REVERTED");
                                return paymentRepository.save(payment);
                            })
                            .then(Mono.defer(() -> {
                                booking.setBookingState("CANCELED");
                                booking.setDateUpdated(LocalDateTime.now());
                                return bookingRepository.save(booking);
                            }));
                });
    }

    public Flux<Booking> findByUserId(Integer userId) {
        return bookingRepository.findByUserId(userId);
    }
}
