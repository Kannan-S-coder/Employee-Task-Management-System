package com.example.employeetaskmanagement.service;

import com.example.employeetaskmanagement.entity.Project;
import com.example.employeetaskmanagement.exception.ResourceNotFoundException;
import com.example.employeetaskmanagement.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Service layer providing business logic for Project management.
 */
@Service
public class ProjectService {

    private static final List<String> VALID_STATUSES = Arrays.asList("PLANNED", "IN_PROGRESS", "COMPLETED");

    private final ProjectRepository projectRepository;

    // Constructor injection
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    /**
     * Retrieve all projects from the database.
     */
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    /**
     * Retrieve a project by ID or throw ResourceNotFoundException if not found.
     */
    public Project getProjectById(Integer id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
    }

    /**
     * Create a new project after validation.
     */
    public Project createProject(Project project) {
        validateProject(project);

        // Normalize status to uppercase
        if (project.getStatus() == null || project.getStatus().trim().isEmpty()) {
            project.setStatus("PLANNED");
        } else {
            project.setStatus(project.getStatus().trim().toUpperCase());
        }

        return projectRepository.save(project);
    }

    /**
     * Update an existing project after validation.
     */
    public Project updateProject(Integer id, Project projectDetails) {
        Project existingProject = getProjectById(id);
        validateProject(projectDetails);

        existingProject.setName(projectDetails.getName().trim());
        existingProject.setDescription(projectDetails.getDescription());
        existingProject.setStartDate(projectDetails.getStartDate());
        existingProject.setEndDate(projectDetails.getEndDate());

        if (projectDetails.getStatus() != null && !projectDetails.getStatus().trim().isEmpty()) {
            existingProject.setStatus(projectDetails.getStatus().trim().toUpperCase());
        }

        return projectRepository.save(existingProject);
    }

    /**
     * Delete a project by ID after ensuring it exists.
     */
    public void deleteProject(Integer id) {
        Project existingProject = getProjectById(id);
        projectRepository.delete(existingProject);
    }

    /**
     * Business validation rules for projects:
     * 1. Project name cannot be null or empty.
     * 2. If both startDate and endDate exist, endDate cannot be before startDate.
     * 3. Status must be one of PLANNED, IN_PROGRESS, COMPLETED (if provided).
     */
    private void validateProject(Project project) {
        if (project == null) {
            throw new IllegalArgumentException("Project data cannot be null");
        }

        if (project.getName() == null || project.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be empty");
        }

        if (project.getStartDate() != null && project.getEndDate() != null) {
            if (project.getEndDate().isBefore(project.getStartDate())) {
                throw new IllegalArgumentException("Project end date cannot be before start date");
            }
        }

        if (project.getStatus() != null && !project.getStatus().trim().isEmpty()) {
            String upperStatus = project.getStatus().trim().toUpperCase();
            if (!VALID_STATUSES.contains(upperStatus)) {
                throw new IllegalArgumentException("Invalid status: '" + project.getStatus() +
                        "'. Status must be one of: PLANNED, IN_PROGRESS, COMPLETED");
            }
        }
    }
}
