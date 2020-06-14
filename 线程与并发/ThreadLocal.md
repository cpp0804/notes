
## 参考博文
[Java 并发 - ThreadLocal详解](https://www.pdai.tech/md/java/thread/java-thread-x-threadlocal.html)
[Java 之 ThreadLocal 详解](https://juejin.im/post/5965ef1ff265da6c40737292)



[TOC]

# 1. 概念
使用ThreadLocal维护的变量，会为每个线程创建单独的变量副本。各线程通过set和get操作属于自己的变量副本。通常将ThreadLocal变量定义为private static
```java
public class ThreadLocalTest {

    private static String strLabel;
    private static ThreadLocal<String> threadLabel = new ThreadLocal<>();

    public static void main(String[] args) {
        test();
    }

    public static void test() {
        strLabel = "main";
        threadLabel.set("main");

        Thread thread = new Thread(() -> {
            strLabel = "child";
            threadLabel.set("child");
        });
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("strLabel = " + strLabel);
        System.out.println("threadLabel = " + threadLabel.get());
    }
}
/*
strLabel = child
threadLabel = main
*/
```

# 2. 实现机制
- get()
1. 根据当前线程获取到他的ThreadLocalMap
2. 如果ThreadLocalMap不为null，则以当前的ThreadLocal为key去map中寻找value。如果value存在就返回；如果不存在则调用setInitialValue()以当前ThreadLocal为key，存入一个默认的value
3. 如果ThreadLocalMap为null，则调用setInitialValue()创建一个新的map，并以当前ThreadLocal为key，存入一个默认的value

```java
public T get() {
    Thread t = Thread.currentThread();
    ThreadLocalMap map = getMap(t);
    if (map != null) {
        ThreadLocalMap.Entry e = map.getEntry(this);
        if (e != null) {
            @SuppressWarnings("unchecked")
            T result = (T)e.value;
            return result;
        }
    }
    return setInitialValue();
}

private T setInitialValue() {
    //initialValue() 是 ThreadLocal 的初始值，默认返回 null，子类可以重写改方法
    T value = initialValue();
    Thread t = Thread.currentThread();
    ThreadLocalMap map = getMap(t);
    if (map != null)
        map.set(this, value);
    else
        createMap(t, value);
    return value;
}
```

- set()
1. 根据当前线程获取到他的ThreadLocalMap
2. 如果ThreadLocalMap不为null，则以当前ThreadLocal为key，和value一起放入map中
3. 如果ThreadLocalMap为null，则创建一个新的map再放入key-value
```java
public void set(T value) {
    Thread t = Thread.currentThread();
    MaThreadLocalp map = getMap(t);
    if (map != null)
        map.set(this, value);
    else
        createMap(t, value);
}
```

- remove
1. 根据当前线程获取到他的ThreadLocalMap
2. 如果ThreadLocalMap不为null，则从map中移除以当前ThreadLocal为key的键值对
```java
public void remove() {
    ThreadLocalMap m = getMap(Thread.currentThread());
    if (m != null)
        m.remove(this);
}
```

# 3. ThreadLocalMap
ThreadLocalMap是ThreadLocal的静态内部类，它内部使用Entry数组来存储Key-Value，Entry实现继承了WeakReference<ThreadLocal<?>>,说明key以弱引用的方式保存  [四种引用](../java基础/四种引用.md)

```java
// 初始容量，必须是 2 的幂
private static final int INITIAL_CAPACITY = 16;

// 存储数据的哈希表
private Entry[] table;

// table 中已存储的条目数
private int size = 0;

// 表示一个阈值，当 table 中存储的对象达到该值时就会扩容
private int threshold;

// 设置 threshold 的值
private void setThreshold(int len) {
    threshold = len * 2 / 3;
}

static class Entry extends WeakReference<ThreadLocal<?>> {
    Object value;

    Entry(ThreadLocal<?> k, Object v) {
        super(k);
        value = v;
    }
}
```

- 调用 set(ThreadLocal key, Object value) 方法将数据保存到哈希表中
1. 根据当前ThreadLocal计算出索引位置，得到entry
2. 如果entry为null，则以当前ThreadLocal和value创建entry并放入map中
3. 如果entry不为null并且他对应的key为null，则要清除entry的value，然后放入新的key和value
4. 如果entry不为null并且他对应的key和新key相同，则更新value

```java
private void set(ThreadLocal key, Object value) {

    Entry[] tab = table;
    int len = tab.length;
    // 计算要存储的索引位置
    int i = key.threadLocalHashCode & (len-1);

    // 循环判断要存放的索引位置是否已经存在 Entry，若存在，进入循环体
    for (Entry e = tab[i];
         e != null;
         e = tab[i = nextIndex(i, len)]) {
        ThreadLocal k = e.get();

        // 若索引位置的 Entry 的 key 和要保存的 key 相等，则更新该 Entry 的值
        if (k == key) {
            e.value = value;
            return;
        }

        // 若索引位置的 Entry 的 key 为 null（key 已经被回收了），表示该位置的 Entry 已经无效，用要保存的键值替换该位置上的 Entry
        if (k == null) {
            replaceStaleEntry(key, value, i);
            return;
        }
    }

    // 要存放的索引位置没有 Entry，将当前键值作为一个 Entry 保存在该位置
    tab[i] = new Entry(key, value);
    // 增加 table 存储的条目数
    int sz = ++size;
    // 清除一些无效的条目并判断 table 中的条目数是否已经超出阈值
    if (!cleanSomeSlots(i, sz) && sz >= threshold)
        rehash(); // 调整 table 的容量，并重新摆放 table 中的 Entry
}
```

# 4. 内存泄露
使用弱引用方式关联ThreadLocal，可以在ThreadLocal对象在外部被清除时避免由于map的关联而不能GC，但仍会存在value的不能被清除，所以在ThreadLocalMap 的 set()，get() 和 remove() 方法中，都有清除key为null的Entry的操作，这样做是为了降低内存泄漏发生的可能

我们在使用 ThreadLocal 的时候，每次用完 ThreadLocal 都调用 remove() 方法清除数据，防止内存泄漏


# 5. 应用场景
- 每个线程维护了一个“序列号”
```java
public class SerialNum {
    // The next serial number to be assigned
    private static int nextSerialNum = 0;

    private static ThreadLocal serialNum = new ThreadLocal() {
        protected synchronized Object initialValue() {
            return new Integer(nextSerialNum++);
        }
    };

    public static int get() {
        return ((Integer) (serialNum.get())).intValue();
    }
}
```

- Session的管理
```java
private static final ThreadLocal threadSession = new ThreadLocal();  
  
public static Session getSession() throws InfrastructureException {  
    Session s = (Session) threadSession.get();  
    try {  
        if (s == null) {  
            s = getSessionFactory().openSession();  
            threadSession.set(s);  
        }  
    } catch (HibernateException ex) {  
        throw new InfrastructureException(ex);  
    }  
    return s;  
}  
  
```


- 在线程内部创建ThreadLocal
```java
public class ThreadLocalTest implements Runnable{
    
    ThreadLocal<Student> StudentThreadLocal = new ThreadLocal<Student>();

    @Override
    public void run() {
        String currentThreadName = Thread.currentThread().getName();
        System.out.println(currentThreadName + " is running...");
        Random random = new Random();
        int age = random.nextInt(100);
        System.out.println(currentThreadName + " is set age: "  + age);
        Student Student = getStudentt(); //通过这个方法，为每个线程都独立的new一个Studentt对象，每个线程的的Studentt对象都可以设置不同的值
        Student.setAge(age);
        System.out.println(currentThreadName + " is first get age: " + Student.getAge());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println( currentThreadName + " is second get age: " + Student.getAge());
        
    }
    
    private Student getStudentt() {
        Student Student = StudentThreadLocal.get();
        if (null == Student) {
            Student = new Student();
            StudentThreadLocal.set(Student);
        }
        return Student;
    }

    public static void main(String[] args) {
        ThreadLocalTest t = new ThreadLocalTest();
        Thread t1 = new Thread(t,"Thread A");
        Thread t2 = new Thread(t,"Thread B");
        t1.start();
        t2.start();
    }
    
}

class Student{
    int age;
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
    
}
```


# 5. InheritableThreadLocal
