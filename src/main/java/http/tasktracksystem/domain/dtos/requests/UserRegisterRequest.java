package http.tasktracksystem.domain.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record UserRegisterRequest(

        @NotBlank(message = "Username should not be blank.")
        @Size(min = 3, max = 50, message = "Username length should be min 3 max 50 characters.")
        String username,

        @NotBlank(message = "Email should not be blank.")
        @Email(message = "Email address should be valid.")
        String email,

        @NotBlank(message = "Password should not be blank.")
        @Size(min = 3, max = 100, message = "Password length should be min 3 max 100 characters.")
        String password
) {
}
