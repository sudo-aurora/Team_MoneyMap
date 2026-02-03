import axios from 'axios';

const API_BASE_URL = '/api/v1';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Response interceptor to extract data from ApiResponseDTO
apiClient.interceptors.response.use(
  response => {
    // Extract data from the ApiResponseDTO wrapper
    if (response.data && response.data.data !== undefined) {
      return response.data.data;
    }
    return response.data;
  },
  error => {
    // Handle errors
    const message = error.response?.data?.message || error.message || 'An error occurred';
    return Promise.reject(new Error(message));
  }
);

export default apiClient;
