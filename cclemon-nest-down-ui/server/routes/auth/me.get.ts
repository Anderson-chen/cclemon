/**
 * GET /auth/me
 * 從 session 回傳目前登入的 userInfo，供 client authStore 使用
 * 不暴露任何 token
 */
import { getAppSession } from '../../utils/session'

export default defineEventHandler(async (event) => {
  const session = await getAppSession(event)

  if (!session.data.accessToken) {
    throw createError({ statusCode: 401, message: 'Unauthenticated' })
  }

  return session.data.userInfo ?? null
})
