import axios from 'axios';

const baseURL = (process.env.VUE_APP_HEALTH_BASE_URL as string || '').replace(/\/$/, '');
const API_BASE = `${baseURL}/api/v1`;

/**
 * 模擬圖片上傳
 */
export async function uploadImage(file: File): Promise<string> {
  const formData = new FormData();
  formData.append('file', file);
  
  const resp = await axios.post<{ url: string }>(`${API_BASE}/upload`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  });
  
  return resp.data.url;
}
