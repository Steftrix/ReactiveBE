package org.mastersdbis.mtsdreactive.Entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Table("booking")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @Column("id")
    private Integer id;

    @Column("date_created")
    private LocalDateTime dateCreated;

    @Column("date_updated")
    private LocalDateTime dateUpdated;

    @Column("bookingstate")
    private String bookingState;

    @Column("deliveryaddress")
    private String deliveryAddress;

    @Column("duedate")
    private LocalDate dueDate;

    @Column("booking_time")
    private LocalTime dueTime;

    @Column("price")
    private Double price;

    @Column("service_id")
    private Integer serviceId;

    @Column("user_id")
    private Integer userId;
}
