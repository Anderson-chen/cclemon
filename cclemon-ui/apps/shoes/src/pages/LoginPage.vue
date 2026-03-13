<template>
  <div class="login-root">
    <!-- Desktop: two-column; Mobile: single column -->
    <div class="login-container">
      <!-- Brand panel (hidden on mobile) -->
      <div class="login-brand-panel">
        <div class="brand-inner">
          <div class="brand-icon-wrap">
            <q-icon name="spa" size="48px" color="white" />
          </div>
          <h1 class="brand-title">Shoes Reborn</h1>
          <p class="brand-subtitle">洗鞋管理系統</p>
          <div class="brand-features">
            <div class="brand-feature">
              <q-icon name="check_circle" size="18px" color="teal-2" class="q-mr-sm" />
              <span>智慧訂單追蹤</span>
            </div>
            <div class="brand-feature">
              <q-icon name="check_circle" size="18px" color="teal-2" class="q-mr-sm" />
              <span>會員管理中心</span>
            </div>
            <div class="brand-feature">
              <q-icon name="check_circle" size="18px" color="teal-2" class="q-mr-sm" />
              <span>即時收益報表</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Form panel -->
      <div class="login-form-panel">
        <!-- Mobile logo (only shows on small screens) -->
        <div class="mobile-logo">
          <q-icon name="spa" size="32px" color="teal-8" />
          <span class="mobile-logo-text">Shoes Reborn</span>
        </div>

        <div class="form-inner">
          <div class="form-heading">
            <h2 class="form-title">歡迎回來</h2>
            <p class="form-subtitle">請輸入您的帳號和密碼</p>
          </div>

          <!-- Error banner -->
          <q-banner
            v-if="errorMessage"
            class="login-error-banner q-mb-lg"
            dense
            rounded
          >
            <template v-slot:avatar>
              <q-icon name="warning" color="negative" />
            </template>
            {{ errorMessage }}
          </q-banner>

          <!-- Login form -->
          <q-form
            method="post"
            @reset="onReset"
            :action="`${VUE_APP_AUTH_URL}/login`"
            @submit="onSubmit"
            class="login-form"
          >
            <q-input
              filled
              v-model="username"
              name="username"
              label="帳號"
              dense
              class="q-mb-sm"
              lazy-rules
              :rules="[(val) => !!val || '帳號不可為空']"
              bg-color="grey-1"
            >
              <template v-slot:prepend>
                <q-icon name="person_outline" color="teal-7" />
              </template>
            </q-input>

            <q-input
              filled
              type="password"
              v-model="password"
              name="password"
              label="密碼"
              dense
              class="q-mb-lg"
              lazy-rules
              :rules="[(val) => !!val || '密碼不可為空']"
              bg-color="grey-1"
            >
              <template v-slot:prepend>
                <q-icon name="lock_outline" color="teal-7" />
              </template>
            </q-input>

            <q-input type="hidden" name="_csrf" v-model="csrfToken" />

            <q-btn
              label="登入"
              type="submit"
              color="teal-8"
              class="full-width login-btn q-mb-sm"
              unelevated
            />
            <q-btn
              label="清除"
              type="reset"
              color="teal-8"
              class="full-width"
              flat
              dense
            />
          </q-form>

          <!-- SSO divider -->
          <div class="sso-divider">
            <span class="sso-divider-line"></span>
            <span class="sso-divider-text">或使用第三方帳號</span>
            <span class="sso-divider-line"></span>
          </div>

          <!-- SSO buttons -->
          <div class="sso-buttons">
            <button type="button" class="sso-btn" @click="redirectSSO('google')" aria-label="使用 Google 登入">
              <img
                src="google/signin-assets/Web/png@1x/dark/web_dark_rd_na@1x.png"
                alt="Google Sign-In"
                class="sso-btn-img"
              />
            </button>
            <button type="button" class="sso-btn sso-btn--github" @click="redirectSSO('github')" aria-label="使用 GitHub 登入">
              <img
                src="github\github-mark\github-mark.svg"
                alt="GitHub Sign-In"
                class="sso-btn-img sso-btn-img--github"
              />
              <span>GitHub</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import axios from 'axios';

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
  let csrfRes = await axios.get(`${VUE_APP_AUTH_URL}/csrf-token`, {
    withCredentials: true,
  });
  csrfToken.value = csrfRes.data.token;
});
</script>

<style scoped>
/* ── Root & Layout ──────────────────────────────────────────── */
.login-root {
  min-height: 100dvh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8fafc;
  padding: 16px;
}

.login-container {
  display: flex;
  width: 100%;
  max-width: 900px;
  min-height: 540px;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.12), 0 4px 16px rgba(0, 0, 0, 0.06);
  background: white;
}

/* ── Brand Panel ─────────────────────────────────────────────── */
.login-brand-panel {
  display: none; /* hidden on mobile */
  flex: 0 0 340px;
  background: linear-gradient(160deg, #0f766e 0%, #0d9488 60%, #14b8a6 100%);
  position: relative;
  overflow: hidden;
}

.login-brand-panel::before {
  content: '';
  position: absolute;
  width: 300px;
  height: 300px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.06);
  top: -80px;
  right: -80px;
}

.login-brand-panel::after {
  content: '';
  position: absolute;
  width: 200px;
  height: 200px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.04);
  bottom: -60px;
  left: -60px;
}

.brand-inner {
  position: relative;
  z-index: 1;
  padding: 48px 40px;
  display: flex;
  flex-direction: column;
  height: 100%;
  color: white;
}

.brand-icon-wrap {
  width: 72px;
  height: 72px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
  backdrop-filter: blur(8px);
}

.brand-title {
  font-size: 1.75rem;
  font-weight: 800;
  color: white;
  margin: 0 0 6px 0;
  letter-spacing: -0.02em;
}

.brand-subtitle {
  font-size: 0.9rem;
  color: rgba(255, 255, 255, 0.7);
  margin: 0 0 40px 0;
  font-weight: 400;
}

.brand-features {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-top: auto;
  padding-bottom: 8px;
}

.brand-feature {
  display: flex;
  align-items: center;
  font-size: 0.9rem;
  color: rgba(255, 255, 255, 0.85);
  font-weight: 500;
}

/* ── Form Panel ──────────────────────────────────────────────── */
.login-form-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 32px 24px;
  background: white;
}

.mobile-logo {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 28px;
}

.mobile-logo-text {
  font-size: 1.1rem;
  font-weight: 700;
  color: #0f172a;
}

.form-inner {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  max-width: 380px;
  width: 100%;
  margin: 0 auto;
}

.form-heading {
  margin-bottom: 28px;
}

.form-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 6px 0;
  letter-spacing: -0.01em;
}

.form-subtitle {
  font-size: 0.875rem;
  color: #64748b;
  margin: 0;
}

.login-error-banner {
  background: #fef2f2;
  border: 1px solid #fecaca;
  color: #991b1b;
  border-radius: 8px;
}

.login-form {
  margin-bottom: 20px;
}

.login-btn {
  height: 44px;
  font-size: 0.9375rem;
  font-weight: 600;
  border-radius: 8px;
  letter-spacing: 0.01em;
}

/* ── SSO Section ────────────────────────────────────────────── */
.sso-divider {
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 20px 0;
}

.sso-divider-line {
  flex: 1;
  height: 1px;
  background: #e2e8f0;
}

.sso-divider-text {
  font-size: 0.75rem;
  color: #94a3b8;
  white-space: nowrap;
  font-weight: 500;
}

.sso-buttons {
  display: flex;
  gap: 12px;
}

.sso-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 10px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  background: white;
  cursor: pointer;
  transition: all 0.15s ease;
  font-size: 0.875rem;
  font-weight: 500;
  color: #334155;
  min-height: 44px;
}

.sso-btn:hover {
  background: #f8fafc;
  border-color: #cbd5e1;
}

.sso-btn--github {
  background: #24292f;
  border-color: #24292f;
  color: white;
}

.sso-btn--github:hover {
  background: #32383f;
  border-color: #32383f;
}

.sso-btn-img {
  height: 20px;
  width: auto;
}

.sso-btn-img--github {
  filter: invert(1);
  height: 18px;
}

/* ── Responsive ─────────────────────────────────────────────── */
@media (min-width: 600px) {
  .login-form-panel {
    padding: 48px 40px;
  }

  .mobile-logo {
    margin-bottom: 32px;
  }
}

@media (min-width: 900px) {
  .login-brand-panel {
    display: flex;
  }

  .mobile-logo {
    display: none;
  }
}

@media (max-width: 599px) {
  .login-root {
    padding: 0;
    align-items: stretch;
  }

  .login-container {
    border-radius: 0;
    box-shadow: none;
    min-height: 100dvh;
  }
}
</style>
