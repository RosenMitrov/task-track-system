package http.tasktracksystem.domain.dtos.requests;

import http.tasktracksystem.domain.enums.RoleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record ChangeUserRolesRequest(
        @NotBlank(message = "Username should not be blank.")
        @Size(min = 3, max = 50, message = "Username length should be min 3 max 50 characters.")
        String changeTo,

        @NotEmpty(message = "Please provide at least one role.")
        Set<RoleType> roles) {
}
