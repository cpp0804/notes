package thread;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierDemo {

    public static void main(String[] args) {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(3, new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + " barrier action");
            }
        });
        MyThread t1 = new MyThread("t1", cyclicBarrier);
        MyThread t2 = new MyThread("t2", cyclicBarrier);
        t1.start();
        t2.start();
        System.out.println(Thread.currentThread().getName() + " going to await");
        try {
            cyclicBarrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + " continue");
    }

    static class MyThread extends Thread {
        private CyclicBarrier cb;
        public MyThread(String name, CyclicBarrier cb) {
            super(name);
            this.cb = cb;
        }

        public void run() {
            System.out.println(Thread.currentThread().getName() + " going to await");
            try {
                cb.await();
                System.out.println(Thread.currentThread().getName() + " continue");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
