# NestDown — 設計規格書

**日期：** 2026-03-18
**狀態：** 已核准
**技術棧：** Vue 3 + TypeScript + Vite（前端）· NestJS（後端）
**設計系統：** `design-system/nestdown/MASTER.md`（有機生物風 + 玻璃態 · 青碧療癒配色）

---

## 1. 產品概述

NestDown 是一款個人健康追蹤 PWA，目標用戶是希望建立可持續健康習慣的人——而非追求競技成績或健身目標。整體體驗平靜、無壓力、貼近生活。核心理念：*「身體正在好的方向」*。

**主要平台：** 可安裝的 PWA（iOS / Android），桌面版為輔助體驗。
**帳號系統：** 多用戶，透過現有 Auth Center — OAuth + Email/Password 登入。

---

## 2. 功能範圍

### 2.1 核心追蹤模組

| 模組 | 功能 |
|------|------|
| **飲食** | 手動新增餐點、個人食物庫、營養素分解（熱量/蛋白質/碳水/脂肪）、複製昨日餐點、消耗熱量估算 |
| **運動** | 運動紀錄、運動筆記、運動模板、計時器、消耗熱量估算、個人紀錄（PR）追蹤 |
| **體重** | 每日體重輸入、趨勢圖表、身體圍度量測（腰/臀/手臂等）、體態照片上傳＋對比 |
| **水分** | 每日飲水量追蹤（首頁快速新增小工具） |

### 2.2 首頁儀表板

- 每日健康分數（0–100，Peak-End Rule 設計：48px Lora 字型大數字）
- 快速記錄按鈕
- 今日摘要：熱量 / 運動 / 體重 / 連續天數
- 目標環（Zeigarnik Effect — 顯示未完成進度）
- 連續天數卡片（Loss Aversion 溫和提醒框架）
- 飲水小工具
- If-Then 提醒卡片（根據高風險時段條件顯示）
- 日曆視圖（近 7 天 + 月曆）

### 2.3 創新功能

| 功能 | 說明 |
|------|------|
| **E — 每日健康分數** | 綜合飲食完整度、運動、飲水、紀錄一致性計算出的複合分數，顯示於首頁最顯眼位置 |
| **F — 週/月洞察報告** | 自動生成摘要：趨勢分析、最高/最低日、行為模式，以可讀文字敘述＋圖表呈現 |
| **G — 運動模板 + 計時器** | 儲存可重複使用的運動模板，內建休息計時器並搭配推播通知 |

### 2.4 附加功能

| 功能 | 說明 |
|------|------|
| 飲水追蹤 | 首頁快速新增按鈕（+200ml / +250ml / +500ml） |
| 身體圍度 | 彈性量測類型，趨勢圖表顯示於體重頁籤內 |
| 體態照片 | 上傳＋依日期對比，顯示於體重頁籤內 |
| 個人紀錄（PR） | 每個運動項目的個人最佳紀錄歷史 |
| 目標設定 | 週/月目標，以圓環顯示於首頁 |
| PWA 推播通知 | 運動計時器結束、每日提醒、連續天數保護 |
| 消耗熱量估算 | 根據運動類型＋時間＋用戶體重估算 |
| 日曆視圖 | 熱力圖概覽，點擊日期查看當天紀錄 |
| 複製昨日餐點 | 飲食頁標題列一鍵複製，邊界情況見§11 |
| If-Then 備用計畫 | 心理學功能，在設定頁配置，於首頁顯示提醒卡片 |

### 2.5 第一版不納入範圍

- 社群 / 社交功能
- BMI 追蹤
- 離線資料同步（PWA 僅支援安裝，需要網路連線）
- 外部食物資料庫（v1 僅個人食物庫）
- AI 生成洞察報告（v1 規則式，v2 再導入 AI）

---

## 3. 導航架構

### 手機版（底部 Tab 列 — 固定）

```
[ 首頁 ] [ 飲食 ] [ 運動 ] [ 體重 ] [ 報告 ]
```

啟用中 Tab：青碧 `#0A9488` 圖示＋粗體標籤。未啟用：40% 透明度。
底部背景：`rgba(120,220,204,0.88)` + `backdrop-filter: blur(16px)`。
底部安全區：`env(safe-area-inset-bottom)`（iPhone 瀏海/底部條）。

### 平板（768–1023px）

使用**底部 Tab 列**（與手機相同）。此斷點不顯示側欄。統計卡片改為 2 欄網格排版。`PageShell.vue` 在 `viewport < 1024px` 時渲染 `BottomNav`。

### 桌面（≥ 1024px — 左側欄）

同樣 5 個模組改為垂直側欄呈現。側欄寬度：固定 220px。內容區 `margin-left: 220px`。
側欄背景：青碧漸層上的玻璃卡片。

### 各頁面入口

- **首頁**：儀表板——分數 + 摘要 + 快速記錄
- **飲食**：今日餐點列表 + 新增餐點 → 食物搜尋 / 手動輸入 / 個人庫
- **運動**：今日運動 + 新增運動 → 使用模板或手動
- **體重**：體重圖表 + 新增體重 + 圍度 + 體態照片
- **報告**：週/月洞察 + 圖表

---

## 4. 設計系統

設計 Token 定義於 `design-system/nestdown/MASTER.md`。重點摘要：

| Token | 數值 |
|-------|------|
| 主色 | `#0A9488` |
| 行動按鈕（CTA） | `#E86010` |
| 頁面背景 | `#F0FDFA` |
| 文字色 | `#083830` |
| 標題字型 | Lora |
| 內文字型 | Raleway |
| 圓角 | 有機風（各角不一致） |
| 玻璃透明度 | `rgba(255,255,255,0.32)` + `blur(18px)` |
| 最小點擊區域 | 44×44px |
| 過渡動畫 | 150–300ms |

---

## 5. 元件架構

### 5.1 頁面元件（views）

```
src/views/
  HomeView.vue            # 首頁儀表板
  DietView.vue            # 飲食紀錄 + 複製昨日按鈕
  DietAddView.vue         # 新增餐點底部 Sheet / 頁面
  ExerciseView.vue        # 運動紀錄 + If-Then 入口
  ExerciseAddView.vue     # 新增運動 / 選擇模板
  WeightView.vue          # 體重 + 圍度 + 體態照片
  ReportView.vue          # 洞察報告 + 圖表
  SettingsView.vue        # 個人資料、目標、通知、If-Then 計畫
  auth/
    LoginView.vue         # Email/密碼表單 + OAuth 按鈕
    AuthCallbackView.vue  # 處理 OAuth 重導向，見§9
```

**LoginView 版面：** 青碧漸層背景上置中的卡片。欄位：Email + 密碼。按鈕：「登入」（橘色 CTA）、「使用 Google 登入」（玻璃次要）、「使用 Apple 登入」（玻璃次要）。附上「前往註冊」連結。錯誤狀態：在失敗欄位下方顯示行內錯誤訊息。

**AuthCallbackView：** 無可見 UI，顯示置中 Loading 動畫。掛載時讀取 `?code=` / `?token=` 查詢參數，呼叫 `authStore.handleCallback()`，成功則導向 `/home`，失敗則導向 `/auth/login?error=callback_failed`。

### 5.2 共用元件

```
src/components/
  layout/
    BottomNav.vue           # 手機/平板底部 Tab 列（< 1024px 顯示）
    SidebarNav.vue          # 桌面左側欄（≥ 1024px 顯示）
    PageShell.vue           # 包覆所有頁面：套用正確導航 + padding-bottom
  home/
    HealthScoreCard.vue     # 大型分數展示（Peak-End Rule — 48px Lora）
    DailySummaryGrid.vue    # 4 格統計（熱量/運動/體重/連續天數）
    GoalRings.vue           # 圓形目標進度環（Zeigarnik Effect）
    StreakCard.vue          # 連續天數卡片（Loss Aversion 框架）
    QuickLogBar.vue         # 快速新增捷徑按鈕
    WaterWidget.vue         # 首頁飲水快速新增小工具
    IfThenReminderCard.vue  # 高風險時段條件顯示的提醒卡片
  diet/
    MealCard.vue            # 單筆餐點顯示
    FoodSearchModal.vue     # 食物搜尋 / 個人食物庫
    NutritionBar.vue        # 營養素進度條
    CopyYesterdayButton.vue # 飲食頁標題欄動作按鈕，見§11
  exercise/
    WorkoutCard.vue         # 運動紀錄項目
    ExerciseTimer.vue       # 休息計時器（含推播通知）
    TemplateSelector.vue    # 選擇已儲存的運動模板
    PRBadge.vue             # 個人紀錄（PR）標章
  weight/
    WeightChart.vue         # 折線圖（vue-chartjs）
    MeasurementForm.vue     # 身體圍度輸入表單
    BodyPhotoUpload.vue     # 上傳照片；進入 BodyPhotoCompare
    BodyPhotoCompare.vue    # 日期並排對比
  report/
    InsightNarrative.vue    # 規則式文字摘要（v1）
    TrendChart.vue          # 週/月趨勢圖
    HeatmapCalendar.vue     # 熱力日曆視圖
  shared/
    GlassCard.vue           # 可重用玻璃態卡片包裝
    OrangeButton.vue        # CTA 按鈕（#E86010）
    GlassButton.vue         # 次要玻璃按鈕
    InputField.vue          # 有焦點光圈的輸入框
    BadgeTag.vue            # 膠囊形標籤
    BottomSheet.vue         # 底部滑出 Modal（300ms 緩動）
    LoadingSkeleton.vue     # 內容佔位骨架
    AppToast.vue            # Toast 通知（成功/錯誤/資訊）
```

### 5.3 載入狀態策略

- **頁面初次載入：** 全頁骨架（`LoadingSkeleton.vue`），形狀對應實際內容（分數卡形狀、網格形狀）。
- **操作回應（新增/更新/刪除）：** 樂觀 UI — 立即更新本地 Store，API 錯誤時還原並顯示 Toast。
- **報告生成：** 在 `ReportView` 內容區顯示 Spinner（非全頁）。
- **背景抓取：** 靜默進行；超過 3 秒才顯示輕量行內 Spinner。
- `uiStore.isLoading` 使用 per-key Map：`{ [key: string]: boolean }`，不使用單一全域旗標。

### 5.4 錯誤處理規範

所有 API 呼叫遵循以下統一模式：

```typescript
// Store 內的標準錯誤處理器
async function apiAction() {
  uiStore.setLoading('action-key', true)
  try {
    const result = await api.someCall()
    // 更新 Store 狀態
  } catch (err) {
    const message = parseApiError(err) // 提取用戶可讀的錯誤訊息
    uiStore.showToast({ type: 'error', message })
    // 若為樂觀更新：還原本地狀態
  } finally {
    uiStore.setLoading('action-key', false)
  }
}
```

`parseApiError(err)` 定義於 `src/utils/apiError.ts`：
- 401 → 導向 `/auth/login`（Token 過期）
- 403 → 「權限不足」
- 404 → 「找不到資料」
- 422 → 使用伺服器回傳的 `message` 欄位
- 5xx → 「伺服器錯誤，請稍後再試」
- 網路錯誤 → 「網路連線失敗」

`AppToast.vue` 渲染於 `PageShell` 頂部；成功/資訊類 3 秒後自動消失；錯誤類須手動關閉。

### 5.5 Store（Pinia — 組合式 API `defineStore`）

```
src/stores/
  authStore.ts          # Session、OAuth Token、handleCallback()、logout()
  dietStore.ts          # 餐點、食物庫、copyYesterday()
  exerciseStore.ts      # 運動、模板、PR
  weightStore.ts        # 體重紀錄、圍度
  waterStore.ts         # 每日飲水量
  photoStore.ts         # 體態照片 metadata
  reportStore.ts        # 快取的洞察資料
  goalStore.ts          # 用戶目標
  ifThenStore.ts        # If-Then 計畫 + 高風險時段
  uiStore.ts            # isLoading Map、Toast 佇列、Modal 狀態
```

---

## 6. 資料模型（前端 TypeScript）

```typescript
// 用戶
interface User {
  id: string
  name: string
  email: string
  avatarUrl?: string
  birthdate?: string
  height?: number           // 公分
  startWeight?: number      // 公斤
  goals: UserGoals
}
interface UserGoals {
  dailyCalories?: number    // 每日熱量目標（大卡）
  weeklyExerciseDays?: number
  dailyWaterMl?: number
  targetWeight?: number     // 公斤
}

// 飲食
interface Meal {
  id: string
  userId: string
  date: string              // YYYY-MM-DD
  type: 'breakfast' | 'lunch' | 'dinner' | 'snack'
  foods: FoodEntry[]
  createdAt: string
}
interface FoodEntry {
  foodId?: string           // 來自個人食物庫
  name: string
  calories: number
  protein: number
  carbs: number
  fat: number
  amount: number            // 公克
}

// 運動
interface Workout {
  id: string
  userId: string
  date: string
  exercises: ExerciseEntry[]
  notes?: string
  totalMinutes: number
  estimatedCaloriesBurned: number
}
interface ExerciseEntry {
  name: string
  sets?: number
  reps?: number
  weight?: number           // 公斤
  durationMinutes?: number
  isPR?: boolean
}

// 體重
interface WeightEntry {
  id: string
  userId: string
  date: string
  weight: number            // 公斤
  measurements?: BodyMeasurements
}
interface BodyMeasurements {
  waist?: number            // 腰圍（公分）
  hip?: number              // 臀圍
  chest?: number            // 胸圍
  arm?: number              // 手臂圍
  thigh?: number            // 大腿圍
}

// 飲水
interface WaterEntry {
  id: string
  userId: string
  date: string
  totalMl: number
  logs: { time: string; ml: number }[]
}

// 健康分數
interface DayScore {
  date: string
  score: number             // 0–100，以下四項加權總和
  breakdown: {
    diet: number            // 35%：熱量目標達成度（±10% 內 = 100 分）
    exercise: number        // 35%：有紀錄運動 = 100，無 = 0
    water: number           // 20%：每日飲水目標達成百分比
    consistency: number     // 10%：過去 7 天有記錄天數比例（例：5/7 = 71）
  }
}
// 計算公式：score = diet×0.35 + exercise×0.35 + water×0.20 + consistency×0.10

// If-Then 計畫
interface IfThenPlan {
  id: string
  trigger: string           // 「如果今天下班太晚...」
  action: string            // 「那我就在家做 15 分鐘拉伸」
  category: 'exercise' | 'diet' | 'general'
  atRiskWindowStart: string // HH:mm，例：「18:00」
  atRiskWindowEnd: string   // HH:mm，例：「21:00」
  active: boolean
}
// 當前時間落在啟用計畫的時間窗口內時，首頁顯示 IfThenReminderCard

// 體態照片
interface BodyPhoto {
  id: string
  userId: string
  date: string
  url: string
  thumbnailUrl: string
  angle?: 'front' | 'side' | 'back'  // 正面 / 側面 / 背面
}
```

---

## 7. 路由設定

```typescript
const routes = [
  { path: '/', redirect: '/home' },
  { path: '/home',            component: HomeView,         meta: { tab: 'home' } },
  { path: '/diet',            component: DietView,         meta: { tab: 'diet' } },
  { path: '/diet/add',        component: DietAddView,      meta: { tab: 'diet', hideNav: true } },
  { path: '/exercise',        component: ExerciseView,     meta: { tab: 'exercise' } },
  { path: '/exercise/add',    component: ExerciseAddView,  meta: { tab: 'exercise', hideNav: true } },
  { path: '/weight',          component: WeightView,       meta: { tab: 'weight' } },
  { path: '/report',          component: ReportView,       meta: { tab: 'report' } },
  { path: '/settings',        component: SettingsView },
  { path: '/auth/login',      component: LoginView,        meta: { public: true } },
  { path: '/auth/callback',   component: AuthCallbackView, meta: { public: true } },
]
```

**Auth 守衛：** 導航守衛檢查 `authStore.isAuthenticated`。若為 false 且路由非 `meta.public`，重導向至 `/auth/login?redirect=<目標路徑>`。登入成功後重導回 `?redirect` 或 `/home`。

---

## 8. 後端整合（NestJS）

前端呼叫現有 NestJS 後端（`nest-down` 專案）。所有 HTTP 請求透過 `src/api/` 服務層使用 Axios 發送。

```typescript
// src/api/
  auth.ts          # login()、oauthLogin()、refreshToken()、logout()
  diet.ts          # getMeals()、addMeal()、updateMeal()、deleteMeal()、copyYesterday()、getFoodLibrary()、addFood()
  exercise.ts      # getWorkouts()、addWorkout()、updateWorkout()、deleteWorkout()、getTemplates()、saveTemplate()、getPRs()
  weight.ts        # getWeightEntries()、addWeight()、updateWeight()、deleteWeight()
  water.ts         # getWaterEntry()、logWater()
  photo.ts         # uploadPhoto()、getPhotos()、deletePhoto()
  report.ts        # getWeeklyReport()、getMonthlyReport()
  score.ts         # getDayScore()、getScoreHistory()
  goals.ts         # getGoals()、updateGoals()
  ifThen.ts        # getPlans()、createPlan()、updatePlan()、deletePlan()
  notifications.ts # subscribe(subscription: PushSubscription)、unsubscribe()
```

**Axios 實例**（`src/api/client.ts`）：
- `baseURL`：來自環境變數 `VITE_API_BASE_URL`
- 請求攔截器：從 `localStorage('nestdown_token')` 取得 Token，附加至 `Authorization: Bearer <token>`
- 回應攔截器：401 → 呼叫 `authStore.logout()` → 導向 `/auth/login`；其他錯誤 → 以標準化 `AppError` 形式重新拋出

---

## 9. 登入驗證流程

### Email / 密碼登入

1. 用戶在 `LoginView` 提交表單
2. `authStore.login(email, password)` → 呼叫 `api/auth.login()`
3. 成功：Token 存入 `localStorage('nestdown_token')`，設定 `authStore.user`，導向 `/home`
4. 失敗：`LoginView` 在失敗欄位下方顯示行內錯誤訊息

### OAuth 登入（Google / Apple）

1. 用戶點擊 `LoginView` 的 OAuth 按鈕
2. `authStore.startOAuth(provider)` → 瀏覽器重導向至 NestJS OAuth 端點（例：`/auth/google`）
3. NestJS 處理 OAuth 流程 → 重導回前端 `/auth/callback?token=<jwt>`
4. `AuthCallbackView` 掛載時從 `route.query` 讀取 `?token=`，呼叫 `authStore.handleCallback(token)`
5. `handleCallback`：儲存 Token、取得用戶資料、導向 `/home`（或 `?redirect` 指定路徑）
6. Token 無效或缺失：導向 `/auth/login?error=callback_failed`，`LoginView` 顯示橫幅錯誤訊息

---

## 10. PWA 設定

```typescript
// vite.config.ts — VitePWA plugin
{
  registerType: 'autoUpdate',
  manifest: {
    name: 'NestDown',
    short_name: 'NestDown',
    theme_color: '#0A9488',
    background_color: '#F0FDFA',
    display: 'standalone',
    orientation: 'portrait',
    icons: [
      { src: '/icons/pwa-192.png', sizes: '192x192', type: 'image/png' },
      { src: '/icons/pwa-512.png', sizes: '512x512', type: 'image/png' }
    ]
  },
  workbox: {
    // 僅快取靜態資源，不做離線資料同步
    globPatterns: ['**/*.{js,css,html,ico,png,svg,woff2}'],
    runtimeCaching: []
  }
}
```

### Web Push 推播通知

**前端設定**（`useNotifications.ts`）：
1. 用戶在設定頁選擇開啟，呼叫 `Notification.requestPermission()`
2. 授權後：`registration.pushManager.subscribe({ userVisibleOnly: true, applicationServerKey: VITE_VAPID_PUBLIC_KEY })`
3. 將產生的 `PushSubscription` 物件透過 `api/notifications.subscribe()` 傳送至後端
4. 訂閱資訊儲存於 `notificationStore.subscription`

**VAPID 金鑰：** 前端使用環境變數 `VITE_VAPID_PUBLIC_KEY`（公鑰）。私鑰由後端持有，絕不暴露於前端。

**後端觸發情境（NestJS）：**
- 運動計時器：前端送出計時完成事件 → 後端推播通知
- 每日提醒：Cron 任務在用戶設定時間觸發 → 後端推播
- 連續天數保護：Cron 於每晚 9 點檢查未記錄的用戶 → 後端推播

**推播訂閱 API：** `POST /notifications/subscribe`（儲存訂閱）、`DELETE /notifications/subscribe`（取消訂閱），資料儲存於後端資料庫的用戶記錄中。

---

## 11. UX 邊界情況處理

### 複製昨日餐點

- **觸發位置：** `CopyYesterdayButton.vue` 在 `DietView` 標題列，僅在今日尚無餐點紀錄時顯示
- **昨日有餐點：** 顯示確認對話框「複製昨天的 3 餐到今天？」→ 確認後呼叫 `dietStore.copyYesterday()` → 樂觀更新 → 成功 Toast
- **昨日無餐點：** 按鈕隱藏（在 `dietStore.loadTodayDiet()` 中一併取得昨日資料）
- **今日已有餐點：** 按鈕隱藏，不顯示複製入口

### 飲水記錄

- `WaterWidget.vue` 顯示目前總量 / 目標（例：「1,200 / 2,000 ml」）
- 三個快速新增按鈕：+200ml / +250ml / +500ml
- 長按任意按鈕開啟自訂輸入（數字輸入框）
- 無獨立路由，所有互動在小工具內完成；自訂數量透過底部 Sheet 輸入

---

## 12. 心理學功能：If-Then 備用計畫

在 `SettingsView` 設定。用戶定義：
1. **觸發情境** — 現實中可能發生的阻礙（「如果今天下班太晚...」）
2. **備用行動** — 更輕量的替代方案（「那我就在家做 20 分鐘拉伸」）
3. **類別** — 運動 / 飲食 / 一般
4. **高風險時段** — 觸發情境最可能發生的時間區間（例：18:00–21:00）

首頁 `IfThenReminderCard.vue`：
- 當前時間落入任一啟用計畫的 `atRiskWindowStart`–`atRiskWindowEnd` 時才渲染
- 每次顯示一個計畫（依優先順序最高者）
- 兩個動作按鈕：「我會這樣做」（關閉卡片）、「查看計畫」（導向設定頁 If-Then 區塊）
- 語氣絕不帶評判——將備用選項呈現為正向的主動選擇，而非失敗後的退而求其次

另可從 `ExerciseView` 標題列的「備用計畫」連結進入設定頁相關區塊。

---

## 13. 體態照片

無獨立路由。照片功能整合於 `WeightView` 的頁籤中：

```
WeightView 頁籤：[ 體重 | 體圍 | 體態照片 ]
```

**上傳流程：** `BodyPhotoUpload.vue` → 選擇檔案 → 預覽 → 確認上傳 → POST 至後端 → 加入照片列表。

**對比流程：** `BodyPhotoCompare.vue`（WeightView 內的 Modal 或全螢幕 Sheet）→ 選擇兩個日期 → 並排顯示。支援依角度篩選（正面 / 側面 / 背面）。

---

## 14. 無障礙設計（Accessibility）

- WCAG AA — 所有文字最低對比度 4.5:1
- 所有可點擊元素：`cursor: pointer`
- 焦點光圈：`:focus-visible` 使用 `box-shadow: 0 0 0 3px rgba(10,148,136,0.4)`
- 所有圖片：`alt` 屬性；體態照片以日期為 alt 文字
- 表單輸入：`<label>` 關聯（顯示或 `aria-label`）
- 底部導覽：`role="navigation"`、`aria-label="主選單"`；啟用中 Tab 加上 `aria-current="page"`
- `prefers-reduced-motion`：停用所有動畫與過渡效果
- PWA 滾動容器：`overscroll-behavior: contain`

---

## 15. 響應式斷點

| 斷點 | 導航 | 版面 |
|------|------|------|
| `< 768px` | 底部 Tab 列 | 單欄，全寬卡片 |
| `768px – 1023px` | 底部 Tab 列 | 統計卡片 2 欄網格 |
| `≥ 1024px` | 左側欄（220px） | 多欄內容區 |

`PageShell.vue` 邏輯：
```typescript
const isMobile = computed(() => viewport.width < 1024)
// isMobile 時渲染 BottomNav，否則渲染 SidebarNav
// 顯示 BottomNav 時加上 padding-bottom: 64px + safe-area
```

---

## 16. 檔案結構

```
src/
  views/
    auth/              # LoginView、AuthCallbackView
    HomeView.vue
    DietView.vue / DietAddView.vue
    ExerciseView.vue / ExerciseAddView.vue
    WeightView.vue
    ReportView.vue
    SettingsView.vue
  components/
    layout/            # BottomNav、SidebarNav、PageShell
    home/              # HealthScoreCard、DailySummaryGrid、GoalRings、StreakCard、QuickLogBar、WaterWidget、IfThenReminderCard
    diet/              # MealCard、FoodSearchModal、NutritionBar、CopyYesterdayButton
    exercise/          # WorkoutCard、ExerciseTimer、TemplateSelector、PRBadge
    weight/            # WeightChart、MeasurementForm、BodyPhotoUpload、BodyPhotoCompare
    report/            # InsightNarrative、TrendChart、HeatmapCalendar
    shared/            # GlassCard、OrangeButton、GlassButton、InputField、BadgeTag、BottomSheet、LoadingSkeleton、AppToast
  composables/
    useHealthScore.ts  # 分數計算邏輯（見§6 DayScore 公式）
    useStreak.ts
    useGoals.ts
    useNotifications.ts
    useIfThen.ts
    useViewport.ts     # 響應式視窗寬度（用於導航切換）
  stores/              # 見§5.5
  api/                 # 見§8
  router/
    index.ts           # 路由設定 + Auth 守衛
  utils/
    apiError.ts        # parseApiError() 函式
    scoreCalc.ts       # 健康分數計算公式
    dateUtils.ts       # 日期格式化工具
  types/
    models.ts          # 所有 TypeScript 介面（見§6）
  assets/
    main.css           # @import 字型、CSS 變數、全域 Reset
public/
  icons/               # PWA 圖示（192、512）
design-system/
  nestdown/
    MASTER.md          # 全域設計 Token
    pages/             # 頁面特定覆寫設定
docs/
  superpowers/
    specs/
      2026-03-18-nestdown-design.md   # 本文件
```

---

## 17. 待決事項 / v2 規劃

| 項目 | v1 決策 | v2 規劃 |
|------|---------|---------|
| 健康分數演算法 | 規則式加權（見§6） | 依用戶行為 ML 調整權重 |
| 洞察報告生成 | 規則式文字 | 導入 Claude API 生成敘述 |
| 食物資料庫 | 僅個人食物庫 | 串接外部營養素 API |
| 體態照片儲存 | 後端上傳 → 回傳 URL | CDN + 圖片壓縮優化 |
| 社群功能 | 不納入範圍 | 選擇性好友互相督促配對 |
