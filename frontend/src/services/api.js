import axios from 'axios';

/**
 * Centralized Axios instance configured for the Spring Boot backend REST APIs.
 * Base URL: http://localhost:8080/api
 */
const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
});

export default api;
