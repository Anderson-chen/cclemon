<script setup lang="ts">
definePageMeta({ middleware: ['auth'] })
import type { IfThenPlan } from '@/types/models'

const router = useRouter()
const auth = useAuthStore()
const goals = useGoalStore()
const ifThen = useIfThenStore()
const { isEnabled, toggle: toggleNotif, isSupported } = useNotifications()

onMounted(async () => {
  await Promise.all([
    goals.fetchGoals(),
    ifThen.fetchPlans(),
  ])
})

// Goals form
const goalsForm = reactive({
  dailyCalories: goals.goals.dailyCalories || 2000,
  weeklyExerciseDays: goals.goals.weeklyExerciseDays || 3,
  dailyWaterMl: goals.goals.dailyWaterMl || 2000,
  targetWeight: goals.goals.targetWeight || undefined as number | undefined,
})

async function saveGoals() {
  await goals.updateGoals({ ...goalsForm })
}

// If-Then plan form
const showPlanSheet = ref(false)
const editingPlan = ref<IfThenPlan | null>(null)
const planForm = reactive({
  trigger: '',
  action: '',
  category: 'general' as IfThenPlan['category'],
  atRiskWindowStart: '17:00',
  atRiskWindowEnd: '20:00',
  active: true,
})

function openNewPlan() {
  editingPlan.value = null
  planForm.trigger = ''
  planForm.action = ''
  planForm.category = 'general'
  planForm.atRiskWindowStart = '17:00'
  planForm.atRiskWindowEnd = '20:00'
  planForm.active = true
  showPlanSheet.value = true
}

function openEditPlan(plan: IfThenPlan) {
  editingPlan.value = plan
  planForm.trigger = plan.trigger
  planForm.action = plan.action
  planForm.category = plan.category
  planForm.atRiskWindowStart = plan.atRiskWindowStart
  planForm.atRiskWindowEnd = plan.atRiskWindowEnd
  planForm.active = plan.active
  showPlanSheet.value = true
}

async function savePlan() {
  if (!planForm.trigger || !planForm.action) return
  if (editingPlan.value) {
    await ifThen.updatePlan(editingPlan.value.id, { ...planForm })
  } else {
    await ifThen.addPlan({ ...planForm })
  }
  showPlanSheet.value = false
}

function handleLogout() {
  auth.logout()
  navigateTo('/auth/login')
}

const categoryLabels: Record<string, string> = {
  exercise: '運動',
  diet: '飲食',
  general: '一般',
}

const categoryColors: Record<string, 'teal' | 'orange' | 'blue'> = {
  exercise: 'teal',
  diet: 'orange',
  general: 'blue',
}
</script>

<template>
  <PageShell>
    <div class="screen-bg" aria-hidden="true">
      <div class="blob blob-1" />
      <div class="blob blob-2" />
    </div>

    <div class="settings-content">
      <!-- Header -->
      <header class="page-header">
        <h1 class="page-title">設定</h1>
      </header>

      <!-- Profile card -->
      <GlassCard class="section">
        <div class="profile-card">
          <div class="profile-avatar">
            <img v-if="auth.user?.avatarUrl" :src="auth.user.avatarUrl" :alt="auth.user?.name" />
            <span v-else>{{ (auth.user?.name ?? '？').charAt(0) }}</span>
          </div>
          <div class="profile-info">
            <p class="profile-name">{{ auth.user?.name }}</p>
            <p class="profile-email">{{ auth.user?.email }}</p>
          </div>
        </div>
      </GlassCard>

      <!-- Goals -->
      <GlassCard class="section">
        <h2 class="section-title">目標設定</h2>
        <div class="goals-form">
          <div class="goals-grid">
            <InputField
              v-model="goalsForm.dailyCalories"
              type="number"
              label="每日熱量目標 (kcal)"
              placeholder="2000"
            />
            <InputField
              v-model="goalsForm.weeklyExerciseDays"
              type="number"
              label="每週運動天數"
              placeholder="3"
            />
            <InputField
              v-model="goalsForm.dailyWaterMl"
              type="number"
              label="每日飲水目標 (ml)"
              placeholder="2000"
            />
            <InputField
              v-model="goalsForm.targetWeight"
              type="number"
              step="0.1"
              label="目標體重 (kg)"
              placeholder="65"
            />
          </div>
          <OrangeButton full-width @click="saveGoals" :loading="goals.notificationsEnabled === false && false">
            儲存目標
          </OrangeButton>
        </div>
      </GlassCard>

      <!-- Notifications -->
      <GlassCard class="section">
        <h2 class="section-title">通知設定</h2>
        <div class="notif-row">
          <div class="notif-info">
            <p class="notif-label">健康提醒通知</p>
            <p class="notif-hint">{{ isSupported ? '接收飲水、運動等健康提醒' : '您的瀏覽器不支援通知功能' }}</p>
          </div>
          <button
            class="toggle-switch"
            :class="{ on: isEnabled }"
            :disabled="!isSupported"
            @click="toggleNotif"
            role="switch"
            :aria-checked="isEnabled"
          >
            <div class="toggle-thumb" />
          </button>
        </div>
      </GlassCard>

      <!-- If-Then Plans -->
      <GlassCard class="section">
        <div class="plans-header">
          <h2 class="section-title">備用計畫 (If-Then)</h2>
          <GlassButton size="sm" @click="openNewPlan">+ 新增</GlassButton>
        </div>

        <p class="plans-desc">
          預先設定應對風險情境的計畫，當你面對挑戰時，你已經知道該怎麼做！
        </p>

        <div v-if="ifThen.plans.length === 0" class="empty-plans">
          尚無備用計畫，新增第一個計畫吧！
        </div>

        <div v-else class="plans-list">
          <div
            v-for="plan in ifThen.plans"
            :key="plan.id"
            class="plan-item"
            :class="{ inactive: !plan.active }"
          >
            <div class="plan-header">
              <BadgeTag :color="categoryColors[plan.category]" size="sm">
                {{ categoryLabels[plan.category] }}
              </BadgeTag>
              <div class="plan-controls">
                <span class="plan-window">{{ plan.atRiskWindowStart }}-{{ plan.atRiskWindowEnd }}</span>
                <button class="plan-toggle-btn" @click="ifThen.togglePlan(plan.id)" :title="plan.active ? '停用' : '啟用'">
                  {{ plan.active ? '✓' : '○' }}
                </button>
                <button class="plan-edit-btn" @click="openEditPlan(plan)">
                  <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                    <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                  </svg>
                </button>
                <button class="plan-delete-btn" @click="ifThen.deletePlan(plan.id)">
                  <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M3 6h18M8 6V4h8v2M19 6l-1 14H6L5 6"/>
                  </svg>
                </button>
              </div>
            </div>
            <p class="plan-trigger"><strong>如果</strong> {{ plan.trigger }}</p>
            <p class="plan-action"><strong>那就</strong> {{ plan.action }}</p>
          </div>
        </div>
      </GlassCard>

      <!-- Logout -->
      <div class="section">
        <button class="logout-btn" @click="handleLogout">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
            <polyline points="16 17 21 12 16 7"/>
            <line x1="21" y1="12" x2="9" y2="12"/>
          </svg>
          登出帳號
        </button>
      </div>
    </div>

    <!-- Plan Sheet -->
    <BottomSheet v-model="showPlanSheet" :title="editingPlan ? '編輯備用計畫' : '新增備用計畫'" height="full">
      <div class="plan-form">
        <div class="category-select">
          <span class="form-label">類別</span>
          <div class="category-btns">
            <button
              v-for="c in ['exercise', 'diet', 'general']"
              :key="c"
              class="cat-btn"
              :class="{ active: planForm.category === c }"
              @click="planForm.category = c as any"
            >
              {{ categoryLabels[c] }}
            </button>
          </div>
        </div>

        <div class="form-field">
          <label class="form-label">觸發情境（如果...）</label>
          <textarea
            v-model="planForm.trigger"
            class="plan-textarea"
            placeholder="例如：下班後感到疲勞，想跳過運動時"
            rows="2"
          />
        </div>

        <div class="form-field">
          <label class="form-label">應對行動（那就...）</label>
          <textarea
            v-model="planForm.action"
            class="plan-textarea"
            placeholder="例如：先換上運動服，只做 10 分鐘，開始了就停不下來"
            rows="3"
          />
        </div>

        <div class="time-row">
          <div class="time-field">
            <label class="form-label">風險時段開始</label>
            <input v-model="planForm.atRiskWindowStart" type="time" class="time-input" />
          </div>
          <div class="time-field">
            <label class="form-label">風險時段結束</label>
            <input v-model="planForm.atRiskWindowEnd" type="time" class="time-input" />
          </div>
        </div>

        <div class="plan-form-actions">
          <GlassButton full-width @click="showPlanSheet = false">取消</GlassButton>
          <OrangeButton full-width @click="savePlan">儲存計畫</OrangeButton>
        </div>
      </div>
    </BottomSheet>
  </PageShell>
</template>

<style scoped>
.settings-content {
  position: relative;
  z-index: 1;
  padding: 0 16px;
  max-width: 680px;
  margin: 0 auto;
}

.page-header {
  padding: 56px 0 20px;
}

.page-title {
  font-family: 'Lora', serif;
  font-size: 24px;
  font-weight: 700;
  color: #083830;
}

.section {
  margin-bottom: 16px;
}

.section-title {
  font-family: 'Lora', serif;
  font-size: 16px;
  font-weight: 600;
  color: #083830;
  margin-bottom: 16px;
}

.profile-card {
  display: flex;
  align-items: center;
  gap: 16px;
}

.profile-avatar {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #0A9488, #2AACA8);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 22px;
  font-weight: 700;
  flex-shrink: 0;
  overflow: hidden;
  box-shadow: 0 3px 12px rgba(10, 148, 136, 0.25);
}

.profile-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.profile-name {
  font-size: 16px;
  font-weight: 700;
  color: #083830;
}

.profile-email {
  font-size: 13px;
  color: #0A7870;
}

.goals-form {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.goals-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.notif-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.notif-label {
  font-size: 14px;
  font-weight: 600;
  color: #083830;
  margin-bottom: 2px;
}

.notif-hint {
  font-size: 12px;
  color: #0A7870;
}

.toggle-switch {
  width: 52px;
  height: 30px;
  border-radius: 15px;
  background: rgba(10, 148, 136, 0.15);
  border: none;
  cursor: pointer;
  position: relative;
  transition: background 200ms;
  flex-shrink: 0;
}

.toggle-switch.on {
  background: #0A9488;
}

.toggle-switch:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.toggle-thumb {
  position: absolute;
  top: 3px;
  left: 3px;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: white;
  transition: transform 200ms;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.15);
}

.toggle-switch.on .toggle-thumb {
  transform: translateX(22px);
}

.plans-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.plans-desc {
  font-size: 13px;
  color: #0A7870;
  line-height: 1.5;
  margin-bottom: 16px;
}

.empty-plans {
  font-size: 13px;
  color: rgba(8, 56, 48, 0.4);
  padding: 20px;
  text-align: center;
  background: rgba(10, 148, 136, 0.03);
  border-radius: 10px;
  border: 1px dashed rgba(10, 148, 136, 0.15);
}

.plans-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.plan-item {
  padding: 14px;
  background: rgba(255, 255, 255, 0.45);
  border: 1px solid rgba(10, 148, 136, 0.12);
  border-radius: 12px;
  transition: opacity 200ms;
}

.plan-item.inactive {
  opacity: 0.55;
}

.plan-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.plan-controls {
  display: flex;
  align-items: center;
  gap: 8px;
}

.plan-window {
  font-size: 11px;
  color: #0A7870;
}

.plan-toggle-btn,
.plan-edit-btn,
.plan-delete-btn {
  width: 30px;
  height: 30px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 200ms;
  font-size: 14px;
}

.plan-toggle-btn {
  background: rgba(10, 148, 136, 0.08);
  color: #0A9488;
}

.plan-edit-btn {
  background: rgba(10, 148, 136, 0.08);
  color: #0A7870;
}

.plan-delete-btn {
  background: rgba(229, 62, 62, 0.07);
  color: rgba(229, 62, 62, 0.6);
}

.plan-delete-btn:hover {
  background: rgba(229, 62, 62, 0.14);
  color: #e53e3e;
}

.plan-trigger, .plan-action {
  font-size: 13px;
  color: #083830;
  line-height: 1.5;
  margin-bottom: 4px;
}

.plan-trigger strong {
  color: #0A7870;
}

.plan-action strong {
  color: #0A9488;
}

.logout-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  width: 100%;
  padding: 16px;
  background: rgba(229, 62, 62, 0.07);
  border: 1.5px solid rgba(229, 62, 62, 0.2);
  border-radius: 14px;
  font-family: 'Raleway', sans-serif;
  font-size: 14px;
  font-weight: 700;
  color: #c53030;
  cursor: pointer;
  transition: all 200ms;
}

.logout-btn:hover {
  background: rgba(229, 62, 62, 0.12);
  border-color: rgba(229, 62, 62, 0.35);
}

/* Plan form styles */
.plan-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding-bottom: 24px;
}

.form-label {
  display: block;
  font-size: 13px;
  font-weight: 600;
  color: #083830;
  margin-bottom: 6px;
}

.category-select {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.category-btns {
  display: flex;
  gap: 8px;
}

.cat-btn {
  flex: 1;
  padding: 8px;
  border: 1.5px solid rgba(10, 148, 136, 0.15);
  background: rgba(255, 255, 255, 0.4);
  border-radius: 8px;
  font-family: 'Raleway', sans-serif;
  font-size: 13px;
  font-weight: 600;
  color: rgba(8, 56, 48, 0.5);
  cursor: pointer;
  transition: all 200ms;
}

.cat-btn.active {
  background: rgba(10, 148, 136, 0.12);
  border-color: #0A9488;
  color: #0A9488;
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.plan-textarea {
  width: 100%;
  padding: 12px 14px;
  background: rgba(255, 255, 255, 0.55);
  border: 1.5px solid rgba(10, 148, 136, 0.2);
  border-radius: 12px;
  font-family: 'Raleway', sans-serif;
  font-size: 14px;
  color: #083830;
  outline: none;
  resize: vertical;
  line-height: 1.5;
}

.plan-textarea:focus {
  border-color: #0A9488;
  box-shadow: 0 0 0 3px rgba(10, 148, 136, 0.1);
}

.time-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.time-input {
  width: 100%;
  padding: 10px 12px;
  background: rgba(255, 255, 255, 0.55);
  border: 1.5px solid rgba(10, 148, 136, 0.2);
  border-radius: 10px;
  font-family: 'Raleway', sans-serif;
  font-size: 14px;
  color: #083830;
  outline: none;
}

.time-input:focus {
  border-color: #0A9488;
}

.plan-form-actions {
  display: flex;
  gap: 10px;
}
</style>
