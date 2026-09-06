package com.example.employeetaskmanagement.repository;

import com.example.employeetaskmanagement.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for Project entity.
 * Provides standard CRUD operations (save, findById, findAll, deleteById, etc.).
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {
}
