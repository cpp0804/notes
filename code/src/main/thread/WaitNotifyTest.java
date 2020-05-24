package thread;

import java.util.ArrayList;
import java.util.List;

/**
 * 实现一个容器，提供两个方法，add，size 写两个线程，线程1添加10个元素到容器中，线程2实现监控元素的个数，当个数到5个时，线程2给出提示并结束
 */
public class WaitNotifyTest {

    volatile List list = new ArrayList();

    public void add(int i) {
        list.add(i);
    }

    public int getSize() {
        return list.size();
    }

    public static void main(String[] args) {
        WaitNotifyTest test = new WaitNotifyTest();
        Object obj = new Object();

        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (obj) {
                    System.out.println("t2 启动");
                    if (test.getSize() != 5) {
                        try {
                            obj.wait();
                            System.out.println("t2 结束");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    obj.notify();
                }
            }
        }, "t2").start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (obj) {
                    System.out.println("t1 启动");
                    for (int i = 0; i < 10; i++) {
                        test.add(i);
                        System.out.println("add" + i);
                        if (test.getSize() == 5) {
                            obj.notify();
                            try {
                                obj.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
            }
        }, "t1").start();
    }


}
