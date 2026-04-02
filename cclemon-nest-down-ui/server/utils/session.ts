/**
 * [業務說明]
 * Server-side session 封裝，使用 h3 內建的 useSession。
 * token 存在 httpOnly cookie，client 完全不可讀取。
 *
 * [技術考量]
 * - h3 useSession 使用 AES-GCM 加密 cookie，需要 sessionSecret
 * - pkceVerifier 暫存於 session，callback 後清除
 * - maxAge 24h，配合 access token 生命週期
 */

import type { H3Event } from 'h3'
import type { UserinfoResponse } from './oidc'

export type SessionData = {
  pkceVerifier?: string
  accessToken?: string
  refreshToken?: string
  idToken?: string
  userInfo?: UserinfoResponse
}

export async function getAppSession(event: H3Event) {
  const config = useRuntimeConfig(event)
  return useSession<SessionData>(event, {
    password: config.sessionSecret,
    name: 'nestdown_session',
    maxAge: 60 * 60 * 24,
    cookie: {
      httpOnly: true,
      secure: process.env.NODE_ENV === 'production',
      sameSite: 'lax',
    },
  })
}
