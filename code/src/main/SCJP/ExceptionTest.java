package SCJP;

import sun.jvm.hotspot.utilities.Assert;

import java.io.IOException;

public class ExceptionTest {
    public static void main(String[] args) {
        a();
    }

    void doStuff() throws Exception {
        doMore();
    }

    void doMore() throws Exception {
        throw new IOException();
    }

    private static void a() {
        assert (1 > 2) : "wrong";
        // more code assuming y is greater than x
    }

}
