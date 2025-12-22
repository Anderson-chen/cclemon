package org.cclemon.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
@Configuration
@Slf4j
public class ResourceSecurityConfig {

    private final String authorizationServerUrl;
    private final String cclemonUiUrl;
    private final String localProfile = AbstractEnvironment.RESERVED_DEFAULT_PROFILE_NAME;

    public ResourceSecurityConfig(@Value("${authorization-server.url}") String authorizationServerUrl,
                                  @Value("${cclemon-ui.url}") String cclemonUiUrl) {
        this.authorizationServerUrl = authorizationServerUrl;
        this.cclemonUiUrl = cclemonUiUrl;
    }

    @Bean
    @Order(1)
    @Profile("!" + localProfile)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("PROD/NON-DEV PROFILE ACTIVE: JWT authentication is enabled.");
        http.authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .csrf(AbstractHttpConfigurer::disable);
        return http.cors(Customizer.withDefaults()).build();
    }

    @Bean
    @Order(2)
    @Profile(localProfile)
    public SecurityFilterChain devFilterChain(HttpSecurity http) throws Exception {
        log.warn("DEV PROFILE ACTIVE: All security checks are disabled. All requests will be permitted.");
        http.authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
                .csrf(AbstractHttpConfigurer::disable);
        return http.cors(Customizer.withDefaults()).build();
    }

    @Bean
    @Profile("!" + localProfile)
    @Retryable(
            maxAttempts = 5,
            backoff = @Backoff(delay = 5000)
    )
    JwtDecoder jwtDecoder() {
        log.info("Attempting to fetch JwtDecoder from issuer: {}", authorizationServerUrl);
        JwtDecoder decoder = JwtDecoders.fromIssuerLocation(authorizationServerUrl);
        log.info("Successfully initialized JwtDecoder");
        return decoder;
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.addAllowedOrigin(cclemonUiUrl);
        config.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
