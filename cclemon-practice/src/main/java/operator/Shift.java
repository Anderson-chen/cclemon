package operator;

import lombok.extern.java.Log;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Log
public class Shift {

    static int case1() {
        return 'a' >> 1;
    }

    public static void main(String[] args) {
        var pazz = "123";
        var ans = Base64.getEncoder().encodeToString(pazz.getBytes(StandardCharsets.UTF_8));
        log.info(ans);
    }
}
