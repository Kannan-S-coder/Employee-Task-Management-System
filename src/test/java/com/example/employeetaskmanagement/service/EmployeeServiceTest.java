package com.example.employeetaskmanagement.service;

import com.example.employeetaskmanagement.entity.Employee;
import com.example.employeetaskmanagement.exception.ResourceNotFoundException;
import com.example.employeetaskmanagement.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee1;
    private Employee employee2;

    @BeforeEach
    void setUp() {
        employee1 = new Employee("Alice Smith", "alice@example.com", "Engineering", "Software Engineer");
        employee1.setId(1);

        employee2 = new Employee("Bob Jones", "bob@example.com", "Marketing", "Marketing Specialist");
        employee2.setId(2);
    }

    @Test
    @DisplayName("Should return all employees")
    void testGetAllEmployees() {
        when(employeeRepository.findAll()).thenReturn(Arrays.asList(employee1, employee2));

        List<Employee> result = employeeService.getAllEmployees();

        assertEquals(2, result.size());
        assertEquals("Alice Smith", result.get(0).getName());
        assertEquals("Bob Jones", result.get(1).getName());
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return employee by ID when found")
    void testGetEmployeeById_Success() {
        when(employeeRepository.findById(1)).thenReturn(Optional.of(employee1));

        Employee result = employeeService.getEmployeeById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("alice@example.com", result.getEmail());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when employee ID not found")
    void testGetEmployeeById_NotFound() {
        when(employeeRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.getEmployeeById(99));
    }

    @Test
    @DisplayName("Should create employee successfully")
    void testCreateEmployee_Success() {
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee1);

        Employee saved = employeeService.createEmployee(employee1);

        assertNotNull(saved);
        assertEquals("Alice Smith", saved.getName());
        verify(employeeRepository, times(1)).save(employee1);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when creating employee with empty name")
    void testCreateEmployee_EmptyName() {
        Employee invalid = new Employee("", "valid@example.com", "HR", "Manager");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> employeeService.createEmployee(invalid)
        );
        assertEquals("Employee name cannot be empty", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when creating employee with empty email")
    void testCreateEmployee_EmptyEmail() {
        Employee invalid = new Employee("John", "   ", "HR", "Manager");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> employeeService.createEmployee(invalid)
        );
        assertEquals("Employee email cannot be empty", ex.getMessage());
    }

    @Test
    @DisplayName("Should update employee successfully")
    void testUpdateEmployee_Success() {
        when(employeeRepository.findById(1)).thenReturn(Optional.of(employee1));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee1);

        Employee updateDetails = new Employee("Alice Johnson", "alice.j@example.com", "Product", "Tech Lead");
        Employee updated = employeeService.updateEmployee(1, updateDetails);

        assertNotNull(updated);
        assertEquals("Alice Johnson", updated.getName());
        assertEquals("alice.j@example.com", updated.getEmail());
        assertEquals("Product", updated.getDepartment());
        assertEquals("Tech Lead", updated.getRole());
    }

    @Test
    @DisplayName("Should delete employee successfully when found")
    void testDeleteEmployee_Success() {
        when(employeeRepository.findById(1)).thenReturn(Optional.of(employee1));

        employeeService.deleteEmployee(1);

        verify(employeeRepository, times(1)).delete(employee1);
    }
}
