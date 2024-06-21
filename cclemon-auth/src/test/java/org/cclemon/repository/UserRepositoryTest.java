package org.cclemon.repository;

import org.cclemon.encoder.PazzwordEncoder;
import org.cclemon.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;


@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    @Autowired
    PasswordEncoder encoder;

    @Test
    void save() {
        var user = new User();
        user.setId(2L);
        user.setPassword(encoder.encode("123"));
        user.setUsername("A");
        repository.save(user);
    }


}