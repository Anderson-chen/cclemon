package org.cclemon.handler;

import org.apache.commons.collections4.MapUtils;
import org.cclemon.entity.User;
import org.cclemon.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;


@Component
public final class UserRepositoryOAuth2UserHandler implements Consumer<OAuth2User> {

    @Autowired
    private final UserRepository userRepository;

    public UserRepositoryOAuth2UserHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void accept(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String userName = MapUtils.getString(attributes, "");
        Optional<User> opt = userRepository.findByUsername(userName);
        if (opt.isEmpty()) {
            System.out.println("Saving first-time user");
            User newUser = new User();
            newUser.setUsername(userName);
            this.userRepository.save(newUser);
        }
    }
}
