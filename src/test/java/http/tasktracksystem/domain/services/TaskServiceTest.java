package http.tasktracksystem.domain.services;

import http.tasktracksystem.domain.dtos.requests.TaskCreateRequest;
import http.tasktracksystem.domain.dtos.requests.TaskUpdateRequest;
import http.tasktracksystem.domain.dtos.responses.ApiResponse;
import http.tasktracksystem.domain.entities.TaskEntity;
import http.tasktracksystem.domain.entities.TaskGroupEntity;
import http.tasktracksystem.domain.entities.UserEntity;
import http.tasktracksystem.domain.enums.TaskStatus;
import http.tasktracksystem.domain.repositories.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static http.tasktracksystem.domain.utils.responses.GeneralTemplates.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskGroupService taskGroupService;

    @InjectMocks
    private TaskService taskServiceTest;

    @Test
    void test_create_successWithoutAssigneeAndGroupTask() {
        final String username = "admin";
        final String taskTitle = "Task Title";
        final TaskStatus status = TaskStatus.IN_PROGRESS;
        final String taskDescription = "Task description";

        UserEntity creator = new UserEntity();
        creator.setUsername(username);

        TaskEntity newTask = new TaskEntity();
        newTask.setId(1L);
        newTask.setTitle(taskTitle);
        newTask.setStatus(status);
        newTask.setCreatedBy(creator);
        newTask.setCreatedAt(Instant.now());
        newTask.setUpdatedAt(Instant.now());

        TaskCreateRequest request = TaskCreateRequest.builder()
                .title(taskTitle)
                .description(taskDescription)
                .status(status)
                .build();

        when(this.userService.getUserByUsername(username)).thenReturn(creator);
        when(this.taskRepository.saveAndFlush(any(TaskEntity.class))).thenReturn(newTask);

        ApiResponse<Map<String, Object>> response = this.taskServiceTest.create(request, username);


        verify(this.taskRepository, times(1)).saveAndFlush(any(TaskEntity.class));

        assertNotNull(response);
        assertEquals(CREATE_TASK, response.message());
        assertNotNull(response.data());
        assertEquals(1L, response.data().get(ID));
        assertEquals(taskTitle, response.data().get(TITLE));
        assertEquals(status, response.data().get(STATUS));
        assertEquals(username, response.data().get(CREATED_BY));
    }


    @Test
    void assignTo_success_withProvidedAssignee() {
        Long taskId = 1L;
        String assigneeUsername = "assigneeUser";
        String loggedInUsername = "admin";

        UserEntity assignee = new UserEntity();
        assignee.setUsername(assigneeUsername);

        UserEntity creator = new UserEntity();
        creator.setUsername("creatorUser");

        TaskEntity task = new TaskEntity();
        task.setId(taskId);
        task.setTitle("Task Title");
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setCreatedAt(Instant.now());
        task.setUpdatedAt(Instant.now());
        task.setCreatedBy(creator);

        when(userService.getUserByUsername(assigneeUsername)).thenReturn(assignee);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.saveAndFlush(any(TaskEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ApiResponse<Map<String, Object>> response = taskServiceTest.assignTo(taskId, assigneeUsername, loggedInUsername);

        ArgumentCaptor<TaskEntity> taskCaptor = ArgumentCaptor.forClass(TaskEntity.class);
        verify(userService).getUserByUsername(assigneeUsername);
        verify(taskRepository).findById(taskId);
        verify(taskRepository).saveAndFlush(taskCaptor.capture());

        TaskEntity savedTask = taskCaptor.getValue();
        assertEquals(assigneeUsername, savedTask.getAssignedTo().getUsername());

        assertNotNull(response);
        assertEquals(ASSIGN_TASK, response.message());
        assertNotNull(response.data());
        assertEquals(taskId, response.data().get(ID));
        assertEquals("Task Title", response.data().get(TITLE));
        assertEquals(TaskStatus.IN_PROGRESS, response.data().get(STATUS));
        assertEquals("creatorUser", response.data().get(CREATED_BY));
        assertEquals("assigneeUser", response.data().get(ASSIGNED_TO));
    }

    @Test
    void test_create_successWithAssigneeAndGroupTask() {
        final String username = "admin";
        final String assignTo = "user";
        final String taskTitle = "Task Title";
        final TaskStatus status = TaskStatus.IN_PROGRESS;
        final String taskDescription = "Task description";
        final Long taskGroupId = 1L;

        UserEntity creator = new UserEntity();
        creator.setUsername(username);
        UserEntity assignee = new UserEntity();
        assignee.setUsername(assignTo);

        TaskEntity newTask = new TaskEntity();
        newTask.setId(1L);
        newTask.setTitle(taskTitle);
        newTask.setStatus(status);
        newTask.setCreatedBy(creator);
        newTask.setAssignedTo(assignee);
        newTask.setCreatedAt(Instant.now());
        newTask.setUpdatedAt(Instant.now());

        TaskCreateRequest request = TaskCreateRequest.builder()
                .title(taskTitle)
                .description(taskDescription)
                .status(status)
                .assignedToUsername(assignTo)
                .taskGroupId(taskGroupId)
                .build();

        when(this.userService.getUserByUsername(username)).thenReturn(creator);
        when(this.userService.getUserByUsername(assignTo)).thenReturn(assignee);
        when(this.taskGroupService.getTaskGroupById(taskGroupId)).thenReturn(new TaskGroupEntity());
        when(this.taskRepository.saveAndFlush(any(TaskEntity.class))).thenReturn(newTask);

        ApiResponse<Map<String, Object>> response = this.taskServiceTest.create(request, username);

        ArgumentCaptor<TaskEntity> taskCaptor = ArgumentCaptor.forClass(TaskEntity.class);
        verify(this.taskRepository).saveAndFlush(taskCaptor.capture());

        TaskEntity capturedTask = taskCaptor.getValue();
        assertNotNull(capturedTask);
        assertEquals(taskTitle, capturedTask.getTitle());
        assertEquals(taskDescription, capturedTask.getDescription());
        assertEquals(status, capturedTask.getStatus());
        assertEquals(creator, capturedTask.getCreatedBy());
        assertEquals(assignee, capturedTask.getAssignedTo());

        assertNotNull(response);
        assertEquals(CREATE_TASK, response.message());
        assertNotNull(response.data());
        assertEquals(1L, response.data().get(ID));
        assertEquals(taskTitle, response.data().get(TITLE));
        assertEquals(status, response.data().get(STATUS));
        assertEquals(username, response.data().get(CREATED_BY));
    }

    @Test
    void addToGroup_success() {

        Long taskId = 1L;
        Long taskGroupId = 10L;
        String addedBy = "admin";

        TaskGroupEntity taskGroup = new TaskGroupEntity();
        taskGroup.setId(taskGroupId);
        taskGroup.setName("Important Group");

        UserEntity creator = new UserEntity();
        creator.setUsername("creator");

        TaskEntity task = new TaskEntity();
        task.setId(taskId);
        task.setTitle("My Task");
        task.setCreatedBy(creator);

        when(taskGroupService.getTaskGroupById(taskGroupId)).thenReturn(taskGroup);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.saveAndFlush(any(TaskEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ApiResponse<Map<String, Object>> response = taskServiceTest.addToGroup(taskId, taskGroupId, addedBy);

        ArgumentCaptor<TaskEntity> taskCaptor = ArgumentCaptor.forClass(TaskEntity.class);

        verify(taskGroupService).getTaskGroupById(taskGroupId);
        verify(taskRepository).findById(taskId);
        verify(taskRepository).saveAndFlush(taskCaptor.capture());

        TaskEntity savedTask = taskCaptor.getValue();
        assertEquals(taskGroup, savedTask.getTaskGroup());

        assertNotNull(response);
        assertEquals(ADD_TASK_TO_GROUP, response.message());

        Map<String, Object> data = response.data();
        assertNotNull(data);
        assertEquals(addedBy, data.get(ADDED_TASK_TO_GROUP_BY));
        assertEquals(taskId, data.get(TASK_ID));
        assertEquals("My Task", data.get(TASK_TITLE));
        assertEquals(taskGroupId, data.get(TASK_GROUP_ID));
        assertEquals("Important Group", data.get(TASK_NAME));
        assertEquals("creator", data.get(TASK_CREATED_BY));
    }


    @Test
    void update_success_withAssigneeAndCreator() {
        Long taskId = 100L;
        Long createdById = 1L;
        Long assignedToId = 2L;
        String updatedTitle = "Updated Task";
        TaskStatus updatedStatus = TaskStatus.DONE;
        String updatedDescription = "Updated Description";

        UserEntity creator = new UserEntity();
        creator.setId(createdById);
        creator.setUsername("creator");

        UserEntity assignee = new UserEntity();
        assignee.setId(assignedToId);
        assignee.setUsername("assignee");

        TaskEntity existingTask = new TaskEntity();
        existingTask.setId(taskId);
        existingTask.setTitle("Old Title");
        existingTask.setStatus(TaskStatus.TODO);
        existingTask.setDescription("Old Description");
        existingTask.setCreatedAt(Instant.now().minusSeconds(3600));
        existingTask.setUpdatedAt(Instant.now().minusSeconds(3600));

        when(this.taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(this.userService.getUserById(createdById)).thenReturn(creator);
        when(this.userService.getUserById(assignedToId)).thenReturn(assignee);


        when(this.taskRepository.saveAndFlush(any(TaskEntity.class)))
                .thenAnswer(invocation -> {
                    TaskEntity savedTask = invocation.getArgument(0);
                    savedTask.setId(taskId);
                    return savedTask;
                });

        TaskUpdateRequest updateRequest = TaskUpdateRequest.builder()
                .title(updatedTitle)
                .status(updatedStatus)
                .description(updatedDescription)
                .createdById(createdById)
                .assignedToId(assignedToId)
                .build();

        ApiResponse<Map<String, Object>> response = this.taskServiceTest.update(taskId, updateRequest);

        verify(this.taskRepository).findById(taskId);
        verify(this.userService).getUserById(createdById);
        verify(this.userService).getUserById(assignedToId);
        verify(this.taskRepository).saveAndFlush(any(TaskEntity.class));

        assertNotNull(response);
        assertEquals(UPDATE_TASK, response.message());

        Map<String, Object> data = response.data();
        assertNotNull(data);
        assertEquals(taskId, data.get(ID));
        assertEquals(updatedTitle, data.get(TITLE));
        assertEquals(updatedStatus, data.get(STATUS));
        assertEquals("creator", data.get(CREATED_BY));
        assertEquals("assignee", data.get(ASSIGNED_TO));
        assertNotNull(data.get(CREATED_AT));
        assertNotNull(data.get(UPDATED_AT));
    }
}