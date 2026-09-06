package com.example.employeetaskmanagement.service;

import com.example.employeetaskmanagement.dto.TaskRequest;
import com.example.employeetaskmanagement.entity.Employee;
import com.example.employeetaskmanagement.entity.Project;
import com.example.employeetaskmanagement.entity.Task;
import com.example.employeetaskmanagement.exception.ResourceNotFoundException;
import com.example.employeetaskmanagement.repository.EmployeeRepository;
import com.example.employeetaskmanagement.repository.ProjectRepository;
import com.example.employeetaskmanagement.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Service layer providing business logic for Task management.
 */
@Service
public class TaskService {

    private static final List<String> VALID_PRIORITIES = Arrays.asList("LOW", "MEDIUM", "HIGH");
    private static final List<String> VALID_STATUSES = Arrays.asList("TODO", "IN_PROGRESS", "COMPLETED");

    private final TaskRepository taskRepository;
    private final EmployeeRepository employeeRepository;
    private final ProjectRepository projectRepository;

    // Constructor injection
    public TaskService(TaskRepository taskRepository,
                       EmployeeRepository employeeRepository,
                       ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.employeeRepository = employeeRepository;
        this.projectRepository = projectRepository;
    }

    /**
     * Retrieve tasks with optional filtering by status, priority, employeeId, and projectId.
     */
    public List<Task> getAllTasks(String status, String priority, Integer employeeId, Integer projectId) {
        String normalizedStatus = (status != null && !status.trim().isEmpty()) ? status.trim().toUpperCase() : null;
        String normalizedPriority = (priority != null && !priority.trim().isEmpty()) ? priority.trim().toUpperCase() : null;

        if (normalizedStatus == null && normalizedPriority == null && employeeId == null && projectId == null) {
            return taskRepository.findAll();
        }

        return taskRepository.findTasksWithFilters(normalizedStatus, normalizedPriority, employeeId, projectId);
    }

    /**
     * Retrieve a task by ID or throw ResourceNotFoundException.
     */
    public Task getTaskById(Integer id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
    }

    /**
     * Create a new task after validating rules, employee existence, and project existence.
     */
    public Task createTask(TaskRequest request) {
        validateTaskRequest(request);

        if (request.getEmployeeId() == null) {
            throw new IllegalArgumentException("Employee ID is required");
        }
        if (request.getProjectId() == null) {
            throw new IllegalArgumentException("Project ID is required");
        }

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + request.getEmployeeId()));

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + request.getProjectId()));

        String priority = (request.getPriority() != null && !request.getPriority().trim().isEmpty())
                ? request.getPriority().trim().toUpperCase()
                : "MEDIUM";

        String status = (request.getStatus() != null && !request.getStatus().trim().isEmpty())
                ? request.getStatus().trim().toUpperCase()
                : "TODO";

        Task task = new Task(
                request.getTitle().trim(),
                request.getDescription(),
                priority,
                status,
                request.getDeadline(),
                employee,
                project
        );

        return taskRepository.save(task);
    }

    /**
     * Update an existing task.
     */
    public Task updateTask(Integer id, TaskRequest request) {
        Task existingTask = getTaskById(id);
        validateTaskRequest(request);

        existingTask.setTitle(request.getTitle().trim());
        existingTask.setDescription(request.getDescription());
        existingTask.setDeadline(request.getDeadline());

        if (request.getPriority() != null && !request.getPriority().trim().isEmpty()) {
            existingTask.setPriority(request.getPriority().trim().toUpperCase());
        }

        if (request.getStatus() != null && !request.getStatus().trim().isEmpty()) {
            existingTask.setStatus(request.getStatus().trim().toUpperCase());
        }

        if (request.getEmployeeId() != null) {
            Employee employee = employeeRepository.findById(request.getEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + request.getEmployeeId()));
            existingTask.setEmployee(employee);
        }

        if (request.getProjectId() != null) {
            Project project = projectRepository.findById(request.getProjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + request.getProjectId()));
            existingTask.setProject(project);
        }

        return taskRepository.save(existingTask);
    }

    /**
     * Delete a task by ID after ensuring it exists.
     */
    public void deleteTask(Integer id) {
        Task existingTask = getTaskById(id);
        taskRepository.delete(existingTask);
    }

    /**
     * Basic validation for task request fields.
     */
    private void validateTaskRequest(TaskRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Task data cannot be null");
        }

        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be empty");
        }

        if (request.getPriority() != null && !request.getPriority().trim().isEmpty()) {
            String upperPriority = request.getPriority().trim().toUpperCase();
            if (!VALID_PRIORITIES.contains(upperPriority)) {
                throw new IllegalArgumentException("Invalid priority: '" + request.getPriority() +
                        "'. Priority must be one of: LOW, MEDIUM, HIGH");
            }
        }

        if (request.getStatus() != null && !request.getStatus().trim().isEmpty()) {
            String upperStatus = request.getStatus().trim().toUpperCase();
            if (!VALID_STATUSES.contains(upperStatus)) {
                throw new IllegalArgumentException("Invalid status: '" + request.getStatus() +
                        "'. Status must be one of: TODO, IN_PROGRESS, COMPLETED");
            }
        }
    }
}
