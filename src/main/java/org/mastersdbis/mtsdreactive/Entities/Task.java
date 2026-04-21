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

/**
 * Composite PK: (tasknumber, booking_id).
 * R2DBC does not support composite @Id out of the box.
 * We use tasknumber as @Id for R2DBC mapping and handle
 * composite operations through R2dbcEntityTemplate in the repository.
 */
@Table("task")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    // R2DBC requires a single @Id — we use tasknumber
    // but all queries use BOTH tasknumber AND booking_id
    @Id
    @Column("tasknumber")
    private Integer taskNumber;

    @Column("booking_id")
    private Integer bookingId;

    @Column("date_created")
    private LocalDateTime dateCreated;

    @Column("date_updated")
    private LocalDateTime dateUpdated;

    @Column("description")
    private String description;

    @Column("duedate")
    private LocalDate duedate;

    @Column("status")
    private String status;
}