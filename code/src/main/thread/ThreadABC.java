package thread;

/**
 * 使用wait、notify实现线程的顺序执行
1.从大的方向上来讲，该问题为三线程间的同步唤醒操作，
主要的目的就是ThreadA->ThreadB->ThreadC->ThreadA循环执行三个线程
2.为了控制线程执行的顺序，那么就必须要确定唤醒、等待的顺序，
所以每一个线程必须同时持有两个对象锁，才能继续执行。
一个对象锁是prev，就是前一个线程所持有的对象锁。还有一个就是自身对象锁
3.为了控制执行的顺序，必须要先持有prev锁，也就是前一个线程要释放自身对象锁，
再去申请自身对象锁，两者兼备时打印，之后首先调用self.notify()释放自身对象锁，
唤醒下一个等待线程，再调用prev.wait()释放prev对象锁，终止当前线程
*/
public class ThreadABC implements Runnable {

    private String name;
    private Object prev;
    private Object self;

    public ThreadABC(String name, Object prev, Object self) {
        this.name = name;
        this.prev = prev;
        this.self = self;
    }

    @Override
    public void run() {
        int count = 10;
        while (count > 0) {
            synchronized (prev) {
                synchronized (self) {
                    System.out.println(name);
                    count--;
                    self.notify();
                }
                try {
                    prev.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Object a = new Object();
        Object b = new Object();
        Object c = new Object();
        ThreadABC ta = new ThreadABC("A", c, a);
        ThreadABC tb = new ThreadABC("B", a, b);
        ThreadABC tc = new ThreadABC("C", b, c);

        new Thread(ta).start();
        Thread.sleep(100);

        new Thread(tb).start();
        Thread.sleep(100);

        new Thread(tc).start();
        Thread.sleep(100);
    }
}
