<template>
  <div
    class="login-page flex flex-center"
    style="height: 100vh; background: linear-gradient(135deg, #667eea, #764ba2)"
  >
    <q-card
      class="q-pa-lg shadow-12"
      style="
        width: 400px;
        border-radius: 20px;
        background: rgba(255, 255, 255, 0.95);
      "
    >
      <q-card-section class="q-px-xl q-py-lg">
        <div class="text-h5 text-center q-mb-lg" style="font-weight: 600">
          登入
        </div>

        <!-- 登入失敗訊息 -->
        <q-banner
          v-if="errorMessage"
          class="q-mb-md"
          dense
          color="negative"
          icon="warning"
        >
          {{ errorMessage }}
        </q-banner>

        <!-- Spring Authorization Server 標準表單 -->
        <q-form
          method="post"
          @reset="onReset"
          :action="`${VUE_APP_AUTH_URL}/login`"
          @submit="onSubmit"
        >
          <q-input
            filled
            v-model="username"
            name="username"
            label="帳號"
            dense
            outlined
            class="q-mb-sm"
            lazy-rules
            :rules="[(val) => !!val || '帳號不可為空']"
            prepend-inner-icon="person"
          />
          <q-input
            filled
            type="password"
            v-model="password"
            name="password"
            label="密碼"
            dense
            outlined
            class="q-mb-md"
            lazy-rules
            :rules="[(val) => !!val || '密碼不可為空']"
            prepend-inner-icon="lock"
          />
          <q-input type="hidden" name="_csrf" v-model="csrfToken" />
          <q-btn
            label="登入"
            type="submit"
            color="primary"
            class="full-width q-mb-md"
            style="font-weight: 600"
          />
          <q-btn
            label="清除"
            type="reset"
            color="primary"
            class="full-width q-mb-md"
            style="font-weight: 600"
            flat
          />
        </q-form>
        <div class="flex justify-center q-gutter-sm">
          <!-- google -->
          <q-btn
            round
            dense
            flat
            style="background: #fff; width: 40px; height: 40px; padding: 0"
            @click="redirectSSO('google')"
          >
            <img
              src="google/signin-assets/Web/png@1x/dark/web_dark_rd_na@1x.png"
              alt="Google Sign-In"
            />
          </q-btn>
          <!-- github -->
          <q-btn
            round
            dense
            flat
            style="background: #fff; width: 40px; height: 40px; padding: 0"
            @click="redirectSSO('github')"
          >
            <img
              src="github\github-mark\github-mark.svg"
              alt="Github Sign-In"
              style="background: #fff; width: 40px; height: 40px"
            />
          </q-btn>
        </div>
      </q-card-section>
    </q-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { authApi } from 'src/boot/axios';

const errorMessage = ref('');
const username = ref(null);
const password = ref(null);
const { VUE_APP_AUTH_URL } = process.env;
const csrfToken = ref(null);
const redirectSSO = (provider) => {
  window.location.href = `${VUE_APP_AUTH_URL}/oauth2/authorization/${provider}`;
};

const onSubmit = async (evt) => {
  evt.target.submit();
};

const onReset = () => {
  username.value = null;
  password.value = null;
};

onMounted(async () => {
  let csrfRes = await authApi.get(`${VUE_APP_AUTH_URL}/csrf-token`, {
    withCredentials: true,
  });
  csrfToken.value = csrfRes.data.token;
});
</script>

<style scoped>
.login-page {
  animation: fadeIn 0.8s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
