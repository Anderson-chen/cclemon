<script setup lang="ts">
definePageMeta({ layout: false })

const route = useRoute()
const auth = useAuthStore()

const loading = ref(false)
const error = ref(route.query.error === 'callback_failed' ? 'OAuth 登入失敗，請再試一次' : '')

async function handleLogin() {
  loading.value = true
  error.value = ''
  try {
    auth.login()
    // login() 會觸發 redirect 到 /auth/authorize，不會回到這裡
  } catch {
    error.value = '無法連接至登入伺服器，請稍後再試'
    loading.value = false
  }
}
</script>

<template>
  <div class="login-root">
    <div class="login-container">
      <!-- 左側品牌面板（桌機顯示） -->
      <div class="login-brand-panel">
        <div class="brand-inner">
          <div class="brand-icon-wrap">
            <span class="brand-icon">N</span>
          </div>
          <h1 class="brand-title">NestDown</h1>
          <p class="brand-subtitle">你的健康旅程，從這裡開始</p>
          <div class="brand-features">
            <div class="brand-feature">
              <svg
                width="16"
                height="16"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2.5"
              >
                <polyline points="20 6 9 17 4 12" />
              </svg>
              <span>飲食與卡路里追蹤</span>
            </div>
            <div class="brand-feature">
              <svg
                width="16"
                height="16"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2.5"
              >
                <polyline points="20 6 9 17 4 12" />
              </svg>
              <span>運動記錄與體重管理</span>
            </div>
            <div class="brand-feature">
              <svg
                width="16"
                height="16"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2.5"
              >
                <polyline points="20 6 9 17 4 12" />
              </svg>
              <span>健康評分與趨勢報告</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右側表單面板 -->
      <div class="login-form-panel">
        <div class="mobile-logo">
          <div class="logo-mark">N</div>
          <span class="mobile-logo-text">NestDown</span>
        </div>

        <div class="form-inner">
          <div class="form-heading">
            <h2 class="form-title">歡迎回來</h2>
            <p class="form-subtitle">請透過您的帳號登入</p>
          </div>

          <div v-if="error" class="error-banner">
            <svg
              width="16"
              height="16"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
            >
              <circle cx="12" cy="12" r="10" />
              <path d="M12 8v4m0 4h.01" />
            </svg>
            {{ error }}
          </div>

          <button
            class="login-btn"
            :class="{ 'login-btn--loading': loading }"
            :disabled="loading"
            @click="handleLogin"
          >
            <span v-if="loading" class="btn-spinner" />
            <span v-else>前往登入</span>
          </button>

          <p class="login-hint">
            點擊後將跳轉至安全登入頁面，<br />
            支援帳號密碼及第三方登入。
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-root {
  min-height: 100dvh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0fdfa;
  padding: 16px;
}

.login-container {
  display: flex;
  width: 100%;
  max-width: 860px;
  min-height: 520px;
  border-radius: 20px;
  overflow: hidden;
  box-shadow:
    0 20px 60px rgba(10, 148, 136, 0.15),
    0 4px 16px rgba(0, 0, 0, 0.06);
  background: white;
}

.login-brand-panel {
  display: none;
  flex: 0 0 320px;
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
  width: 68px;
  height: 68px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
  backdrop-filter: blur(8px);
}

.brand-icon {
  font-family: 'Lora', serif;
  font-size: 32px;
  font-weight: 700;
  color: white;
}

.brand-title {
  font-family: 'Lora', serif;
  font-size: 1.75rem;
  font-weight: 800;
  color: white;
  margin: 0 0 6px 0;
  letter-spacing: -0.02em;
}

.brand-subtitle {
  font-size: 0.875rem;
  color: rgba(255, 255, 255, 0.7);
  margin: 0 0 40px 0;
}

.brand-features {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-top: auto;
}

.brand-feature {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 0.875rem;
  color: rgba(255, 255, 255, 0.85);
  font-weight: 500;
}

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
  gap: 10px;
  margin-bottom: 28px;
}

.logo-mark {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #0a9488, #14b8a6);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: 'Lora', serif;
  font-size: 20px;
  font-weight: 700;
  color: white;
}

.mobile-logo-text {
  font-family: 'Lora', serif;
  font-size: 1.1rem;
  font-weight: 700;
  color: #083830;
}

.form-inner {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  max-width: 360px;
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

.error-banner {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #fef2f2;
  border: 1px solid #fecaca;
  border-radius: 10px;
  padding: 10px 14px;
  font-size: 13px;
  color: #991b1b;
  margin-bottom: 20px;
}

.login-btn {
  width: 100%;
  height: 48px;
  background: linear-gradient(135deg, #0a9488, #0d9488);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 0.9375rem;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition:
    opacity 0.15s ease,
    transform 0.1s ease;
  letter-spacing: 0.01em;
}

.login-btn:hover:not(:disabled) {
  opacity: 0.92;
  transform: translateY(-1px);
}

.login-btn:active:not(:disabled) {
  transform: translateY(0);
}

.login-btn--loading,
.login-btn:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}

.btn-spinner {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255, 255, 255, 0.4);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.login-hint {
  margin-top: 16px;
  font-size: 12px;
  color: #94a3b8;
  text-align: center;
  line-height: 1.6;
}

@media (min-width: 600px) {
  .login-form-panel {
    padding: 48px 40px;
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
