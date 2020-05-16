package thread;

import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * 测试CAS
 */
public class CASTest {

    static AtomicReference<String> atomicReference = new AtomicReference<>("A");
    static AtomicStampedReference<String> atomicReferenceVersion = new AtomicStampedReference<>("A", 1);

    public static void main(String[] args) {
        atomicReferenceTest();

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

    public static void atomicReferenceTest() {

        // 创建两个Person对象，它们的id分别是101和102。
        Person p1 = new Person(101);
        Person p2 = new Person(102);
        // 新建AtomicReference对象，初始化它的值为p1对象
        AtomicReference ar = new AtomicReference(p1);

        Person p3 = (Person) ar.get();
        //Person并没有覆盖equals()方法，而是采用继承自Object.java的equals()方法用==判断
        System.out.println("p3.equals(p1)=" + p3.equals(p1));

        // 通过CAS设置ar。如果ar的值为p1的话，则将其设置为p2。
        ar.compareAndSet(p1, p2);

        p3 = (Person) ar.get();
        System.out.println("p3 is " + p3);
        System.out.println("p3.equals(p1)=" + p3.equals(p1));
        System.out.println("p3.equals(p2)=" + p3.equals(p2));
    }

    static class Person {
        volatile long id;

        public Person(long id) {
            this.id = id;
        }

        public String toString() {
            return "id:" + id;
        }
    }

    public static AtomicIntegerFieldUpdater<DataDemo> updater(String name){
        return AtomicIntegerFieldUpdater.newUpdater(DataDemo.class,name);
    }


    public static void testAtomicIntegerFieldUpdater() {
        DataDemo data = new DataDemo();
        System.out.println("publicVar = "+updater("publicVar").addAndGet(data, 2));

         /* 由于在DataDemo类中属性value2/value3,在TestAtomicIntegerFieldUpdater中不能访问
         将报 java.lang.IllegalAccessException异常
         */
//        System.out.println("protectedVar = "+updater("protectedVar").getAndAdd(data,2));
//        System.out.println("privateVar = "+updater("privateVar").getAndAdd(data,2));

        //System.out.println("staticVar = "+updater("staticVar").getAndIncrement(data));//报java.lang.IllegalArgumentException
        /*
         * 下面报异常：java.lang.IllegalArgumentException:must be integer
         * */
//        System.out.println("integerVar = "+updater("integerVar").getAndIncrement(data));
//        System.out.println("longVar = "+updater("longVar").getAndIncrement(data));
    }

    static class DataDemo{
        public volatile int publicVar=3;
        protected volatile int protectedVar=4;
        private volatile  int privateVar=5;

        public volatile static int staticVar = 10;
        //public  final int finalVar = 11;

        public volatile Integer integerVar = 19;
        public volatile Long longVar = 18L;

    }
}
