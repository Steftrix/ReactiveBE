package com.mastersdbis.mtsdreactive.Services;

import com.mastersdbis.mtsdreactive.Entities.Task;
import com.mastersdbis.mtsdreactive.Repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void checkOverdueTasks() {
        taskRepository.findAll()
                .filter(task -> !task.getStatus().equals("OVERDUE") &&
                        task.getDuedate().isBefore(LocalDate.now()))
                .flatMap(task -> {
                    task.setStatus("OVERDUE");
                    return taskRepository.save(task);
                })
                .subscribe();  // fire-and-forget for scheduled jobs
    }

    public Mono<Task> addTask(Task task) {
        return taskRepository.findMaxTaskNumber(task.getBookingId())
                .flatMap(max -> {
                    task.setTaskNumber(max + 1);
                    task.setStatus("TODO");
                    task.setDateCreated(LocalDateTime.now());
                    return taskRepository.save(task);
                });
    }
}
