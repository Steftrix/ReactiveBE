package com.diz.rea.Entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalTime;

@Table("booking")
@Getter
@Setter
@NoArgsConstructor
public class Booking extends AbstractEntity {

    @Id
    private Integer id;

    @Column("user_id")
    private Integer userId;

    @Column("service_id")
    private Integer serviceId;

    @Column("duedate")
    private LocalDate dueDate;

    @Column("booking_time")
    private LocalTime dueTime;

    @Column("deliveryaddress")
    private String deliveryAddress;

    @Column("price")
    private Double price;

    @Column("bookingstate")
    private String bookingState;
}
