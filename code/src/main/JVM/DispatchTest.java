package JVM;

/**
 * 测试静态分派和动态分派
 */
public class DispatchTest {

    public static void main(String[] args) {
        staticDispatch();
    }

    //静态分派
    public static void staticDispatch() {
        Human man = new Man();
        Human woman = new Woman();
        DispatchTest test = new DispatchTest();
        test.sayHello(man);
        test.sayHello(woman);
    }

    //动态分派
    public static void dynamicDispatch() {
        Human man = new Man();
        Human woman = new Woman();
        man.sayHello();
        woman.sayHello();
        man = new Woman();
        man.sayHello();
    }
    static abstract class Human {
        protected abstract void sayHello();
    }

    static class Man extends Human {
        protected void sayHello() {
            System.out.println("man say hello");
        }
    }

    static class Woman extends Human {
        protected void sayHello() {
            System.out.println("woman say hello");
        }
    }

    public void sayHello(Human guy) {
        System.out.println("hello guy");
    }

    public void sayHello(Man man) {
        System.out.println("hello man");
    }

    public void sayHello(Woman woman) {
        System.out.println("hello woman");
    }


}
