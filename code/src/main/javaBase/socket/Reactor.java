package javaBase.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class Reactor implements Runnable {
    final Selector selector;
    final ServerSocketChannel serverSocket;

    Reactor(int port) throws IOException {
        selector = Selector.open();
        serverSocket = ServerSocketChannel.open();
        serverSocket.socket().bind(new InetSocketAddress(port));
        serverSocket.configureBlocking(false);
        //注册ServerSocketChannel的兴趣事件为连接OP_ACCEPT
        SelectionKey sk = serverSocket.register(selector, SelectionKey.OP_ACCEPT);
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
                    dispatch((SelectionKey) (it.next()));
                selected.clear();
            }
        } catch (IOException ex) { /* ... */ }
    }

    void dispatch(SelectionKey k) {
        //取得attach附加的对象处理
        Runnable r = (Runnable) (k.attachment());
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
            } catch (IOException ex) { /* ... */ }
        }
    }
}
