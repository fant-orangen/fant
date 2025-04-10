/**
 * Axios configuration module.
 *
 * This module provides a preconfigured Axios instance with the base API URL.
 * It creates a reusable HTTP client that can be imported throughout the application
 * for making consistent API requests to the backend.
 *
 * @module axiosConfig
 */
import axios from 'axios';

/**
 * Configured Axios instance.
 *
 * The instance is set up with a base URL pointing to the backend API.
 * This allows using relative paths in API requests throughout the application.
 */
const axiosInstance = axios.create({
  baseURL: 'http://localhost:8080', // Replace with your actual base URL and port
});

export default axiosInstance;
