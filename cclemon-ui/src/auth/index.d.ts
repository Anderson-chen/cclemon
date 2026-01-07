export interface AuthFlow {
  openIdConfig: openIdConfig;
  authorize(codeVerifier: string): void;
  getAccessToken(code: string, codeVerifier: string): Promise<tokenResponse>;
  getRefreshToken(refreshToken: string): Promise<tokenResponse>;
  revokeToken(token: string): void;
  logout(idTokenHint: string): void;
  getUserInfo(accessToken: string): Promise<userinfoResponse>;
}

export type openIdConfig = {
  issuer: string;
  authorization_endpoint: string;
  device_authorization_endpoint: string;
  token_endpoint: string;
  token_endpoint_auth_methods_supported: string[];
  jwks_uri: string;
  userinfo_endpoint: string;
  end_session_endpoint: string;
  response_types_supported: string[];
  grant_types_supported: string[];
  revocation_endpoint: string;
  revocation_endpoint_auth_methods_supported: string[];
  introspection_endpoint: string;
  introspection_endpoint_auth_methods_supported: string[];
  code_challenge_methods_supported: string[];
  subject_types_supported: string[];
  id_token_signing_alg_values_supported: string[];
  scopes_supported: string[];
};

// 授權碼請求參數
export type authorizeRequest = {
  response_type: string;
  client_id: string;
  redirect_uri: string;
  scope: string;
  code_challenge: string;
  code_challenge_method: string;
};

export type accessTokenRequest = {
  grant_type: string;
  redirect_uri: string;
  code: string;
  code_verifier: string;
  client_id: string;
};

export type tokenResponse = {
  access_token: string;
  refresh_token: string;
  scope: string;
  id_token: string;
  token_type: string;
  expires_in: number;
};

export type refreshTokenRequest = {
  grant_type: string;
  refresh_token: string;
};

export type revokeTokenRequest = {
  token: string;
};

export type logoutRequest = {
  id_token_hint: string;
  post_logout_redirect_uri: string;
};

export type userinfoResponse = {
  sub: string;
  aud: Array<string>;
  nbf: number;
  scope: Array<string>;
  iss: string;
  exp: number;
  iat: number;
  userName: string;
  jti: string;
};
