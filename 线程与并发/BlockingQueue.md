## 参考博文
[JUC集合: BlockingQueue详解](https://www.pdai.tech/md/java/thread/java-thread-x-juc-collection-BlockingQueue.html)
[Java并发指南11：解读 Java 阻塞队列 BlockingQueue](https://yq.aliyun.com/articles/640072)

[TOC]


# 1. 阻塞队列
BlockingQueue适用于生产者和消费者的场景。生产者线程将元素插入队列中，当阻塞队列满时生产者线程将会阻塞，直到消费者线程取走元素；消费者线程将元素从队列中取走，当阻塞队列空时消费者线程将会阻塞，直到生产者线程放入元素。

![生产者和消费者](./pic/BlockingQueue_生产者和消费者.png)

阻塞队列提供4类方法来插入、去除和查看。对于无界阻塞队列，offer永远返回true，put永远不会被阻塞

方法|抛异常 |返回特殊值 |一直阻塞 |超时退出 |
---|---|---|---|---|
插入|add(e) |offer(e) |put(e) |offer(e, time, unit) 
移除 |remove() |poll()| take() |poll(time, unit) 
检查| element() |peek()  


无法向一个 BlockingQueue 中插入 null。如果你试图插入 null，BlockingQueue 将会抛出一个 NullPointerException。
可以访问到 BlockingQueue 中的所有元素，而不仅仅是开始和结束的元素


# 2. 阻塞队列的几种实现
## 2.1 数组阻塞队列 ArrayBlockingQueue
- 数组实现
- 有界
- 支持公平和非公平的队列
- 实现使用一个lock和两个condition


![ArrayBlockingQueue](./pic/BlockingQueue_ArrayBlockingQueue.png)

公平指按照线程被阻塞的先后顺序访问队列，非公平指当队列可用的时候，阻塞的线程可以随意争夺访问资格。默认使用非公平队列。其底层也是传入true/false来创建公平或者非公平的ReentrantLock


```java
public class ArrayBlockingQueue<E> extends AbstractQueue<E> implements BlockingQueue<E>, java.io.Serializable {
    final Object[] items;

    int takeIndex;

    int putIndex;

    int count;

    final ReentrantLock lock;

    private final Condition notEmpty;

    private final Condition notFull;

    public ArrayBlockingQueue(int capacity) {
        this(capacity, false);
    }
    //capacity决定了数组大小，也就是队列上限，一旦初始化完成就不能修改
    public ArrayBlockingQueue(int capacity, boolean fair) {
        if (capacity <= 0)
            throw new IllegalArgumentException();
        this.items = new Object[capacity];
        //通过传入的布尔值来决定是非公平还是公平
        lock = new ReentrantLock(fair);
        notEmpty = lock.newCondition();
        notFull =  lock.newCondition();
    }

    //enqueue和dequeue是BlockingQueue中定义的方法
     private void enqueue(E x) {
        // assert lock.getHoldCount() == 1;
        // assert items[putIndex] == null;
        final Object[] items = this.items;
        items[putIndex] = x;
        if (++putIndex == items.length)
            putIndex = 0;
        count++;
        notEmpty.signal();
    }

    private E dequeue() {
        // assert lock.getHoldCount() == 1;
        // assert items[takeIndex] != null;
        final Object[] items = this.items;
        @SuppressWarnings("unchecked")
        E x = (E) items[takeIndex];
        items[takeIndex] = null;
        if (++takeIndex == items.length)
            takeIndex = 0;
        count--;
        if (itrs != null)
            itrs.elementDequeued();
        notFull.signal();
        return x;
    }
    //核心还是调用的offer，如果offer返回true就抛出IllegalStateException
    public boolean add(E e) {
        return super.add(e);
    }


}

```

## 2.2 有界链阻塞队列 LinkedBlockingQueue
- 双向链表实现
- 有界
- 实现使用2个lock和2个condition

takeLock 和 notEmpty 搭配：如果要获取（take）一个元素，需要获取 takeLock 锁，但是获取了锁还不够，如果队列此时为空，还需要队列不为空（notEmpty）这个条件（Condition）。

putLock 需要和 notFull 搭配：如果要插入（put）一个元素，需要获取 putLock 锁，但是获取了锁还不够，如果队列此时已满，还需要队列不是满的（notFull）这个条件（Condition）

```java
public class LinkedBlockingQueue<E> extends AbstractQueue<E> implements BlockingQueue<E>, java.io.Serializable {
    static class Node<E> {
        E item;

        Node<E> prev;

        Node<E> next;

        Node(E x) { item = x; }
    }

    private final int capacity;

    private final AtomicInteger count = new AtomicInteger();

    transient Node<E> head;

    private transient Node<E> last;

    private final ReentrantLock takeLock = new ReentrantLock();

    private final Condition notEmpty = takeLock.newCondition();

    private final ReentrantLock putLock = new ReentrantLock();

    private final Condition notFull = putLock.newCondition();

    public LinkedBlockingQueue() {
        this(Integer.MAX_VALUE);
    }

    public LinkedBlockingQueue(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException();
        this.capacity = capacity;
        last = head = new Node<E>(null);
    }

    public void put(E e) throws InterruptedException {
        if (e == null) throw new NullPointerException();
        // 如果你纠结这里为什么是 -1，可以看看 offer 方法。这就是个标识成功、失败的标志而已。
        int c = -1;
        Node<E> node = new Node(e);
        final ReentrantLock putLock = this.putLock;
        final AtomicInteger count = this.count;
        // 必须要获取到 putLock 才可以进行插入操作
        putLock.lockInterruptibly();
        try {
            // 如果队列满，等待 notFull 的条件满足。
            while (count.get() == capacity) {
                notFull.await();
            }
            // 入队
            enqueue(node);
            // count 原子加 1，c 还是加 1 前的值
            c = count.getAndIncrement();
            // 如果这个元素入队后，还有至少一个槽可以使用，调用 notFull.signal() 唤醒等待线程。
            // 哪些线程会等待在 notFull 这个 Condition 上呢？
            if (c + 1 < capacity)
                notFull.signal();
        } finally {
            // 入队后，释放掉 putLock
            putLock.unlock();
        }
        // 如果 c == 0，那么代表队列在这个元素入队前是空的（不包括head空节点），
        // 那么所有的读线程都在等待 notEmpty 这个条件，等待唤醒，这里做一次唤醒操作
        if (c == 0)
            signalNotEmpty();
}

    // 入队的代码非常简单，就是将 last 属性指向这个新元素，并且让原队尾的 next 指向这个元素
    // 这里入队没有并发问题，因为只有获取到 putLock 独占锁以后，才可以进行此操作
    private void enqueue(Node<E> node) {
        // assert putLock.isHeldByCurrentThread();
        // assert last.next == null;
        last = last.next = node;
    }

    // 元素入队后，如果需要，调用这个方法唤醒读线程来读
    private void signalNotEmpty() {
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lock();
        try {
            notEmpty.signal();
        } finally {
            takeLock.unlock();
        }
    }

    public E take() throws InterruptedException {
    E x;
    int c = -1;
    final AtomicInteger count = this.count;
    final ReentrantLock takeLock = this.takeLock;
    // 首先，需要获取到 takeLock 才能进行出队操作
    takeLock.lockInterruptibly();
    try {
        // 如果队列为空，等待 notEmpty 这个条件满足再继续执行
        while (count.get() == 0) {
            notEmpty.await();
        }
        // 出队
        x = dequeue();
        // count 进行原子减 1
        c = count.getAndDecrement();
        // 如果这次出队后，队列中至少还有一个元素，那么调用 notEmpty.signal() 唤醒其他的读线程
        if (c > 1)
            notEmpty.signal();
    } finally {
        // 出队后释放掉 takeLock
        takeLock.unlock();
    }
    // 如果 c == capacity，那么说明在这个 take 方法发生的时候，队列是满的
    // 既然出队了一个，那么意味着队列不满了，唤醒写线程去写
    if (c == capacity)
        signalNotFull();
    return x;
}
    // 取队头，出队
    private E dequeue() {
        // assert takeLock.isHeldByCurrentThread();
        // assert head.item == null;
        // 之前说了，头结点是空的
        Node<E> h = head;
        Node<E> first = h.next;
        h.next = h; // help GC
        // 设置这个为新的头结点
        head = first;
        E x = first.item;
        first.item = null;
        return x;
    }
    // 元素出队后，如果需要，调用这个方法唤醒写线程来写
    private void signalNotFull() {
        final ReentrantLock putLock = this.putLock;
        putLock.lock();
        try {
            notFull.signal();
        } finally {
            putLock.unlock();
        }
    }
}
```

## 2.3 无界链阻塞队列 LinkedTransferQueue
- 基于链表实现
- 无界
- 基于unsafe实现

transfer和tryTransfer方法
>transfer
如果有消费者线程阻塞等待元素(使用了take或带时间的poll)，transfer会将生产者传入的元素立刻给消费者。如果没有消费者在等待，transfer会将元素放入tail节点，直到被消费者消费了才返回

>tryTransfer
试探生产者生产的元素是否能直接给消费者，如果没有消费者等待则返回false。但无论是否有消费者接受，方法都立即返回

```java
public class LinkedTransferQueue<E> extends AbstractQueue<E>
    implements TransferQueue<E>, java.io.Serializable {

    static final class Node {
        final boolean isData;   // false if this is a request node
        volatile Object item;   // initially non-null if isData; CASed to match
        volatile Node next;
        volatile Thread waiter; // null until waiting

        // CAS methods for fields
        final boolean casNext(Node cmp, Node val) {
            return UNSAFE.compareAndSwapObject(this, nextOffset, cmp, val);
        }

        final boolean casItem(Object cmp, Object val) {
            // assert cmp == null || cmp.getClass() != Node.class;
            return UNSAFE.compareAndSwapObject(this, itemOffset, cmp, val);
        }

        Node(Object item, boolean isData) {
            UNSAFE.putObject(this, itemOffset, item); // relaxed write
            this.isData = isData;
        }

        final void forgetNext() {
            UNSAFE.putObject(this, nextOffset, this);
        }

        final void forgetContents() {
            UNSAFE.putObject(this, itemOffset, this);
            UNSAFE.putObject(this, waiterOffset, null);
        }

        final boolean isMatched() {
            Object x = item;
            return (x == this) || ((x == null) == isData);
        }

        final boolean isUnmatchedRequest() {
            return !isData && item == null;
        }

        final boolean cannotPrecede(boolean haveData) {
            boolean d = isData;
            Object x;
            return d != haveData && (x = item) != this && (x != null) == d;
        }

        final boolean tryMatchData() {
            // assert isData;
            Object x = item;
            if (x != null && x != this && casItem(x, null)) {
                LockSupport.unpark(waiter);
                return true;
            }
            return false;
        }

        private static final sun.misc.Unsafe UNSAFE;
        private static final long itemOffset;
        private static final long nextOffset;
        private static final long waiterOffset;
        static {
            try {
                UNSAFE = sun.misc.Unsafe.getUnsafe();
                Class<?> k = Node.class;
                itemOffset = UNSAFE.objectFieldOffset
                    (k.getDeclaredField("item"));
                nextOffset = UNSAFE.objectFieldOffset
                    (k.getDeclaredField("next"));
                waiterOffset = UNSAFE.objectFieldOffset
                    (k.getDeclaredField("waiter"));
            } catch (Exception e) {
                throw new Error(e);
            }
        }
    }

    transient volatile Node head;

    private transient volatile Node tail;

    private transient volatile int sweepVotes;
```


## 2.4 具有优先级的阻塞队列 PriorityBlockingQueue
- 基于数组实现，默认是小顶堆
- 无界，空间不够自动扩容
- 可以自定义实现compareTo或传入Comparator来指定元素的排序规则
- 使用一个lock和一个condition
- 是 PriorityQueue 的线程安全版本
- 不可以插入 null 值

```java
public class PriorityBlockingQueue<E> extends AbstractQueue<E> implements BlockingQueue<E>, java.io.Serializable {

    // 构造方法中，如果不指定大小的话，默认大小为 11
    private static final int DEFAULT_INITIAL_CAPACITY = 11;
    // 数组的最大容量
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    // 这个就是存放数据的数组
    private transient Object[] queue;

    // 队列当前大小
    private transient int size;

    // 大小比较器，如果按照自然序排序，那么此属性可设置为 null
    private transient Comparator<? super E> comparator;

    // 并发控制所用的锁，所有的 public 且涉及到线程安全的方法，都必须先获取到这个锁
    private final ReentrantLock lock;

    // 这个很好理解，其实例由上面的 lock 属性创建
    private final Condition notEmpty;

    // 这个也是用于锁，用于数组扩容的时候，需要先获取到这个锁，才能进行扩容操作
    // 其使用 CAS 操作
    private transient volatile int allocationSpinLock;

    // 用于序列化和反序列化的时候用，对于 PriorityBlockingQueue 我们应该比较少使用到序列化
    private PriorityQueue q;

    public PriorityBlockingQueue() {
        this(DEFAULT_INITIAL_CAPACITY, null);
    }

    public PriorityBlockingQueue(int initialCapacity) {
        this(initialCapacity, null);
    }

    public PriorityBlockingQueue(int initialCapacity,
                                 Comparator<? super E> comparator) {
        if (initialCapacity < 1)
            throw new IllegalArgumentException();
        this.lock = new ReentrantLock();
        this.notEmpty = lock.newCondition();
        this.comparator = comparator;
        this.queue = new Object[initialCapacity];
    }

    public PriorityBlockingQueue(Collection<? extends E> c) {
        this.lock = new ReentrantLock();
        this.notEmpty = lock.newCondition();
        boolean heapify = true; // true if not known to be in heap order
        boolean screen = true;  // true if must screen for nulls
        if (c instanceof SortedSet<?>) {
            SortedSet<? extends E> ss = (SortedSet<? extends E>) c;
            this.comparator = (Comparator<? super E>) ss.comparator();
            heapify = false;
        }
        else if (c instanceof PriorityBlockingQueue<?>) {
            PriorityBlockingQueue<? extends E> pq =
                (PriorityBlockingQueue<? extends E>) c;
            this.comparator = (Comparator<? super E>) pq.comparator();
            screen = false;
            if (pq.getClass() == PriorityBlockingQueue.class) // exact match
                heapify = false;
        }
        Object[] a = c.toArray();
        int n = a.length;
        // If c.toArray incorrectly doesn't return Object[], copy it.
        if (a.getClass() != Object[].class)
            a = Arrays.copyOf(a, n, Object[].class);
        if (screen && (n == 1 || this.comparator != null)) {
            for (int i = 0; i < n; ++i)
                if (a[i] == null)
                    throw new NullPointerException();
        }
        this.queue = a;
        this.size = n;
        if (heapify)
            heapify();
    }
```

## 2.5 延迟队列 DelayQueue
- 对元素持有特定时间才能提取元素
- 基于PriorityQueue实现
- 队列中的元素必须实现Delay接口
- 使用一个lock和一个condition

应用场景：
>1. 缓存系统：用DelayQueue保存缓存中数据的有效期，使用一个线程循环检查队列，如果元素能被获取就代表缓存有效期到了
>2. 定时任务调度：使用DelayQueue保存任务和执行时间，如果任务能被获取就立即执行。TimerQueue就是使用DelayQueue实现的

```java
public class DelayQueue<E extends Delayed> extends AbstractQueue<E> implements BlockingQueue<E> {

    private final transient ReentrantLock lock = new ReentrantLock();
    private final Condition available = lock.newCondition();

    private final PriorityQueue<E> q = new PriorityQueue<E>();

    private Thread leader = null;

    public DelayQueue() {}

    public DelayQueue(Collection<? extends E> c) {
        this.addAll(c);
    }
```


- Delay接口
```java
/*
TimeUnit枚举
DAYS
HOURS
INUTES
SECONDS
MILLISECONDS
MICROSECONDS
NANOSECONDS
*/
//getDelay返回当前元素还要延迟多长时间。如果返回的是 0 或者负值，延迟将被认为过期，该元素将会在 DelayQueue 的下一次 take 被调用的时候被释放掉
public interface Delayed extends Comparable<Delayed> {
    long getDelay(TimeUnit unit);
}
```

### 实现Delay接口
参照ScheduledFutureTask的ScheduledFutureTask
```java
private static final AtomicLong sequencer = new AtomicLong();

private class ScheduledFutureTask<V> extends FutureTask<V> implements RunnableScheduledFuture<V> {
    private final long sequenceNumber;

    private long time;

    private final long period;

    /*
    * ns:延迟到什么时候可以使用
    * period:任务重复执行的间隔时间
    */
    ScheduledFutureTask(Runnable r, V result, long ns, long period) {
        super(r, result);
        this.time = ns;
        this.period = period;
        this.sequenceNumber = sequencer.getAndIncrement();
    }

    //当前元素还要延迟多久
    public long getDelay(TimeUnit unit) {
        return unit.convert(time - now(), NANOSECONDS);
    }

    //实现compareTo来指定元素顺序，例如让延迟时间最长的放在队列尾部
    public int compareTo(Delayed other) {
            if (other == this) // compare zero if same object
                return 0;
            if (other instanceof ScheduledFutureTask) {
                ScheduledFutureTask<?> x = (ScheduledFutureTask<?>)other;
                long diff = time - x.time;
                if (diff < 0)
                    return -1;
                else if (diff > 0)
                    return 1;
                else if (sequenceNumber < x.sequenceNumber)
                    return -1;
                else
                    return 1;
            }
            long diff = getDelay(NANOSECONDS) - other.getDelay(NANOSECONDS);
            return (diff < 0) ? -1 : (diff > 0) ? 1 : 0;
    }
}
```

### 实现延迟阻塞队列
leader是等待获取队列头元素的线程，如果leader!=null则说明已经有线程在等待获取元素，则使用await方法使得当前线程等待；如果leader=null则将当前线程设置成leader。并让当前线程等待signal或者delay时间
```java
    public E take() throws InterruptedException {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            for (;;) {
                E first = q.peek();
                if (first == null)
                    available.await();
                else {
                    long delay = first.getDelay(NANOSECONDS);
                    if (delay <= 0)
                        return q.poll();
                    first = null; // don't retain ref while waiting
                    if (leader != null)
                        available.await();
                    else {
                        Thread thisThread = Thread.currentThread();
                        leader = thisThread;
                        try {
                            available.awaitNanos(delay);
                        } finally {
                            if (leader == thisThread)
                                leader = null;
                        }
                    }
                }
            }
        } finally {
            if (leader == null && q.peek() != null)
                available.signal();
            lock.unlock();
        }
    }
```

## 2.6 同步队列 SynchronousQueue
SynchronousQueue是不存储元素的阻塞队列，每个put必须等待一个take取走元素后才能继续添加元素

- 支持公平和非公平

>插入和获取元素都是调用的transfer方法：
>1. 如果队列空，或者队列中的节点和当前的线程操作类型一致(都是读或者都是写)，将当前线程加入到等待队列。所以队列中等待的节点一定都有一样的操作类型
>2. 如果队列有等待节点，并且和当前线程的操作可以匹配(读匹配写或写匹配读)，对于公平的就将队列的队头出队并返回数据；对于非公平的将当前节点压入栈顶，和栈中的节点进行匹配，然后将这两个节点出栈

```java
public class SynchronousQueue<E> extends AbstractQueue<E> implements BlockingQueue<E>, java.io.Serializable {

    //默认是非公平TransferStack
    public SynchronousQueue() {
        this(false);
    }
    //传入true的话是公平TransferQueue
    public SynchronousQueue(boolean fair) {
        transferer = fair ? new TransferQueue<E>() : new TransferStack<E>();
    }

    abstract static class Transferer<E> {
    /*这个方法用于转移元素，从生产者手上转到消费者手上。也可以被动地，消费者调用这个方法来从生产者手上取元素
    第一个参数 e 如果不是 null，代表场景为：将元素从生产者转移给消费者
    如果是 null，代表消费者等待生产者提供元素，然后返回值就是相应的生产者提供的元素
    第二个参数代表是否设置超时，如果设置超时，超时时间是第三个参数的值
    返回值如果是 null，代表超时，或者中断。具体是哪个，可以通过检测中断状态得到
    */

    // 写入值
    public void put(E o) throws InterruptedException {
        if (o == null) throw new NullPointerException();
        if (transferer.transfer(o, false, 0) == null) { // 1
            Thread.interrupted();
            throw new InterruptedException();
        }
    }
    // 读取值并移除
    public E take() throws InterruptedException {
        Object e = transferer.transfer(null, false, 0); // 2
        if (e != null)
            return (E)e;
        Thread.interrupted();
        throw new InterruptedException();
    }
        abstract E transfer(E e, boolean timed, long nanos);
    }

    static final class TransferStack<E> extends Transferer<E> {

        volatile SNode head;

        static final class SNode {
            volatile SNode next;        // next node in stack
            volatile SNode match;       // the node matched to this
            volatile Thread waiter;     // to control park/unpark
            Object item;                // data; or null for REQUESTs
            int mode;

            SNode(Object item) {
                this.item = item;
            }
            ...
        } 
        E transfer(E e, boolean timed, long nanos) {
            SNode s = null; // constructed/reused as needed
            int mode = (e == null) ? REQUEST : DATA;

            for (;;) {
                SNode h = head;
                if (h == null || h.mode == mode) {  // empty or same-mode
                    if (timed && nanos <= 0) {      // can't wait
                        if (h != null && h.isCancelled())
                            casHead(h, h.next);     // pop cancelled node
                        else
                            return null;
                    } else if (casHead(h, s = snode(s, e, h, mode))) {
                        SNode m = awaitFulfill(s, timed, nanos);
                        if (m == s) {               // wait was cancelled
                            clean(s);
                            return null;
                        }
                        if ((h = head) != null && h.next == s)
                            casHead(h, s.next);     // help s's fulfiller
                        return (E) ((mode == REQUEST) ? m.item : s.item);
                    }
                } else if (!isFulfilling(h.mode)) { // try to fulfill
                    if (h.isCancelled())            // already cancelled
                        casHead(h, h.next);         // pop and retry
                    else if (casHead(h, s=snode(s, e, h, FULFILLING|mode))) {
                        for (;;) { // loop until matched or waiters disappear
                            SNode m = s.next;       // m is s's match
                            if (m == null) {        // all waiters are gone
                                casHead(s, null);   // pop fulfill node
                                s = null;           // use new node next time
                                break;              // restart main loop
                            }
                            SNode mn = m.next;
                            if (m.tryMatch(s)) {
                                casHead(s, mn);     // pop both s and m
                                return (E) ((mode == REQUEST) ? m.item : s.item);
                            } else                  // lost match
                                s.casNext(m, mn);   // help unlink
                        }
                    }
                } else {                            // help a fulfiller
                    SNode m = h.next;               // m is h's match
                    if (m == null)                  // waiter is gone
                        casHead(h, null);           // pop fulfilling node
                    else {
                        SNode mn = m.next;
                        if (m.tryMatch(h))          // help match
                            casHead(h, mn);         // pop both h and m
                        else                        // lost match
                            h.casNext(m, mn);       // help unlink
                    }
                }
            }
        }
        ...   
    }

    static final class TransferQueue<E> extends Transferer<E> {
        static final class QNode {
        volatile QNode next;          // 可以看出来，等待队列是单向链表
        volatile Object item;         // CAS'ed to or from null
        volatile Thread waiter;       // 将线程对象保存在这里，用于挂起和唤醒
        final boolean isData;         // 用于判断是写线程节点(isData == true)，还是读线程节点

        QNode(Object item, boolean isData) {
            this.item = item;
            this.isData = isData;
        }

        transient volatile QNode cleanMe;

        TransferQueue() {
            QNode h = new QNode(null, false); // initialize to dummy node.
            head = h;
            tail = h;
        }

        Object transfer(Object e, boolean timed, long nanos) {

            QNode s = null; // constructed/reused as needed
            boolean isData = (e != null);

            for (;;) {
                QNode t = tail;
                QNode h = head;
                if (t == null || h == null)         // saw uninitialized value
                    continue;                       // spin

                // 队列空，或队列中节点类型和当前节点一致，
                // 即我们说的第一种情况，将节点入队即可。读者要想着这块 if 里面方法其实就是入队
                if (h == t || t.isData == isData) { // empty or same-mode
                    QNode tn = t.next;
                    // t != tail 说明刚刚有节点入队，continue 即可
                    if (t != tail)                  // inconsistent read
                        continue;
                    // 有其他节点入队，但是 tail 还是指向原来的，此时设置 tail 即可
                    if (tn != null) {               // lagging tail
                        // 这个方法就是：如果 tail 此时为 t 的话，设置为 tn
                        advanceTail(t, tn);
                        continue;
                    }
                    // 
                    if (timed && nanos <= 0)        // can't wait
                        return null;
                    if (s == null)
                        s = new QNode(e, isData);
                    // 将当前节点，插入到 tail 的后面
                    if (!t.casNext(null, s))        // failed to link in
                        continue;

                    // 将当前节点设置为新的 tail
                    advanceTail(t, s);              // swing tail and wait
                    // 看到这里，请读者先往下滑到这个方法，看完了以后再回来这里，思路也就不会断了
                    Object x = awaitFulfill(s, e, timed, nanos);
                    // 到这里，说明之前入队的线程被唤醒了，准备往下执行
                    if (x == s) {                   // wait was cancelled
                        clean(t, s);
                        return null;
                    }

                    if (!s.isOffList()) {           // not already unlinked
                        advanceHead(t, s);          // unlink if head
                        if (x != null)              // and forget fields
                            s.item = s;
                        s.waiter = null;
                    }
                    return (x != null) ? x : e;

                // 这里的 else 分支就是上面说的第二种情况，有相应的读或写相匹配的情况
                } else {                            // complementary-mode
                    QNode m = h.next;               // node to fulfill
                    if (t != tail || m == null || h != head)
                        continue;                   // inconsistent read

                    Object x = m.item;
                    if (isData == (x != null) ||    // m already fulfilled
                        x == m ||                   // m cancelled
                        !m.casItem(x, e)) {         // lost CAS
                        advanceHead(h, m);          // dequeue and retry
                        continue;
                    }

                    advanceHead(h, m);              // successfully fulfilled
                    LockSupport.unpark(m.waiter);
                    return (x != null) ? x : e;
                }
            }
        }

        void advanceTail(QNode t, QNode nt) {
            if (tail == t)
                UNSAFE.compareAndSwapObject(this, tailOffset, t, nt);
        }
    
    // 自旋或阻塞，直到满足条件，这个方法返回
    Object awaitFulfill(QNode s, Object e, boolean timed, long nanos) {

        long lastTime = timed ? System.nanoTime() : 0;
        Thread w = Thread.currentThread();
        // 判断需要自旋的次数，
        int spins = ((head.next == s) ?
                    (timed ? maxTimedSpins : maxUntimedSpins) : 0);
        for (;;) {
            // 如果被中断了，那么取消这个节点
            if (w.isInterrupted())
                // 就是将当前节点 s 中的 item 属性设置为 this
                s.tryCancel(e);
            Object x = s.item;
            // 这里是这个方法的唯一的出口
            if (x != e)
                return x;
            // 如果需要，检测是否超时
            if (timed) {
                long now = System.nanoTime();
                nanos -= now - lastTime;
                lastTime = now;
                if (nanos <= 0) {
                    s.tryCancel(e);
                    continue;
                }
            }
            if (spins > 0)
                --spins;
            // 如果自旋达到了最大的次数，那么检测
            else if (s.waiter == null)
                s.waiter = w;
            // 如果自旋到了最大的次数，那么线程挂起，等待唤醒
            else if (!timed)
                LockSupport.park(this);
            // spinForTimeoutThreshold 这个之前讲 AQS 的时候其实也说过，剩余时间小于这个阈值的时候，就
            // 不要进行挂起了，自旋的性能会比较好
            else if (nanos > spinForTimeoutThreshold)
                LockSupport.parkNanos(this, nanos);
        }
    }
}
```



## 2.7 链阻塞双端队列 LinkedBlockingDeque
- 基于链表实现
- 双向阻塞队列，可以在队列两端插入或取出元素
- 实现BlockingDeque接口
- 使用一个lock和两个condition

```java
public class LinkedBlockingDeque<E> extends AbstractQueue<E> implements BlockingDeque<E>, java.io.Serializable {
    static final class Node<E> {
            E item;

            Node<E> prev;

            Node<E> next;

            Node(E x) {
                item = x;
            }
        }

        transient Node<E> first;

        transient Node<E> last;

        /** Number of items in the deque */
        private transient int count;

        /** Maximum number of items in the deque */
        private final int capacity;

        /** Main lock guarding all access */
        final ReentrantLock lock = new ReentrantLock();

        /** Condition for waiting takes */
        private final Condition notEmpty = lock.newCondition();

        /** Condition for waiting puts */
        private final Condition notFull = lock.newCondition();
}
```

- BlockingDeque接口
```java
package java.util.concurrent;
import java.util.*;

public interface BlockingDeque<E> extends BlockingQueue<E>, Deque<E> {
    
    void addFirst(E e);

    void addLast(E e);

    boolean offerFirst(E e);

    boolean offerLast(E e);

    void putFirst(E e) throws InterruptedException;

    void putLast(E e) throws InterruptedException;

    boolean offerFirst(E e, long timeout, TimeUnit unit)
        throws InterruptedException;

    boolean offerLast(E e, long timeout, TimeUnit unit)
        throws InterruptedException;

    E takeFirst() throws InterruptedException;

    E takeLast() throws InterruptedException;

    E pollFirst(long timeout, TimeUnit unit)
        throws InterruptedException;

    E pollLast(long timeout, TimeUnit unit)
        throws InterruptedException;

    boolean removeFirstOccurrence(Object o);

    boolean removeLastOccurrence(Object o);

    boolean add(E e);

    boolean offer(E e);

    void put(E e) throws InterruptedException;

    boolean offer(E e, long timeout, TimeUnit unit)
        throws InterruptedException;

    E remove();

    E poll();

    E take() throws InterruptedException;

    E poll(long timeout, TimeUnit unit)
        throws InterruptedException;

    E element();

    E peek();

    boolean remove(Object o);

    public boolean contains(Object o);

    public int size();

    Iterator<E> iterator();

    void push(E e);
}

```