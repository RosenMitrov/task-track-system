package http.tasktracksystem.domain.dtos.requests;

import http.tasktracksystem.domain.enums.TaskGroupStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TaskGroupRequest(
        @NotBlank(message = "Task group name should not be blank.")
        @Size(min = 3, message = "Task group name length should be min 3 characters.")
        String name,

        TaskGroupStatus status
) {
}
