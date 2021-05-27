## 参考博文
[关键字: volatile详解](https://www.pdai.tech/md/java/thread/java-thread-x-key-volatile.html)

[TOC]


# 1. 用处
一个共享变量(类的实例变量、静态变量)被volatile修饰后，就有两层语义：
1. 保证可见性：一旦某个线程修改了变量，其他线程能立即看到修改
2. 保证有序性：对volatile变量禁止指令重排

只适用于只有单个线程更新变量的值的情况

## 可见性
下面代码中线程1先执行，线程2后执行。线程1等待线程2将stop设置为true后，将新值刷回主存，然后跳出while循环。但假如线程2在工作内存中将stop设置为true后，没有马上刷回主存就去做别的事情了，线程1就会一直等待。
```java
//线程1
boolean stop = false;
while(!stop){
    doSomething();
}
 
//线程2
stop = true;
```

但如果用volatile修饰stop：
1. 当线程2对变量进行修改后会立即写入主存
2. 并且会使线程1的工作缓存中缓存变量stop的缓存行无效(反映到硬件层的话，就是CPU的L1或者L2缓存中对应的缓存行无效)
3. 线程1发现缓存行无效，将会再去主存中读取新的值
```java
volatile boolean stop = false;
```

## 有序性
volatile禁止指令重排序有两层意思：
1. 当执行到对volatile变量的读写操作时，他前面的操作肯定已经全部执行且结果对后面的语句可见，后面的操作都还没执行
2. 进行指令重排时，不能把volatile变量后面的语句放到前面执行，也不能把volatile放到他前面的语句执行


flag为volatile变量，所以指令重排时：
1. 他不能放到语句1、语句2前面
2. 也不能放到语句4、语句5后面
3. 语句1、语句2、语句4、语句5的执行顺序是不一定固定的
4. 执行到语句3时，语句1、语句2一定执行完毕了，并且结果对语句3、语句4、语句5可见
```java
//x、y为非volatile变量
//flag为volatile变量
 
x = 2;        //语句1
y = 0;        //语句2
flag = true;  //语句3
x = 4;        //语句4
y = -1;       //语句5
```

对于之前的这个例子，使用volatile修饰inited就能保证执行语句2前，context一定初始化完毕
```java
//线程1:
context = loadContext();   //语句1
inited = true;             //语句2
 
//线程2:
while(!inited ){
  sleep()
}
doSomethingwithconfig(context);
```

## 不能保证原子性
inc++其实对应3个步骤：
1. 从主存中读(getstatic)
2. 在工作内存中加1(iconst_1、iadd)
3. 写回主存(putstatic)

假如线程1执行了前两个步骤，还没有写回主存。同时线程2也执行了前两个步骤并写回了主存，那么当线程1再写回主存就会出错
```java
public class Test {
    public volatile int inc = 0;
     
    public void increase() {
        inc++;
    }
     
    public static void main(String[] args) {
        final Test test = new Test();
        for(int i=0;i<10;i++){
            new Thread(){
                public void run() {
                    for(int j=0;j<1000;j++)
                        test.increase();
                };
            }.start();
        }
         
        while(Thread.activeCount()>1)  //保证前面的线程都执行完
            Thread.yield();
        System.out.println(test.inc);
    }
}
```

可以使用下面3种方法保证原子性：
- 采用synchronized：
```
public class Test {
    public  int inc = 0;
    
    public synchronized void increase() {
        inc++;
    }
    
    public static void main(String[] args) {
        final Test test = new Test();
        for(int i=0;i<10;i++){
            new Thread(){
                public void run() {
                    for(int j=0;j<1000;j++)
                        test.increase();
                };
            }.start();
        }
        
        while(Thread.activeCount()>1)  //保证前面的线程都执行完
            Thread.yield();
        System.out.println(test.inc);
    }
}
```

- 采用Lock：
```

public class Test {
    public  int inc = 0;
    Lock lock = new ReentrantLock();
    
    public void increase() {
        lock.lock();
        try {
            inc++;
        } finally{
            lock.unlock();
        }
    }
    
    public static void main(String[] args) {
        final Test test = new Test();
        for(int i=0;i<10;i++){
            new Thread(){
                public void run() {
                    for(int j=0;j<1000;j++)
                        test.increase();
                };
            }.start();
        }
        
        while(Thread.activeCount()>1)  //保证前面的线程都执行完
            Thread.yield();
        System.out.println(test.inc);
    }
}
```
- 采用AtomicInteger：
```
public class Test {
    public AtomicInteger inc = new AtomicInteger();
     
    public void increase() {
        inc.incrementAndGet();
    }
    
    public static void main(String[] args) {
        final Test test = new Test();
        for(int i=0;i<10;i++){
            new Thread(){
                public void run() {
                    for(int j=0;j<1000;j++)
                        test.increase();
                };
            }.start();
        }
        
        while(Thread.activeCount()>1)  //保证前面的线程都执行完
            Thread.yield();
        System.out.println(test.inc);
    }
}
```

# 2. 实现原理
被volatile修饰的变量，会有一个lock的字节码指令，它相当于一个内存屏障来禁止特定类型的重排序：
1. 使得修改的数据从工作内存写回主存
2. 在将本CPU的缓存写入主存时，也会使得别的CPU中的缓存无效 ([原子操作的实现原理](./原子操作的实现原理.md))


# 3. 使用场景
- 状态标记量
```java
volatile boolean flag = false;
 
while(!flag){
    doSomething();
}
 
public void setFlag() {
    flag = true;
}
```

```java
volatile boolean inited = false;
//线程1:
context = loadContext();  
inited = true;            
 
//线程2:
while(!inited){
sleep()
}
doSomethingwithconfig(context);
```

- double check

[单例模式](./单例模式.md)
```java
class Singleton{
    private volatile static Singleton instance = null;
     
    private Singleton() {
         
    }
     
    public static Singleton getInstance() {
        if(instance==null) {
            synchronized (Singleton.class) {
                if(instance==null)
                    instance = new Singleton();
            }
        }
        return instance;
    }
}
```

