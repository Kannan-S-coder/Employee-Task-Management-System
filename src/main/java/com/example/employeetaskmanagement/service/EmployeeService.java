package com.example.employeetaskmanagement.service;

import com.example.employeetaskmanagement.entity.Employee;
import com.example.employeetaskmanagement.exception.ResourceNotFoundException;
import com.example.employeetaskmanagement.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer providing business logic for Employee management.
 */
@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    // Constructor injection (recommended Spring best practice)
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    /**
     * Retrieve all employees from the database.
     */
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    /**
     * Retrieve an employee by ID or throw ResourceNotFoundException if not found.
     */
    public Employee getEmployeeById(Integer id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
    }

    /**
     * Create and save a new employee after basic validation.
     */
    public Employee createEmployee(Employee employee) {
        validateEmployee(employee);
        return employeeRepository.save(employee);
    }

    /**
     * Update an existing employee's details.
     */
    public Employee updateEmployee(Integer id, Employee employeeDetails) {
        Employee existingEmployee = getEmployeeById(id);
        validateEmployee(employeeDetails);

        existingEmployee.setName(employeeDetails.getName().trim());
        existingEmployee.setEmail(employeeDetails.getEmail().trim());
        existingEmployee.setDepartment(employeeDetails.getDepartment());
        existingEmployee.setRole(employeeDetails.getRole());

        return employeeRepository.save(existingEmployee);
    }

    /**
     * Delete an employee by ID after ensuring they exist.
     */
    public void deleteEmployee(Integer id) {
        Employee existingEmployee = getEmployeeById(id);
        employeeRepository.delete(existingEmployee);
    }

    /**
     * Simple validation helper ensuring name and email are present and non-blank.
     */
    private void validateEmployee(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee data cannot be null");
        }
        if (employee.getName() == null || employee.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Employee name cannot be empty");
        }
        if (employee.getEmail() == null || employee.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Employee email cannot be empty");
        }
    }
}
