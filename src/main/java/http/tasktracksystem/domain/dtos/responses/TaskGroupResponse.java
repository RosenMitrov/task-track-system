package http.tasktracksystem.domain.dtos.responses;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record TaskGroupResponse(
        String name,
        Instant createdAt,
        List<TaskResponse> tasks
) {
}
