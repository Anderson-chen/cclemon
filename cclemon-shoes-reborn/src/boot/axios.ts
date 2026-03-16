import axios from 'axios';
import { useUserStore } from 'src/stores/userStore';
// import { track } from 'src/utils/track'
const { getAccessToken } = useUserStore();
const { VUE_APP_HEALTH_BASE_URL } = process.env;
const authApi = axios.create({
  //   withCredentials: true,
});
const healthApi = axios.create({
  baseURL: VUE_APP_HEALTH_BASE_URL,
  headers: { Authorization: getAccessToken() },
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

healthApi.interceptors.request.use(config => {
  // track('API', config.url ?? '', { method: config.method ?? '' })
  return config
}, error => {
  return Promise.reject(error)
})

export { authApi, healthApi };
