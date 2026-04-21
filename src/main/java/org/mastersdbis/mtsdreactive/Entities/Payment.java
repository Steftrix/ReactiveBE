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

@Table("payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @Column("id")
    private Integer id;

    @Column("date_created")
    private LocalDateTime dateCreated;

    @Column("date_updated")
    private LocalDateTime dateUpdated;

    @Column("amount")
    private Double amount;

    @Column("paymentdate")
    private LocalDate paymentDate;

    @Column("paymentmethod")
    private String paymentMethod;

    @Column("paymentstate")
    private String paymentState;

    @Column("booking_id")
    private Integer bookingId;
}
