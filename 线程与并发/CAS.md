## 参考博文
[深入理解CAS算法原理](https://www.jianshu.com/p/21be831e851e)
[JUC原子类: CAS, Unsafe和原子类详解](https://www.pdai.tech/md/java/thread/java-thread-x-juc-AtomicInteger.html)
[CAS详解](https://www.jianshu.com/p/8e74009684c7)
[Java魔法类：Unsafe应用解析](https://tech.meituan.com/2019/02/14/talk-about-java-magic-class-unsafe.html)


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


# 2. JAVA实现的CAS
## 2.1 Unsafe
Java 无法直接访问底层操作系统，只能通过本地（native）方法来访问。Unsafe 提供了硬件级别的操作，比如说获取某个属性在内存中的位置，比如说修改对象的私有字段值

Unsafe是单例类，并且调用getUnsafe获取Unsafe实例的调用类必须是引导类加载器Bootstrap Classloader加载的，否则抛出SecurityException异常。
```java
public final class Unsafe {
  // 单例对象
  private static final Unsafe theUnsafe;

  private Unsafe() {
  }
  @CallerSensitive
  public static Unsafe getUnsafe() {
    Class var0 = Reflection.getCallerClass();
    // 仅在引导类加载器`BootstrapClassLoader`加载时才合法
    if(!VM.isSystemDomainLoader(var0.getClassLoader())) {    
      throw new SecurityException("Unsafe");
    } else {
      return theUnsafe;
    }
  }
}
```

不提倡在代码中显示使用Unsafe类，但是可以有两种获取他的示例的方法：
1. 通过Java命令行命令-Xbootclasspath/a把调用Unsafe相关方法的类A所在jar包路径追加到默认的bootstrap路径中，使得A被引导类加载器加载，从而通过Unsafe.getUnsafe方法安全的获取Unsafe实例
```
java -Xbootclasspath/a: ${path}   // 其中path为调用Unsafe相关方法的类所在jar包路径 
```

2. 通过反射获取单例对象theUnsafe
```java
private static Unsafe reflectGetUnsafe() {
    try {
      Field field = Unsafe.class.getDeclaredField("theUnsafe");
      field.setAccessible(true);
      return (Unsafe) field.get(null);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return null;
    }
}
```

## 应用
![Unsafe应用](./pic/CAS_Unsafe应用.png)

### 内存操作
在Java中创建的对象都分配在堆内存中，由虚拟机进程管控。而堆外内存是虚拟机管控之外的内存区域，它由操作系统管理。Java对堆外内存的操作通过Unsafe提供的操作堆外内存的native方法
```java
//分配内存, 相当于C++的malloc函数
public native long allocateMemory(long bytes);
//扩充内存
public native long reallocateMemory(long address, long bytes);
//释放内存
public native void freeMemory(long address);
//在给定的内存块中设置值
public native void setMemory(Object o, long offset, long bytes, byte value);
//内存拷贝
public native void copyMemory(Object srcBase, long srcOffset, Object destBase, long destOffset, long bytes);
//获取给定地址值，忽略修饰限定符的访问限制。与此类似操作还有: getInt，getDouble，getLong，getChar等
public native Object getObject(Object o, long offset);
//为给定地址设置值，忽略修饰限定符的访问限制，与此类似操作还有: putInt,putDouble，putLong，putChar等
public native void putObject(Object o, long offset, Object x);
//获取给定地址的byte类型的值（当且仅当该内存地址为allocateMemory分配时，此方法结果为确定的）
public native byte getByte(long address);
//为给定地址设置byte类型的值（当且仅当该内存地址为allocateMemory分配时，此方法结果才是确定的）
public native void putByte(long address, byte x);
```

- 使用堆外内存的原因
1. 改善GC时的回收停顿：使用堆外内存，可以使堆内存保持一个比较小的规模，减少GC的回收停顿时间
2. 提高I/O操作的性能：在I/O过程中， 需要将堆内存数据拷贝到堆外内存中， 对需要频繁拷贝并且生命周期短的数据可以直接放到堆外内存


DirectByteBuffer是Java用于实现堆外内存的一个重要类，通常用在通信过程中做缓冲池，如在Netty、MINA等NIO框架中应用广泛。DirectByteBuffer对于堆外内存的创建、使用、销毁等逻辑均由Unsafe提供的堆外内存API来实现。



### CAS
atomic原子类都是调用Unsafe中的方法实现的，它里面的方法都是native方法

经过反编译，可以看到他的CAS方法有3种
```java
public final native boolean compareAndSwapObject(Object paramObject1, long paramLong, Object paramObject2, Object paramObject3);

public final native boolean compareAndSwapInt(Object paramObject, long paramLong, int paramInt1, int paramInt2);

public final native boolean compareAndSwapLong(Object paramObject, long paramLong1, long paramLong2, long paramLong3);
```



## JAVA中CAS的应用——atomic
[atomic](./atomic.md)