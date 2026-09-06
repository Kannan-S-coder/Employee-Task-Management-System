package com.example.employeetaskmanagement.service;

import com.example.employeetaskmanagement.dto.TaskRequest;
import com.example.employeetaskmanagement.entity.Employee;
import com.example.employeetaskmanagement.entity.Project;
import com.example.employeetaskmanagement.entity.Task;
import com.example.employeetaskmanagement.exception.ResourceNotFoundException;
import com.example.employeetaskmanagement.repository.EmployeeRepository;
import com.example.employeetaskmanagement.repository.ProjectRepository;
import com.example.employeetaskmanagement.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private TaskService taskService;

    private Employee employee;
    private Project project;
    private Task task1;

    @BeforeEach
    void setUp() {
        employee = new Employee("Alice Smith", "alice@example.com", "Engineering", "Software Engineer");
        employee.setId(1);

        project = new Project("Task System", "Internal system", LocalDate.now(), LocalDate.now().plusMonths(3), "IN_PROGRESS");
        project.setId(1);

        task1 = new Task("Setup Database", "Configure MySQL", "HIGH", "TODO", LocalDate.now().plusWeeks(1), employee, project);
        task1.setId(1);
    }

    @Test
    @DisplayName("Should return all tasks when no filters provided")
    void testGetAllTasks_NoFilter() {
        when(taskRepository.findAll()).thenReturn(Arrays.asList(task1));

        List<Task> result = taskService.getAllTasks(null, null, null, null);

        assertEquals(1, result.size());
        assertEquals("Setup Database", result.get(0).getTitle());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return filtered tasks when filters provided")
    void testGetAllTasks_WithFilter() {
        when(taskRepository.findTasksWithFilters("TODO", "HIGH", 1, 1))
                .thenReturn(Arrays.asList(task1));

        List<Task> result = taskService.getAllTasks("TODO", "HIGH", 1, 1);

        assertEquals(1, result.size());
        assertEquals("Setup Database", result.get(0).getTitle());
        verify(taskRepository, times(1)).findTasksWithFilters("TODO", "HIGH", 1, 1);
    }

    @Test
    @DisplayName("Should return task by ID when found")
    void testGetTaskById_Success() {
        when(taskRepository.findById(1)).thenReturn(Optional.of(task1));

        Task result = taskService.getTaskById(1);

        assertNotNull(result);
        assertEquals("Setup Database", result.getTitle());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when task ID not found")
    void testGetTaskById_NotFound() {
        when(taskRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.getTaskById(99));
    }

    @Test
    @DisplayName("Should create task successfully with valid request")
    void testCreateTask_Success() {
        TaskRequest request = new TaskRequest(
                "Setup Database", "Configure MySQL", "HIGH", "TODO",
                LocalDate.now().plusWeeks(1), 1, 1
        );

        when(employeeRepository.findById(1)).thenReturn(Optional.of(employee));
        when(projectRepository.findById(1)).thenReturn(Optional.of(project));
        when(taskRepository.save(any(Task.class))).thenReturn(task1);

        Task created = taskService.createTask(request);

        assertNotNull(created);
        assertEquals("Setup Database", created.getTitle());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when employee does not exist")
    void testCreateTask_EmployeeNotFound() {
        TaskRequest request = new TaskRequest("Title", "Desc", "HIGH", "TODO", LocalDate.now(), 99, 1);
        when(employeeRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.createTask(request));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when project does not exist")
    void testCreateTask_ProjectNotFound() {
        TaskRequest request = new TaskRequest("Title", "Desc", "HIGH", "TODO", LocalDate.now(), 1, 99);
        when(employeeRepository.findById(1)).thenReturn(Optional.of(employee));
        when(projectRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.createTask(request));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when title is empty")
    void testCreateTask_EmptyTitle() {
        TaskRequest request = new TaskRequest("", "Desc", "HIGH", "TODO", LocalDate.now(), 1, 1);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> taskService.createTask(request));
        assertEquals("Task title cannot be empty", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when priority is invalid")
    void testCreateTask_InvalidPriority() {
        TaskRequest request = new TaskRequest("Title", "Desc", "URGENT", "TODO", LocalDate.now(), 1, 1);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> taskService.createTask(request));
        assertEquals("Invalid priority: 'URGENT'. Priority must be one of: LOW, MEDIUM, HIGH", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when status is invalid")
    void testCreateTask_InvalidStatus() {
        TaskRequest request = new TaskRequest("Title", "Desc", "HIGH", "PENDING", LocalDate.now(), 1, 1);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> taskService.createTask(request));
        assertEquals("Invalid status: 'PENDING'. Status must be one of: TODO, IN_PROGRESS, COMPLETED", ex.getMessage());
    }

    @Test
    @DisplayName("Should update task successfully")
    void testUpdateTask_Success() {
        when(taskRepository.findById(1)).thenReturn(Optional.of(task1));
        when(taskRepository.save(any(Task.class))).thenReturn(task1);

        TaskRequest updateRequest = new TaskRequest(
                "Updated Setup Database", "Updated Desc", "MEDIUM", "IN_PROGRESS",
                LocalDate.now().plusDays(5), null, null
        );

        Task updated = taskService.updateTask(1, updateRequest);

        assertNotNull(updated);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Should delete task successfully when found")
    void testDeleteTask_Success() {
        when(taskRepository.findById(1)).thenReturn(Optional.of(task1));

        taskService.deleteTask(1);

        verify(taskRepository, times(1)).delete(task1);
    }
}
