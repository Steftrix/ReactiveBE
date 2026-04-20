package com.mastersdbis.mtsdreactive.Repositories;

import com.mastersdbis.mtsdreactive.Entities.Booking;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.LocalTime;

public interface BookingRepository extends ReactiveCrudRepository<Booking, Integer> {

    Flux<Booking> findByUserId(Integer userId);

    Flux<Booking> findByServiceId(Integer serviceId);

    Flux<Booking> findByDueDateAndDueTime(LocalDate dueDate, LocalTime dueTime);

    Flux<Booking> findByDueDateBetween(LocalDate start, LocalDate end);

    Flux<Booking> findByUserIdAndBookingState(Integer userId, String bookingState);

    Flux<Booking> findByUserIdAndBookingStateAndDueDate(
            Integer userId, String bookingState, LocalDate dueDate);

    @Query("""
        SELECT b.* FROM booking b
        JOIN service s ON b.service_id = s.id
        WHERE s.provider_id = :providerId
    """)
    Flux<Booking> findByServiceProviderId(Integer providerId);

    @Query("""
        SELECT b.* FROM booking b
        JOIN service s ON b.service_id = s.id
        WHERE s.provider_id = :providerId
        AND b.bookingstate = :state
    """)
    Flux<Booking> findByServiceProviderIdAndState(Integer providerId, String state);
}
