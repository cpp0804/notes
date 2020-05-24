## 参考博文
[JUC工具类: CyclicBarrier详解](https://www.pdai.tech/md/java/thread/java-thread-x-juc-tool-cyclicbarrier.html)




[TOC]

# 1. 概述
让一组线程到达一个屏障时被阻塞，直到最后一个线程到达了，被拦截的线程才会继续运行

# 2. 源码分析
## 内部属性
该属性有一个为ReentrantLock对象，有一个为Condition对象，而Condition对象又是基于AQS的，所以，归根到底，底层还是由AQS提供支持

可以指定在所有线程都进入屏障后的执行动作(barrierCommand)，该执行动作由最后一个进行屏障的线程执行
```java
public class CyclicBarrier {
    
    /** The lock for guarding barrier entry */
    // 可重入锁
    private final ReentrantLock lock = new ReentrantLock();
    /** Condition to wait on until tripped */
    // 条件队列
    private final Condition trip = lock.newCondition();
    /** The number of parties */
    // 参与的线程数量
    private final int parties;
    /* The command to run when tripped */
    // 由最后一个进入 barrier 的线程执行的操作
    private final Runnable barrierCommand;
    /** The current generation */
    // 当前代
    private Generation generation = new Generation();
    // 正在等待进入屏障的线程数量
    private int count;

    public CyclicBarrier(int parties, Runnable barrierAction) {
        // 参与的线程数量小于等于0，抛出异常
        if (parties <= 0) throw new IllegalArgumentException();
        // 设置parties
        this.parties = parties;
        // 设置count
        this.count = parties;
        // 设置barrierCommand
        this.barrierCommand = barrierAction;
    }   

    private static class Generation {
        boolean broken = false;
    }
}
```

## await()方法
调用await方法的线程告诉CyclicBarrier自己已经到达同步点，然后当前线程被阻塞。直到parties个参与线程调用了await方法，CyclicBarrier同样提供带超时时间的await和不带超时时间的await方法

使用独占锁来执行await方法

```java
public int await() throws InterruptedException, BrokenBarrierException {
    try {
        // 不超时等待
        return dowait(false, 0L);
    } catch (TimeoutException toe) {
        throw new Error(toe); // cannot happen
    }


public int await(long timeout, TimeUnit unit)
    throws InterruptedException,
            BrokenBarrierException,
            TimeoutException {
    return dowait(true, unit.toNanos(timeout));
}

private int dowait(boolean timed, long nanos)
    throws InterruptedException, BrokenBarrierException,
            TimeoutException {
    // 获取独占锁
    final ReentrantLock lock = this.lock;
    lock.lock();
    try {
        // 当前代
        final Generation g = generation;
        // 如果这代损坏了，抛出异常
        if (g.broken)
            throw new BrokenBarrierException();
 
        // 如果线程中断了，抛出异常
        if (Thread.interrupted()) {
            // 将损坏状态设置为true
            // 并通知其他阻塞在此栅栏上的线程
            breakBarrier();
            throw new InterruptedException();
        }
 
        // 获取下标
        int index = --count;
        // 如果是 0，说明最后一个线程调用了该方法
        if (index == 0) {  // tripped
            boolean ranAction = false;
            try {
                final Runnable command = barrierCommand;
                // 执行栅栏任务
                if (command != null)
                    command.run();
                ranAction = true;
                // 更新一代，将count重置，将generation重置
                // 唤醒之前等待的线程
                nextGeneration();
                return 0;
            } finally {
                // 如果执行栅栏任务的时候失败了，就将损坏状态设置为true
                if (!ranAction)
                    breakBarrier();
            }
        }
 
        // loop until tripped, broken, interrupted, or timed out
        for (;;) {
            try {
                 // 如果没有时间限制，则直接等待，直到被唤醒
                if (!timed)
                    trip.await();
                // 如果有时间限制，则等待指定时间
                else if (nanos > 0L)
                    nanos = trip.awaitNanos(nanos);
            } catch (InterruptedException ie) {
                // 当前代没有损坏
                if (g == generation && ! g.broken) {
                    // 让栅栏失效
                    breakBarrier();
                    throw ie;
                } else {
                    // 上面条件不满足，说明这个线程不是这代的
                    // 就不会影响当前这代栅栏的执行，所以，就打个中断标记
                    Thread.currentThread().interrupt();
                }
            }
 
            // 当有任何一个线程中断了，就会调用breakBarrier方法
            // 就会唤醒其他的线程，其他线程醒来后，也要抛出异常
            if (g.broken)
                throw new BrokenBarrierException();
 
            // g != generation表示正常换代了，返回当前线程所在栅栏的下标
            // 如果 g == generation，说明还没有换代，那为什么会醒了？
            // 因为一个线程可以使用多个栅栏，当别的栅栏唤醒了这个线程，就会走到这里，所以需要判断是否是当前代。
            // 正是因为这个原因，才需要generation来保证正确。
            if (g != generation)
                return index;
            
            // 如果有时间限制，且时间小于等于0，销毁栅栏并抛出异常
            if (timed && nanos <= 0L) {
                breakBarrier();
                throw new TimeoutException();
            }
        }
    } finally {
        // 释放独占锁
        lock.unlock();
    }
}
```

# 3. 实例
```java
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
/*
main going to await
t2 going to await
t1 going to await
t1 barrier action
main continue
t1 continue
t2 continue
*/
```