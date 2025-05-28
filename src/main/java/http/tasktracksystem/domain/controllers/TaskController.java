package http.tasktracksystem.domain.controllers;

import http.tasktracksystem.domain.dtos.requests.TaskCreateRequest;
import http.tasktracksystem.domain.dtos.requests.TaskUpdateRequest;
import http.tasktracksystem.domain.dtos.responses.ApiResponse;
import http.tasktracksystem.domain.security.user.AppUserDetails;
import http.tasktracksystem.domain.services.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Tasks API", description = "API that provides operations related to tasks.")
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskService taskService;

    @Operation(
            summary = "Create new task",
            description = "Returns message with task id.")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> create(@Valid @RequestBody TaskCreateRequest request,
                                                                   @AuthenticationPrincipal AppUserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.taskService.create(request, userDetails.getUsername()));
    }

    @Operation(
            summary = "Assign user to already existing task based on parameters.",
            description = "Returns message for task with assignee details.")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PatchMapping("/{taskId}/assign")
    public ResponseEntity<ApiResponse<Map<String, Object>>> assign(@PathVariable("taskId") Long taskId,
                                                                   @RequestParam(required = false) String assignee,
                                                                   @AuthenticationPrincipal
                                                                   AppUserDetails userDetails) {

        return ResponseEntity.ok(this.taskService.assignTo(taskId, assignee, userDetails.getUsername()));
    }

    @Operation(
            summary = "Add group to task.",
            description = "Returns message for task with group details.")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PatchMapping("{taskId}/{taskGroupId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> addToGroup(@PathVariable("taskId") Long taskId,
                                                                       @PathVariable("taskGroupId") Long taskGroupId,
                                                                       @AuthenticationPrincipal
                                                                       AppUserDetails userDetails) {
        return ResponseEntity.ok(this.taskService.addToGroup(taskId, taskGroupId, userDetails.getUsername()));
    }

    @Operation(
            summary = "Update task with new parameters.",
            description = "Returns message for task details.")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PutMapping("/{taskId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> update(@PathVariable("taskId") Long taskId,
                                                                   @Valid @RequestBody TaskUpdateRequest request) {
        return ResponseEntity.ok(this.taskService.update(taskId, request));
    }
}
