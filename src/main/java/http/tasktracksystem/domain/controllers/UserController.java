package http.tasktracksystem.domain.controllers;

import http.tasktracksystem.domain.dtos.requests.ChangeUserRolesRequest;
import http.tasktracksystem.domain.dtos.requests.UserRegisterRequest;
import http.tasktracksystem.domain.dtos.responses.ApiResponse;
import http.tasktracksystem.domain.security.user.AppUserDetails;
import http.tasktracksystem.domain.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "User API", description = "API that provides operations related to users.")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Register new user.",
            description = "Returns response message with user details.")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(@Valid @RequestBody
                                                                     UserRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.userService.registerUser(request));
    }

    @Operation(
            summary = "Remove/Delete user.",
            description = "Returns response message for removed user.")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @DeleteMapping("/remove/{userId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> delete(@PathVariable("userId") Long userId,
                                                                   @AuthenticationPrincipal
                                                                   AppUserDetails userDetails) {
        return ResponseEntity.ok(this.userService.delete(userId, userDetails.getUsername()));
    }

    @Operation(
            summary = "Change user roles.",
            description = "Returns response message wit role changes.")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PatchMapping("/change/role")
    public ResponseEntity<ApiResponse<Map<String, Object>>> applyRoles(@Valid @RequestBody ChangeUserRolesRequest request,
                                                                       @AuthenticationPrincipal AppUserDetails userDetails) {
        return ResponseEntity.ok(this.userService.applyRoles(request, userDetails.getUsername()));
    }
}
