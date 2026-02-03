import apiClient from './api';

export const alertService = {
  // Get all alerts with pagination
  getAll: (page = 0, size = 10) => {
    return apiClient.get(`/alerts?page=${page}&size=${size}`);
  },

  // Get alert by ID
  getById: (id) => {
    return apiClient.get(`/alerts/${id}`);
  },

  // Get alerts by status
  getByStatus: (status, page = 0, size = 10) => {
    return apiClient.get(`/alerts/status/${status}?page=${page}&size=${size}`);
  },

  // Get alerts by severity
  getBySeverity: (severity, page = 0, size = 10) => {
    return apiClient.get(`/alerts/severity/${severity}?page=${page}&size=${size}`);
  },

  // Get prioritized open alerts
  getOpenPrioritized: (limit = 20) => {
    return apiClient.get(`/alerts/open/prioritized?limit=${limit}`);
  },

  // Acknowledge alert
  acknowledge: (id, operatorName = 'Admin') => {
    return apiClient.put(`/alerts/${id}/acknowledge?operatorName=${encodeURIComponent(operatorName)}`);
  },

  // Update alert status
  updateStatus: (id, statusData) => {
    return apiClient.put(`/alerts/${id}/status`, statusData);
  },

  // Get alert statistics
  getStatistics: () => {
    return apiClient.get('/alerts/statistics');
  },

  // Get alert count by status
  getCountByStatus: (status) => {
    return apiClient.get(`/alerts/count/${status}`);
  },
};
