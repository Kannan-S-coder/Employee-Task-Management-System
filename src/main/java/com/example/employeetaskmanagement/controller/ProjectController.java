package com.example.employeetaskmanagement.controller;

import com.example.employeetaskmanagement.entity.Project;
import com.example.employeetaskmanagement.service.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * REST Controller exposing CRUD endpoints for Project management.
 */
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    // Constructor injection
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * GET /api/projects - Retrieve all projects.
     * Returns HTTP 200 (OK) with list of projects.
     */
    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        List<Project> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    /**
     * GET /api/projects/{id} - Retrieve a specific project by ID.
     * Returns HTTP 200 (OK) or HTTP 404 (Not Found).
     */
    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable Integer id) {
        Project project = projectService.getProjectById(id);
        return ResponseEntity.ok(project);
    }

    /**
     * POST /api/projects - Create a new project.
     * Returns HTTP 201 (Created) with created project details or HTTP 400.
     */
    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody Project project) {
        Project createdProject = projectService.createProject(project);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
    }

    /**
     * PUT /api/projects/{id} - Update an existing project.
     * Returns HTTP 200 (OK) with updated project details, HTTP 400, or HTTP 404.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(
            @PathVariable Integer id,
            @RequestBody Project projectDetails) {
        Project updatedProject = projectService.updateProject(id, projectDetails);
        return ResponseEntity.ok(updatedProject);
    }

    /**
     * DELETE /api/projects/{id} - Delete a project by ID.
     * Returns HTTP 204 (No Content) on success or HTTP 404.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Integer id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Handler for validation errors (e.g., empty name, invalid date range, invalid status).
     * Returns HTTP 400 (Bad Request).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
    }
}
