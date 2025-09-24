package org.cclemon.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.cclemon.handler.FederatedIdentityAuthenticationSuccessHandler;
import org.cclemon.handler.UserRepositoryOAuth2UserHandler;
import org.cclemon.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationContext;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
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
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final String cclemonUiUrl;

    public SecurityConfig(@Value("${cclemon-ui.url}") String cclemonUiUrl) {
        this.cclemonUiUrl = cclemonUiUrl;
    }

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
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http, UserRepository userRepository) throws Exception {

        // Enable authorization server
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = OAuth2AuthorizationServerConfigurer.authorizationServer();

        // Enable OpenID Connect 1.0
        http.securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
                .with(authorizationServerConfigurer, (authorizationServer) ->
                        authorizationServer.oidc(oidc -> oidc.userInfoEndpoint((userinfo -> userinfo.userInfoMapper(getUserInfoMapper(userRepository)))))
                )
                .authorizeHttpRequests((authorize) ->
                        authorize.anyRequest().authenticated()
                );

        // Accept access tokens for User Info and/or Client Registration
        http.oauth2ResourceServer((resourceServer) -> resourceServer.jwt(Customizer.withDefaults()));

        //cors
        http.cors(Customizer.withDefaults());

        // Redirect to the login page when not authenticated from the authorization endpoint
        http.exceptionHandling((exceptions) -> exceptions
                .defaultAuthenticationEntryPointFor(
                        new LoginUrlAuthenticationEntryPoint(cclemonUiUrl + "/login"),
                        new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                )
        );

        return http.build();
    }

    /**
     * 自訂 OIDC UserInfo Endpoint 的資料映射邏輯。
     * <p>
     * 說明：
     * - 當 Client 使用 Access Token 呼叫 `/userinfo` 端點時，Spring Authorization Server
     * 會建立一個 {@link OidcUserInfoAuthenticationContext}，內含目前請求的驗證資訊與 Token Claims。
     * - 我們透過這個 context 判斷是誰在呼叫，並查詢資料庫取得該使用者的詳細資料。
     * - 最後回傳 {@link OidcUserInfo} 物件，Spring 會將它序列化為 JSON 回傳給 Client。
     * <p>
     * 規範：
     * - `sub` 是 OIDC 規範中必須要有的 Claim，代表使用者的唯一識別符。
     * - 其他欄位如 `username`、`email`、`roles` 可視需求擴充，皆屬於自訂 Claims。
     *
     * @param userRepository 用來查詢使用者資料的 Repository
     * @return 生成 UserInfo 的函數
     */
    private Function<OidcUserInfoAuthenticationContext, OidcUserInfo> getUserInfoMapper(UserRepository userRepository) {
        return (context) -> {

            //取得目前經過驗證的使用者資訊（通常是 JwtAuthenticationToken）
            Authentication authentication = context.getAuthentication();

            // 從 Authentication 中取得 username（通常是 subject 或 principal name）
            String username = authentication.getName();

            //查詢資料庫，取得該使用者的完整資料
            Optional<org.cclemon.entity.User> opt = userRepository.findByUsername(username);

            //組裝要回傳的 UserInfo claims
            Map<String, Object> claims = opt.map(user -> Map.<String, Object>of(
                    "sub", user.getId(),
                    "userName", user.getUsername()
            )).orElse(null);

            return new OidcUserInfo(claims);
        };
    }

    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, UserRepositoryOAuth2UserHandler userRepository)
            throws Exception {

        // request filter for auth
        http.authorizeHttpRequests((authorize) -> authorize
                .requestMatchers("/csrf-token", "/actuator/health").permitAll()
                .anyRequest().authenticated()
        );

        // sso login page
        http.formLogin(login -> login.loginProcessingUrl("/login"));

        //social login page
        http.oauth2Login(configurer -> {
            configurer.failureUrl(cclemonUiUrl + "/login?error");
            configurer.successHandler(new FederatedIdentityAuthenticationSuccessHandler(userRepository));
        });

        //cors
        http.cors(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> {
            var opt = userRepository.findByUsername(username);
            if (opt.isEmpty()) {
                throw new UsernameNotFoundException("User not found");
            }
            var user = opt.get();
            return User.withUsername(user.getUsername())
                    .password(user.getPassword())
                    .authorities("USER")
                    .build();
        };
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
        config.addAllowedOriginPattern(cclemonUiUrl);
        config.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}