package SCJP;

import java.util.Date;

public class GCTest {
    public static void main(String[] args) {
        Runtime rt = Runtime.getRuntime();
        System.out.println("Total JVM memory: " + rt.totalMemory());
        System.out.println("Before Memory = " + rt.freeMemory());
        Date d = null;
        for (int i = 0; i < 10000; i++) {
            d = new Date();
            d = null;
        }
        System.out.println("After Memory = " + rt.freeMemory());
        rt.gc();
        System.out.println("After GC Memory = " + rt.freeMemory());// an alternate to System.gc() System.out.println("After GC Memory = "
        Const1 s = null;
        System.out.println(true ^ false);
        System.out.println(true ^ true);
        System.out.println(false ^ false);
        System.out.println(false ^ true);
        System.out.println(1 ^ 2);

        System.out.println(true & true);
        System.out.println(true & false);
        System.out.println(false & false);
        System.out.println(false & true);

        System.out.println("------------");
        System.out.println(true | true);
        System.out.println(true | false);
        System.out.println(false | false);
        System.out.println(false | true);
    }
}

