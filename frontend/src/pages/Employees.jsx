import React, { useState, useEffect } from 'react';
import {
  getEmployees,
  createEmployee,
  updateEmployee,
  deleteEmployee,
} from '../services/employeeService';

function Employees() {
  const [employees, setEmployees] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState(null);

  // Form State
  const initialFormState = {
    name: '',
    email: '',
    department: '',
    role: '',
  };
  const [formData, setFormData] = useState(initialFormState);
  const [formErrors, setFormErrors] = useState({});
  const [editingId, setEditingId] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  // Fetch employees on component mount
  useEffect(() => {
    fetchEmployees();
  }, []);

  const fetchEmployees = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await getEmployees();
      setEmployees(data);
    } catch (err) {
      console.error('Failed to fetch employees:', err);
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
      errors.name = 'Employee name is required';
    }
    if (!formData.email || !formData.email.trim()) {
      errors.email = 'Email is required';
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email.trim())) {
      errors.email = 'Please enter a valid email address';
    }
    if (!formData.department || !formData.department.trim()) {
      errors.department = 'Department is required';
    }
    if (!formData.role || !formData.role.trim()) {
      errors.role = 'Role is required';
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
        email: formData.email.trim(),
        department: formData.department.trim(),
        role: formData.role.trim(),
      };

      if (editingId) {
        await updateEmployee(editingId, payload);
        showSuccess('Employee updated successfully!');
      } else {
        await createEmployee(payload);
        showSuccess('Employee added successfully!');
      }

      // Reset form and refresh list
      setFormData(initialFormState);
      setEditingId(null);
      setFormErrors({});
      await fetchEmployees();
    } catch (err) {
      console.error('Failed to save employee:', err);
      setError(
        err.response?.data?.error ||
        err.response?.data?.message ||
        'Failed to save employee. Please try again.'
      );
    } finally {
      setSubmitting(false);
    }
  };

  const handleEdit = (emp) => {
    setEditingId(emp.id);
    setFormData({
      name: emp.name || '',
      email: emp.email || '',
      department: emp.department || '',
      role: emp.role || '',
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
    const confirmed = window.confirm(`Are you sure you want to delete employee "${name}"?`);
    if (!confirmed) return;

    try {
      setError(null);
      await deleteEmployee(id);
      showSuccess(`Employee "${name}" deleted successfully.`);
      if (editingId === id) {
        handleCancelEdit();
      }
      await fetchEmployees();
    } catch (err) {
      console.error('Failed to delete employee:', err);
      setError(
        err.response?.data?.error ||
        err.response?.data?.message ||
        'Failed to delete employee. (If this employee has assigned tasks, delete those tasks first).'
      );
    }
  };

  return (
    <div className="page-container">
      <div className="page-header">
        <h1>Employee Management</h1>
        <p className="page-subtitle">Add, view, edit, and manage employee records.</p>
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

      {/* Employee Form Card */}
      <div className="form-card">
        <div className="card-header">
          <h2>{editingId ? '✏️ Edit Employee' : '➕ Add New Employee'}</h2>
          {editingId && (
            <span className="editing-badge">Editing ID #{editingId}</span>
          )}
        </div>

        <form onSubmit={handleSubmit} className="employee-form" noValidate>
          <div className="form-grid">
            <div className="form-group">
              <label htmlFor="name">Full Name *</label>
              <input
                type="text"
                id="name"
                name="name"
                placeholder="e.g. Alice Smith"
                value={formData.name}
                onChange={handleInputChange}
                className={formErrors.name ? 'input-error' : ''}
              />
              {formErrors.name && <span className="error-text">{formErrors.name}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="email">Email Address *</label>
              <input
                type="email"
                id="email"
                name="email"
                placeholder="e.g. alice@example.com"
                value={formData.email}
                onChange={handleInputChange}
                className={formErrors.email ? 'input-error' : ''}
              />
              {formErrors.email && <span className="error-text">{formErrors.email}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="department">Department *</label>
              <input
                type="text"
                id="department"
                name="department"
                placeholder="e.g. Engineering"
                value={formData.department}
                onChange={handleInputChange}
                className={formErrors.department ? 'input-error' : ''}
              />
              {formErrors.department && <span className="error-text">{formErrors.department}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="role">Role / Position *</label>
              <input
                type="text"
                id="role"
                name="role"
                placeholder="e.g. Senior Software Engineer"
                value={formData.role}
                onChange={handleInputChange}
                className={formErrors.role ? 'input-error' : ''}
              />
              {formErrors.role && <span className="error-text">{formErrors.role}</span>}
            </div>
          </div>

          <div className="form-actions">
            <button type="submit" className="btn btn-primary" disabled={submitting}>
              {submitting ? 'Saving...' : editingId ? 'Update Employee' : 'Add Employee'}
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

      {/* Employees Table Card */}
      <div className="table-card">
        <div className="card-header">
          <h2>👥 Employee List ({employees.length})</h2>
          <button className="btn btn-sm btn-outline" onClick={fetchEmployees} title="Refresh employee list">
            🔄 Refresh
          </button>
        </div>

        {loading ? (
          <div className="table-loading">
            <div className="spinner"></div>
            <p>Loading employees...</p>
          </div>
        ) : employees.length === 0 ? (
          <div className="table-empty">
            <p>No employees found. Use the form above to add your first employee.</p>
          </div>
        ) : (
          <div className="table-responsive">
            <table className="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Name</th>
                  <th>Email</th>
                  <th>Department</th>
                  <th>Role</th>
                  <th className="th-actions">Actions</th>
                </tr>
              </thead>
              <tbody>
                {employees.map((emp) => (
                  <tr key={emp.id} className={editingId === emp.id ? 'row-editing' : ''}>
                    <td className="td-id">#{emp.id}</td>
                    <td className="td-name">
                      <strong>{emp.name}</strong>
                    </td>
                    <td>{emp.email}</td>
                    <td>
                      <span className="badge-dept">{emp.department || '—'}</span>
                    </td>
                    <td>{emp.role || '—'}</td>
                    <td className="td-actions">
                      <button
                        className="btn btn-xs btn-edit"
                        onClick={() => handleEdit(emp)}
                        title="Edit employee"
                      >
                        ✏️ Edit
                      </button>
                      <button
                        className="btn btn-xs btn-delete"
                        onClick={() => handleDelete(emp.id, emp.name)}
                        title="Delete employee"
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

export default Employees;
