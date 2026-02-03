import apiClient from './api';

export const portfolioService = {
  // Get all portfolios with pagination
  getAll: (page = 0, size = 10) => {
    return apiClient.get(`/portfolios?page=${page}&size=${size}`);
  },

  // Get portfolio by ID
  getById: (id) => {
    return apiClient.get(`/portfolios/${id}`);
  },

  // Get portfolio with assets
  getWithAssets: (id) => {
    return apiClient.get(`/portfolios/${id}/assets`);
  },

  // Get portfolios by client ID
  getByClientId: (clientId) => {
    return apiClient.get(`/portfolios/client/${clientId}`);
  },

  // Create portfolio
  create: (portfolioData) => {
    return apiClient.post('/portfolios', portfolioData);
  },

  // Update portfolio
  update: (id, portfolioData) => {
    return apiClient.put(`/portfolios/${id}`, portfolioData);
  },

  // Recalculate portfolio value
  recalculate: (id) => {
    return apiClient.post(`/portfolios/${id}/recalculate`);
  },

  // Get total value by client
  getTotalValueByClient: (clientId) => {
    return apiClient.get(`/portfolios/client/${clientId}/total-value`);
  },
};
