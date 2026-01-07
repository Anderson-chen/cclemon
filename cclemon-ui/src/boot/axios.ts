import axios from 'axios';
import { useUserStore } from 'src/stores/userStore';
import { track } from 'src/utils/track'
const { getAccessToken } = useUserStore();
const { VUE_APP_HEALTH_BASE_URL } = process.env;
const authApi = axios.create({
  //   withCredentials: true,
});
const healthApi = axios.create({
  baseURL: VUE_APP_HEALTH_BASE_URL,
  headers: { Authorization: getAccessToken() },
});

healthApi.interceptors.request.use(config => {
  track('API', config.url ?? '', { method: config.method ?? '' })
  return config
}, error => {
  return Promise.reject(error)
})

export { authApi, healthApi };
