package thread;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * 测试CAS
 */
public class CASTest {

    static AtomicReference<String> atomicReference = new AtomicReference<>("A");
    static AtomicStampedReference<String> atomicReferenceVersion = new AtomicStampedReference<>("A", 1);

    public static void main(String[] args) {
        ABAWithVersion();
    }

    public static void ABA() {
        new Thread(() -> {
            atomicReference.compareAndSet("A", "B");
            atomicReference.compareAndSet("B", "A");
        }, "t2").start();
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(atomicReference.compareAndSet("A", "C")
                    + "\t" + atomicReference.get());
        }, "t1").start();
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
