package com.example.employeetaskmanagement.dto;

import java.time.LocalDate;

/**
 * Request payload for creating or updating a Task.
 */
public class TaskRequest {

    private String title;
    private String description;
    private String priority;
    private String status;
    private LocalDate deadline;
    private Integer employeeId;
    private Integer projectId;

    public TaskRequest() {
    }

    public TaskRequest(String title, String description, String priority, String status,
                       LocalDate deadline, Integer employeeId, Integer projectId) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.deadline = deadline;
        this.employeeId = employeeId;
        this.projectId = projectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    @Override
    public String toString() {
        return "TaskRequest{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", priority='" + priority + '\'' +
                ", status='" + status + '\'' +
                ", deadline=" + deadline +
                ", employeeId=" + employeeId +
                ", projectId=" + projectId +
                '}';
    }
}
