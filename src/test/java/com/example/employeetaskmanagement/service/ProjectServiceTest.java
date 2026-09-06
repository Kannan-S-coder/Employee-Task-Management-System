package com.example.employeetaskmanagement.service;

import com.example.employeetaskmanagement.entity.Project;
import com.example.employeetaskmanagement.exception.ResourceNotFoundException;
import com.example.employeetaskmanagement.repository.ProjectRepository;
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
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    private Project project1;
    private Project project2;

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

        project2 = new Project(
                "Mobile App Revamp",
                "Modernizing iOS/Android apps",
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2026, 12, 31),
                "PLANNED"
        );
        project2.setId(2);
    }

    @Test
    @DisplayName("Should return all projects")
    void testGetAllProjects() {
        when(projectRepository.findAll()).thenReturn(Arrays.asList(project1, project2));

        List<Project> result = projectService.getAllProjects();

        assertEquals(2, result.size());
        assertEquals("Task Management System", result.get(0).getName());
        verify(projectRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return project by ID when found")
    void testGetProjectById_Success() {
        when(projectRepository.findById(1)).thenReturn(Optional.of(project1));

        Project result = projectService.getProjectById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Task Management System", result.getName());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when project ID not found")
    void testGetProjectById_NotFound() {
        when(projectRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> projectService.getProjectById(99));
    }

    @Test
    @DisplayName("Should create project successfully with valid data")
    void testCreateProject_Success() {
        when(projectRepository.save(any(Project.class))).thenReturn(project1);

        Project saved = projectService.createProject(project1);

        assertNotNull(saved);
        assertEquals("Task Management System", saved.getName());
        verify(projectRepository, times(1)).save(project1);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when creating project with empty name")
    void testCreateProject_EmptyName() {
        Project invalid = new Project("", "Desc", LocalDate.now(), LocalDate.now().plusDays(10), "PLANNED");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> projectService.createProject(invalid)
        );
        assertEquals("Project name cannot be empty", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when endDate is before startDate")
    void testCreateProject_EndDateBeforeStartDate() {
        Project invalid = new Project(
                "Invalid Project",
                "Desc",
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 5, 1), // Before startDate
                "PLANNED"
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> projectService.createProject(invalid)
        );
        assertEquals("Project end date cannot be before start date", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when status is invalid")
    void testCreateProject_InvalidStatus() {
        Project invalid = new Project(
                "Invalid Status Project",
                "Desc",
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 6, 1),
                "UNKNOWN_STATUS"
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> projectService.createProject(invalid)
        );
        assertEquals("Invalid status: 'UNKNOWN_STATUS'. Status must be one of: PLANNED, IN_PROGRESS, COMPLETED", ex.getMessage());
    }

    @Test
    @DisplayName("Should update project successfully")
    void testUpdateProject_Success() {
        when(projectRepository.findById(1)).thenReturn(Optional.of(project1));
        when(projectRepository.save(any(Project.class))).thenReturn(project1);

        Project updatedDetails = new Project(
                "Updated Project Name",
                "Updated Description",
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 8, 31),
                "COMPLETED"
        );

        Project updated = projectService.updateProject(1, updatedDetails);

        assertNotNull(updated);
        assertEquals("Updated Project Name", updated.getName());
        assertEquals("COMPLETED", updated.getStatus());
    }

    @Test
    @DisplayName("Should delete project successfully when found")
    void testDeleteProject_Success() {
        when(projectRepository.findById(1)).thenReturn(Optional.of(project1));

        projectService.deleteProject(1);

        verify(projectRepository, times(1)).delete(project1);
    }
}
