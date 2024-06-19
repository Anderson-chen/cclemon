package operator;


import lombok.extern.java.Log;


@Log
public class Increment {

    public static int case1() {

        var answer = 0;

        for (int i = 0; i < 10; i++) {
            answer = i;
            log.info(String.valueOf(answer));
        }

        return answer;
    }

    public static int case2() {

        var answer = 0;

        for (int i = 0; i < 10; ++i) {
            answer = i;
            log.info(String.valueOf(answer));
        }

        return answer;
    }

    public static int case3() {

        var answer = 0;

        for (int i = 0; i < 10; ) {
            answer = i++;
            log.info(String.valueOf(answer));
        }

        return answer;
    }

    public static int case4() {

        var answer = 0;

        for (int i = 0; i < 10; ) {
            answer = ++i;
            log.info(String.valueOf(answer));
        }

        return answer;
    }
}
