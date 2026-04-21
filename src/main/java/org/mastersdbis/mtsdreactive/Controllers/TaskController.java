package org.mastersdbis.mtsdreactive.Controllers;

import lombok.RequiredArgsConstructor;
import org.mastersdbis.mtsdreactive.DTO.TaskDTO;
import org.mastersdbis.mtsdreactive.Services.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/add")
    public Mono<ResponseEntity<Void>> addTask(@RequestBody TaskDTO dto) {
        return taskService.addTask(dto.toTask())
            .then(Mono.just(ResponseEntity.status(HttpStatus.CREATED).<Void>build()))
            .onErrorResume(e ->
                Mono.just(ResponseEntity.badRequest().<Void>build()));
    }

    @PutMapping("/update")
    public Mono<ResponseEntity<Void>> updateTask(@RequestBody TaskDTO dto) {
        return taskService.updateTask(dto.toTask())
            .then(Mono.just(ResponseEntity.ok().<Void>build()));
    }

    @PutMapping("/manageState")
    public Mono<ResponseEntity<Void>> manageState(
            @RequestParam Integer taskNumber,
            @RequestParam Integer bookingId,
            @RequestParam String state) {
        return taskService.manageTaskState(bookingId, taskNumber, state)
            .then(Mono.just(ResponseEntity.ok().<Void>build()));
    }

    @DeleteMapping("/delete")
    public Mono<ResponseEntity<Void>> deleteTask(@RequestBody TaskDTO dto) {
        return taskService.deleteTask(dto.getBookingId(), dto.getTaskNumber())
            .then(Mono.just(ResponseEntity.ok().<Void>build()));
    }

    @GetMapping("/find")
    public Mono<ResponseEntity<TaskDTO>> findById(
            @RequestParam Integer bookingId,
            @RequestParam Integer taskNr) {
        return taskService.findByBookingIdAndTaskNumber(bookingId, taskNr)
            .map(t -> ResponseEntity.ok(TaskDTO.fromTask(t)))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/findByBooking")
    public Mono<ResponseEntity<List<TaskDTO>>> findByBooking(@RequestParam Integer bookingId) {
        return taskService.findByBookingId(bookingId)
            .map(TaskDTO::fromTask)
            .collectList()
            .map(ResponseEntity::ok);
    }

    @GetMapping("/findByBookingAndDueDate")
    public Mono<ResponseEntity<List<TaskDTO>>> findByBookingAndDueDate(
            @RequestParam Integer bookingId,
            @RequestParam LocalDate dueDate) {
        return taskService.findByBookingIdAndDueDate(bookingId, dueDate)
            .map(TaskDTO::fromTask)
            .collectList()
            .map(ResponseEntity::ok);
    }

    @GetMapping("/findByBookingAndStatus")
    public Mono<ResponseEntity<List<TaskDTO>>> findByBookingAndStatus(
            @RequestParam Integer bookingId,
            @RequestParam String status) {
        return taskService.findByBookingIdAndStatus(bookingId, status)
            .map(TaskDTO::fromTask)
            .collectList()
            .map(ResponseEntity::ok);
    }
}