import React, { useState, useEffect } from 'react';
import { getDashboardData } from '../services/dashboardService';

function Dashboard({ setActiveTab }) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchDashboard();
  }, []);

  const fetchDashboard = async () => {
    try {
      setLoading(true);
      setError(null);
      const metrics = await getDashboardData();
      setData(metrics);
    } catch (err) {
      console.error('Failed to load dashboard data:', err);
      setError(
        err.response?.data?.message ||
        'Unable to connect to the backend server. Please ensure Spring Boot is running on port 8080.'
      );
    } finally {
      setLoading(false);
    }
  };

  // Helper to calculate percentage safely without NaN/division by zero
  const getPercentage = (count, total) => {
    if (!total || total === 0) return 0;
    return Math.round((count / total) * 100);
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
    <div className="page-container dashboard-page">
      <div className="dashboard-header">
        <div>
          <h1>Dashboard Overview</h1>
          <p className="page-subtitle">Real-time statistics across employees, projects, and task execution.</p>
        </div>
        <button className="btn btn-primary btn-sm" onClick={fetchDashboard} disabled={loading}>
          🔄 Refresh Dashboard
        </button>
      </div>

      {error && (
        <div className="alert alert-error">
          <span>⚠️ {error}</span>
          <button className="alert-close" onClick={() => setError(null)}>×</button>
        </div>
      )}

      {loading ? (
        <div className="table-loading">
          <div className="spinner"></div>
          <p>Calculating dashboard statistics...</p>
        </div>
      ) : data ? (
        <>
          {/* 1. Top Statistics Cards */}
          <div className="stats-grid">
            <div className="stat-card">
              <div className="stat-icon icon-emp">👥</div>
              <div className="stat-content">
                <span className="stat-label">Total Employees</span>
                <span className="stat-value">{data.employeesCount}</span>
              </div>
            </div>

            <div className="stat-card">
              <div className="stat-icon icon-proj">📁</div>
              <div className="stat-content">
                <span className="stat-label">Total Projects</span>
                <span className="stat-value">{data.projectsCount}</span>
              </div>
            </div>

            <div className="stat-card">
              <div className="stat-icon icon-tasks">✅</div>
              <div className="stat-content">
                <span className="stat-label">Total Tasks</span>
                <span className="stat-value">{data.totalTasksCount}</span>
              </div>
            </div>

            <div className="stat-card stat-card-success">
              <div className="stat-icon icon-completed">🎉</div>
              <div className="stat-content">
                <span className="stat-label">Completed Tasks</span>
                <span className="stat-value">{data.completedTasksCount}</span>
              </div>
            </div>

            <div className="stat-card stat-card-warning">
              <div className="stat-icon icon-pending">⏳</div>
              <div className="stat-content">
                <span className="stat-label">Pending Tasks</span>
                <span className="stat-value">{data.pendingTasksCount}</span>
              </div>
            </div>

            <div className="stat-card stat-card-danger">
              <div className="stat-icon icon-high">🔥</div>
              <div className="stat-content">
                <span className="stat-label">High Priority</span>
                <span className="stat-value">{data.highPriorityTasksCount}</span>
              </div>
            </div>
          </div>

          {/* 2 & 3. Breakdown Analytics (Status & Priority CSS Bars) */}
          <div className="analytics-grid">
            {/* Status Breakdown */}
            <div className="analytics-card">
              <div className="analytics-header">
                <h3>📊 Tasks by Status</h3>
                <span className="analytics-total">{data.totalTasksCount} Tasks</span>
              </div>

              <div className="progress-group">
                <div className="progress-label-row">
                  <span className="label-name">TODO</span>
                  <span className="label-val">
                    {data.statusBreakdown.TODO} ({getPercentage(data.statusBreakdown.TODO, data.totalTasksCount)}%)
                  </span>
                </div>
                <div className="progress-track">
                  <div
                    className="progress-fill fill-todo"
                    style={{ width: `${getPercentage(data.statusBreakdown.TODO, data.totalTasksCount)}%` }}
                  ></div>
                </div>
              </div>

              <div className="progress-group">
                <div className="progress-label-row">
                  <span className="label-name">IN_PROGRESS</span>
                  <span className="label-val">
                    {data.statusBreakdown.IN_PROGRESS} ({getPercentage(data.statusBreakdown.IN_PROGRESS, data.totalTasksCount)}%)
                  </span>
                </div>
                <div className="progress-track">
                  <div
                    className="progress-fill fill-in-progress"
                    style={{ width: `${getPercentage(data.statusBreakdown.IN_PROGRESS, data.totalTasksCount)}%` }}
                  ></div>
                </div>
              </div>

              <div className="progress-group">
                <div className="progress-label-row">
                  <span className="label-name">COMPLETED</span>
                  <span className="label-val">
                    {data.statusBreakdown.COMPLETED} ({getPercentage(data.statusBreakdown.COMPLETED, data.totalTasksCount)}%)
                  </span>
                </div>
                <div className="progress-track">
                  <div
                    className="progress-fill fill-completed"
                    style={{ width: `${getPercentage(data.statusBreakdown.COMPLETED, data.totalTasksCount)}%` }}
                  ></div>
                </div>
              </div>
            </div>

            {/* Priority Breakdown */}
            <div className="analytics-card">
              <div className="analytics-header">
                <h3>⚡ Tasks by Priority</h3>
                <span className="analytics-total">{data.totalTasksCount} Tasks</span>
              </div>

              <div className="progress-group">
                <div className="progress-label-row">
                  <span className="label-name">LOW</span>
                  <span className="label-val">
                    {data.priorityBreakdown.LOW} ({getPercentage(data.priorityBreakdown.LOW, data.totalTasksCount)}%)
                  </span>
                </div>
                <div className="progress-track">
                  <div
                    className="progress-fill fill-low"
                    style={{ width: `${getPercentage(data.priorityBreakdown.LOW, data.totalTasksCount)}%` }}
                  ></div>
                </div>
              </div>

              <div className="progress-group">
                <div className="progress-label-row">
                  <span className="label-name">MEDIUM</span>
                  <span className="label-val">
                    {data.priorityBreakdown.MEDIUM} ({getPercentage(data.priorityBreakdown.MEDIUM, data.totalTasksCount)}%)
                  </span>
                </div>
                <div className="progress-track">
                  <div
                    className="progress-fill fill-medium"
                    style={{ width: `${getPercentage(data.priorityBreakdown.MEDIUM, data.totalTasksCount)}%` }}
                  ></div>
                </div>
              </div>

              <div className="progress-group">
                <div className="progress-label-row">
                  <span className="label-name">HIGH</span>
                  <span className="label-val">
                    {data.priorityBreakdown.HIGH} ({getPercentage(data.priorityBreakdown.HIGH, data.totalTasksCount)}%)
                  </span>
                </div>
                <div className="progress-track">
                  <div
                    className="progress-fill fill-high"
                    style={{ width: `${getPercentage(data.priorityBreakdown.HIGH, data.totalTasksCount)}%` }}
                  ></div>
                </div>
              </div>
            </div>
          </div>

          {/* 4. Recent Tasks List */}
          <div className="table-card">
            <div className="card-header">
              <h2>🕒 Recent Tasks (Latest {data.recentTasks.length})</h2>
              {setActiveTab && (
                <button
                  className="btn btn-sm btn-outline"
                  onClick={() => setActiveTab('tasks')}
                >
                  View All Tasks →
                </button>
              )}
            </div>

            {data.recentTasks.length === 0 ? (
              <div className="table-empty">
                <p>No recent tasks found. Create a task in the Tasks section to get started.</p>
              </div>
            ) : (
              <div className="table-responsive">
                <table className="data-table">
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Task Title</th>
                      <th>Assignee</th>
                      <th>Project</th>
                      <th>Priority</th>
                      <th>Status</th>
                      <th>Deadline</th>
                    </tr>
                  </thead>
                  <tbody>
                    {data.recentTasks.map((task) => (
                      <tr key={task.id}>
                        <td className="td-id">#{task.id}</td>
                        <td className="td-name">
                          <strong>{task.title}</strong>
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
                            {task.priority}
                          </span>
                        </td>
                        <td>
                          <span className={getStatusBadgeClass(task.status)}>
                            {task.status}
                          </span>
                        </td>
                        <td>{task.deadline || '—'}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>

          {/* 5. Quick Navigation Section */}
          {setActiveTab && (
            <div className="quick-actions-card">
              <div className="quick-actions-header">
                <h3>🚀 Quick Management Actions</h3>
              </div>
              <div className="quick-actions-grid">
                <button
                  className="quick-action-btn"
                  onClick={() => setActiveTab('employees')}
                >
                  <span className="qa-icon">👥</span>
                  <div className="qa-text">
                    <strong>Manage Employees</strong>
                    <small>Add, update, or remove staff members</small>
                  </div>
                </button>

                <button
                  className="quick-action-btn"
                  onClick={() => setActiveTab('projects')}
                >
                  <span className="qa-icon">📁</span>
                  <div className="qa-text">
                    <strong>Manage Projects</strong>
                    <small>Track schedules, timelines & statuses</small>
                  </div>
                </button>

                <button
                  className="quick-action-btn"
                  onClick={() => setActiveTab('tasks')}
                >
                  <span className="qa-icon">✅</span>
                  <div className="qa-text">
                    <strong>Manage Tasks</strong>
                    <small>Assign deliverables and track progress</small>
                  </div>
                </button>
              </div>
            </div>
          )}
        </>
      ) : null}
    </div>
  );
}

export default Dashboard;
