import org.junit.jupiter.api.Test;

public class JvmTest {

    // 屬於方法區：靜態變數
    private static String staticVar = "I'm in method area";

    // 屬於堆：物件成員變數
    private String instanceVar = "I'm in heap";


    @Test
    void TestJvmMemory(){

        // 屬於棧：區域變數
        int stackVar = 42;

        // new 出的物件會存在堆中
        JvmTest demo = new JvmTest();

        // 呼叫方法，會進入新的 stack frame
        demo.testMethod(stackVar);

        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void testMethod(int number) {
        String localStr = "Local in stack";
        System.out.println(staticVar);     // 方法區
        System.out.println(instanceVar);   // 堆
        System.out.println(localStr);      // 棧
        System.out.println(number);        // 棧
    }

}
