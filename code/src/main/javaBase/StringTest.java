package javaBase;

/**
 * 该类对String的一些特性做测试
 */
public class StringTest {
    public static void main(String[] args) {
        stringEquals();
    }

    /**
     * 比较String的相等例子，表明他在字符串常量池是怎么表现的
     */
    public static void stringEquals() {
        String s1 = "Hello";
        String s2 = "Hello";
        String s3 = "Hel" + "lo";
        String s4 = "Hel" + new String("lo");
        String s5 = new String("Hello");
        String s6 = s5.intern();
        String s7 = "H";
        String s8 = "ello";
        String s9 = s7 + s8;

        System.out.println(s1 == s2);  // true
        System.out.println(s1 == s3);  // true
        System.out.println(s1 == s4);  // false
        System.out.println(s1 == s9);  // false
        System.out.println(s4 == s5);  // false
        System.out.println(s1 == s6);  // true
    }
}
