package thread;

import java.util.concurrent.TimeUnit;

/**
 * 守护线程测试
 */
public class DaemonTest extends Thread {


    public static void main(String args[]) throws InterruptedException {
        daemondExtendTest();
    }

    //测试守护线程
    public static void daemondTest() {
        //        DaemonTest test = new DaemonTest();
//        test.setDaemon(true);
//        test.start();
//        System.out.println("isDaemon=" + test.isDaemon());

        //创建一个用户线程
        Thread userThread = new Thread() {
            public void run() {
                for (int i = 1; i <= 5; i++) {
                    System.out.println("用户线程第" + i + "次运行.....");
                }
                System.out.println("用户线程退出.....");
            }
        };
        //创建一个用户模拟守护线程的线程
        Thread daemonThread = new Thread() {
            public void run() {
                for (int i = 1; i <= 100; i++) {
                    System.out.println("守护线程第" + i + "次运行.....");
                }
                System.out.println("守护线程退出.....");
            }
        };
        //让daemonThread成为守护线程
        daemonThread.setDaemon(true);//这里必须在启动前设置，如果不设置，默认人是用户线程
        userThread.start();
        daemonThread.start();
    }


    public void run() {
        for (int i = 0; i < 100; i++) {
            System.out.println(i);
        }
    }

    //测试父子线程
    public static void parentTest() {
        T1 t1 = new T1("子线程1");
        t1.start();
        System.out.println("主线程结束");
    }

    public static class T1 extends Thread {
        public T1(String name) {
            super(name);
        }

        @Override
        public void run() {
//            System.out.println(this.getName() + "开始执行," + (this.isDaemon() ? "我是守护线程" : "我是用户线程"));
//            for (int i =0; i < 100; i++) {
//                System.out.println(this.isDaemon() ? "我是守护线程" : "我是用户线程" + i);
//            }
            System.out.println(this.getName() + ".daemon:" + this.isDaemon());
        }
    }

    public static void daemondExtendTest() throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + ".daemon:" + Thread.currentThread().isDaemon());

        T1 t1 = new T1("t1");
        t1.start();

        Thread t2 = new Thread() {
            @Override
            public void run() {
                System.out.println(this.getName() + ".daemon:" + this.isDaemon());
                T1 t3 = new T1("t3");
                t3.start();
            }
        };

        t2.setName("t2");
        t2.setDaemon(true);
        t2.start();

        TimeUnit.SECONDS.sleep(2);
    }


}
