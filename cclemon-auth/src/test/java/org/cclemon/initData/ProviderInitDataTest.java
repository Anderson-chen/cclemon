package org.cclemon.initData;


import org.cclemon.entity.system.ConfigKey;
import org.cclemon.entity.system.ConfigKeyI18n;
import org.cclemon.entity.system.ConfigKeyI18nId;
import org.cclemon.entity.system.ConfigValue;
import org.cclemon.repository.system.ConfigKeyI18nRepository;
import org.cclemon.repository.system.ConfigKeyRepository;
import org.cclemon.repository.system.ConfigValueRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@SpringBootTest
public class ProviderInitDataTest {

    @Autowired
    private ConfigKeyRepository configKeyRepository;

    @Autowired
    private ConfigValueRepository configValueRepository;

    @Autowired
    private ConfigKeyI18nRepository configKeyI18nRepository;

    @Test
//    @Transactional
    public void initProviderData() {
        // ===== 1. ConfigKey =====
        ConfigKey googleKey = ConfigKey.builder()
                .code("provider.google")
                .groupName("provider")
                .type("STRING")
                .defaultValue("GOOGLE")
                .description("Google 第三方登入")
                .isSecret(false)
                .isDeprecated(false)
                .build();

        ConfigKey githubKey = ConfigKey.builder()
                .code("provider.github")
                .groupName("provider")
                .type("STRING")
                .defaultValue("GITHUB")
                .description("GitHub 第三方登入")
                .isSecret(false)
                .isDeprecated(false)
                .build();

        ConfigKey facebookKey = ConfigKey.builder()
                .code("provider.facebook")
                .groupName("provider")
                .type("STRING")
                .defaultValue("FACEBOOK")
                .description("Facebook 第三方登入")
                .isSecret(false)
                .isDeprecated(false)
                .build();

        configKeyRepository.saveAll(Set.of(googleKey, githubKey, facebookKey));

        // ===== 2. ConfigValue =====
        ConfigValue googleValue = ConfigValue.builder()
                .configKey(googleKey)
                .scopeType("GLOBAL")
                .valueString("GOOGLE")
                .isActive(true)
                .createdBy("system")
                .version(1)
                .build();

        ConfigValue githubValue = ConfigValue.builder()
                .configKey(githubKey)
                .scopeType("GLOBAL")
                .valueString("GITHUB")
                .isActive(true)
                .createdBy("system")
                .version(1)
                .build();

        ConfigValue facebookValue = ConfigValue.builder()
                .configKey(facebookKey)
                .scopeType("GLOBAL")
                .valueString("FACEBOOK")
                .isActive(true)
                .createdBy("system")
                .version(1)
                .build();

        configValueRepository.saveAll(Set.of(googleValue, githubValue, facebookValue));

        // ===== 3. ConfigKeyI18n =====
        ConfigKeyI18n googleEn = ConfigKeyI18n.builder()
                .id(new ConfigKeyI18nId("provider.google", "en"))
                .configKey(googleKey)
                .displayName("Google")
                .descriptionLocal("Google OAuth Login")
                .build();

        ConfigKeyI18n googleZh = ConfigKeyI18n.builder()
                .id(new ConfigKeyI18nId("provider.google", "zh_TW"))
                .configKey(googleKey)
                .displayName("Google")
                .descriptionLocal("Google 第三方登入")
                .build();

        ConfigKeyI18n githubEn = ConfigKeyI18n.builder()
                .id(new ConfigKeyI18nId("provider.github", "en"))
                .configKey(githubKey)
                .displayName("GitHub")
                .descriptionLocal("GitHub OAuth Login")
                .build();

        ConfigKeyI18n githubZh = ConfigKeyI18n.builder()
                .id(new ConfigKeyI18nId("provider.github", "zh_TW"))
                .configKey(githubKey)
                .displayName("GitHub")
                .descriptionLocal("GitHub 第三方登入")
                .build();

        ConfigKeyI18n facebookEn = ConfigKeyI18n.builder()
                .id(new ConfigKeyI18nId("provider.facebook", "en"))
                .configKey(facebookKey)
                .displayName("Facebook")
                .descriptionLocal("Facebook OAuth Login")
                .build();

        ConfigKeyI18n facebookZh = ConfigKeyI18n.builder()
                .id(new ConfigKeyI18nId("provider.facebook", "zh_TW"))
                .configKey(facebookKey)
                .displayName("Facebook")
                .descriptionLocal("Facebook 第三方登入")
                .build();

        configKeyI18nRepository.saveAll(Set.of(
                googleEn, googleZh, githubEn, githubZh, facebookEn, facebookZh
        ));
    }
}

