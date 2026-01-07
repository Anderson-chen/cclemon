import { defineStore } from 'pinia';
import { ref } from 'vue';
import localStorage from './localStorage';
import { tokenResponse } from 'src/auth/index.d';
const { VUE_APP_USER_PROFILE_KEY, VUE_APP_TOKEN_KEY } = process.env;
export const useUserStore = defineStore('user', () => {
  const username = ref<string>(localStorage.getItem(VUE_APP_USER_PROFILE_KEY));
  function getAccessToken(): string {
    return localStorage.getItem<tokenResponse>(VUE_APP_TOKEN_KEY)?.access_token;
  }
  return { username, getAccessToken };
});
