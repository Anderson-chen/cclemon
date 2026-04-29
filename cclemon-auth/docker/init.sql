USE `cclemon-auth`;

CREATE TABLE IF NOT EXISTS client (
    id varchar(255) NOT NULL,
    clientId varchar(255) NOT NULL,
    clientIdIssuedAt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    clientSecret varchar(255) DEFAULT NULL,
    clientSecretExpiresAt timestamp DEFAULT NULL,
    clientName varchar(255) NOT NULL,
    clientAuthenticationMethods varchar(1000) NOT NULL,
    authorizationGrantTypes varchar(1000) NOT NULL,
    redirectUris varchar(1000) DEFAULT NULL,
    postLogoutRedirectUris varchar(1000) DEFAULT NULL,
    scopes varchar(1000) NOT NULL,
    clientSettings varchar(2000) NOT NULL,
    tokenSettings varchar(2000) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS authorization (
    id varchar(255) NOT NULL,
    registeredClientId varchar(255) NOT NULL,
    principalName varchar(255) NOT NULL,
    authorizationGrantType varchar(255) NOT NULL,
    authorizedScopes varchar(1000) DEFAULT NULL,
    attributes varchar(4000) DEFAULT NULL,
    state varchar(500) DEFAULT NULL,
    authorizationCodeValue varchar(4000) DEFAULT NULL,
    authorizationCodeIssuedAt timestamp DEFAULT NULL,
    authorizationCodeExpiresAt timestamp DEFAULT NULL,
    authorizationCodeMetadata varchar(2000) DEFAULT NULL,
    accessTokenValue varchar(4000) DEFAULT NULL,
    accessTokenIssuedAt timestamp DEFAULT NULL,
    accessTokenExpiresAt timestamp DEFAULT NULL,
    accessTokenMetadata varchar(2000) DEFAULT NULL,
    accessTokenType varchar(255) DEFAULT NULL,
    accessTokenScopes varchar(1000) DEFAULT NULL,
    refreshTokenValue varchar(4000) DEFAULT NULL,
    refreshTokenIssuedAt timestamp DEFAULT NULL,
    refreshTokenExpiresAt timestamp DEFAULT NULL,
    refreshTokenMetadata varchar(2000) DEFAULT NULL,
    oidcIdTokenValue varchar(4000) DEFAULT NULL,
    oidcIdTokenIssuedAt timestamp DEFAULT NULL,
    oidcIdTokenExpiresAt timestamp DEFAULT NULL,
    oidcIdTokenMetadata varchar(2000) DEFAULT NULL,
    oidcIdTokenClaims varchar(2000) DEFAULT NULL,
    userCodeValue varchar(4000) DEFAULT NULL,
    userCodeIssuedAt timestamp DEFAULT NULL,
    userCodeExpiresAt timestamp DEFAULT NULL,
    userCodeMetadata varchar(2000) DEFAULT NULL,
    deviceCodeValue varchar(4000) DEFAULT NULL,
    deviceCodeIssuedAt timestamp DEFAULT NULL,
    deviceCodeExpiresAt timestamp DEFAULT NULL,
    deviceCodeMetadata varchar(2000) DEFAULT NULL,
    PRIMARY KEY (id)
) DEFAULT CHARSET=ascii;

CREATE TABLE IF NOT EXISTS authorizationConsent (
    registeredClientId varchar(255) NOT NULL,
    principalName varchar(255) NOT NULL,
    authorities varchar(1000) NOT NULL,
    PRIMARY KEY (registeredClientId, principalName)
);

INSERT IGNORE INTO client (id, authorizationGrantTypes, clientAuthenticationMethods, clientId, clientIdIssuedAt, clientName, clientSecret, clientSecretExpiresAt, clientSettings, postLogoutRedirectUris, redirectUris, scopes, tokenSettings)
VALUES (
    '9217ba79-24fe-41a6-a95d-1c47465ba720',
    'refresh_token,authorization_code',
    'none',
    'nest-down-ui',
    '2026-01-06 09:19:07.354099',
    '9217ba79-24fe-41a6-a95d-1c47465ba720',
    '{noop}secret',
    null,
    '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":true,"settings.client.require-authorization-consent":true}',
    'http://127.0.0.1:3000/auth/login',
    'http://127.0.0.1:3000/auth/callback',
    'openid,profile,email,offline_access',
    '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.x509-certificate-bound-access-tokens":false,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration","PT5M"],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"},"settings.token.refresh-token-time-to-live":["java.time.Duration","PT1H"],"settings.token.authorization-code-time-to-live":["java.time.Duration","PT5M"],"settings.token.device-code-time-to-live":["java.time.Duration","PT5M"]}'
);


INSERT INTO `cclemon-auth`.user (id, country, password, username, accountLocked, avatarUrl, createdAt, displayName, email, emailVerified, enabled, lastLoginAt, phoneNumber, phoneNumberVerified, provider, providerId) VALUES ('25f02721-c187-4e13-aa38-1457e6d45c95', 'taiwan', 'MTIz', 'A', false, null, null, null, null, false, false, null, null, false, null, null);
