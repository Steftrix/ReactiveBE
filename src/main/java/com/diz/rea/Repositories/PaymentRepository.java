package com.diz.rea.Repositories;

import com.diz.rea.Entities.Payment;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PaymentRepository extends ReactiveCrudRepository<Payment, Integer> {

    Mono<Payment> findByBookingId(Integer bookingId);
    Flux<Payment> findByPaymentState(String paymentState);

    @Query("SELECT p.* FROM payment p JOIN booking b ON p.booking_id = b.id WHERE b.user_id = :userId")
    Flux<Payment> findByUserId(Integer userId);

    @Query("SELECT p.* FROM payment p JOIN booking b ON p.booking_id = b.id WHERE b.user_id = :userId AND p.paymentstate = :status")
    Flux<Payment> findByUserIdAndStatus(Integer userId, String status);

    @Query("SELECT p.* FROM payment p JOIN booking b ON p.booking_id = b.id WHERE b.service_id = :serviceId")
    Flux<Payment> findByServiceId(Integer serviceId);

    @Query("SELECT p.* FROM payment p JOIN booking b ON p.booking_id = b.id JOIN service s ON b.service_id = s.id WHERE s.provider_id = :providerId")
    Flux<Payment> findByProviderId(Integer providerId);
}
