import apiClient from './api';

export const paymentService = {
  // Get all payments with pagination
  getAll: (page = 0, size = 10) => {
    return apiClient.get(`/payments?page=${page}&size=${size}`);
  },

  // Get payment by ID
  getById: (id) => {
    return apiClient.get(`/payments/${id}`);
  },

  // Get payment with full details
  getDetails: (id) => {
    return apiClient.get(`/payments/${id}/details`);
  },

  // Get payments by status
  getByStatus: (status, page = 0, size = 10) => {
    return apiClient.get(`/payments/status/${status}?page=${page}&size=${size}`);
  },

  // Create payment
  create: (paymentData) => {
    return apiClient.post('/payments', paymentData);
  },

  // Validate payment
  validate: (id) => {
    return apiClient.post(`/payments/${id}/validate`);
  },

  // Send payment
  send: (id) => {
    return apiClient.post(`/payments/${id}/send`);
  },

  // Complete payment
  complete: (id) => {
    return apiClient.post(`/payments/${id}/complete`);
  },

  // Fail payment
  fail: (id, errorCode, errorMessage) => {
    return apiClient.post(`/payments/${id}/fail?errorCode=${errorCode}&errorMessage=${encodeURIComponent(errorMessage)}`);
  },
};
