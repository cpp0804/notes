## 参考博文
[深入理解CAS算法原理](https://www.jianshu.com/p/21be831e851e)
[JUC原子类: CAS, Unsafe和原子类详解](https://www.pdai.tech/md/java/thread/java-thread-x-juc-AtomicInteger.html)
[CAS详解](https://www.jianshu.com/p/8e74009684c7)


[TOC]

# 1. CAS
## 1.1 概念
有三个操作数：
1. 主存值V
2. 旧的预期值A
3. 要修改的新值B

当且仅当A=V时，线程才能将V修改成B

例如两个线程要修改主存中的某个值56，线程1和线程2都将内存中的值读到自己的工作内存。线程1先将值修改成57，并写回主存，此时V=56,A=56,B=56，线程1写回主存成功。接下来线程2将值修改成58，并写回主存，此时V=57,A=56,B=59，线程2修改失败

CAS是CPU指令集级的原子操作操作，执行速度非常快，它避免了加锁的开销。他的实现是基于硬件平台的汇编指令，就是说CAS是靠硬件实现的


## 1.2 CAS的问题
### ABA问题
CAS在检查时是检查数据有没有发生变化，但如果一个值由A变成B再变成A，进行CAS时会认为他没有变化

第一个线程先将变量值改成B，再改回A；另一个线程将值改成C成功了，没有关注到中间的变化
```java
package thread;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 测试CAS
 */
public class CASTest {

    static AtomicReference<String> atomicReference = new AtomicReference<>("A");

    public static void main(String[] args){
        ABA();
    }

    public static void ABA() {
        new Thread(() -> {
            atomicReference.compareAndSet("A","B");
            atomicReference.compareAndSet("B","A");
        },"t2").start();
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(atomicReference.compareAndSet("A","C")
                    + "\t" + atomicReference.get());
        },"t1").start();
    }
}
//true	C
```


- 解决方案:给变量带上版本号

比如现在主内存中的是A，版本号是1，然后t1和t2线程拷贝一份到自己工作内存。t2将A改为B，刷回主内存。此时主内存中的是B，版本号为2。然后再t2再改回A，此时主内存中的是A，版本号为3。这个时候t1线程终于来了，自己工作内存是A，版本号是1，主内存中是A，但是版本号为3，它就知道已经有人动过手脚了

具体可以使用AtomicStampedReference解决ABA问题
```java
public class AtomicStampedReference<V> {
    private static class Pair<T> {
        final T reference;  //维护对象引用
        final int stamp;  //用于标志版本
        private Pair(T reference, int stamp) {
            this.reference = reference;
            this.stamp = stamp;
        }
        static <T> Pair<T> of(T reference, int stamp) {
            return new Pair<T>(reference, stamp);
        }
    }
    private volatile Pair<V> pair;
    ....
    
    /**
      * expectedReference ：更新之前的原始值
      * newReference : 将要更新的新值
      * expectedStamp : 期待更新的标志版本
      * newStamp : 将要更新的标志版本
      */
    public boolean compareAndSet(V   expectedReference,
                             V   newReference,
                             int expectedStamp,
                             int newStamp) {
        // 获取当前的(元素值，版本号)对
        Pair<V> current = pair;
        return
            // 引用没变
            expectedReference == current.reference &&
            // 版本号没变
            expectedStamp == current.stamp &&
            // 新引用等于旧引用
            ((newReference == current.reference &&
            // 新版本号等于旧版本号
            newStamp == current.stamp) ||
            // 构造新的Pair对象并CAS更新
            casPair(current, Pair.of(newReference, newStamp)));
    }

    private boolean casPair(Pair<V> cmp, Pair<V> val) {
        // 调用Unsafe的compareAndSwapObject()方法CAS更新pair的引用为新引用
        return UNSAFE.compareAndSwapObject(this, pairOffset, cmp, val);
    }
}
```


```java
package thread;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * 测试CAS
 */
public class CASTest {

    static AtomicStampedReference<String> atomicReferenceVersion = new AtomicStampedReference<>("A", 1);

    public static void main(String[] args) {
        ABAWithVersion();
    }

    public static void ABAWithVersion() {
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);// 睡一秒，让t1线程拿到最初的版本号
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            atomicReferenceVersion.compareAndSet("A", "B", atomicReferenceVersion.getStamp(), atomicReferenceVersion.getStamp() + 1);
            atomicReferenceVersion.compareAndSet("B", "A", atomicReferenceVersion.getStamp(), atomicReferenceVersion.getStamp() + 1);
        }, "t2").start();
        new Thread(() -> {
            int stamp = atomicReferenceVersion.getStamp();//拿到最开始的版本号
            try {
                TimeUnit.SECONDS.sleep(3);// 睡3秒，让t2线程的ABA操作执行完
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(atomicReferenceVersion.compareAndSet("A", "C", stamp, stamp + 1));
        }, "t1").start();
    }
}
//false
```

### 只能保证一个变量的原子性
对于多个变量的线程安全就要用到锁


### 自旋CAS开销大
如果使用CAS+自旋长时间不成功，会浪费CPU的执行开销

# 2. JAVA实现的CAS
## Unsafe
[Unsafe](./Unsafe.md)



## JAVA中CAS的应用——atomic
[atomic](./atomic.md)


# 3. CAS+自旋
使用CAS+自旋实现线程安全的计数器

```java
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
/*
983122
1000000
79
*/
```