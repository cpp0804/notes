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
