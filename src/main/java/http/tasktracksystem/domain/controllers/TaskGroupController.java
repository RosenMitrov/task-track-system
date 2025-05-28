package http.tasktracksystem.domain.controllers;

import http.tasktracksystem.domain.dtos.requests.TaskGroupRequest;
import http.tasktracksystem.domain.dtos.responses.ApiResponse;
import http.tasktracksystem.domain.dtos.responses.PageResponse;
import http.tasktracksystem.domain.dtos.responses.TaskGroupResponse;
import http.tasktracksystem.domain.services.TaskGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Task Gruop API", description = "API that provides operations related to task groups.")
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/task-groups")
public class TaskGroupController {

    private final TaskGroupService taskGroupService;

    @Operation(
            summary = "Get all task groups.",
            description = "Returns response message with all task groups as pageable sorted by different params.")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<PageResponse<TaskGroupResponse>> getAll(@RequestParam(name = "page", defaultValue = "0") int page,
                                                                  @RequestParam(name = "size", defaultValue = "10") int size,
                                                                  @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
                                                                  @RequestParam(name = "direction", defaultValue = "asc") String direction) {
        return ResponseEntity.ok(new PageResponse<>(this.taskGroupService.getAll(page, size, sortBy, direction)));
    }

    @Operation(
            summary = "Create new task group.",
            description = "Returns response message with details for created task group.")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> create(@Valid @RequestBody TaskGroupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.taskGroupService.create(request));
    }
}
