## 参考博文
[JUC锁: LockSupport详解](https://www.pdai.tech/md/java/thread/java-thread-x-lock-LockSupport.html)


[TOC]


# 1. 源码分析
## 构造函数
LockSupport只有一个私有构造函数，无法被实例化

```java
public class LockSupport {
    // Hotspot implementation via intrinsics API
    private static final sun.misc.Unsafe UNSAFE;
    // 表示内存偏移地址
    private static final long parkBlockerOffset;
    // 表示内存偏移地址
    private static final long SEED;
    // 表示内存偏移地址
    private static final long PROBE;
    // 表示内存偏移地址
    private static final long SECONDARY;

    // 私有构造函数，无法被实例化
    private LockSupport() {}
 
}
```

## park函数
调用park阻塞线程，直到：
1. 调用unpark函数，释放该线程的许可
2. 该线程被中断
3. 设置的时间到了

park不会释放锁

```java
    public static void park(){
        // 获取许可，设置时间为无限长，直到可以获取许可
        UNSAFE.park(false, 0L);
    }

    public static void park(Object blocker){
        // 获取当前线程
        Thread t = Thread.currentThread();
        // 设置Blocker
        setBlocker(t, blocker);
        // 获取许可
        UNSAFE.park(false, 0L);
        // 重新可运行后再此设置Blocker
        setBlocker(t, null);
    }
    private static void setBlocker(Thread t, Object arg) {
        // 设置线程t的parkBlocker字段的值为arg
        UNSAFE.putObject(t, parkBlockerOffset, arg);
    } 

    //阻塞一定时间的park
    public static void parkNanos(Object blocker, long nanos) {
        if (nanos > 0) { // 时间大于0
            // 获取当前线程
            Thread t = Thread.currentThread();
            // 设置Blocker
            setBlocker(t, blocker);
            // 获取许可，并设置了时间
            UNSAFE.park(false, nanos);
            // 设置许可
            setBlocker(t, null);
    }
}
  
```

## unpark函数
解除线程的阻塞状态
```java
public static void unpark(Thread thread) {
    if (thread != null) // 线程为不空
        UNSAFE.unpark(thread); // 释放该线程许可
}
```


# 2. 示例
## 使用wait/notify实现线程同步
[线程基础](./线程基础.md)

```java
class MyThread extends Thread {
    
    public void run() {
        synchronized (this) {
            System.out.println("before notify");            
            notify();
            System.out.println("after notify");    
        }
    }
}


public class WaitAndNotifyDemo {
    public static void main(String[] args) throws InterruptedException {
        MyThread myThread = new MyThread();            
        synchronized (myThread) {
            try {        
                myThread.start();
                // 主线程睡眠3s
                Thread.sleep(3000);
                System.out.println("before wait");
                // 阻塞主线程
                myThread.wait();
                System.out.println("after wait");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }            
        }        
    }
}
/*
before wait
before notify
after notify
after wait
*/
```

## 使用park/unpark实现线程同步
```java
import java.util.concurrent.locks.LockSupport;

class MyThread extends Thread {
    private Object object;

    public MyThread(Object object) {
        this.object = object;
    }

    public void run() {
        System.out.println("before unpark");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 获取blocker
        System.out.println("Blocker info " + LockSupport.getBlocker((Thread) object));
        // 释放许可
        LockSupport.unpark((Thread) object);
        // 休眠500ms，保证先执行park中的setBlocker(t, null);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 再次获取blocker
        System.out.println("Blocker info " + LockSupport.getBlocker((Thread) object));

        System.out.println("after unpark");
    }
}


public class test {
    public static void main(String[] args) {
        MyThread myThread = new MyThread(Thread.currentThread());
        myThread.start();
        System.out.println("before park");
        // 获取许可
        LockSupport.park("ParkAndUnparkDemo");
        System.out.println("after park");
    }
}
/*
before park
before unpark
Blocker info ParkAndUnparkDemo
after park
Blocker info null
after unpark
*/
```

# 3. 方法比较
[方法比较](./方法比较.md)