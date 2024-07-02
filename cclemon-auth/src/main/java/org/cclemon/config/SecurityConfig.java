package org.cclemon.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.cclemon.encoder.PazzwordEncoder;
import org.cclemon.repository.impl.JpaUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationContext;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;
import java.util.function.Function;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        // Enable OpenID Connect 1.0
        Function<OidcUserInfoAuthenticationContext, OidcUserInfo> userInfoMapper = (context) -> {
            OidcUserInfoAuthenticationToken authentication = context.getAuthentication();
            JwtAuthenticationToken principal = (JwtAuthenticationToken) authentication.getPrincipal();
            return new OidcUserInfo(principal.getToken().getClaims());
        };
        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(oidc -> oidc.userInfoEndpoint((userinfo -> userinfo.userInfoMapper(userInfoMapper))));

        // Accept access tokens for User Info and/or Client Registration
        http.oauth2ResourceServer((resourceServer) -> resourceServer.jwt(Customizer.withDefaults()));

        // social login
        http.oauth2ResourceServer((resourceServer) -> resourceServer.jwt(Customizer.withDefaults()));

        //cors
        http.cors(Customizer.withDefaults());

        // Redirect to the login page when not authenticated from the
        // authorization endpoint
        http.exceptionHandling((exceptions) -> exceptions
                .defaultAuthenticationEntryPointFor(
                        new LoginUrlAuthenticationEntryPoint("/login"),
                        new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                )
        );

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)
            throws Exception {

        // request filter for auth
        http.authorizeHttpRequests((authorize) -> authorize
                .anyRequest().authenticated()
        );
        // sso login page
        http.formLogin(Customizer.withDefaults());
        //social login page
        http.oauth2Login(Customizer.withDefaults());
        //cors
        http.cors(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(JpaUserRepository jpaUserRepository) {
        var provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(jpaUserRepository);
        provider.setPasswordEncoder(new PazzwordEncoder());
        return provider;
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.addAllowedOrigin("http://127.0.0.1:5173");
        config.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}