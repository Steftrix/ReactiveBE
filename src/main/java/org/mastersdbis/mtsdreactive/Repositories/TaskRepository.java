package org.mastersdbis.mtsdreactive.Repositories;

import org.mastersdbis.mtsdreactive.Entities.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Manual repository using R2dbcEntityTemplate.
 * This is required because ReactiveCrudRepository expects a single @Id,
 * but task has a composite PK (tasknumber, booking_id).
 * All queries explicitly include BOTH columns to respect the composite key.
 */
@Repository
@RequiredArgsConstructor
public class TaskRepository {

    private final R2dbcEntityTemplate template;

    public Mono<Task> findByBookingIdAndTaskNumber(Integer bookingId, Integer taskNumber) {
        return template.selectOne(
                Query.query(
                        Criteria.where("booking_id").is(bookingId)
                                .and("tasknumber").is(taskNumber)
                ),
                Task.class
        );
    }

    public Flux<Task> findByBookingId(Integer bookingId) {
        return template.select(
                Query.query(Criteria.where("booking_id").is(bookingId)),
                Task.class
        );
    }

    public Flux<Task> findAll() {
        return template.select(Task.class).all();
    }

    public Flux<Task> findByBookingIdAndDueDate(Integer bookingId, LocalDate dueDate) {
        return template.select(
                Query.query(
                        Criteria.where("booking_id").is(bookingId)
                                .and("duedate").is(dueDate)
                ),
                Task.class
        );
    }

    public Flux<Task> findByBookingIdAndStatus(Integer bookingId, String status) {
        return template.select(
                Query.query(
                        Criteria.where("booking_id").is(bookingId)
                                .and("status").is(status)
                ),
                Task.class
        );
    }

    public Mono<Integer> findMaxTaskNumberByBookingId(Integer bookingId) {
        return template.getDatabaseClient()
                .sql("SELECT COALESCE(MAX(tasknumber), 0) FROM task WHERE booking_id = :bookingId")
                .bind("bookingId", bookingId)
                .map((row, meta) -> row.get(0, Integer.class))
                .one()
                .defaultIfEmpty(0);
    }

    public Mono<Task> save(Task task) {
        if (task.getTaskNumber() == null) {
            return template.insert(task);
        }
        // For update: match on composite key
        return template.update(
                Query.query(
                        Criteria.where("tasknumber").is(task.getTaskNumber())
                                .and("booking_id").is(task.getBookingId())
                ),
                Update.update("status", task.getStatus())
                        .set("description", task.getDescription())
                        .set("duedate", task.getDuedate())
                        .set("date_updated", LocalDateTime.now()),
                Task.class
        ).then(Mono.just(task));
    }

    public Mono<Void> delete(Integer bookingId, Integer taskNumber) {
        return template.delete(
                Query.query(
                        Criteria.where("booking_id").is(bookingId)
                                .and("tasknumber").is(taskNumber)
                ),
                Task.class
        ).then();
    }

    public Mono<Void> updateStatus(Integer bookingId, Integer taskNumber, String status) {
        return template.update(
                Query.query(
                        Criteria.where("booking_id").is(bookingId)
                                .and("tasknumber").is(taskNumber)
                ),
                Update.update("status", status)
                        .set("date_updated", LocalDateTime.now()),
                Task.class
        ).then();
    }
}