package http.tasktracksystem.domain.dtos.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record ApiResponse<T>(String message, T data) {
}
