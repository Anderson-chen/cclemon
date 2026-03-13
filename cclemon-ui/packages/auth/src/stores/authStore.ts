import { defineStore } from 'pinia';
import { AuthFlowImpli, initOpenIdConfig } from '../flow/AuthFlowImpli';
import { AuthFlow, tokenResponse } from '../flow/types';
import { ref, computed } from 'vue';
import { useUserStore } from './userStore';
import localStorage from '../utils/localStorage';
import sessionStorage from '../utils/sessionStorage';

const VUE_APP_AUTH_CONFIG = process.env.VUE_APP_AUTH_CONFIG ?? '';
const VUE_APP_TOKEN_KEY = process.env.VUE_APP_TOKEN_KEY ?? '';
const VUE_APP_USER_PROFILE_KEY = process.env.VUE_APP_USER_PROFILE_KEY ?? '';
const VUE_APP_VERIFIER = process.env.VUE_APP_VERIFIER ?? '';

export const useAuthStore = defineStore('auth', () => {
  const userStore = useUserStore();
  const authFlow = ref<AuthFlow>();
  const tokenInfo = ref<tokenResponse | undefined>(
    localStorage.getItem<tokenResponse>(VUE_APP_TOKEN_KEY),
  );
  const isAuthenticated = computed(() => Boolean(tokenInfo.value));
  const codeVerifier = ref('')//（隨機字串）;

  function login() {
    codeVerifier.value = crypto.randomUUID()
    sessionStorage.setItem(VUE_APP_VERIFIER, codeVerifier.value)
    authFlow.value?.authorize(codeVerifier.value);
  }

  async function logout() {
    localStorage.clear();
    authFlow.value?.revokeToken(tokenInfo.value?.access_token ?? '');
    authFlow.value?.revokeToken(tokenInfo.value?.refresh_token ?? '');
    authFlow.value?.logout(tokenInfo.value?.id_token ?? '');
  }

  async function useAuthorizationCode() {
    const url = new URL(window.location.href);
    const code = url.searchParams.get('code');
    if (code) {
      url.searchParams.delete('code');
      window.history.replaceState({}, document.title, url.toString());
      tokenInfo.value = await authFlow.value?.getAccessToken(code, sessionStorage.getItem<string>(VUE_APP_VERIFIER));
      const res = await authFlow.value?.getUserInfo(
        tokenInfo.value?.access_token ?? '',
      );

      userStore.username = res?.userName ?? '';

      localStorage.setItem(VUE_APP_TOKEN_KEY, tokenInfo.value);
      localStorage.setItem(VUE_APP_USER_PROFILE_KEY, userStore.username);
      sessionStorage.removeItem(VUE_APP_VERIFIER)
    }
  }

  async function initAuth() {
    const configRes = await initOpenIdConfig(VUE_APP_AUTH_CONFIG);
    authFlow.value = new AuthFlowImpli(configRes);
  }

  return {
    isAuthenticated,
    initAuth,
    login,
    logout,
    useAuthorizationCode,
  };
});
