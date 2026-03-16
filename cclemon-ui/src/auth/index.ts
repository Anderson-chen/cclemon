import { authApi } from '../boot/axios';
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
} from './index.d';
import { objectToQueryParams, objectToFormData } from '../utils/parse';
const {
  VUE_APP_CLIENT_ID,
  VUE_APP_REDIRECT_URI,
  VUE_APP_SCOPE,
  VUE_APP_POST_LOGOUT_REDIRECT_URI,
} = process.env;

// SHA256 + Base64URL 編碼成 code_challenge
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
      client_id: VUE_APP_CLIENT_ID
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
      objectToFormData(request)
    );
    return response.data;
  }
  async revokeToken(token: string) {
    const request: revokeTokenRequest = {
      token: token,
    };
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
