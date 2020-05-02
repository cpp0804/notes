package JVM;

/**
 * 该类对init和clinit进行测试
 */
public class ClinitInitTest {

    public static void main(String[] args) {
//        tempAndBlock();
        test();
    }

    /**
     * 测试实例变量初始化和代码块初始化
     */
    public static void tempAndBlock() {
        InstanceVariableInitializer a =new InstanceVariableInitializer(8);
    }

    /**
     * 测试实例变量初始化和代码块初始化
     */
    public static class InstanceVariableInitializer {

        private int i = 1;
        private int j = i + 1;

        public InstanceVariableInitializer(int var) {
            System.out.println(i);
            System.out.println(j);
            this.i = var;
            System.out.println(i);
            System.out.println(j);
        }

        {               // 实例代码块
            j += 3;

        }
    }

    /**
     * 整体举例
     * 父类
     */
    static class Foo {
        int i = 1;

        Foo() {
            System.out.println(i); //(1)
            int x = getValue();
            System.out.println(x); //(2)
        }

        {
            i = 2;
        }

        protected int getValue() {
            return i;
        }
    }

    //子类
    static class Bar extends Foo {
        int j = 1;

        Bar() {
            j = 2;
        }

        {
            j = 3;
        }

        @Override
        protected int getValue() {
            return j;
        }
    }

    public static void test() {
        Bar bar = new Bar();
        System.out.println(bar.getValue());  //(3)
    }
}
