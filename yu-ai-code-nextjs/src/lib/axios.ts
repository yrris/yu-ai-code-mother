import axios from 'axios';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8123/api';

/**
 * Axios instance configured for the API
 */
export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true, // Important for session cookies
});

/**
 * Request interceptor to add authentication or other headers
 */
apiClient.interceptors.request.use(
  (config) => {
    // You can add custom headers here if needed
    // For example, CSRF tokens or custom auth headers
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

/**
 * Response interceptor to handle errors globally
 */
apiClient.interceptors.response.use(
  (response) => {
    // Extract data from the response
    return response;
  },
  (error) => {
    // Handle common errors
    if (error.response) {
      const { status, data } = error.response;

      switch (status) {
        case 401:
          // Unauthorized - redirect to login
          if (typeof window !== 'undefined' && !window.location.pathname.includes('/login')) {
            window.location.href = '/login';
          }
          break;
        case 403:
          // Forbidden
          console.error('Access forbidden:', data.message || 'Forbidden');
          break;
        case 404:
          // Not found
          console.error('Resource not found:', data.message || 'Not found');
          break;
        case 500:
          // Server error
          console.error('Server error:', data.message || 'Internal server error');
          break;
        default:
          console.error('API error:', data.message || 'Unknown error');
      }
    } else if (error.request) {
      // Network error
      console.error('Network error: No response received from server');
    } else {
      // Other errors
      console.error('Error:', error.message);
    }

    return Promise.reject(error);
  }
);

/**
 * Type-safe API response wrapper
 */
export interface ApiResponse<T = any> {
  code: number;
  data: T;
  message: string;
}

/**
 * Extract data from API response
 */
export const extractData = <T>(response: ApiResponse<T>): T => {
  if (response.code !== 0) {
    throw new Error(response.message || 'API request failed');
  }
  return response.data;
};
