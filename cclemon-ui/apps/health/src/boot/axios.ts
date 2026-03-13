import axios from 'axios';
import { useUserStore } from 'src/stores/userStore';

const VUE_APP_HEALTH_BASE_URL = process.env.VUE_APP_HEALTH_BASE_URL ?? '';

// authApi: plain axios instance for auth-server calls (no Bearer token)
export const authApi = axios.create();

export const healthApi = axios.create({
  baseURL: VUE_APP_HEALTH_BASE_URL,
  paramsSerializer: (params) => {
    const sp = new URLSearchParams();
    for (const [key, value] of Object.entries(params)) {
      if (value === undefined || value === null) continue;
      if (Array.isArray(value)) {
        value.forEach((v) => sp.append(key, String(v)));
      } else {
        sp.append(key, String(value));
      }
    }
    return sp.toString();
  },
});

healthApi.interceptors.request.use((config) => {
  const token = useUserStore().getAccessToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
