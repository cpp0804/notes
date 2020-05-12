package thread;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 测试ReentrantLock
 */
public class ReentrantLockTest {

    private static final Lock lock = new ReentrantLock();


    public static void main(String[] args) {
        fairLock();
    }


    public static void fairLock() {
        System.out.println("公平锁测试");
        for (int i = 0; i < 5; i++) {
            new Thread(new ThreadDemo(i)).start();
        }
    }

    static class ThreadDemo implements Runnable {
        Integer id;

        public ThreadDemo(Integer id) {
            this.id = id;
        }

        @Override
        public void run() {
            for (int i = 0; i < 2; i++) {
                try {
                    lock.lock();
                    System.out.println("获得锁的线程：" + id);
                    TimeUnit.MILLISECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        }
    }
}
