package operator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class IncrementTest {

    @Test
    void testCase1() {
        var actual = Increment.case1();
        Assertions.assertThat(actual).isEqualTo(9);
    }

    @Test
    void testCase2() {
        var actual = Increment.case2();
        Assertions.assertThat(actual).isEqualTo(9);
    }

    @Test
    void testCase3() {
        var actual = Increment.case3();
        Assertions.assertThat(actual).isEqualTo(9);
    }

    @Test
    void testCase4() {
        var actual = Increment.case4();
        Assertions.assertThat(actual).isEqualTo(10);
    }
}