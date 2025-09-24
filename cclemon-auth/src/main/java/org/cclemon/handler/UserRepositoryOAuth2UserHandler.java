package org.cclemon.handler;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.cclemon.entity.User;
import org.cclemon.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@Component
public final class UserRepositoryOAuth2UserHandler implements Consumer<OAuth2User> {

    private final UserRepository userRepository;

    public UserRepositoryOAuth2UserHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void accept(OAuth2User oAuth2User) {

        Map<String, Object> attributes = oAuth2User.getAttributes();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof OAuth2AuthenticationToken oauth2Token) {
            String registrationId = oauth2Token.getAuthorizedClientRegistrationId();

            String sub = MapUtils.getString(attributes, "sub");
            String userName = MapUtils.getString(attributes, "email");
            Optional<User> opt = userRepository.findByUsername(userName);
            if (opt.isEmpty() && StringUtils.isNotBlank(userName)) {
                User newUser = new User();
                newUser.setId(UUID.randomUUID().toString());
                newUser.setProvider(registrationId);
                newUser.setProviderId(sub);
                newUser.setUsername(userName);
                newUser.setEmail(userName);
                this.userRepository.save(newUser);
            }
        }
    }
}
