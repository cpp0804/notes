## 参考博文
[两种高效的服务器设计模型：Reactor和Proactor模型](https://blog.csdn.net/u013074465/article/details/46276967)
[Java NIO - IO多路复用详解](https://www.pdai.tech/md/java/io/java-io-nio-select-epoll.html)
[Linux 的 IO 通信 以及 Reactor 线程模型浅析](https://zhuanlan.zhihu.com/p/35065251)
[第十二节 netty前传-NIO reactor模式](https://www.jianshu.com/p/baa3af1a1ad1)


[TOC]


# 1. 典型的多路复用IO实现

IO模型| 相对性能 |关键思路 |操作系统 |JAVA支持情况 
---|---|---|---|---|
select |较高 |Reactor |windows/Linux |支持,Reactor模式(反应器设计模式)。Linux操作系统的 kernels 2.4内核版本之前，默认使用select；而目前windows下对同步IO的支持，都是select模型 
poll |较高 |Reactor |Linux |Linux下的JAVA NIO框架，Linux kernels 2.6内核版本之前使用poll进行支持。也是使用的Reactor模式 
epoll |高 Reactor/Proactor| Linux |Linux kernels 2.6内核版本及以后使用epoll进行支持；Linux kernels 2.6内核版本之前使用poll进行支持；另外一定注意，由于Linux下没有Windows下的IOCP技术提供真正的 异步IO 支持，所以Linux下使用epoll模拟异步IO 
kqueue |高 |Proactor |Linux |目前JAVA的版本不支持

![多路复用IO](../pic/Linux五种IO模型_多路复用IO.png)


# 2. Reactor 线程模型
中心思想是将所有的IO事件注册到一个中心 I/O 多路复用器上，同时主线程/进程阻塞在多路复用器上。一旦有 I/O 事件到来或是准备就绪(文件描述符或 socket 可读、写)，多路复用器返回并将事先注册的相应 I/O 事件分发到对应的处理器中

Reactor 利用事件驱动机制实现，和普通函数调用的不同之处在于：应用程序不是主动的调用某个 API 完成处理，而是恰恰相反，Reactor 逆置了事件处理流程，应用程序需要提供相应的接口并注册到 Reactor 上，如果相应的事件发生，Reactor 将主动调用应用程序注册的接口，这些接口又称为 “回调函数”。用 “好莱坞原则” 来形容 Reactor 再合适不过了：不要打电话给我们，我们会打电话通知你



## 2.1 组件
- Reactor：，在一个单独的线程中运行，负责监听和分发事件。把IO事件分配给对应的handler处理
- Acceptor：处理客户端连接事件，建立对应client的Handler，并向Reactor注册此Handler
- Handler：处理程序执行IO事件要完成的实际事件，处理非阻塞的任务


## 2.2 Reactor 线程模型
### 单线程模型：单Reactor单线程Hanndler
接待员和服务员是同一个人，一直为顾客服务。客流量较少适合

![](../pic/)



### 多线程模型（单 Reactor）：单Reactor多线程Hanndler



### 多线程模型（多 Reactor)：主从Reactor多线程Hanndler

- Reactor类 作为nio 响应器模式的核心部分。承载了selector选择器、ServerSocketChannel 服务端的通道。三个重要的功能

- Acceptor 类的作用其一是获取客户端与服务端的连接，其二是获取连接后调用handler处理（为了简化，handler使用状态模式来模拟其他事件，所以这里一旦有客户端连接，就会通过初始设置READING = 0 读事件）
```java
class Reactor implements Runnable {
    final Selector selector;
    final ServerSocketChannel serverSocket;

    Reactor(int port) throws IOException {
        selector = Selector.open();
        serverSocket = ServerSocketChannel.open();
        serverSocket.socket().bind(new InetSocketAddress(port));
        serverSocket.configureBlocking(false);
        //注册ServerSocketChannel的兴趣事件为连接OP_ACCEPT
        SelectionKey sk = serverSocket.register(selector,SelectionKey.OP_ACCEPT);
        //附加Acceptor，稍后调用attachment可以取得该对象
        sk.attach(new Acceptor());
    }
    public void run() {  //normally in a new Thread
        try {
            while (!Thread.interrupted()) {
                //阻塞 直到有有一個通道返回
                selector.select();
                Set selected = selector.selectedKeys();
                Iterator it = selected.iterator();
                //循环检测是否有新事件注册
                while (it.hasNext())
                    //同步分发
                    dispatch((SelectionKey)(it.next()));
                selected.clear();
            }
        } catch (IOException ex) { /* ... */ }
    }
    void dispatch(SelectionKey k) {
        //取得attach附加的对象处理
        Runnable r = (Runnable)(k.attachment());
        if (r != null)
            r.run();
    }

    // class Reactor continued
class Acceptor implements Runnable {
        // inner
        public void run() {
            try {
          //接受到通道套接字的连接
                SocketChannel c = serverSocket.accept();
                if (c != null)
                    new Handler(selector, c);
            }
            catch(IOException ex) { /* ... */ }
        }
    }
}
```

- Handler 类 1、将自身绑定到选择器、并注册读事件（ sk = socket.register( sel, 0); sk.attach(this);）2、 根据READING SENDING 状态判断事件
```java
/**
 * handler用到状态模式，根据当前读写的状态分别处理
 */
final class Handler implements Runnable {
    final SocketChannel socket;
    final SelectionKey sk;
    ByteBuffer input = ByteBuffer.allocate(1024);
    ByteBuffer output = ByteBuffer.allocate(1024);
    static final int READING  = 0 ,SENDING = 1;
    int state = READING;
    Handler(Selector sel , SocketChannel c) throws IOException {
        socket = c;
        //设置通道为非阻塞
        c.configureBlocking(false);
// Optionally try first read now
        sk = socket.register( sel, 0);
        sk.attach(this);
        sk.interestOps(SelectionKey.OP_READ);
        //select阻塞后，可以用wakeup唤醒；执行wakeup时，如果没有阻塞的select  那么执行完wakeup后下一个执行select就会立即返回。
        sel.wakeup();
    }
    boolean inputIsComplete()  {/* 相关处理略... */ return true; }
    boolean outputIsComplete() { /*相关处理略 ... */return true; }
    void process(){}
    public void run() {
        try {
            if (state == READING)
                read();
            else if (state ==SENDING)
                send();
        } catch (IOException ex) { /* ... */ }
    }
    void read()throws IOException {
        socket.read(input);
        if (inputIsComplete()) {
            process();
            // 处理完成后设置发送状态
            state =SENDING;
            //注册写事件
            sk.interestOps(SelectionKey.OP_WRITE);
        }
    }
    void send()throws IOException {
        socket.write(output);
        if (outputIsComplete())
            sk.cancel();
    }
}
```