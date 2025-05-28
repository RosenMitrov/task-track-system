package http.tasktracksystem.domain.services;

import http.tasktracksystem.domain.dtos.requests.TaskCreateRequest;
import http.tasktracksystem.domain.dtos.requests.TaskUpdateRequest;
import http.tasktracksystem.domain.dtos.responses.ApiResponse;
import http.tasktracksystem.domain.entities.TaskEntity;
import http.tasktracksystem.domain.entities.TaskGroupEntity;
import http.tasktracksystem.domain.entities.UserEntity;
import http.tasktracksystem.domain.enums.TaskStatus;
import http.tasktracksystem.domain.exceptions.custom.NotFoundException;
import http.tasktracksystem.domain.repositories.TaskRepository;
import http.tasktracksystem.domain.utils.responses.GeneralUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static http.tasktracksystem.domain.utils.responses.GeneralTemplates.*;
import static http.tasktracksystem.domain.utils.responses.GeneralUtils.formatRow;

@RequiredArgsConstructor
@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;
    private final TaskGroupService taskGroupService;

    /**
     * @param request  TaskCreateRequest - Title, Description, Status, AssignedTo and GroupId.
     * @param username Authenticated user.
     * @return ApiResponse based on given parameters.
     */
    public ApiResponse<Map<String, Object>> create(TaskCreateRequest request,
                                                   String username) {

        UserEntity creator = this.userService.getUserByUsername(username);

        TaskEntity newTask = createNewTask(request);
        newTask.setCreatedBy(creator);

        linkTaskToUser(request.assignedToUsername(), newTask);
        linkTaskToGroup(request.taskGroupId(), newTask);

        newTask = this.taskRepository.saveAndFlush(newTask);
        return GeneralUtils.buildApiResponse(
                CREATE_TASK,
                Map.of(ID, newTask.getId(),
                        TITLE, newTask.getTitle(),
                        STATUS, newTask.getStatus(),
                        CREATED_AT, newTask.getCreatedAt(),
                        UPDATED_AT, newTask.getUpdatedAt(),
                        CREATED_BY, newTask.getCreatedBy().getUsername()));
    }

    /**
     * @param taskId           Used id of task.
     * @param assigneeUsername Assign to username if present (Optional).
     * @param loggedInUsername Assign to loggedIn user if assigneeUsername not present.
     * @return ApiResponse based on given parameters.
     */
    public ApiResponse<Map<String, Object>> assignTo(Long taskId,
                                                     String assigneeUsername,
                                                     String loggedInUsername) {
        String assignTo = Optional.ofNullable(assigneeUsername)
                .orElse(loggedInUsername);
        UserEntity assignee = this.userService.getUserByUsername(assignTo);

        TaskEntity task = this.taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException(formatRow(TASK_ID_NOT_FOUND, taskId)));

        task.setAssignedTo(assignee);
        task.setUpdatedAt(Instant.now());
        task = this.taskRepository.saveAndFlush(task);

        return GeneralUtils.buildApiResponse(
                ASSIGN_TASK,
                Map.of(ID, task.getId(),
                        TITLE, task.getTitle(),
                        STATUS, task.getStatus(),
                        CREATED_AT, task.getCreatedAt(),
                        UPDATED_AT, task.getUpdatedAt(),
                        ASSIGNED_TO, task.getAssignedTo().getUsername(),
                        CREATED_BY, task.getCreatedBy().getUsername()));
    }

    /**
     * @param taskId  Task by given id to be updated.
     * @param request TaskUpdateRequest - Title, Description, Status, CreatedBy and AssignedTo.
     * @return ApiResponse based on given parameters.
     */
    public ApiResponse<Map<String, Object>> update(Long taskId,
                                                   TaskUpdateRequest request) {

        TaskEntity task = updateById(taskId, request);
        this.taskRepository.saveAndFlush(task);

        return GeneralUtils.buildApiResponse(
                UPDATE_TASK,
                Map.of(ID, task.getId(),
                        TITLE, task.getTitle(),
                        STATUS, task.getStatus(),
                        CREATED_AT, task.getCreatedAt(),
                        UPDATED_AT, task.getUpdatedAt(),
                        ASSIGNED_TO, task.getAssignedTo() != null ? task.getAssignedTo().getUsername() : NOT_PRESENT_VALUE,
                        CREATED_BY, task.getCreatedBy() != null ? task.getCreatedBy().getUsername() : NOT_PRESENT_VALUE));
    }

    /**
     * @param taskId      Task wit id to be added.
     * @param taskGroupId Task to be added to grop task id.
     * @param addedBy     LoggedIn user.
     * @return ApiResponse based on given parameters.
     */
    public ApiResponse<Map<String, Object>> addToGroup(Long taskId,
                                                       Long taskGroupId,
                                                       String addedBy) {
        TaskGroupEntity taskGroup = this.taskGroupService.getTaskGroupById(taskGroupId);

        TaskEntity task = this.taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException(formatRow(TASK_ID_NOT_FOUND, taskId)));
        task.setTaskGroup(taskGroup);

        this.taskRepository.saveAndFlush(task);
        return GeneralUtils.buildApiResponse(
                ADD_TASK_TO_GROUP,
                Map.of(ADDED_TASK_TO_GROUP_BY, addedBy,
                        TASK_ID, task.getId(),
                        TASK_TITLE, task.getTitle(),
                        TASK_GROUP_ID, taskGroup.getId(),
                        TASK_NAME, taskGroup.getName(),
                        TASK_CREATED_BY, task.getCreatedBy() != null ? task.getCreatedBy().getUsername() : NOT_PRESENT_VALUE));
    }

    private TaskEntity createNewTask(TaskCreateRequest request) {
        TaskStatus status = Optional.ofNullable(request.status()).orElse(TaskStatus.TODO);

        return TaskEntity.builder()
                .title(request.title())
                .description(request.description())
                .status(status)
                .taskGroup(null)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    private void linkTaskToUser(String username,
                                TaskEntity newTask) {
        if (StringUtils.isNotBlank(username)) {
            UserEntity assignee = this.userService.getUserByUsername(username);
            newTask.setAssignedTo(assignee);
        }
    }

    private void linkTaskToGroup(Long taskGroupId,
                                 TaskEntity newTask) {
        if (taskGroupId != null) {
            TaskGroupEntity taskGroupById = this.taskGroupService.getTaskGroupById(taskGroupId);
            newTask.setTaskGroup(taskGroupById);
        }
    }

    private TaskEntity updateById(Long taskId,
                                  TaskUpdateRequest request) {
        TaskEntity task = this.taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException(formatRow(TASK_ID_NOT_FOUND, taskId)));

        task.setTitle(request.title());
        task.setStatus(request.status());
        task.setUpdatedAt(Instant.now());

        applyOrNull(
                request.description(),
                Function.identity(),
                task::setDescription,
                () -> task.setDescription(null)
        );

        applyOrNull(request.createdById(),
                userService::getUserById,
                task::setCreatedBy,
                () -> task.setCreatedBy(null)
        );

        applyOrNull(request.assignedToId(),
                userService::getUserById,
                task::setAssignedTo,
                () -> task.setAssignedTo(null)
        );
        return task;
    }


    private <T, R> void applyOrNull(T input,
                                    Function<T, R> mapper,
                                    Consumer<R> setter,
                                    Runnable ifNull) {
        Optional.ofNullable(input)
                .map(mapper)
                .ifPresentOrElse(setter, ifNull);
    }
}
