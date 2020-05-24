## 参考博文
[并发容器之CopyOnWriteArrayList详解](https://blog.csdn.net/thinkwon/article/details/102508258)
[JUC集合: CopyOnWriteArrayList详解](https://www.pdai.tech/md/java/thread/java-thread-x-juc-collection-CopyOnWriteArrayList.html)


[TOC]

# 1. 概述
- ArrayList在多线程下不安全，如果读线程读时有写线程在写会抛出ConcurrentModificationException
- Vector是线程安全的，但他利用独占synchronzied的方式效率很低
- 读写锁[ReenTrantReadWriteLock](./ReenTrantReadWriteLock.md)在写锁被占用时，其他线程还是不能读

CpoyOnWriteArrayList通过Copy-On-Write(COW)，某个线程在对list进行修改时先复制一个副本，对副本操作，然后再将容器的指针指向这个副本。在这过程中，读线程还是可以读取原来的容器。这是在牺牲实时一致性的情况下不阻塞读线程。适合读多写少的场景

# 2. 源码分析
内部维护的是volatile修饰的数组，当数组引用改变时能对其他线程立马可见

```java
public class CopyOnWriteArrayList<E> implements List<E>, RandomAccess, Cloneable, java.io.Serializable {

    final transient ReentrantLock lock = new ReentrantLock();

    private transient volatile Object[] array;

    private static final sun.misc.Unsafe UNSAFE;
}
```

## get()
没有任何同步机制，就只是读取而已
```java
public E get(int index) {
    return get(getArray(), index);
}
/**
 * Gets the array.  Non-private so as to also be accessible
 * from CopyOnWriteArraySet class.
 */
final Object[] getArray() {
    return array;
}
private E get(Object[] a, int index) {
    return (E) a[index];
}
```

## add()
对数据修改的线程必须获得lock锁
```java
public boolean add(E e) {
    final ReentrantLock lock = this.lock;
	//1. 使用Lock,保证写线程在同一时刻只有一个
    lock.lock();
    try {
		//2. 获取旧数组引用
        Object[] elements = getArray();
        int len = elements.length;
		//3. 创建新的数组，并将旧数组的数据复制到新数组中
        Object[] newElements = Arrays.copyOf(elements, len + 1);
		//4. 往新数组中添加新的数据	        
		newElements[len] = e;
		//5. 将旧数组引用指向新的数组
        setArray(newElements);
        return true;
    } finally {
        lock.unlock();
    }
}
```

## 迭代器
COWIterator维护一个数组保存当时容器的快照，即指向当时容器内数组的引用，该数组在迭代器期间不会改变，在迭代器上进行的元素更改操作(remove、set 和 add)不受支持。这些方法将抛出 UnsupportedOperationException
```java
public Iterator<E> iterator() {
    return new COWIterator<E>(getArray(), 0);
}

static final class COWIterator<E> implements ListIterator<E> {
    /** Snapshot of the array */
    // 快照
    private final Object[] snapshot;

    // 游标
    private int cursor;
    // 构造函数
    private COWIterator(Object[] elements, int initialCursor) {
        cursor = initialCursor;
        snapshot = elements;
    }
    // 是否还有下一项
    public boolean hasNext() {
        return cursor < snapshot.length;
    }
    // 是否有上一项
    public boolean hasPrevious() {
        return cursor > 0;
    }
    // next项
    @SuppressWarnings("unchecked")
    public E next() {
        if (! hasNext()) // 不存在下一项，抛出异常
            throw new NoSuchElementException();
        // 返回下一项
        return (E) snapshot[cursor++];
    }

    @SuppressWarnings("unchecked")
    public E previous() {
        if (! hasPrevious())
            throw new NoSuchElementException();
        return (E) snapshot[--cursor];
    }
    
    // 下一项索引
    public int nextIndex() {
        return cursor;
    }
    
    // 上一项索引
    public int previousIndex() {
        return cursor-1;
    }

    // 不支持remove操作
    public void remove() {
        throw new UnsupportedOperationException();
    }

    // 不支持set操作
    public void set(E e) {
        throw new UnsupportedOperationException();
    }

    // 不支持add操作
    public void add(E e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void forEachRemaining(Consumer<? super E> action) {
        Objects.requireNonNull(action);
        Object[] elements = snapshot;
        final int size = elements.length;
        for (int i = cursor; i < size; i++) {
            @SuppressWarnings("unchecked") E e = (E) elements[i];
            action.accept(e);
        }
        cursor = size;
    }
}
```


## copyOf()函数
该函数用于复制指定的数组，截取或用 null 填充(如有必要)，以使副本具有指定的长度
```java
public static <T,U> T[] copyOf(U[] original, int newLength, Class<? extends T[]> newType) {
    @SuppressWarnings("unchecked")
    // 确定copy的类型(将newType转化为Object类型，将Object[].class转化为Object类型，判断两者是否相等，若相等，则生成指定长度的Object数组
    // 否则,生成指定长度的新类型的数组)
    T[] copy = ((Object)newType == (Object)Object[].class)
        ? (T[]) new Object[newLength]
        : (T[]) Array.newInstance(newType.getComponentType(), newLength);
    // 将original数组从下标0开始，复制长度为(original.length和newLength的较小者),复制到copy数组中(也从下标0开始)
    System.arraycopy(original, 0, copy, 0,
                        Math.min(original.length, newLength));
    return copy;
}
```

# 3. 实例
```java
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

class PutThread extends Thread {
    private CopyOnWriteArrayList<Integer> cowal;

    public PutThread(CopyOnWriteArrayList<Integer> cowal) {
        this.cowal = cowal;
    }

    public void run() {
        try {
            for (int i = 100; i < 110; i++) {
                cowal.add(i);
                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class CopyOnWriteArrayListDemo {
    public static void main(String[] args) {
        CopyOnWriteArrayList<Integer> cowal = new CopyOnWriteArrayList<Integer>();
        for (int i = 0; i < 10; i++) {
            cowal.add(i);
        }
        PutThread p1 = new PutThread(cowal);
        p1.start();
        Iterator<Integer> iterator = cowal.iterator();
        while (iterator.hasNext()) {
            System.out.print(iterator.next() + " ");
        }
        System.out.println();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        iterator = cowal.iterator();
        while (iterator.hasNext()) {
            System.out.print(iterator.next() + " ");
        }
    }
}
/*
0 1 2 3 4 5 6 7 8 9 100 
0 1 2 3 4 5 6 7 8 9 100 101 102 103
*/
```