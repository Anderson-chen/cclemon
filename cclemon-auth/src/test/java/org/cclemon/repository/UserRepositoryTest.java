package org.cclemon.repository;

import org.cclemon.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;


@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    @Autowired
    PasswordEncoder encoder;

    @Test
    void save() {
        var user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setPassword(encoder.encode("123"));
        user.setUsername("A");
        user.setCountry("taiwan");
        repository.save(user);
    }


}