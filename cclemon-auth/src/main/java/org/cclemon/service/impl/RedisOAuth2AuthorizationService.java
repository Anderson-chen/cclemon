package org.cclemon.service.impl;

import lombok.RequiredArgsConstructor;
import org.cclemon.service.RedisService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.time.Instant;

@RequiredArgsConstructor
@Component
public class RedisOAuth2AuthorizationService implements OAuth2AuthorizationService {

    private final RedisService redisService;
    private final JsonMapper jsonMapper;

    private String authKey(String id) {
        return "oauth2:auth:id:" + id;
    }

    private String tokenKey(String token) {
        return "oauth2:auth:token:" + token;
    }

    private Duration getTtl(OAuth2Authorization.Token<?> token) {
        if (token == null || token.getToken() == null || token.getToken().getExpiresAt() == null) {
            return Duration.ofMinutes(10);
        }
        Instant now = Instant.now();
        long seconds = Duration.between(now, token.getToken().getExpiresAt()).getSeconds();
        return seconds > 0 ? Duration.ofSeconds(seconds) : Duration.ofSeconds(10);
    }

    @Override
    public void save(OAuth2Authorization authorization) {
        String value = jsonMapper.writeValueAsString(authorization);

        // save main authorization object
        redisService.set(authKey(authorization.getId()), value);

        // save tokens if present
        saveToken(authorization.getToken(OAuth2AccessToken.class), value);
        saveToken(authorization.getToken(OAuth2RefreshToken.class), value);
        saveToken(authorization.getToken(OAuth2AuthorizationCode.class), value);
    }

    private void saveToken(OAuth2Authorization.Token<?> token, String value) {
        if (token != null && token.getToken() != null) {
            Duration ttl = getTtl(token);
            redisService.set(tokenKey(token.getToken().getTokenValue()), value, ttl);
        }
    }

    @Override
    public OAuth2Authorization findById(String id) {
        String value = redisService.get(authKey(id));
        if (value == null) return null;
        return jsonMapper.readValue(value, OAuth2Authorization.class);
    }

    @Override
    public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
        String value = redisService.get(tokenKey(token));
        if (value == null) return null;
        return jsonMapper.readValue(value, OAuth2Authorization.class);
    }

    @Override
    public void remove(OAuth2Authorization authorization) {
        redisService.delete(authKey(authorization.getId()));

        deleteToken(authorization.getToken(OAuth2AccessToken.class));
        deleteToken(authorization.getToken(OAuth2RefreshToken.class));
        deleteToken(authorization.getToken(OAuth2AuthorizationCode.class));
    }

    private void deleteToken(OAuth2Authorization.Token<?> token) {
        if (token != null && token.getToken() != null) {
            redisService.delete(tokenKey(token.getToken().getTokenValue()));
        }
    }
}
