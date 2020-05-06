package javaBase;

/**
 * 用于测试equals和==
 */
public class EqualsTest {

    public static void main(String[] args) {
        equalsTest();
    }

    public static void equalsTest() {
        User u1 = new User(1, "pp");
        User u2 = new User(1, "pp");

        System.out.println(u1.equals(u2));
    }
}
