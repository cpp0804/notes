package JVM;

/**
 * 用于测试垃圾回收
 */
public class GCTest {
    private Object instance = null;

    public static void main(String[] args) {
//        circleReference();
    }

    /**
     * 测试引用计数法的循环引用问题
     */
    public static void circleReference() {

        GCTest a = new GCTest();
        GCTest b = new GCTest();
        a.instance = b;
        b.instance = a;

        a = null;
        b = null;

        System.gc();
    }
}
