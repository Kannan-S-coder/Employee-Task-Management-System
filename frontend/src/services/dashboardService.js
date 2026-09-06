import api from './api';

/**
 * Service to aggregate dashboard data from existing backend REST endpoints.
 */
export const getDashboardData = async () => {
  const [employeesRes, projectsRes, tasksRes] = await Promise.all([
    api.get('/employees'),
    api.get('/projects'),
    api.get('/tasks'),
  ]);

  const employees = employeesRes.data || [];
  const projects = projectsRes.data || [];
  const tasks = tasksRes.data || [];

  // Calculate Task Status metrics
  const todoCount = tasks.filter((t) => t.status === 'TODO').length;
  const inProgressCount = tasks.filter((t) => t.status === 'IN_PROGRESS').length;
  const completedCount = tasks.filter((t) => t.status === 'COMPLETED').length;
  const pendingCount = todoCount + inProgressCount;

  // Calculate Task Priority metrics
  const lowPriorityCount = tasks.filter((t) => t.priority === 'LOW').length;
  const mediumPriorityCount = tasks.filter((t) => t.priority === 'MEDIUM').length;
  const highPriorityCount = tasks.filter((t) => t.priority === 'HIGH').length;

  // Get recent tasks (latest 5)
  const recentTasks = [...tasks].slice(-5).reverse();

  return {
    employeesCount: employees.length,
    projectsCount: projects.length,
    totalTasksCount: tasks.length,
    completedTasksCount: completedCount,
    pendingTasksCount: pendingCount,
    highPriorityTasksCount: highPriorityCount,
    statusBreakdown: {
      TODO: todoCount,
      IN_PROGRESS: inProgressCount,
      COMPLETED: completedCount,
    },
    priorityBreakdown: {
      LOW: lowPriorityCount,
      MEDIUM: mediumPriorityCount,
      HIGH: highPriorityCount,
    },
    recentTasks,
  };
};
