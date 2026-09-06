package com.example.employeetaskmanagement.controller;

import com.example.employeetaskmanagement.entity.Employee;
import com.example.employeetaskmanagement.exception.ResourceNotFoundException;
import com.example.employeetaskmanagement.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    private Employee employee1;

    @BeforeEach
    void setUp() {
        employee1 = new Employee("Alice Smith", "alice@example.com", "Engineering", "Software Engineer");
        employee1.setId(1);
    }

    @Test
    @DisplayName("GET /api/employees - Should return list of employees with 200 OK")
    void testGetAllEmployees() throws Exception {
        when(employeeService.getAllEmployees()).thenReturn(Arrays.asList(employee1));

        mockMvc.perform(get("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Alice Smith")))
                .andExpect(jsonPath("$[0].email", is("alice@example.com")));
    }

    @Test
    @DisplayName("GET /api/employees/1 - Should return employee with 200 OK")
    void testGetEmployeeById_Success() throws Exception {
        when(employeeService.getEmployeeById(1)).thenReturn(employee1);

        mockMvc.perform(get("/api/employees/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Alice Smith")));
    }

    @Test
    @DisplayName("GET /api/employees/99 - Should return 404 Not Found when non-existent")
    void testGetEmployeeById_NotFound() throws Exception {
        when(employeeService.getEmployeeById(99))
                .thenThrow(new ResourceNotFoundException("Employee not found with id: 99"));

        mockMvc.perform(get("/api/employees/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/employees - Should return 201 Created on valid input")
    void testCreateEmployee_Success() throws Exception {
        when(employeeService.createEmployee(any(Employee.class))).thenReturn(employee1);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Alice Smith")));
    }

    @Test
    @DisplayName("POST /api/employees - Should return 400 Bad Request when validation fails")
    void testCreateEmployee_ValidationFailure() throws Exception {
        when(employeeService.createEmployee(any(Employee.class)))
                .thenThrow(new IllegalArgumentException("Employee name cannot be empty"));

        Employee invalid = new Employee("", "valid@example.com", "HR", "Lead");

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Employee name cannot be empty")));
    }

    @Test
    @DisplayName("PUT /api/employees/1 - Should return 200 OK on update")
    void testUpdateEmployee_Success() throws Exception {
        when(employeeService.updateEmployee(eq(1), any(Employee.class))).thenReturn(employee1);

        mockMvc.perform(put("/api/employees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Alice Smith")));
    }

    @Test
    @DisplayName("DELETE /api/employees/1 - Should return 204 No Content")
    void testDeleteEmployee_Success() throws Exception {
        doNothing().when(employeeService).deleteEmployee(1);

        mockMvc.perform(delete("/api/employees/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/employees/99 - Should return 404 Not Found when non-existent")
    void testDeleteEmployee_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Employee not found with id: 99"))
                .when(employeeService).deleteEmployee(99);

        mockMvc.perform(delete("/api/employees/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
