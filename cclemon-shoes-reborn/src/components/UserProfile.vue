<template>
  <div v-if="!isAuthenticated">
    <q-btn
      @click="loginAction"
      :loading="loading"
      color="primary"
      label="登入"
    />
  </div>
  <div v-else>
    <q-btn
      @click="logoutAction"
      :loading="loading"
      color="primary"
      label="登出"
    />
  </div>
  <div class="q-mx-xs">{{ username }}</div>
</template>
<script lang="ts" setup>
defineOptions({
  name: 'UserProfile',
});
import { ref } from 'vue';
import { useQuasar } from 'quasar';
import { useAuthStore } from 'src/stores/authStore';
import { useUserStore } from 'src/stores/userStore';
import { storeToRefs } from 'pinia';

const authStore = useAuthStore();
const userStore = useUserStore();
const $q = useQuasar();
const loading = ref<boolean>(false);
const { username } = storeToRefs(userStore);
const { isAuthenticated } = storeToRefs(authStore);
const { initAuth, login, logout, useAuthorizationCode } = authStore;

const loginAction = () => {
  $q.loading.show();
  loading.value = true;
  login();
};
const logoutAction = () => {
  $q.loading.show();
  loading.value = true;
  logout();
};

const init = async () => {
  await initAuth();
  $q.loading.show();
  loading.value = true;
  await useAuthorizationCode();
  loading.value = false;
  $q.loading.hide();
};

await init();
</script>
