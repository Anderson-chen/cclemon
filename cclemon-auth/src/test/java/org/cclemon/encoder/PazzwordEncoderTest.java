package org.cclemon.encoder;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PazzwordEncoderTest {

    @InjectMocks
    PazzwordEncoder encoder;

    @Test
    void encode() {
        var encoded = encoder.encode("123");
        Assertions.assertThat(encoded).isEqualTo("MTIz");
    }

    @Test
    void matches() {
        var actual = encoder.matches("123", "MTIz");
        Assertions.assertThat(actual).isTrue();
    }
}