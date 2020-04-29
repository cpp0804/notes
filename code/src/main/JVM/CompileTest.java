package JVM;

/**
 * 为了测试javac的编译过程
 */
public class CompileTest {
    static int a;

    public static void main(String[] args) {
//        int a;
        System.out.println("hello world");
        int b=1+a;
        System.out.println(b);
    }
}
