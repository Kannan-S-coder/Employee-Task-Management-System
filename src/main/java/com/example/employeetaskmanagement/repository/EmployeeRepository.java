package com.example.employeetaskmanagement.repository;

import com.example.employeetaskmanagement.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for Employee entity.
 * Provides out-of-the-box CRUD operations (save, findById, findAll, deleteById, etc.).
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
}
