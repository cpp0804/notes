package thread;

/**
 * 测试三个interrupt的方法
 */
public class InterruptTest {

    public static void main(String[] args) {
        test();
    }

    public static void test() {
        try {
            MyThread01 myThread = new MyThread01();
            myThread.start();

            myThread.sleep(1000);

//            myThread.interrupt();

            Thread.currentThread().interrupt();

            System.out.println("myThread是否中断" + myThread.isInterrupted());
            System.out.println("mainThread是否中断" + Thread.currentThread().isInterrupted());

            System.out.println("-----------------------------");

            System.out.println("myThread是否中断1 " + myThread.interrupted());
            System.out.println("myThread是否中断2 " + myThread.interrupted());
        } catch (InterruptedException e) {
            System.out.println("main catch");
        }
        System.out.println("main end");
    }

    public static class MyThread01 extends Thread {
        @Override
        public void run() {
            super.run();
            for (int i = 0; i < 5; i++) {
                System.out.println("i= " + i);
            }
        }
    }
}
