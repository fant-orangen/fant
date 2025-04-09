import axios from 'axios';
import { useUserStore } from "@/stores/UserStore.ts";

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
})

api.interceptors.request.use((config) => {
  const userStore = useUserStore();
  if (userStore.token) {
    config.headers.Authorization = `Bearer ${userStore.token}`
  }
  return config;
})

export default api;
