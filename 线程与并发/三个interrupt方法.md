## 参考博文
[interrupt(),interrupted() 和 isInterrupted() 的区别](https://juejin.im/post/5bdfa23be51d451ad03495f5)
[interrupt、interrupted和isInterrupted的区别](https://www.jianshu.com/p/edc5d5575aee)


[TOC]

# 1. interrupt()
将调用该方法的线程状态设置成中断状态，但不影响线程的执行。

只有在将当前线程设置中断位的情况下，只要在自己线程执行过程中遇到可中断方法(线程处于阻塞、限期等待或者无限期等待状态)，不论是当前线程调用可中断方法还是其他线程调用可中断方法(sleep、wait等)，都会抛出InterruptedException异常

对于当前线程的子线程，只能在自己的run方法中才能抛出InterruptedException异常
```java
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

            Thread.currentThread().interrupt();
            myThread.sleep(1000);

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
/*
i= 0
main catch
main end
i= 1
i= 2
i= 3
i= 4
*/
```

# 2. interrupted()
是个静态方法,作用于当前线程。

获取==当前线程==的中断状态位，并清除中断标记(true)。只有==当前线程==能清除自己的中断位

实现调用的就是isInterrupted(true)

```java
public static boolean interrupted() {
    return currentThread().isInterrupted(true);
}
```

```java
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

            myThread.interrupt();

//            Thread.currentThread().interrupt();

            System.out.println("myThread是否中断" + myThread.isInterrupted());
            System.out.println("mainThread是否中断" + Thread.currentThread().isInterrupted());

            System.out.println("-----------------------------");

            //其实执行的是Thread.currentThread().interrupted()
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
/*
i= 0
i= 1
i= 2
i= 3
i= 4
myThread是否中断false
mainThread是否中断false
-----------------------------
myThread是否中断1 false
myThread是否中断2 false
main end
*/
```


```java
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
/*
i= 0
i= 1
i= 2
i= 3
i= 4
myThread是否中断false
mainThread是否中断true
-----------------------------
myThread是否中断1 true
myThread是否中断2 false
main end
*/
```


# 4. isInterrupted()
实现调用的就是isInterrupted(false)，作用于调用该方法的线程对象。调用该方法的线程对象不一定是当前线程，例如可以在线程A中使用线程B调用。

获取==调用该方法的线程对象==的中断状态位，但不清除状态位(false)

```java
public boolean isInterrupted() {
    return isInterrupted(false);
}
```
