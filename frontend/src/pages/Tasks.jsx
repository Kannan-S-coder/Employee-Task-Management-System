import React, { useState, useEffect } from 'react';
import {
  getTasks,
  createTask,
  updateTask,
  deleteTask,
} from '../services/taskService';
import { getEmployees } from '../services/employeeService';
import { getProjects } from '../services/projectService';

function Tasks() {
  const [tasks, setTasks] = useState([]);
  const [employees, setEmployees] = useState([]);
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState(null);

  // Filter State
  const [filters, setFilters] = useState({
    status: '',
    priority: '',
    employeeId: '',
    projectId: '',
  });

  // Form State
  const initialFormState = {
    title: '',
    description: '',
    priority: 'MEDIUM',
    status: 'TODO',
    deadline: '',
    employeeId: '',
    projectId: '',
  };
  const [formData, setFormData] = useState(initialFormState);
  const [formErrors, setFormErrors] = useState({});
  const [editingId, setEditingId] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  // Load dropdown data and tasks on mount
  useEffect(() => {
    loadInitialData();
  }, []);

  // Fetch tasks when filters change
  useEffect(() => {
    fetchTasks();
  }, [filters]);

  const loadInitialData = async () => {
    try {
      setLoading(true);
      setError(null);
      const [empsData, projsData] = await Promise.all([
        getEmployees().catch(() => []),
        getProjects().catch(() => []),
      ]);
      setEmployees(empsData);
      setProjects(projsData);
    } catch (err) {
      console.error('Failed to load initial data:', err);
    }
  };

  const fetchTasks = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await getTasks(filters);
      setTasks(data);
    } catch (err) {
      console.error('Failed to fetch tasks:', err);
      setError(
        err.response?.data?.message ||
        'Unable to connect to the backend server. Please ensure Spring Boot is running on port 8080.'
      );
    } finally {
      setLoading(false);
    }
  };

  const showSuccess = (msg) => {
    setSuccessMessage(msg);
    setTimeout(() => {
      setSuccessMessage(null);
    }, 4000);
  };

  // Form Validation
  const validateForm = () => {
    const errors = {};
    if (!formData.title || !formData.title.trim()) {
      errors.title = 'Task title is required';
    }
    if (!formData.priority) {
      errors.priority = 'Priority is required';
    }
    if (!formData.status) {
      errors.status = 'Status is required';
    }
    if (!formData.deadline) {
      errors.deadline = 'Deadline date is required';
    }
    if (!formData.employeeId) {
      errors.employeeId = 'Assigned employee is required';
    }
    if (!formData.projectId) {
      errors.projectId = 'Assigned project is required';
    }

    setFormErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    if (formErrors[name]) {
      setFormErrors((prev) => ({ ...prev, [name]: '' }));
    }
  };

  const handleFilterChange = (e) => {
    const { name, value } = e.target;
    setFilters((prev) => ({ ...prev, [name]: value }));
  };

  const handleClearFilters = () => {
    setFilters({
      status: '',
      priority: '',
      employeeId: '',
      projectId: '',
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;

    try {
      setSubmitting(true);
      setError(null);

      const payload = {
        title: formData.title.trim(),
        description: formData.description ? formData.description.trim() : '',
        priority: formData.priority,
        status: formData.status,
        deadline: formData.deadline,
        employeeId: Number(formData.employeeId),
        projectId: Number(formData.projectId),
      };

      if (editingId) {
        await updateTask(editingId, payload);
        showSuccess('Task updated successfully!');
      } else {
        await createTask(payload);
        showSuccess('Task created successfully!');
      }

      // Reset form and refresh list
      setFormData(initialFormState);
      setEditingId(null);
      setFormErrors({});
      await fetchTasks();
    } catch (err) {
      console.error('Failed to save task:', err);
      setError(
        err.response?.data?.error ||
        err.response?.data?.message ||
        'Failed to save task. Please check the input and try again.'
      );
    } finally {
      setSubmitting(false);
    }
  };

  const handleEdit = (task) => {
    setEditingId(task.id);
    setFormData({
      title: task.title || '',
      description: task.description || '',
      priority: task.priority || 'MEDIUM',
      status: task.status || 'TODO',
      deadline: task.deadline || '',
      employeeId: task.employee?.id ? String(task.employee.id) : '',
      projectId: task.project?.id ? String(task.project.id) : '',
    });
    setFormErrors({});
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleCancelEdit = () => {
    setEditingId(null);
    setFormData(initialFormState);
    setFormErrors({});
  };

  const handleDelete = async (id, title) => {
    const confirmed = window.confirm(`Are you sure you want to delete task "${title}"?`);
    if (!confirmed) return;

    try {
      setError(null);
      await deleteTask(id);
      showSuccess(`Task "${title}" deleted successfully.`);
      if (editingId === id) {
        handleCancelEdit();
      }
      await fetchTasks();
    } catch (err) {
      console.error('Failed to delete task:', err);
      setError(
        err.response?.data?.error ||
        err.response?.data?.message ||
        'Failed to delete task.'
      );
    }
  };

  // Badge helpers
  const getPriorityBadgeClass = (priority) => {
    switch (priority) {
      case 'HIGH':
        return 'priority-badge priority-high';
      case 'LOW':
        return 'priority-badge priority-low';
      case 'MEDIUM':
      default:
        return 'priority-badge priority-medium';
    }
  };

  const getStatusBadgeClass = (status) => {
    switch (status) {
      case 'IN_PROGRESS':
        return 'status-badge status-in-progress';
      case 'COMPLETED':
        return 'status-badge status-completed';
      case 'TODO':
      default:
        return 'status-badge status-todo';
    }
  };

  return (
    <div className="page-container">
      <div className="page-header">
        <h1>Task Management</h1>
        <p className="page-subtitle">Assign, prioritize, and track team tasks across company projects.</p>
      </div>

      {/* Notifications */}
      {successMessage && (
        <div className="alert alert-success">
          <span>✅ {successMessage}</span>
          <button className="alert-close" onClick={() => setSuccessMessage(null)}>×</button>
        </div>
      )}

      {error && (
        <div className="alert alert-error">
          <span>⚠️ {error}</span>
          <button className="alert-close" onClick={() => setError(null)}>×</button>
        </div>
      )}

      {/* Task Form Card */}
      <div className="form-card">
        <div className="card-header">
          <h2>{editingId ? '✏️ Edit Task' : '➕ Create New Task'}</h2>
          {editingId && (
            <span className="editing-badge">Editing ID #{editingId}</span>
          )}
        </div>

        <form onSubmit={handleSubmit} className="task-form" noValidate>
          <div className="form-grid">
            <div className="form-group">
              <label htmlFor="title">Task Title *</label>
              <input
                type="text"
                id="title"
                name="title"
                placeholder="e.g. Implement User Authentication"
                value={formData.title}
                onChange={handleInputChange}
                className={formErrors.title ? 'input-error' : ''}
              />
              {formErrors.title && <span className="error-text">{formErrors.title}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="priority">Priority *</label>
              <select
                id="priority"
                name="priority"
                value={formData.priority}
                onChange={handleInputChange}
                className={formErrors.priority ? 'input-error' : ''}
              >
                <option value="LOW">LOW</option>
                <option value="MEDIUM">MEDIUM</option>
                <option value="HIGH">HIGH</option>
              </select>
              {formErrors.priority && <span className="error-text">{formErrors.priority}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="status">Status *</label>
              <select
                id="status"
                name="status"
                value={formData.status}
                onChange={handleInputChange}
                className={formErrors.status ? 'input-error' : ''}
              >
                <option value="TODO">TODO</option>
                <option value="IN_PROGRESS">IN_PROGRESS</option>
                <option value="COMPLETED">COMPLETED</option>
              </select>
              {formErrors.status && <span className="error-text">{formErrors.status}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="deadline">Deadline *</label>
              <input
                type="date"
                id="deadline"
                name="deadline"
                value={formData.deadline}
                onChange={handleInputChange}
                className={formErrors.deadline ? 'input-error' : ''}
              />
              {formErrors.deadline && <span className="error-text">{formErrors.deadline}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="employeeId">Assign Employee *</label>
              <select
                id="employeeId"
                name="employeeId"
                value={formData.employeeId}
                onChange={handleInputChange}
                className={formErrors.employeeId ? 'input-error' : ''}
              >
                <option value="">-- Select Employee --</option>
                {employees.map((emp) => (
                  <option key={emp.id} value={emp.id}>
                    {emp.name} ({emp.department || 'Staff'})
                  </option>
                ))}
              </select>
              {formErrors.employeeId && <span className="error-text">{formErrors.employeeId}</span>}
              {employees.length === 0 && (
                <span className="hint-text">No employees available. Add employees first.</span>
              )}
            </div>

            <div className="form-group">
              <label htmlFor="projectId">Assign Project *</label>
              <select
                id="projectId"
                name="projectId"
                value={formData.projectId}
                onChange={handleInputChange}
                className={formErrors.projectId ? 'input-error' : ''}
              >
                <option value="">-- Select Project --</option>
                {projects.map((proj) => (
                  <option key={proj.id} value={proj.id}>
                    {proj.name}
                  </option>
                ))}
              </select>
              {formErrors.projectId && <span className="error-text">{formErrors.projectId}</span>}
              {projects.length === 0 && (
                <span className="hint-text">No projects available. Add projects first.</span>
              )}
            </div>
          </div>

          <div className="form-group form-group-full">
            <label htmlFor="description">Description</label>
            <textarea
              id="description"
              name="description"
              rows="3"
              placeholder="Task details, requirements, acceptance criteria..."
              value={formData.description}
              onChange={handleInputChange}
            ></textarea>
          </div>

          <div className="form-actions">
            <button type="submit" className="btn btn-primary" disabled={submitting}>
              {submitting ? 'Saving...' : editingId ? 'Update Task' : 'Create Task'}
            </button>
            {editingId && (
              <button
                type="button"
                className="btn btn-secondary"
                onClick={handleCancelEdit}
                disabled={submitting}
              >
                Cancel
              </button>
            )}
          </div>
        </form>
      </div>

      {/* Filters Card */}
      <div className="filters-card">
        <div className="filters-header">
          <h3>🔍 Filter Tasks</h3>
          <button className="btn btn-xs btn-outline" onClick={handleClearFilters}>
            Clear Filters
          </button>
        </div>
        <div className="filters-grid">
          <div className="filter-group">
            <label>Status</label>
            <select name="status" value={filters.status} onChange={handleFilterChange}>
              <option value="">All Statuses</option>
              <option value="TODO">TODO</option>
              <option value="IN_PROGRESS">IN_PROGRESS</option>
              <option value="COMPLETED">COMPLETED</option>
            </select>
          </div>

          <div className="filter-group">
            <label>Priority</label>
            <select name="priority" value={filters.priority} onChange={handleFilterChange}>
              <option value="">All Priorities</option>
              <option value="LOW">LOW</option>
              <option value="MEDIUM">MEDIUM</option>
              <option value="HIGH">HIGH</option>
            </select>
          </div>

          <div className="filter-group">
            <label>Employee</label>
            <select name="employeeId" value={filters.employeeId} onChange={handleFilterChange}>
              <option value="">All Employees</option>
              {employees.map((emp) => (
                <option key={emp.id} value={emp.id}>
                  {emp.name}
                </option>
              ))}
            </select>
          </div>

          <div className="filter-group">
            <label>Project</label>
            <select name="projectId" value={filters.projectId} onChange={handleFilterChange}>
              <option value="">All Projects</option>
              {projects.map((proj) => (
                <option key={proj.id} value={proj.id}>
                  {proj.name}
                </option>
              ))}
            </select>
          </div>
        </div>
      </div>

      {/* Tasks Table Card */}
      <div className="table-card">
        <div className="card-header">
          <h2>✅ Tasks List ({tasks.length})</h2>
          <button className="btn btn-sm btn-outline" onClick={fetchTasks} title="Refresh tasks list">
            🔄 Refresh
          </button>
        </div>

        {loading ? (
          <div className="table-loading">
            <div className="spinner"></div>
            <p>Loading tasks...</p>
          </div>
        ) : tasks.length === 0 ? (
          <div className="table-empty">
            <p>
              {filters.status || filters.priority || filters.employeeId || filters.projectId
                ? 'No tasks match the selected filters.'
                : 'No tasks found. Use the form above to create your first task.'}
            </p>
          </div>
        ) : (
          <div className="table-responsive">
            <table className="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Title</th>
                  <th>Employee</th>
                  <th>Project</th>
                  <th>Priority</th>
                  <th>Status</th>
                  <th>Deadline</th>
                  <th className="th-actions">Actions</th>
                </tr>
              </thead>
              <tbody>
                {tasks.map((task) => (
                  <tr key={task.id} className={editingId === task.id ? 'row-editing' : ''}>
                    <td className="td-id">#{task.id}</td>
                    <td className="td-name">
                      <strong>{task.title}</strong>
                      {task.description && (
                        <span className="task-subtext">{task.description}</span>
                      )}
                    </td>
                    <td>
                      <span className="assignee-badge">
                        👤 {task.employee?.name || 'Unassigned'}
                      </span>
                    </td>
                    <td>
                      <span className="project-badge">
                        📁 {task.project?.name || 'General'}
                      </span>
                    </td>
                    <td>
                      <span className={getPriorityBadgeClass(task.priority)}>
                        {task.priority || 'MEDIUM'}
                      </span>
                    </td>
                    <td>
                      <span className={getStatusBadgeClass(task.status)}>
                        {task.status || 'TODO'}
                      </span>
                    </td>
                    <td>{task.deadline || '—'}</td>
                    <td className="td-actions">
                      <button
                        className="btn btn-xs btn-edit"
                        onClick={() => handleEdit(task)}
                        title="Edit task"
                      >
                        ✏️ Edit
                      </button>
                      <button
                        className="btn btn-xs btn-delete"
                        onClick={() => handleDelete(task.id, task.title)}
                        title="Delete task"
                      >
                        🗑️ Delete
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}

export default Tasks;
