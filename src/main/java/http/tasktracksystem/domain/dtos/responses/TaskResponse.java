package http.tasktracksystem.domain.dtos.responses;

import http.tasktracksystem.domain.enums.TaskStatus;
import lombok.Builder;

import java.time.Instant;

@Builder
public record TaskResponse(
        String title,
        String description,
        TaskStatus status,
        Instant createdAt,
        Instant updatedAt,
        Long createdById,
        Long assignedToId
) {
}
