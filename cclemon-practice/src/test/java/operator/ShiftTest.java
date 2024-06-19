package operator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShiftTest {

    @Test
    void case1() {
       var actual = Shift.case1();
       Assertions.assertThat(actual).isEqualTo(62);
    }
}