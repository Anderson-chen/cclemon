package org.cclemon.repository.impl;

import org.cclemon.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class JpaUserRepository implements UserDetailsService {

    private final UserRepository userRepository;

    public JpaUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var opt = userRepository.findByUsername(username);
        UserDetails userDetails = null;
        if (opt.isPresent()) {
            userDetails = opt.get();
        }
        return userDetails;
    }
}
