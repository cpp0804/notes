## 参考博文
[JUC锁: 锁核心类AQS详解](https://www.pdai.tech/md/java/thread/java-thread-x-lock-AbstractQueuedSynchronizer.html) ⚠️
[深入理解AbstractQueuedSynchronizer(AQS)](https://juejin.im/post/5aeb07ab6fb9a07ac36350c8) ⚠️


[TOC]

AbstractQueuedSynchronizer(AQS)是构建锁和同步器的框架，ReentrantLock，Semaphore，ReentrantReadWriteLock，SynchronousQueue，FutureTask，CountDownLatch皆是基于AQS的

# 1. AQS核心思想
AQS的核心思想是：
1. 如果某个线程请求一个共享资源，这个共享资源当前是空闲状态，就将该线程设置为有效的工作线程
2. 如果该共享资源被占用，就将该线程放入CLH队列中等待

AQS的实现也是基于[CAS](./CAS.md)、[volatile](./volatile.md)和[LockSupport](./LockSupport.md)


## 1.1 state和两个队列
AQS维持两个东西
1. 状态变量state:他是volatile的，并使用CAS对他进行操作
```java
private volatile int state

//返回同步状态的当前值
protected final int getState() {  
        return state;
}

 // 设置同步状态的值
protected final void setState(int newState) { 
        state = newState;
}

//原子地(CAS操作)将同步状态值设置为给定值update如果当前同步状态的值等于expect(期望值)
protected final boolean compareAndSetState(int expect, int update) {
        return unsafe.compareAndSwapInt(this, stateOffset, expect, update);
}
```

2. 同步队列：是双向链表，包括head结点和tail结点。没获取到锁的线程被封装成Node在队列中排队等待

3. 条件队列：当使用Condition时才用到这个队列，是一个单向链表。一个Condition对应一个队列

![AQS队列](./pic/AQS_AQS队列.png)

对两个队列插入和释放Node都采用了CAS操作


## 1.2 模板模式
AQS基于模板模式设计，如果要实现一个同步器，只需要继承AQS并重写指定的以下方法。这些方法都是对共享资源state的获取与释放，对队列的维护AQS已经实现好了。
```java
//该线程是否正在独占资源。只有用到condition才需要去实现它。
boolean isHeldExclusively()

//独占方式。尝试获取资源，成功则返回true，失败则返回false。
boolean tryAcquire(int)
//独占方式。尝试释放资源，成功则返回true，失败则返回false。
boolean tryRelease(int)

//共享方式。尝试获取资源。负数表示失败；0表示成功，但没有剩余可用资源；正数表示成功，且有剩余资源。
int tryAcquireShared(int)
//共享方式。尝试释放资源，成功则返回true，失败则返回false。
boolean tryReleaseShared(int)
```

以ReentrantLock为例，state初始化为0表示未锁定状态。A线程lock()时，会调用tryAcquire()独占该锁并将state+1。其他线程再tryAcquire()时就会失败，直到A线程unlock()到state=0(即释放锁)为止，其它线程才有机会获取该锁。
释放锁之前，A线程自己是可以重复获取此锁的(state会累加)，这就是可重入的概念。获取多少次就要释放多少次，这样才能保证state是能回到零态的

## 1.3 AQS对资源的共享方式
#### Exclusive(独占)
只有一个线程能执行，如ReentrantLock。

又可分为公平锁和非公平锁：
- 公平锁：按照线程在队列中的排队顺序，先到者先拿到锁
- 非公平锁：当线程要获取锁时，无视队列顺序直接去抢锁，谁抢到就是谁的

#### Share(共享)
Share(共享)：多个线程可同时执行，如Semaphore、CountDownLatch


# 2. AQS源码分析
## AbstractOwnableSynchronizer
AbstractQueuedSynchronizer继承自AbstractOwnableSynchronizer抽象类，并且实现了Serializable接口，可以进行序列化
```java
public abstract class AbstractQueuedSynchronizer extends AbstractOwnableSynchronizer implements java.io.Serializable
```

```java
public abstract class AbstractOwnableSynchronizer implements java.io.Serializable {
    
    // 版本序列号
    private static final long serialVersionUID = 3737899427754241961L;

    // 构造方法
    protected AbstractOwnableSynchronizer() { }

    // 独占模式下的线程
    private transient Thread exclusiveOwnerThread;
    
    // 设置独占线程 
    protected final void setExclusiveOwnerThread(Thread thread) {
        exclusiveOwnerThread = thread;
    }
    
    // 获取独占线程 
    protected final Thread getExclusiveOwnerThread() {
        return exclusiveOwnerThread;
    }
}
```

## AQS内部类——Node
每个线程被阻塞的线程都会被封装成一个Node结点放入队列。每个Node包含了一个Thread类型的引用，并且每个节点都存在一个状态

```java
static final class Node {
    // 共享模式
    static final Node SHARED = new Node();
    // 独占模式
    static final Node EXCLUSIVE = null;        
    /*结点状态
    CANCELLED，值为1，表示当前的线程被取消
    SIGNAL，值为-1，表示当前节点的后继节点包含的线程需要运行，需要进行unpark操作
    CONDITION，值为-2，表示当前节点在等待condition，也就是在condition队列中
    PROPAGATE，值为-3，表示当前场景下后续的acquireShared能够得以执行
    */
    // 值为0，表示当前节点在sync队列中，等待着获取锁
    static final int CANCELLED =  1;
    static final int SIGNAL    = -1;
    static final int CONDITION = -2;
    static final int PROPAGATE = -3;        

    // 其值只能为CANCELLED、SIGNAL、CONDITION、PROPAGATE或0
    // 0表示当前节点在sync队列中，等待着获取锁
    volatile int waitStatus;        
    // 前驱结点
    volatile Node prev;    
    // 后继结点
    volatile Node next;        
    // 结点所对应的线程
    volatile Thread thread;        
    // 下一个等待者
    Node nextWaiter;
    
    // 结点是否在共享模式下等待
    final boolean isShared() {
        return nextWaiter == SHARED;
    }
    
    // 获取前驱结点，若前驱结点为空，抛出异常
    final Node predecessor() throws NullPointerException {
        // 保存前驱结点
        Node p = prev; 
        if (p == null) // 前驱结点为空，抛出异常
            throw new NullPointerException();
        else // 前驱结点不为空，返回
            return p;
    }
    
    // 无参构造方法
    Node() {    // Used to establish initial head or SHARED marker
    }
    
    // 构造方法
        Node(Thread thread, Node mode) {    // Used by addWaiter
        this.nextWaiter = mode;
        this.thread = thread;
    }
    
    // 构造方法
    Node(Thread thread, int waitStatus) { // Used by Condition
        this.waitStatus = waitStatus;
        this.thread = thread;
    }
}
```



## AQS内部类——ConditionObject
在使用Condition前，线程必须获得锁。

- condition.await()：当前线程构造Node进入condition的等待队列,然后释放锁并唤醒后继节点,最后调用LockSupport.park()阻塞当前线程
- condition.signal()：随机解除某一个线程。如果是非公平锁将直接尝试获取锁，如果获取不到将其从condition的等待队列放入AQS的同步队列
- condition.signalAll()：解除condition等待队列中的所有线程

线程执行condition.await()方法，将节点1从同步队列转移到条件队列中。
![await](./pic/ReentrantLock_await.png)

线程执行condition.signal()方法，将节点1从条件队列中转移到同步队列
![signal](./pic/ReentrantLock_signal.png)


阻塞队列的简单实现：
1.入队和出队线程安全
2.当队列满时,入队线程会被阻塞;当队列为空时,出队线程会被阻塞。

```java
package thread;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用Condition和Lock实现阻塞队列
 */
public class BlockingQueue<E> {

    int size;

    ReentrantLock lock = new ReentrantLock();

    LinkedList<E> linkedList = new LinkedList<>();

    Condition notFull = lock.newCondition();
    Condition notEmpty = lock.newCondition();

    public BlockingQueue(int size) {
        this.size = size;
    }

    public void enqueue(E e) throws InterruptedException {
        lock.lock();
        try {
            while (linkedList.size() == size) {
                notFull.await();
            }

            linkedList.addLast(e);
            System.out.println("入队：" + e);
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public E dequeue() throws InterruptedException {
        E e;
        lock.lock();
        try {
            while (linkedList.size() == 0) {
                notEmpty.await();
            }
            e = linkedList.removeFirst();
            System.out.println("出队：" + e);
            notEmpty.signal();

            return e;
        } finally {
            lock.unlock();
        }
    }
}
```

- 源码分析
```java
public class ConditionObject implements Condition, java.io.Serializable {
    // 版本号
    private static final long serialVersionUID = 1173984872572414699L;
    /** First node of condition queue. */
    // condition队列的头结点
    private transient Node firstWaiter;
    /** Last node of condition queue. */
    // condition队列的尾结点
    private transient Node lastWaiter;

    private Node addConditionWaiter() {
         ...
    }

    public final void signal() {
        //首先判断当前线程是否获取锁了
         if (!isHeldExclusively())
                throw new IllegalMonitorStateException();
            Node first = firstWaiter;
            if (first != null)
                doSignal(first);
    }

    private void doSignal(Node first) {
            do {
                if ( (firstWaiter = first.nextWaiter) == null)
                    lastWaiter = null;
                first.nextWaiter = null;
            } while (!transferForSignal(first) &&
                     (first = firstWaiter) != null);
    }

     public final void signalAll() {
         ...
     }

     public final void await() throws InterruptedException {
         if (Thread.interrupted())
                throw new InterruptedException();
            Node node = addConditionWaiter();
            int savedState = fullyRelease(node);
            int interruptMode = 0;
            while (!isOnSyncQueue(node)) {
                LockSupport.park(this);
                if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)
                    break;
            }
            if (acquireQueued(node, savedState) && interruptMode != THROW_IE)
                interruptMode = REINTERRUPT;
            if (node.nextWaiter != null) // clean up if cancelled
                unlinkCancelledWaiters();
            if (interruptMode != 0)
                reportInterruptAfterWait(interruptMode);
     }
}
```

```java
public interface Condition {

    // 等待，当前线程在接到信号或被中断之前一直处于等待状态
    void await() throws InterruptedException;
    
    // 等待，当前线程在接到信号之前一直处于等待状态，不响应中断
    void awaitUninterruptibly();
    
    //等待，当前线程在接到信号、被中断或到达指定等待时间之前一直处于等待状态 
    long awaitNanos(long nanosTimeout) throws InterruptedException;
    
    // 等待，当前线程在接到信号、被中断或到达指定等待时间之前一直处于等待状态。此方法在行为上等效于: awaitNanos(unit.toNanos(time)) > 0
    boolean await(long time, TimeUnit unit) throws InterruptedException;
    
    // 等待，当前线程在接到信号、被中断或到达指定最后期限之前一直处于等待状态
    boolean awaitUntil(Date deadline) throws InterruptedException;
    
    // 唤醒一个等待线程。如果所有的线程都在等待此条件，则选择其中的一个唤醒。在从 await 返回之前，该线程必须重新获取锁。
    void signal();
    
    // 唤醒所有等待线程。如果所有的线程都在等待此条件，则唤醒所有线程。在从 await 返回之前，每个线程都必须重新获取锁。
    void signalAll();
}
```


## AbstractQueuedSynchronizer
- 属性
```java
public abstract class AbstractQueuedSynchronizer extends AbstractOwnableSynchronizer
    implements java.io.Serializable {    
    // 版本号
    private static final long serialVersionUID = 7373984972572414691L;    
    // 头结点
    private transient volatile Node head;    
    // 尾结点
    private transient volatile Node tail;    
    // 状态
    private volatile int state;    
    // 自旋时间
    static final long spinForTimeoutThreshold = 1000L;
    
    // Unsafe类实例
    private static final Unsafe unsafe = Unsafe.getUnsafe();
    // state内存偏移地址
    private static final long stateOffset;
    // head内存偏移地址
    private static final long headOffset;
    // state内存偏移地址
    private static final long tailOffset;
    // tail内存偏移地址
    private static final long waitStatusOffset;
    // next内存偏移地址
    private static final long nextOffset;
}
```


## 独占模式
- acquire

1. 调用tryAcquire():调用该方法的线程会尝试在独占模式下获取锁。如果获取失败，则执行2，3步骤
2. 调用addWaiter():将调用acquire的线程封装成Node加入同步队列中
3. 调用acquireQueued():同步队列中的线程自旋，不断尝试获取资源

```java
  //此方法是独占模式下线程获取共享资源的顶层入口
    public final void acquire(int arg) {
        /*首先，调用使用者重写的tryAcquire方法，
        若返回true，意味着获取同步状态成功，后面的逻辑不再执行；
        若返回false，也就是获取同步状态失败
        */
        boolean hasAcquired = tryAcquire(arg);
        if (!hasAcquired) {
            /*此时，获取同步状态失败，构造独占式同步结点，
            通过addWatiter将此结点添加到同步队列的尾部
            (此时可能会有多个线程结点试图加入同步队列尾部，需要以线程安全的方式添加)
            */
            Node currentThreadNode = addWaiter(Node.EXCLUSIVE);
            /*
            　　加入队列中的结点线程进入自旋状态，
            　　若是老二结点（即前驱结点为头结点），才有机会尝试去获取同步状态(tryAcquire)；
            　　否则，当其前驱结点的状态为SIGNAL，线程便可安心休息，进入阻塞状态，直到被中断或者被前驱结点唤醒
            */
            boolean interrupted = acquireQueued(currentThreadNode, arg);
            if (interrupted) {
                selfInterrupt();
            }
        }
    }

// sync队列中的结点在独占且忽略中断的模式下获取(资源)
final boolean acquireQueued(final Node node, int arg) {
    // 标志
    boolean failed = true;
    try {
        // 中断标志
        boolean interrupted = false;
        for (;;) { // 无限循环
            // 获取node节点的前驱结点
            final Node p = node.predecessor(); 
            // 只有队列中排队的第一个线程才能获得锁
            if (p == head && tryAcquire(arg)) {
                // 把当前节点设置头结点，代表获得锁后把这个线程从同步队列中移除了 
                setHead(node); 
                p.next = null; // help GC
                failed = false; // 设置标志
                return interrupted; 
            }
            if (shouldParkAfterFailedAcquire(p, node) &&
                parkAndCheckInterrupt())
                interrupted = true;
        }
    } finally {
        if (failed)
            cancelAcquire(node);
    }
}

    private void setHead(Node node) {
        head = node;
        node.thread = null;
        node.prev = null;
    }
```

- release

```java
//此方法是独占模式下线程释放共享资源的顶层入口。
//它会释放指定量的资源，如果彻底释放了（即state=0）,它会唤醒等待队列里的其他线程来获取资源
public final boolean release(int arg) {
    if (tryRelease(arg)) {
        Node h = head;//找到头结点
        if (h != null && h.waitStatus != 0)
            unparkSuccessor(h);//唤醒等待队列里的下一个线程
        return true;
    }
    return false;
}
```

## 共享模式
- acquire

```java
//此方法是共享模式下线程获取共享资源的顶层入口。
//它会获取指定量的资源，获取成功则直接返回，获取失败则进入等待队列，直到获取到资源为止，整个过程忽略中断

 public final void acquireShared(int arg) {
      //tryAcquireShared()尝试获取资源，成功则直接返回；
      /*
      　1.当返回值大于0时，表示获取同步状态成功，同时还有剩余同步状态可供其他线程获取；
　　    2.当返回值等于0时，表示获取同步状态成功，但没有可用同步状态了；
　　    3.当返回值小于0时，表示获取同步状态失败。
      */
      if (tryAcquireShared(arg) < 0)
          //失败则通过doAcquireShared()进入等待队列，直到获取到资源为止才返回。
          /*
          当排队中的老二获取到同步状态，如果还有可用资源，会继续传播下去
          如果老二的资源不够，老二会继续park()等待其他线程释放资源，也更不会去唤醒老三和老四了
          */
          doAcquireShared(arg);
 }

//队列中的线程开始自旋，只有当前驱结点是head的节点才会尝试获取锁，如果获取到的state>0则获取成功并从自旋中退出
private void doAcquireShared(int arg) {
        final Node node = addWaiter(Node.SHARED);
        boolean failed = true;
        try {
            boolean interrupted = false;
            for (;;) {
                final Node p = node.predecessor();
                if (p == head) {
                    int r = tryAcquireShared(arg);
                    if (r >= 0) {
                        setHeadAndPropagate(node, r);
                        p.next = null; // help GC
                        if (interrupted)
                            selfInterrupt();
                        failed = false;
                        return;
                    }
                }
                if (shouldParkAfterFailedAcquire(p, node) &&
                    parkAndCheckInterrupt())
                    interrupted = true;
            }
        } finally {
            if (failed)
                cancelAcquire(node);
        }
}
```

- release

```java
//此方法是共享模式下线程释放共享资源的顶层入口。
//它会释放指定量的资源，如果成功释放且允许唤醒等待线程，它会唤醒等待队列里的其他线程来获取资源
public final boolean releaseShared(int arg) {
    if (tryReleaseShared(arg)) {//尝试释放资源
        doReleaseShared();//唤醒后继结点
        return true;
    }
    return false;
}
/*
此方法的流程也比较简单，一句话：释放掉资源后，唤醒后继,跟独占模式下的release()相似.
但有一点稍微需要注意：独占模式下的tryRelease()在完全释放掉资源（state=0）后，才会返回true去唤醒其他线程
这主要是基于独占下可重入的考量；而共享模式下的releaseShared()则没有这种要求
共享模式实质就是控制一定量的线程并发执行，那么拥有资源的线程在释放掉部分资源时就可以唤醒后继等待结点
例如，资源总量是13，A（5）和B（7）分别获取到资源并发运行，C（4）来时只剩1个资源就需要等待。
A在运行过程中释放掉2个资源量，然后tryReleaseShared(2)返回true唤醒C，
C一看只有3个仍不够继续等待；
随后B又释放2个，tryReleaseShared(2)返回true唤醒C，C一看有5个够自己用了，然后C就可以跟A和B一起运行。
而ReentrantReadWriteLock读锁的tryReleaseShared()只有在完全释放掉资源（state=0）才返回true，
所以自定义同步器可以根据需要决定tryReleaseShared()的返回值。
*/
```


# 3. 自定义同步组件
## 独占锁
定义一个锁，在同一时刻只能有一个线程占有

```java
package thread;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 基于AQS实现独占锁
 */
public class Mutex implements Lock {

    //将操作代理到Sync上
    private final Sync sync = new Sync();

    private static class Sync extends AbstractQueuedSynchronizer {

        //是否处于占用状态
        protected boolean isHeldExclusively() {
            return getState() == 1;
        }

        //当状态为0时获取锁
        public boolean tryAcquire(int acquires) {
            if (compareAndSetState(0, 1)) {
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        //释放锁，将状态设置为0
        protected boolean tryRelease(int releases) {
            if (getState() == 0) {
                throw new IllegalMonitorStateException();
            }
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }

        //返回一个condition，每个condition包含一个条件队列
        Condition newCondition() {
            return new ConditionObject();
        }

    }

    @Override
    public void lock() {
        sync.acquire(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    @Override
    public boolean tryLock() {
        return sync.tryAcquire(1);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(time));
    }

    @Override
    public void unlock() {
        sync.release(1);
    }

    @Override
    public Condition newCondition() {
        return sync.newCondition();
    }
}
```

## 共享锁
在同一时刻允许最多两个线程同时访问。设置初始state=2，没当一个线程获取到锁就减1。当state=0时，再获取的线程将阻塞

```java
package thread;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 基于AQS实现共享锁
 */
public class TwinsLock implements Lock {

    //将操作代理到Sync上
    private final Sync sync = new Sync(2);

    private static class Sync extends AbstractQueuedSynchronizer {

        Sync(int count) {
            if (count <= 0) {
                throw new IllegalArgumentException("count must be larger than zero");
            }
            setState(count);
        }


        @Override
        protected int tryAcquireShared(int reduceCount) {
            for (; ; ) {
                int current = getState();
                int newCount = current - reduceCount;
                if (newCount < 0 || compareAndSetState(current, newCount)) {
                    return newCount;
                }
            }
        }

        @Override
        protected boolean tryReleaseShared(int returnCount) {
            for (; ; ) {
                int current = getState();
                int newCount = current + returnCount;
                if (compareAndSetState(current, newCount)) {
                    return true;
                }
            }
        }
    }

    @Override
    public void lock() {
        sync.acquireShared(1);
    }

    @Override
    public void unlock() {
        sync.releaseShared(1);
    }


    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }
}
```