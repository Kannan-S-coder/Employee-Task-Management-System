package com.example.employeetaskmanagement.controller;

import com.example.employeetaskmanagement.entity.Project;
import com.example.employeetaskmanagement.exception.ResourceNotFoundException;
import com.example.employeetaskmanagement.service.ProjectService;
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

@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @Autowired
    private ObjectMapper objectMapper;

    private Project project1;

    @BeforeEach
    void setUp() {
        project1 = new Project(
                "Task Management System",
                "Internal task tracking portal",
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 6, 30),
                "IN_PROGRESS"
        );
        project1.setId(1);
    }

    @Test
    @DisplayName("GET /api/projects - Should return list of projects with 200 OK")
    void testGetAllProjects() throws Exception {
        when(projectService.getAllProjects()).thenReturn(Arrays.asList(project1));

        mockMvc.perform(get("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Task Management System")))
                .andExpect(jsonPath("$[0].status", is("IN_PROGRESS")));
    }

    @Test
    @DisplayName("GET /api/projects/1 - Should return project with 200 OK")
    void testGetProjectById_Success() throws Exception {
        when(projectService.getProjectById(1)).thenReturn(project1);

        mockMvc.perform(get("/api/projects/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Task Management System")));
    }

    @Test
    @DisplayName("GET /api/projects/99 - Should return 404 Not Found when non-existent")
    void testGetProjectById_NotFound() throws Exception {
        when(projectService.getProjectById(99))
                .thenThrow(new ResourceNotFoundException("Project not found with id: 99"));

        mockMvc.perform(get("/api/projects/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/projects - Should return 201 Created on valid input")
    void testCreateProject_Success() throws Exception {
        when(projectService.createProject(any(Project.class))).thenReturn(project1);

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(project1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Task Management System")));
    }

    @Test
    @DisplayName("POST /api/projects - Should return 400 Bad Request when endDate is before startDate")
    void testCreateProject_ValidationFailure() throws Exception {
        when(projectService.createProject(any(Project.class)))
                .thenThrow(new IllegalArgumentException("Project end date cannot be before start date"));

        Project invalid = new Project(
                "Invalid Project",
                "Desc",
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 5, 1),
                "PLANNED"
        );

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Project end date cannot be before start date")));
    }

    @Test
    @DisplayName("PUT /api/projects/1 - Should return 200 OK on update")
    void testUpdateProject_Success() throws Exception {
        when(projectService.updateProject(eq(1), any(Project.class))).thenReturn(project1);

        mockMvc.perform(put("/api/projects/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(project1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Task Management System")));
    }

    @Test
    @DisplayName("DELETE /api/projects/1 - Should return 204 No Content")
    void testDeleteProject_Success() throws Exception {
        doNothing().when(projectService).deleteProject(1);

        mockMvc.perform(delete("/api/projects/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/projects/99 - Should return 404 Not Found when non-existent")
    void testDeleteProject_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Project not found with id: 99"))
                .when(projectService).deleteProject(99);

        mockMvc.perform(delete("/api/projects/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
