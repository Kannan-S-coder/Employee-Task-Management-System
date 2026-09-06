import api from './api';

/**
 * Service methods for Task API operations.
 */
export const getTasks = async (params = {}) => {
  // Clean empty/null parameters before sending
  const cleanParams = {};
  if (params.status && params.status.trim()) cleanParams.status = params.status.trim();
  if (params.priority && params.priority.trim()) cleanParams.priority = params.priority.trim();
  if (params.employeeId) cleanParams.employeeId = params.employeeId;
  if (params.projectId) cleanParams.projectId = params.projectId;

  const response = await api.get('/tasks', { params: cleanParams });
  return response.data;
};

export const getTaskById = async (id) => {
  const response = await api.get(`/tasks/${id}`);
  return response.data;
};

export const createTask = async (taskData) => {
  const response = await api.post('/tasks', taskData);
  return response.data;
};

export const updateTask = async (id, taskData) => {
  const response = await api.put(`/tasks/${id}`, taskData);
  return response.data;
};

export const deleteTask = async (id) => {
  const response = await api.delete(`/tasks/${id}`);
  return response.data;
};
