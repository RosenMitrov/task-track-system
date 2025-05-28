package http.tasktracksystem.domain.dtos.requests;

import http.tasktracksystem.domain.enums.TaskStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record TaskCreateRequest(
        @NotBlank(message = "Title should not be blank.")
        @Size(min = 3, message = "Title length should be min 3 characters.")
        String title,

        @NotBlank(message = "Description should not be blank.")
        @Size(min = 3, message = "Description length should be min 3 characters.")
        String description,

        TaskStatus status,

        @Size(min = 3, max = 50, message = "Assignee username length should be min 3 max 50 characters.")
        String assignedToUsername,

        @Min(value = 1,message = "Task Group ID should be greater than 0.")
        Long taskGroupId
) {
}
