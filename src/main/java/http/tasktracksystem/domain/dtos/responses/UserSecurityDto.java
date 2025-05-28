package http.tasktracksystem.domain.dtos.responses;

import lombok.Builder;

import java.util.List;

@Builder
public record UserSecurityDto(
        Long id,
        String username,
        String email,
        String password,
        List<String> roles
) {
}