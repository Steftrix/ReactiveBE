package com.diz.rea.Entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table("task")
@Getter
@Setter
@NoArgsConstructor
public class Task extends AbstractEntity {

    @Id
    private Integer taskNumber;  // part of composite key

    @Column("booking_id")
    private Integer bookingId;   // part of composite key

    @Column("description")
    private String description;

    @Column("status")
    private String status;

    @Column("duedate")
    private LocalDate duedate;
}
