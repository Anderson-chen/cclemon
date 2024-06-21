package org.cclemon.encoder;

import com.nimbusds.jose.util.Base64;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PazzwordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        return Base64.encode(rawPassword.toString()).toString();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {

        return Base64.encode(rawPassword.toString()).equals(Base64.from(encodedPassword));
    }
}
