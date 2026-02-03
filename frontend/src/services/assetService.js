import apiClient from './api';

export const assetService = {
  // Get all assets
  getAll: (page = 0, size = 10) => {
    return apiClient.get(`/assets?page=${page}&size=${size}`);
  },

  // Get asset by ID
  getById: (id) => {
    return apiClient.get(`/assets/${id}`);
  },

  // Get asset by ID with transactions
  getByIdWithTransactions: (id) => {
    return apiClient.get(`/assets/${id}/transactions`);
  },

  // Get assets by portfolio ID
  getByPortfolioId: (portfolioId) => {
    return apiClient.get(`/assets/portfolio/${portfolioId}`);
  },

  // Get assets by type
  getByType: (assetType) => {
    return apiClient.get(`/assets/type/${assetType}`);
  },

  // Get asset types
  getTypes: () => {
    return apiClient.get('/assets/types');
  },

  // Create asset
  create: (assetData) => {
    return apiClient.post('/assets', assetData);
  },

  // Update asset
  update: (id, assetData) => {
    return apiClient.put(`/assets/${id}`, assetData);
  },

  // Update asset price
  updatePrice: (id, currentPrice) => {
    return apiClient.patch(`/assets/${id}/price?currentPrice=${currentPrice}`);
  },

  // Delete asset
  delete: (id) => {
    return apiClient.delete(`/assets/${id}`);
  },

  // Get total value by asset type
  getTotalValueByType: (assetType) => {
    return apiClient.get(`/assets/type/${assetType}/total-value`);
  },
};
