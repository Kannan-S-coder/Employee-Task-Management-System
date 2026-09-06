package com.example.employeetaskmanagement.repository;

import com.example.employeetaskmanagement.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for Task entity.
 * Provides standard CRUD operations and custom JPQL query for multi-attribute filtering.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

    /**
     * Retrieve tasks matching any combination of optional filter parameters:
     * status, priority, employeeId, and projectId.
     */
    @Query("SELECT t FROM Task t WHERE " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:priority IS NULL OR t.priority = :priority) AND " +
           "(:employeeId IS NULL OR t.employee.id = :employeeId) AND " +
           "(:projectId IS NULL OR t.project.id = :projectId)")
    List<Task> findTasksWithFilters(
            @Param("status") String status,
            @Param("priority") String priority,
            @Param("employeeId") Integer employeeId,
            @Param("projectId") Integer projectId);
}
