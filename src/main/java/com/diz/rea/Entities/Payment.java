package com.diz.rea.Entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table("payment")
@Getter
@Setter
@NoArgsConstructor
public class Payment extends AbstractEntity {

    @Id
    private Integer id;

    @Column("booking_id")
    private Integer bookingId;

    @Column("amount")
    private Double amount;

    @Column("paymentmethod")
    private String paymentMethod;

    @Column("paymentstate")
    private String paymentState;

    @Column("paymentdate")
    private LocalDate paymentDate;
}
