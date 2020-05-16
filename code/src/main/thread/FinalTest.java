package thread;

import java.util.Random;

public class FinalTest {

    static Random r = new Random();
    final int k = r.nextInt(10);
    static final int k2 = r.nextInt(10);

    public static void main(String[] args) {
//        Student student = new Student();
//        student.setId(1);
//        testRefParameterFinal(student);
//        System.out.println(student.getId());

        testStaticFinal();
    }

    /**
     * 测试参数列表中的final参数
     */
    public static void testRefParameterFinal(final Student student) {
//        student = new Student();
        student.setId(2);
    }

    public static void testBaseParameterFinal(final int param) {
//        param = 2;
    }

    public static void testStaticFinal() {
        FinalTest t1 = new FinalTest();
        System.out.println("k=" + t1.k + " k2=" + t1.k2);
        FinalTest t2 = new FinalTest();
        System.out.println("k=" + t2.k + " k2=" + t2.k2);
    }

    public static void test() {
        final byte b1=1;
        final byte b2=3;
        //当程序执行到这一行的时候会出错，因为b1、b2可以自动转换成int类型的变量，运算时java虚拟机对它进行了转换，结果导致把一个int赋值给byte-----出错
        byte b3=b1+b2;

    }
}
