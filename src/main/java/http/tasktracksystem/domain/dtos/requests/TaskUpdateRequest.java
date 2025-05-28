package http.tasktracksystem.domain.dtos.requests;

import http.tasktracksystem.domain.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record TaskUpdateRequest(
        @NotBlank(message = "Title should not be blank.")
        @Size(min = 3, message = "Title length should be min 3 characters.")
        String title,

        String description,

        @NotNull(message = "Task status should not be null.")
        TaskStatus status,

        Long createdById,

        Long assignedToId
) {
}
