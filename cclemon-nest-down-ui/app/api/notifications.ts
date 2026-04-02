export interface NotificationSettings {
  enabled: boolean
  reminderTimes: string[]
  waterReminder: boolean
  exerciseReminder: boolean
  weightReminder: boolean
}

export const notificationsApi = {
  async getSettings(): Promise<NotificationSettings> {
    return {
      enabled: false,
      reminderTimes: ['08:00', '12:00', '18:00'],
      waterReminder: true,
      exerciseReminder: true,
      weightReminder: true
    }
  },

  async updateSettings(settings: Partial<NotificationSettings>): Promise<NotificationSettings> {
    return settings as NotificationSettings
  },

  async subscribePush(subscription: PushSubscription): Promise<void> {},

  async unsubscribePush(): Promise<void> {},
}
