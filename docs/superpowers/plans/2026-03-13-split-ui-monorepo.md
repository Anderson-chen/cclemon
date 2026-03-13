# cclemon-ui Monorepo 拆分實作計畫

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 將 `cclemon-ui/` 轉換為 pnpm workspace monorepo，各業務 app 獨立 build，auth 邏輯透過 `@cclemon/auth` 共用。

**Architecture:** `cclemon-ui/` 升格為 workspace 根目錄，現有 shoes-reborn 程式碼搬至 `apps/shoes/`，auth 相關 TypeScript 抽成 `packages/auth/`，健康管理獨立為 `apps/health/`，未來新業務只需在 `apps/` 新增資料夾。

**Tech Stack:** pnpm workspace, Vue 3, Quasar 2 (`@quasar/app-vite`), Pinia, TypeScript, axios

---

## 目錄結構（完成後）

```
cclemon-ui/                        ← workspace 根
├── pnpm-workspace.yaml
├── package.json                   ← 只含 workspace scripts，無業務程式碼
├── packages/
│   └── auth/                      ← @cclemon/auth 共用套件
│       ├── src/
│       │   ├── index.ts           ← 統一 export
│       │   ├── flow/
│       │   │   ├── AuthFlowImpli.ts
│       │   │   └── types.d.ts
│       │   ├── stores/
│       │   │   ├── authStore.ts
│       │   │   └── userStore.ts
│       │   └── utils/
│       │       ├── localStorage.ts
│       │       ├── sessionStorage.ts
│       │       └── parse.ts
│       ├── package.json
│       └── tsconfig.json
└── apps/
    ├── shoes/                     ← 洗鞋管理（原 cclemon-ui 內容）
    │   ├── src/
    │   ├── quasar.config.ts
    │   └── package.json
    └── health/                    ← 健康管理（新建 Quasar 專案）
        ├── src/
        ├── quasar.config.ts
        └── package.json
```

---

## Chunk 1: Workspace 根目錄初始化

### Task 1: 安裝 pnpm

- [ ] **Step 1: 安裝 pnpm**

```bash
npm install -g pnpm
```

預期輸出：`added 1 package`

- [ ] **Step 2: 確認版本**

```bash
pnpm --version
```

預期：`9.x.x` 以上

---

### Task 2: 建立 workspace 根目錄結構

**Files:**
- Create: `cclemon-ui/pnpm-workspace.yaml`
- Modify: `cclemon-ui/package.json`（改為 workspace root）

- [ ] **Step 1: 建立 workspace 設定檔**

新增 `cclemon-ui/pnpm-workspace.yaml`：

```yaml
packages:
  - 'apps/*'
  - 'packages/*'
```

- [ ] **Step 2: 改寫 workspace 根 package.json**

將 `cclemon-ui/package.json` 改為：

```json
{
  "name": "cclemon-ui-workspace",
  "version": "1.0.0",
  "private": true,
  "scripts": {
    "dev:shoes": "pnpm --filter @cclemon/shoes dev",
    "dev:health": "pnpm --filter @cclemon/health dev",
    "build:shoes": "pnpm --filter @cclemon/shoes build",
    "build:health": "pnpm --filter @cclemon/health build",
    "build:all": "pnpm --filter '@cclemon/*' build"
  }
}
```

- [ ] **Step 3: 建立 apps/ 和 packages/ 目錄**

```bash
mkdir -p cclemon-ui/apps cclemon-ui/packages
```

---

## Chunk 2: @cclemon/auth 共用套件

### Task 3: 建立 packages/auth/ 套件骨架

**Files:**
- Create: `cclemon-ui/packages/auth/package.json`
- Create: `cclemon-ui/packages/auth/tsconfig.json`
- Create: `cclemon-ui/packages/auth/src/index.ts`

- [ ] **Step 1: 建立 package.json**

新增 `cclemon-ui/packages/auth/package.json`：

```json
{
  "name": "@cclemon/auth",
  "version": "1.0.0",
  "private": true,
  "type": "module",
  "main": "./src/index.ts",
  "exports": {
    ".": "./src/index.ts"
  },
  "peerDependencies": {
    "axios": "^1.2.1",
    "pinia": "^2.0.11",
    "quasar": "^2.16.0",
    "vue": "^3.4.18"
  }
}
```

> **注意：** peerDependencies 代表「由消費方 app 提供」，不在這裡安裝，避免重複安裝。

- [ ] **Step 2: 建立 tsconfig.json**

新增 `cclemon-ui/packages/auth/tsconfig.json`：

```json
{
  "compilerOptions": {
    "target": "ESNext",
    "module": "ESNext",
    "moduleResolution": "Bundler",
    "strict": true,
    "skipLibCheck": true
  },
  "include": ["src/**/*"]
}
```

- [ ] **Step 3: 建立 src/ 目錄結構**

```bash
mkdir -p cclemon-ui/packages/auth/src/flow
mkdir -p cclemon-ui/packages/auth/src/stores
mkdir -p cclemon-ui/packages/auth/src/utils
```

---

### Task 4: 移植 auth 程式碼到 @cclemon/auth

**Files:**
- Create: `packages/auth/src/flow/types.d.ts`（從 `src/auth/index.d.ts` 複製）
- Create: `packages/auth/src/flow/AuthFlowImpli.ts`（從 `src/auth/index.ts` 改寫）
- Create: `packages/auth/src/utils/parse.ts`（從 `src/utils/parse.ts` 複製）
- Create: `packages/auth/src/utils/localStorage.ts`（從 `src/stores/localStorage.ts` 複製）
- Create: `packages/auth/src/utils/sessionStorage.ts`（從 `src/stores/sessionStorage.ts` 複製）
- Create: `packages/auth/src/stores/authStore.ts`（從 `src/stores/authStore.ts` 改寫路徑）
- Create: `packages/auth/src/stores/userStore.ts`（從 `src/stores/userStore.ts` 改寫路徑）
- Create: `packages/auth/src/index.ts`（統一 export）

- [ ] **Step 1: 複製 types.d.ts**

複製 `cclemon-ui/src/auth/index.d.ts` 到 `cclemon-ui/packages/auth/src/flow/types.d.ts`，內容不變。

- [ ] **Step 2: 建立 AuthFlowImpli.ts（修正 import 路徑）**

新增 `cclemon-ui/packages/auth/src/flow/AuthFlowImpli.ts`：

```typescript
import axios from 'axios';
import {
  openIdConfig,
  authorizeRequest,
  accessTokenRequest,
  tokenResponse,
  refreshTokenRequest,
  logoutRequest,
  userinfoResponse,
  AuthFlow,
  revokeTokenRequest,
} from './types.d';
import { objectToQueryParams, objectToFormData } from '../utils/parse';

const {
  VUE_APP_CLIENT_ID,
  VUE_APP_REDIRECT_URI,
  VUE_APP_SCOPE,
  VUE_APP_POST_LOGOUT_REDIRECT_URI,
} = process.env;

// 內部 axios 實例，僅用於 auth 端點（無 baseURL）
const authApi = axios.create({});

async function generateCodeChallenge(codeVerifier: string) {
  const buf = new TextEncoder().encode(codeVerifier);
  const hash = await crypto.subtle.digest('SHA-256', buf);
  const base64 = btoa(String.fromCharCode(...new Uint8Array(hash)))
    .replace(/\+/g, '-')
    .replace(/\//g, '_')
    .replace(/=+$/, '');
  return base64;
}

export async function initOpenIdConfig(
  authConfigEndpoint: string,
): Promise<openIdConfig> {
  try {
    const response = await authApi.get(authConfigEndpoint);
    return response.data;
  } catch (error) {
    console.error(error);
    throw error;
  }
}

export class AuthFlowImpli implements AuthFlow {
  openIdConfig: openIdConfig;
  constructor(openIdConfig: openIdConfig) {
    this.openIdConfig = openIdConfig;
  }
  async authorize(codeVerifier: string): Promise<void> {
    const request: authorizeRequest = {
      response_type: 'code',
      client_id: VUE_APP_CLIENT_ID,
      redirect_uri: VUE_APP_REDIRECT_URI,
      scope: VUE_APP_SCOPE,
      code_challenge: await generateCodeChallenge(codeVerifier),
      code_challenge_method: 'S256',
    };
    window.location.href = `${this.openIdConfig.authorization_endpoint}?${objectToQueryParams(request)}`;
  }

  async getAccessToken(code: string, codeVerifier: string): Promise<tokenResponse> {
    const request: accessTokenRequest = {
      grant_type: 'authorization_code',
      redirect_uri: VUE_APP_REDIRECT_URI,
      code: code ?? '',
      code_verifier: codeVerifier,
      client_id: VUE_APP_CLIENT_ID,
    };
    const response = await authApi.post(
      this.openIdConfig.token_endpoint,
      objectToFormData(request),
    );
    return response.data;
  }
  async getRefreshToken(refreshToken: string): Promise<tokenResponse> {
    const request: refreshTokenRequest = {
      grant_type: 'refresh_token',
      refresh_token: refreshToken,
    };
    const response = await authApi.post(
      this.openIdConfig.token_endpoint,
      objectToFormData(request),
    );
    return response.data;
  }
  async revokeToken(token: string) {
    const request: revokeTokenRequest = { token };
    await authApi.post(
      this.openIdConfig.revocation_endpoint,
      objectToFormData(request),
    );
  }
  logout(idTokenHint: string): void {
    const request: logoutRequest = {
      id_token_hint: idTokenHint,
      post_logout_redirect_uri: VUE_APP_POST_LOGOUT_REDIRECT_URI,
    };
    window.location.href = `${this.openIdConfig.end_session_endpoint}?${objectToQueryParams(request)}`;
  }
  async getUserInfo(accessToken: string): Promise<userinfoResponse> {
    const response = await authApi.get(this.openIdConfig.userinfo_endpoint, {
      headers: { Authorization: `Bearer ${accessToken}` },
    });
    return response.data;
  }
}
```

> **關鍵改動：** 移除 `import { authApi } from '../boot/axios'`，改為在套件內部建立乾淨的 `axios.create({})`。

- [ ] **Step 3: 複製 parse.ts**

複製 `cclemon-ui/src/utils/parse.ts` 到 `cclemon-ui/packages/auth/src/utils/parse.ts`，內容不變。

- [ ] **Step 4: 複製 localStorage.ts**

複製 `cclemon-ui/src/stores/localStorage.ts` 到 `cclemon-ui/packages/auth/src/utils/localStorage.ts`，內容不變。

> `import { LocalStorage } from 'quasar'` 由消費方 app 的 peerDependencies 提供，無需修改。

- [ ] **Step 5: 複製 sessionStorage.ts**

複製 `cclemon-ui/src/stores/sessionStorage.ts` 到 `cclemon-ui/packages/auth/src/utils/sessionStorage.ts`，內容不變。

- [ ] **Step 6: 建立 authStore.ts（修正 import 路徑）**

新增 `cclemon-ui/packages/auth/src/stores/authStore.ts`：

```typescript
import { defineStore } from 'pinia';
import { AuthFlowImpli, initOpenIdConfig } from '../flow/AuthFlowImpli';
import { AuthFlow, tokenResponse } from '../flow/types.d';
import { ref, computed } from 'vue';
import { useUserStore } from './userStore';
import localStorage from '../utils/localStorage';
import sessionStorage from '../utils/sessionStorage';

const { VUE_APP_AUTH_CONFIG, VUE_APP_TOKEN_KEY, VUE_APP_USER_PROFILE_KEY, VUE_APP_VERIFIER } =
  process.env;

export const useAuthStore = defineStore('auth', () => {
  const userStore = useUserStore();
  const authFlow = ref<AuthFlow>();
  const tokenInfo = ref<tokenResponse | undefined>(
    localStorage.getItem<tokenResponse>(VUE_APP_TOKEN_KEY),
  );
  const isAuthenticated = computed(() => Boolean(tokenInfo.value));
  const codeVerifier = ref('');

  function login() {
    codeVerifier.value = crypto.randomUUID();
    sessionStorage.setItem(VUE_APP_VERIFIER, codeVerifier.value);
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
      tokenInfo.value = await authFlow.value?.getAccessToken(
        code,
        sessionStorage.getItem(VUE_APP_VERIFIER),
      );
      const res = await authFlow.value?.getUserInfo(
        tokenInfo.value?.access_token ?? '',
      );
      userStore.username = res?.userName ?? '';
      localStorage.setItem(VUE_APP_TOKEN_KEY, tokenInfo.value);
      localStorage.setItem(VUE_APP_USER_PROFILE_KEY, userStore.username);
      sessionStorage.removeItem(VUE_APP_VERIFIER);
    }
  }

  async function initAuth() {
    const configRes = await initOpenIdConfig(VUE_APP_AUTH_CONFIG);
    authFlow.value = new AuthFlowImpli(configRes);
  }

  return { isAuthenticated, initAuth, login, logout, useAuthorizationCode };
});
```

- [ ] **Step 7: 建立 userStore.ts（修正 import 路徑）**

新增 `cclemon-ui/packages/auth/src/stores/userStore.ts`：

```typescript
import { defineStore } from 'pinia';
import { ref } from 'vue';
import localStorage from '../utils/localStorage';
import { tokenResponse } from '../flow/types.d';

const { VUE_APP_USER_PROFILE_KEY, VUE_APP_TOKEN_KEY } = process.env;

export const useUserStore = defineStore('user', () => {
  const username = ref<string>(localStorage.getItem(VUE_APP_USER_PROFILE_KEY));
  function getAccessToken(): string {
    return localStorage.getItem<tokenResponse>(VUE_APP_TOKEN_KEY)?.access_token;
  }
  return { username, getAccessToken };
});
```

- [ ] **Step 8: 建立統一 export 的 index.ts**

新增 `cclemon-ui/packages/auth/src/index.ts`：

```typescript
export { useAuthStore } from './stores/authStore';
export { useUserStore } from './stores/userStore';
export { AuthFlowImpli, initOpenIdConfig } from './flow/AuthFlowImpli';
export type { AuthFlow, tokenResponse } from './flow/types.d';
```

- [ ] **Step 9: Commit**

```bash
cd cclemon-ui
git add packages/
git commit -m "feat(auth): 建立 @cclemon/auth 共用套件"
```

---

## Chunk 3: shoes app 搬移

### Task 5: 將現有程式碼搬至 apps/shoes/

**Files:**
- Move: `cclemon-ui/src/` → `cclemon-ui/apps/shoes/src/`
- Move: `cclemon-ui/quasar.config.ts` → `cclemon-ui/apps/shoes/quasar.config.ts`
- Move: 所有 Quasar 設定檔至 `apps/shoes/`

- [ ] **Step 1: 建立 apps/shoes/ 目錄並搬移**

```bash
cd cclemon-ui
mkdir -p apps/shoes

# 搬移所有 Quasar 專案檔案
mv src apps/shoes/src
mv public apps/shoes/public
mv quasar.config.ts apps/shoes/quasar.config.ts
mv tsconfig.json apps/shoes/tsconfig.json
mv .eslintrc.cjs apps/shoes/.eslintrc.cjs 2>/dev/null || true
mv .prettierrc apps/shoes/.prettierrc 2>/dev/null || true
mv index.html apps/shoes/index.html 2>/dev/null || true
mv .quasar apps/shoes/.quasar 2>/dev/null || true
```

- [ ] **Step 2: 建立 apps/shoes/package.json**

新增 `cclemon-ui/apps/shoes/package.json`（從根目錄的舊 package.json 複製後調整）：

```json
{
  "name": "@cclemon/shoes",
  "version": "0.0.1",
  "description": "洗鞋管理系統",
  "productName": "shoes-reborn",
  "type": "module",
  "private": true,
  "scripts": {
    "dev": "set ENV_FILE=local&& quasar dev",
    "build": "quasar build",
    "build:dev": "set ENV_FILE=dev&& quasar build",
    "build:sit": "set ENV_FILE=sit&& quasar build",
    "build:prod": "set ENV_FILE=prod&& quasar build",
    "lint": "eslint --ext .js,.ts,.vue ./"
  },
  "dependencies": {
    "@cclemon/auth": "workspace:*",
    "@quasar/extras": "^1.17.0",
    "axios": "^1.2.1",
    "pinia": "^2.0.11",
    "quasar": "^2.16.0",
    "vue": "^3.4.18",
    "vue-i18n": "^9.2.2",
    "vue-router": "^4.0.12"
  },
  "devDependencies": {
    "@quasar/app-vite": "^2.0.0-beta.12",
    "@types/node": "^20.5.9",
    "@typescript-eslint/eslint-plugin": "^6.6.0",
    "@typescript-eslint/parser": "^6.6.0",
    "eslint": "^8.57.0",
    "eslint-plugin-vue": "^9.0.0",
    "typescript": "~5.3.0",
    "vue-tsc": "^1.8.22"
  },
  "engines": {
    "node": "^24 || ^22 || ^20 || ^18"
  }
}
```

---

### Task 6: 更新 shoes app 的 import 路徑

**Files:**
- Modify: `apps/shoes/src/stores/authStore.ts`（改用 @cclemon/auth）
- Modify: `apps/shoes/src/stores/userStore.ts`（改用 @cclemon/auth）
- Modify: `apps/shoes/src/boot/axios.ts`（移除 healthApi）
- Delete: `apps/shoes/src/auth/`（不再需要）

- [ ] **Step 1: 改寫 apps/shoes/src/stores/authStore.ts**

```typescript
export { useAuthStore } from '@cclemon/auth';
```

> 直接 re-export，其他引用 authStore 的地方不需要改路徑。

- [ ] **Step 2: 改寫 apps/shoes/src/stores/userStore.ts**

```typescript
export { useUserStore } from '@cclemon/auth';
```

- [ ] **Step 3: 移除 healthApi，簡化 boot/axios.ts**

改寫 `apps/shoes/src/boot/axios.ts`：

```typescript
import axios from 'axios';
import { useUserStore } from 'src/stores/userStore';

const { getAccessToken } = useUserStore();
const { VUE_APP_SHOES_BASE_URL } = process.env;

export const shoesApi = axios.create({
  baseURL: VUE_APP_SHOES_BASE_URL,
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
```

> **注意：** 原來的 `healthApi` 移除，env var 名稱改為 `VUE_APP_SHOES_BASE_URL`（更語意化）。要同步更新各 `.env` 檔。

- [ ] **Step 4: 刪除 shoes app 裡的 src/auth/ 目錄**

```bash
rm -rf cclemon-ui/apps/shoes/src/auth
```

- [ ] **Step 5: 刪除 shoes app 裡的 health 相關程式碼**

```bash
# 刪除 health/weight 頁面
rm cclemon-ui/apps/shoes/src/pages/HealthPage.vue
rm cclemon-ui/apps/shoes/src/pages/HealthPage.types.ts
rm cclemon-ui/apps/shoes/src/pages/WeightPage.vue
rm cclemon-ui/apps/shoes/src/pages/WeightChartPage.vue

# 刪除 health/weight API
rm -rf cclemon-ui/apps/shoes/src/api/health
rm -rf cclemon-ui/apps/shoes/src/api/weight
```

- [ ] **Step 6: 移除 shoes 路由中的 health/weight 路由**

修改 `apps/shoes/src/router/routes.ts`，刪除：

```typescript
// 刪除這三個 route 物件
{ path: '/health', ... }
{ path: '/weight', ... }
{ path: '/weight/chart', ... }
```

- [ ] **Step 7: 安裝 workspace 依賴並驗證 shoes app 可啟動**

```bash
cd cclemon-ui
pnpm install

cd apps/shoes
pnpm dev
```

預期：shoes app 在 port 5173 正常啟動，無 TS 錯誤

- [ ] **Step 8: Commit**

```bash
cd cclemon-ui
git add apps/shoes/ pnpm-workspace.yaml package.json
git commit -m "refactor(shoes): 搬移至 apps/shoes，移除 health 程式碼"
```

---

## Chunk 4: health app 建立

### Task 7: 建立 apps/health/ Quasar 專案

- [ ] **Step 1: 進入 apps/ 目錄並建立 Quasar 專案**

```bash
cd cclemon-ui/apps
pnpm create quasar health
```

選擇設定（依序）：
- Project name: `health`
- Vue component style: `Composition API with <script setup>`
- Quasar version: `Quasar v2`
- Package manager: `pnpm`

- [ ] **Step 2: 更新 apps/health/package.json 名稱與依賴**

修改 `cclemon-ui/apps/health/package.json`：

```json
{
  "name": "@cclemon/health",
  "dependencies": {
    "@cclemon/auth": "workspace:*"
  }
}
```

> 加上 `@cclemon/auth` workspace 依賴，其他依賴保持 Quasar scaffold 產生的。

---

### Task 8: 設定 health app 的 auth 整合

**Files:**
- Create: `apps/health/src/stores/authStore.ts`
- Create: `apps/health/src/stores/userStore.ts`
- Modify: `apps/health/src/boot/axios.ts`
- Create: `apps/health/src/pages/LoginPage.vue`（從 shoes app 複製後調整主題）

- [ ] **Step 1: 建立 authStore.ts（re-export）**

新增 `apps/health/src/stores/authStore.ts`：

```typescript
export { useAuthStore } from '@cclemon/auth';
```

- [ ] **Step 2: 建立 userStore.ts（re-export）**

新增 `apps/health/src/stores/userStore.ts`：

```typescript
export { useUserStore } from '@cclemon/auth';
```

- [ ] **Step 3: 建立 boot/axios.ts**

新增 `apps/health/src/boot/axios.ts`：

```typescript
import axios from 'axios';
import { useUserStore } from 'src/stores/userStore';

const { getAccessToken } = useUserStore();
const { VUE_APP_HEALTH_BASE_URL } = process.env;

export const healthApi = axios.create({
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
```

---

### Task 9: 移植 health 頁面到 health app

**Files:**
- Create: `apps/health/src/pages/HealthPage.vue`（從 shoes 複製）
- Create: `apps/health/src/pages/WeightPage.vue`（從 shoes 複製）
- Create: `apps/health/src/pages/WeightChartPage.vue`（從 shoes 複製）
- Create: `apps/health/src/api/health/`（從 shoes 複製）
- Create: `apps/health/src/api/weight/`（從 shoes 複製）

- [ ] **Step 1: 複製 health/weight 頁面**

```bash
cp cclemon-ui/apps/shoes/src/pages/HealthPage.vue cclemon-ui/apps/health/src/pages/
cp cclemon-ui/apps/shoes/src/pages/HealthPage.types.ts cclemon-ui/apps/health/src/pages/
cp cclemon-ui/apps/shoes/src/pages/WeightPage.vue cclemon-ui/apps/health/src/pages/
cp cclemon-ui/apps/shoes/src/pages/WeightChartPage.vue cclemon-ui/apps/health/src/pages/
```

- [ ] **Step 2: 複製 API 模組**

```bash
cp -r cclemon-ui/apps/shoes/src/api/health cclemon-ui/apps/health/src/api/
cp -r cclemon-ui/apps/shoes/src/api/weight cclemon-ui/apps/health/src/api/
```

- [ ] **Step 3: 更新 health app 路由**

修改 `apps/health/src/router/routes.ts`，加入 health 路由：

```typescript
import { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  { path: '/login', component: () => import('pages/LoginPage.vue') },
  { path: '/', redirect: '/health' },
  {
    path: '/health',
    component: () => import('layouts/MainLayout.vue'),
    children: [{ path: '', component: () => import('pages/HealthPage.vue') }],
  },
  {
    path: '/weight',
    component: () => import('layouts/MainLayout.vue'),
    children: [{ path: '', component: () => import('pages/WeightPage.vue') }],
  },
  {
    path: '/weight/chart',
    component: () => import('layouts/MainLayout.vue'),
    children: [{ path: '', component: () => import('pages/WeightChartPage.vue') }],
  },
  { path: '/:catchAll(.*)*', component: () => import('pages/ErrorNotFound.vue') },
];

export default routes;
```

- [ ] **Step 4: 更新 API 中的 import 路徑**

修改 `apps/health/src/api/health/health.ts`：

```typescript
import { healthApi } from '../../boot/axios';
// 其餘內容不變
```

- [ ] **Step 5: 安裝依賴並驗證 health app 可啟動**

```bash
cd cclemon-ui
pnpm install

cd apps/health
pnpm dev
```

預期：health app 在 port 5173 正常啟動

- [ ] **Step 6: Commit**

```bash
cd cclemon-ui
git add apps/health/
git commit -m "feat(health): 建立 @cclemon/health app，移植健康管理頁面"
```

---

## 驗收清單

- [ ] `pnpm dev:shoes` → 洗鞋管理正常啟動，無 health 相關路由
- [ ] `pnpm dev:health` → 健康管理正常啟動，無 shoes 相關路由
- [ ] `pnpm build:shoes` → `apps/shoes/dist/` 只含洗鞋管理程式碼
- [ ] `pnpm build:health` → `apps/health/dist/` 只含健康管理程式碼
- [ ] auth 流程（登入、token 儲存、userStore）在兩個 app 都正常運作
- [ ] 新增第三個業務：在 `apps/` 建資料夾，`package.json` 加 `@cclemon/auth: workspace:*`，完成

---

## 未來新增業務

```bash
cd cclemon-ui/apps
pnpm create quasar crm        # 建立新 app
# 在 apps/crm/package.json 加入 "@cclemon/auth": "workspace:*"
pnpm install
```

即可。不影響其他 app 的 build。
