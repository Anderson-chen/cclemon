import { defineStore } from 'pinia';
import { ref } from 'vue';
import localStorage from '../utils/localStorage';
import { tokenResponse } from '../flow/types';
const VUE_APP_USER_PROFILE_KEY = process.env.VUE_APP_USER_PROFILE_KEY ?? '';
const VUE_APP_TOKEN_KEY = process.env.VUE_APP_TOKEN_KEY ?? '';
export const useUserStore = defineStore('user', () => {
  const username = ref<string>(localStorage.getItem(VUE_APP_USER_PROFILE_KEY));
  function getAccessToken(): string | undefined {
    return localStorage.getItem<tokenResponse>(VUE_APP_TOKEN_KEY)?.access_token;
  }
  return { username, getAccessToken };
});
