package com.mastersdbis.mtsdreactive.DTO;

import com.mastersdbis.mtsdreactive.Entities.Booking;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingDTO {

    private Integer id;

    @NotNull
    private String username;

    @NotNull
    private Integer serviceId;

    private String serviceName;

    @FutureOrPresent
    private LocalDate dueDate;

    private LocalTime dueTime;

    private String deliveryAddress;

    @NotNull
    private Double price;

    @NotNull
    private String bookingState;

    public static BookingDTO fromBooking(Booking b) {
        BookingDTO dto = new BookingDTO();
        dto.setId(b.getId());
        dto.setServiceId(b.getServiceId());
        dto.setDueDate(b.getDueDate());
        dto.setDueTime(b.getDueTime());
        dto.setDeliveryAddress(b.getDeliveryAddress());
        dto.setPrice(b.getPrice());
        dto.setBookingState(b.getBookingState());
        return dto;
    }
}