package http.tasktracksystem.domain.dtos.responses;

import lombok.Builder;

@Builder
public record TokenResponse(Long id, String username, String email, String token) {
}
