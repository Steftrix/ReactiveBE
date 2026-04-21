package org.mastersdbis.mtsdreactive.Services;

import lombok.RequiredArgsConstructor;
import org.mastersdbis.mtsdreactive.Entities.Task;
import org.mastersdbis.mtsdreactive.Repositories.TaskRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    // ----------------------------------------------------------------
    // Scheduled overdue check — fires every midnight
    // Must call .subscribe() because @Scheduled cannot return a Mono.
    // The subscribe() here is intentional — fire-and-forget is correct
    // for a background maintenance task.
    // ----------------------------------------------------------------

    @Scheduled(cron = "0 0 0 * * ?")
    public void checkOverdueTasks() {
        taskRepository.findAll()
                .filter(task ->
                        !"OVERDUE".equals(task.getStatus()) &&
                                task.getDuedate() != null &&
                                task.getDuedate().isBefore(LocalDate.now()))
                .flatMap(task -> {
                    task.setStatus("OVERDUE");
                    task.setDateUpdated(LocalDateTime.now());
                    return taskRepository.save(task);
                })
                .subscribe(
                        task -> {},
                        error -> {}  // swallow to prevent crashing scheduler thread
                );
    }

    // ----------------------------------------------------------------
    // Queries
    // ----------------------------------------------------------------

    public Mono<Task> findByBookingIdAndTaskNumber(Integer bookingId, Integer taskNumber) {
        return taskRepository.findByBookingIdAndTaskNumber(bookingId, taskNumber);
    }

    public Flux<Task> findByBookingId(Integer bookingId) {
        return taskRepository.findByBookingId(bookingId);
    }

    public Flux<Task> findByBookingIdAndDueDate(Integer bookingId, LocalDate dueDate) {
        return taskRepository.findByBookingIdAndDueDate(bookingId, dueDate);
    }

    public Flux<Task> findByBookingIdAndStatus(Integer bookingId, String status) {
        return taskRepository.findByBookingIdAndStatus(bookingId, status);
    }

    // ----------------------------------------------------------------
    // Mutations
    // ----------------------------------------------------------------

    public Mono<Task> addTask(Task task) {
        return taskRepository.findMaxTaskNumberByBookingId(task.getBookingId())
                .flatMap(maxTaskNumber -> {
                    task.setTaskNumber(maxTaskNumber + 1);
                    task.setStatus("TODO");
                    task.setDateCreated(LocalDateTime.now());
                    task.setDateUpdated(LocalDateTime.now());
                    return taskRepository.save(task);
                });
    }

    public Mono<Task> updateTask(Task task) {
        task.setDateUpdated(LocalDateTime.now());
        return taskRepository.save(task);
    }

    public Mono<Void> manageTaskState(Integer bookingId, Integer taskNumber, String state) {
        return taskRepository.updateStatus(bookingId, taskNumber, state);
    }

    public Mono<Void> deleteTask(Integer bookingId, Integer taskNumber) {
        return taskRepository.delete(bookingId, taskNumber);
    }
}