package com.example.employeetaskmanagement.controller;

import com.example.employeetaskmanagement.dto.TaskRequest;
import com.example.employeetaskmanagement.entity.Employee;
import com.example.employeetaskmanagement.entity.Project;
import com.example.employeetaskmanagement.entity.Task;
import com.example.employeetaskmanagement.exception.ResourceNotFoundException;
import com.example.employeetaskmanagement.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    private Task task1;
    private Employee employee;
    private Project project;

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
    @DisplayName("GET /api/tasks - Should return all tasks with 200 OK")
    void testGetAllTasks() throws Exception {
        when(taskService.getAllTasks(null, null, null, null)).thenReturn(Arrays.asList(task1));

        mockMvc.perform(get("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Setup Database")))
                .andExpect(jsonPath("$[0].employee.name", is("Alice Smith")))
                .andExpect(jsonPath("$[0].project.name", is("Task System")));
    }

    @Test
    @DisplayName("GET /api/tasks?status=TODO&priority=HIGH - Should filter tasks")
    void testGetTasks_WithFilters() throws Exception {
        when(taskService.getAllTasks("TODO", "HIGH", 1, 1)).thenReturn(Arrays.asList(task1));

        mockMvc.perform(get("/api/tasks")
                        .param("status", "TODO")
                        .param("priority", "HIGH")
                        .param("employeeId", "1")
                        .param("projectId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].priority", is("HIGH")))
                .andExpect(jsonPath("$[0].status", is("TODO")));
    }

    @Test
    @DisplayName("GET /api/tasks/1 - Should return task by ID with 200 OK")
    void testGetTaskById_Success() throws Exception {
        when(taskService.getTaskById(1)).thenReturn(task1);

        mockMvc.perform(get("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Setup Database")));
    }

    @Test
    @DisplayName("GET /api/tasks/99 - Should return 404 Not Found when non-existent")
    void testGetTaskById_NotFound() throws Exception {
        when(taskService.getTaskById(99))
                .thenThrow(new ResourceNotFoundException("Task not found with id: 99"));

        mockMvc.perform(get("/api/tasks/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/tasks - Should return 201 Created on valid input")
    void testCreateTask_Success() throws Exception {
        TaskRequest request = new TaskRequest("Setup Database", "Configure MySQL", "HIGH", "TODO", LocalDate.now().plusWeeks(1), 1, 1);
        when(taskService.createTask(any(TaskRequest.class))).thenReturn(task1);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Setup Database")));
    }

    @Test
    @DisplayName("POST /api/tasks - Should return 400 Bad Request on validation error")
    void testCreateTask_ValidationError() throws Exception {
        TaskRequest request = new TaskRequest("", "Configure MySQL", "HIGH", "TODO", LocalDate.now(), 1, 1);
        when(taskService.createTask(any(TaskRequest.class)))
                .thenThrow(new IllegalArgumentException("Task title cannot be empty"));

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Task title cannot be empty")));
    }

    @Test
    @DisplayName("PUT /api/tasks/1 - Should return 200 OK on update")
    void testUpdateTask_Success() throws Exception {
        TaskRequest request = new TaskRequest("Updated Title", "Updated Desc", "LOW", "IN_PROGRESS", LocalDate.now(), 1, 1);
        when(taskService.updateTask(eq(1), any(TaskRequest.class))).thenReturn(task1);

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Setup Database")));
    }

    @Test
    @DisplayName("DELETE /api/tasks/1 - Should return 204 No Content")
    void testDeleteTask_Success() throws Exception {
        doNothing().when(taskService).deleteTask(1);

        mockMvc.perform(delete("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/tasks/99 - Should return 404 Not Found when non-existent")
    void testDeleteTask_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Task not found with id: 99"))
                .when(taskService).deleteTask(99);

        mockMvc.perform(delete("/api/tasks/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
