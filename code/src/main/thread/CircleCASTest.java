package thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自旋CAS实现计数器
 */
public class CircleCASTest {
    //线程安全计数器
    private AtomicInteger atomicInteger = new AtomicInteger(0);
    //非线程安全计数器
    private int i = 0;

    public static void main(String[] args) {
        count();
    }

    public static void count() {
        final CircleCASTest test = new CircleCASTest();
        List<Thread> threads = new ArrayList<>(600);
        long start = System.currentTimeMillis();
        for (int j = 0; j < 100; j++) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 10000; i++) {
                        test.nonSafeCount();
                        test.safeCount();
                    }
                }
            });
            threads.add(t);
        }

        for (Thread t : threads) {
            t.start();
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println(test.i);
        System.out.println(test.atomicInteger.get());
        System.out.println(System.currentTimeMillis() - start);
    }

    //使用CAS+自旋实现线程安全计数器
    private void safeCount() {
        for (; ; ) {
            int i = atomicInteger.get();
            boolean suc = atomicInteger.compareAndSet(i, ++i);
            if (suc) {
                break;
            }
        }
    }

    //非线程安全计数器
    private void nonSafeCount() {
        i++;
    }
}
