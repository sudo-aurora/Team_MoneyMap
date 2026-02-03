import apiClient from './api';

export const clientService = {
  // Get all clients with pagination
  getAll: (page = 0, size = 10) => {
    return apiClient.get(`/clients?page=${page}&size=${size}`);
  },

  // Get client by ID
  getById: (id) => {
    return apiClient.get(`/clients/${id}`);
  },

  // Get client with portfolios
  getWithPortfolios: (id) => {
    return apiClient.get(`/clients/${id}/portfolios`);
  },

  // Search clients
  search: (query, page = 0, size = 10) => {
    return apiClient.get(`/clients/search?query=${query}&page=${page}&size=${size}`);
  },

  // Create client
  create: (clientData) => {
    return apiClient.post('/clients', clientData);
  },

  // Update client
  update: (id, clientData) => {
    return apiClient.put(`/clients/${id}`, clientData);
  },

  // Activate client
  activate: (id) => {
    return apiClient.patch(`/clients/${id}/activate`);
  },

  // Deactivate client
  deactivate: (id) => {
    return apiClient.patch(`/clients/${id}/deactivate`);
  },

  // Delete client
  delete: (id) => {
    return apiClient.delete(`/clients/${id}`);
  },

  // Get active client count
  getActiveCount: () => {
    return apiClient.get('/clients/count/active');
  },
};
