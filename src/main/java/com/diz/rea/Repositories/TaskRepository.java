package com.diz.rea.Repositories;

import com.diz.rea.Entities.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class TaskRepository {

    private final R2dbcEntityTemplate template;

    public Mono<Task> findByBookingIdAndTaskNumber(Integer bookingId, Integer taskNumber) {
        return template.selectOne(
                Query.query(Criteria.where("booking_id").is(bookingId)
                        .and("tasknumber").is(taskNumber)),
                Task.class);
    }

    public Flux<Task> findByBookingId(Integer bookingId) {
        return template.select(
                Query.query(Criteria.where("booking_id").is(bookingId)),
                Task.class);
    }

    public Mono<Task> save(Task task) {
        return template.insert(task);
    }

    public Mono<Void> delete(Task task) {
        return template.delete(
                Query.query(Criteria.where("booking_id").is(task.getBookingId())
                        .and("tasknumber").is(task.getTaskNumber())),
                Task.class).then();
    }

    public Mono<Integer> findMaxTaskNumber(Integer bookingId) {
        return template.getDatabaseClient()
                .sql("SELECT MAX(tasknumber) FROM task WHERE booking_id = :bookingId")
                .bind("bookingId", bookingId)
                .map(row -> row.get(0, Integer.class))
                .one()
                .defaultIfEmpty(0);
    }
}
