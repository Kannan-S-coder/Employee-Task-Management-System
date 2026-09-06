import React, { useState, useEffect } from 'react';
import {
  getProjects,
  createProject,
  updateProject,
  deleteProject,
} from '../services/projectService';

function Projects() {
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState(null);

  // Form State
  const initialFormState = {
    name: '',
    description: '',
    startDate: '',
    endDate: '',
    status: 'PLANNED',
  };
  const [formData, setFormData] = useState(initialFormState);
  const [formErrors, setFormErrors] = useState({});
  const [editingId, setEditingId] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  // Fetch projects on component mount
  useEffect(() => {
    fetchProjects();
  }, []);

  const fetchProjects = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await getProjects();
      setProjects(data);
    } catch (err) {
      console.error('Failed to fetch projects:', err);
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

  // Form validation
  const validateForm = () => {
    const errors = {};
    if (!formData.name || !formData.name.trim()) {
      errors.name = 'Project name is required';
    }
    if (!formData.startDate) {
      errors.startDate = 'Start date is required';
    }
    if (!formData.endDate) {
      errors.endDate = 'End date is required';
    }
    if (formData.startDate && formData.endDate) {
      if (new Date(formData.endDate) < new Date(formData.startDate)) {
        errors.endDate = 'End date cannot be before start date';
      }
    }
    const validStatuses = ['PLANNED', 'IN_PROGRESS', 'COMPLETED'];
    if (!formData.status || !validStatuses.includes(formData.status)) {
      errors.status = 'Status must be one of: PLANNED, IN_PROGRESS, COMPLETED';
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

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;

    try {
      setSubmitting(true);
      setError(null);

      const payload = {
        name: formData.name.trim(),
        description: formData.description ? formData.description.trim() : '',
        startDate: formData.startDate,
        endDate: formData.endDate,
        status: formData.status,
      };

      if (editingId) {
        await updateProject(editingId, payload);
        showSuccess('Project updated successfully!');
      } else {
        await createProject(payload);
        showSuccess('Project added successfully!');
      }

      // Reset form and refresh list
      setFormData(initialFormState);
      setEditingId(null);
      setFormErrors({});
      await fetchProjects();
    } catch (err) {
      console.error('Failed to save project:', err);
      setError(
        err.response?.data?.error ||
        err.response?.data?.message ||
        'Failed to save project. Please check the input and try again.'
      );
    } finally {
      setSubmitting(false);
    }
  };

  const handleEdit = (project) => {
    setEditingId(project.id);
    setFormData({
      name: project.name || '',
      description: project.description || '',
      startDate: project.startDate || '',
      endDate: project.endDate || '',
      status: project.status || 'PLANNED',
    });
    setFormErrors({});
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleCancelEdit = () => {
    setEditingId(null);
    setFormData(initialFormState);
    setFormErrors({});
  };

  const handleDelete = async (id, name) => {
    const confirmed = window.confirm(`Are you sure you want to delete project "${name}"?`);
    if (!confirmed) return;

    try {
      setError(null);
      await deleteProject(id);
      showSuccess(`Project "${name}" deleted successfully.`);
      if (editingId === id) {
        handleCancelEdit();
      }
      await fetchProjects();
    } catch (err) {
      console.error('Failed to delete project:', err);
      setError(
        err.response?.data?.error ||
        err.response?.data?.message ||
        'Failed to delete project. (If tasks are assigned to this project, delete those tasks first).'
      );
    }
  };

  // Helper for status badge styling
  const getStatusBadgeClass = (status) => {
    switch (status) {
      case 'IN_PROGRESS':
        return 'status-badge status-in-progress';
      case 'COMPLETED':
        return 'status-badge status-completed';
      case 'PLANNED':
      default:
        return 'status-badge status-planned';
    }
  };

  return (
    <div className="page-container">
      <div className="page-header">
        <h1>Project Management</h1>
        <p className="page-subtitle">Create, monitor, update, and manage company project deliverables.</p>
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

      {/* Project Form Card */}
      <div className="form-card">
        <div className="card-header">
          <h2>{editingId ? '✏️ Edit Project' : '➕ Add New Project'}</h2>
          {editingId && (
            <span className="editing-badge">Editing ID #{editingId}</span>
          )}
        </div>

        <form onSubmit={handleSubmit} className="project-form" noValidate>
          <div className="form-grid">
            <div className="form-group">
              <label htmlFor="name">Project Name *</label>
              <input
                type="text"
                id="name"
                name="name"
                placeholder="e.g. Cloud Migration Platform"
                value={formData.name}
                onChange={handleInputChange}
                className={formErrors.name ? 'input-error' : ''}
              />
              {formErrors.name && <span className="error-text">{formErrors.name}</span>}
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
                <option value="PLANNED">PLANNED</option>
                <option value="IN_PROGRESS">IN_PROGRESS</option>
                <option value="COMPLETED">COMPLETED</option>
              </select>
              {formErrors.status && <span className="error-text">{formErrors.status}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="startDate">Start Date *</label>
              <input
                type="date"
                id="startDate"
                name="startDate"
                value={formData.startDate}
                onChange={handleInputChange}
                className={formErrors.startDate ? 'input-error' : ''}
              />
              {formErrors.startDate && <span className="error-text">{formErrors.startDate}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="endDate">End Date *</label>
              <input
                type="date"
                id="endDate"
                name="endDate"
                value={formData.endDate}
                onChange={handleInputChange}
                className={formErrors.endDate ? 'input-error' : ''}
              />
              {formErrors.endDate && <span className="error-text">{formErrors.endDate}</span>}
            </div>
          </div>

          <div className="form-group form-group-full">
            <label htmlFor="description">Description</label>
            <textarea
              id="description"
              name="description"
              rows="3"
              placeholder="Brief summary of project scope, objectives, and deliverables..."
              value={formData.description}
              onChange={handleInputChange}
            ></textarea>
          </div>

          <div className="form-actions">
            <button type="submit" className="btn btn-primary" disabled={submitting}>
              {submitting ? 'Saving...' : editingId ? 'Update Project' : 'Add Project'}
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

      {/* Projects Table Card */}
      <div className="table-card">
        <div className="card-header">
          <h2>📁 Project List ({projects.length})</h2>
          <button className="btn btn-sm btn-outline" onClick={fetchProjects} title="Refresh project list">
            🔄 Refresh
          </button>
        </div>

        {loading ? (
          <div className="table-loading">
            <div className="spinner"></div>
            <p>Loading projects...</p>
          </div>
        ) : projects.length === 0 ? (
          <div className="table-empty">
            <p>No projects found. Use the form above to add your first project.</p>
          </div>
        ) : (
          <div className="table-responsive">
            <table className="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Name</th>
                  <th>Description</th>
                  <th>Start Date</th>
                  <th>End Date</th>
                  <th>Status</th>
                  <th className="th-actions">Actions</th>
                </tr>
              </thead>
              <tbody>
                {projects.map((proj) => (
                  <tr key={proj.id} className={editingId === proj.id ? 'row-editing' : ''}>
                    <td className="td-id">#{proj.id}</td>
                    <td className="td-name">
                      <strong>{proj.name}</strong>
                    </td>
                    <td className="td-desc">{proj.description || '—'}</td>
                    <td>{proj.startDate || '—'}</td>
                    <td>{proj.endDate || '—'}</td>
                    <td>
                      <span className={getStatusBadgeClass(proj.status)}>
                        {proj.status || 'PLANNED'}
                      </span>
                    </td>
                    <td className="td-actions">
                      <button
                        className="btn btn-xs btn-edit"
                        onClick={() => handleEdit(proj)}
                        title="Edit project"
                      >
                        ✏️ Edit
                      </button>
                      <button
                        className="btn btn-xs btn-delete"
                        onClick={() => handleDelete(proj.id, proj.name)}
                        title="Delete project"
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

export default Projects;
