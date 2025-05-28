package http.tasktracksystem.domain.services;

import http.tasktracksystem.domain.dtos.requests.TaskGroupRequest;
import http.tasktracksystem.domain.dtos.responses.ApiResponse;
import http.tasktracksystem.domain.dtos.responses.TaskGroupResponse;
import http.tasktracksystem.domain.dtos.responses.TaskResponse;
import http.tasktracksystem.domain.entities.TaskEntity;
import http.tasktracksystem.domain.entities.TaskGroupEntity;
import http.tasktracksystem.domain.enums.TaskGroupStatus;
import http.tasktracksystem.domain.exceptions.custom.AlreadyExistsException;
import http.tasktracksystem.domain.exceptions.custom.NotFoundException;
import http.tasktracksystem.domain.repositories.TaskGroupRepository;
import http.tasktracksystem.domain.utils.responses.GeneralUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static http.tasktracksystem.domain.utils.responses.GeneralTemplates.*;
import static http.tasktracksystem.domain.utils.responses.GeneralUtils.formatRow;

@Slf4j
@AllArgsConstructor
@Service
public class TaskGroupService {

    private final TaskGroupRepository taskGroupRepository;

    /**
     * @param request TaskGroupRequest - Name, Status details.
     * @return ApiResponse based on given parameters.
     */
    public ApiResponse<Map<String, Object>> create(TaskGroupRequest request) {
        if (taskGroupRepository.existsByName(request.name())) {
            log.error("Task group name: '{}' already exists.", request.name());
            throw new AlreadyExistsException(formatRow(TASK_GROUP_ALREADY_EXISTS, request.name()));
        }

        TaskGroupEntity createGroup = this.taskGroupRepository.saveAndFlush(createNewGroup(request));

        return GeneralUtils.buildApiResponse(
                CREATE_TASK_GROUP,
                Map.of(ID, createGroup.getId(),
                        NAME, createGroup.getName(),
                        STATUS, createGroup.getStatus(),
                        CREATED_AT, createGroup.getCreatedAt())
        );
    }

    /**
     * @param pageNumber The number of the page.
     * @param size       Count entities from that page.
     * @param sortBy     ID, NAME, STATUS or DATE.
     * @param direction  Sort ASC or DESC
     * @return Page<TaskGroupResponse> based on given parameters
     */
    public Page<TaskGroupResponse> getAll(int pageNumber,
                                          int size,
                                          String sortBy,
                                          String direction) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        PageRequest pageable = PageRequest.of(pageNumber, size, sort);

        Page<TaskGroupEntity> page = this.taskGroupRepository.findAll(pageable);
        List<TaskGroupResponse> groupResponses = page.getContent()
                .stream()
                .map(this::toTaskGroupResponse)
                .toList();

        return new PageImpl<>(groupResponses, pageable, page.getTotalElements());
    }

    /**
     * Package private as it could be reachable only from domain services.
     *
     * @param taskGroupId ID of TaskGroupEntity
     * @return TaskGroupEntity object
     */
    TaskGroupEntity getTaskGroupById(Long taskGroupId) {
        return this.taskGroupRepository.findById(taskGroupId)
                .orElseThrow(() -> new NotFoundException(formatRow(TASK_GROUP_ID_NOT_FOUND, taskGroupId)));
    }

    private TaskGroupEntity createNewGroup(TaskGroupRequest request) {
        TaskGroupStatus status = Optional.ofNullable(request.status())
                .orElse(TaskGroupStatus.NOT_STARTED);

        return TaskGroupEntity.builder()
                .name(request.name())
                .status(status)
                .createdAt(Instant.now())
                .tasks(new ArrayList<>())
                .build();
    }

    private TaskGroupResponse toTaskGroupResponse(TaskGroupEntity entity) {
        return TaskGroupResponse.builder()
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .tasks(mapToTasksResponse(entity.getTasks()))
                .build();
    }

    private List<TaskResponse> mapToTasksResponse(List<TaskEntity> tasks) {
        return tasks.stream()
                .map(this::toTaskResponse)
                .toList();
    }

    private TaskResponse toTaskResponse(TaskEntity entity) {
        return TaskResponse.builder()
                .title(entity.getTitle())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdById(entity.getCreatedBy() != null ? entity.getCreatedBy().getId() : null)
                .assignedToId(entity.getAssignedTo() != null ? entity.getAssignedTo().getId() : null)
                .build();
    }
}
