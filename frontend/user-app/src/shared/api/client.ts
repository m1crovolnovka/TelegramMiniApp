import axios from 'axios';
import { useAuthStore } from '../store/authStore';

const apiBaseUrl = (import.meta.env.VITE_API_URL ?? '').replace(/\/$/, '');

if (import.meta.env.PROD && !apiBaseUrl) {
  console.error(
    'VITE_API_URL is not set. Add it in Vercel Environment Variables (your backend URL, e.g. https://api.example.com)',
  );
}

export const api = axios.create({
  baseURL: apiBaseUrl,
  headers: { 'Content-Type': 'application/json' },
});

api.interceptors.request.use((config) => {
  const token = useAuthStore.getState().token;
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (r) => r,
  (err) => {
    if (err.response?.status === 404 && !apiBaseUrl) {
      return Promise.reject(
        new Error(
          'API URL не настроен: задайте VITE_API_URL в Vercel (URL вашего бэкенда)',
        ),
      );
    }
    const message =
      err.response?.data?.message ?? err.message ?? 'Network error';
    return Promise.reject(new Error(message));
  },
);
