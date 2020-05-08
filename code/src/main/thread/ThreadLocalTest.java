package thread;

import java.util.HashSet;
import java.util.Set;

/**
 * 测试ThreadLocal
 */
public class ThreadLocalTest {

    private static String strLabel;
    private static ThreadLocal<String> threadLabel = new ThreadLocal<>();
    private static final Set<String>a = new HashSet<>();

    public static void main(String[] args) {
        test();
    }

    public static void test() {
        strLabel = "main";
        threadLabel.set("main");

        Thread thread = new Thread(() -> {
            strLabel = "child";
            threadLabel.set("child");
        });
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("strLabel = " + strLabel);
        System.out.println("threadLabel = " + threadLabel.get());
    }
}
