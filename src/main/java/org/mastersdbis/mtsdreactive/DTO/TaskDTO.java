package org.mastersdbis.mtsdreactive.DTO;

import org.mastersdbis.mtsdreactive.Entities.Task;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {

    private Integer taskNumber;
    private Integer bookingId;
    private String description;
    private String status;

    @NotNull
    private LocalDate dueDate;

    public static TaskDTO fromTask(Task t) {
        TaskDTO dto = new TaskDTO();
        dto.setTaskNumber(t.getTaskNumber());
        dto.setBookingId(t.getBookingId());
        dto.setDescription(t.getDescription());
        dto.setStatus(t.getStatus());
        dto.setDueDate(t.getDuedate());
        return dto;
    }

    public Task toTask() {
        Task t = new Task();
        t.setTaskNumber(this.taskNumber);
        t.setBookingId(this.bookingId);
        t.setDescription(this.description);
        t.setStatus(this.status);
        t.setDuedate(this.dueDate);
        return t;
    }
}