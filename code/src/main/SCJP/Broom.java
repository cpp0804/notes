package SCJP;

public class Broom {

    static class B2 {
        void goB2() {
            System.out.println("hi 2");
        }
    }

    public static void main(String[] args) {
        NestTest.Nest n = new NestTest.Nest(); // both class names n.go();
        B2 b2 = new B2();
        b2.goB2();
    }
}
