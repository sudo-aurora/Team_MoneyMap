import axios from 'axios';

const API_BASE_URL = '/api/v1';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to log requests
apiClient.interceptors.request.use(
  config => {
    console.log('API Request:', config.method?.toUpperCase(), config.url);
    return config;
  },
  error => {
    console.error('Request error:', error);
    return Promise.reject(error);
  }
);

// Response interceptor to extract data from ApiResponseDTO
apiClient.interceptors.response.use(
  response => {
    console.log('API Response:', response.status, response.config.url);
    // Extract data from the ApiResponseDTO wrapper
    if (response.data && response.data.data !== undefined) {
      return response.data.data;
    }
    return response.data;
  },
  error => {
    console.error('Response error:', error.response?.status, error.response?.data);
    // Handle errors
    const message = error.response?.data?.message || error.message || 'An error occurred';
    return Promise.reject(new Error(message));
  }
);

export default apiClient;
