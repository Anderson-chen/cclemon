package org.cclemon.service.impl;

import lombok.RequiredArgsConstructor;
import org.cclemon.service.RedisService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;

@RequiredArgsConstructor
@Component
public class RedisOAuth2AuthorizationConsentService implements OAuth2AuthorizationConsentService {

    private final RedisService redisService;
    private final JsonMapper jsonMapper;

    private String consentKey(String clientId, String principalName) {
        return "oauth2:consent:" + clientId + ":" + principalName;
    }

    private final Duration DEFAULT_TTL = Duration.ofDays(1); // 可以自訂 TTL

    @Override
    public void save(OAuth2AuthorizationConsent consent) {
        if (consent == null) return;
        String key = consentKey(consent.getRegisteredClientId(), consent.getPrincipalName());
        String value = jsonMapper.writeValueAsString(consent);
        redisService.set(key, value, DEFAULT_TTL);
    }

    @Override
    public OAuth2AuthorizationConsent findById(String registeredClientId, String principalName) {
        String key = consentKey(registeredClientId, principalName);
        String value = redisService.get(key);
        if (value == null) return null;
        return jsonMapper.readValue(value, OAuth2AuthorizationConsent.class);
    }

    @Override
    public void remove(OAuth2AuthorizationConsent consent) {
        if (consent == null) return;
        String key = consentKey(consent.getRegisteredClientId(), consent.getPrincipalName());
        redisService.delete(key);
    }
}
