package javaBase.socket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;

public class Handler implements Runnable {
    final SocketChannel socket;
    final SelectionKey sk;
    ByteBuffer input = ByteBuffer.allocate(1024);
    ByteBuffer output = ByteBuffer.allocate(1024);
    static final int READING = 0, SENDING = 1;
    int state = READING;

    Handler(Selector sel, SocketChannel c) throws IOException {
        socket = c;
        //设置通道为非阻塞
        c.configureBlocking(false);
// Optionally try first read now
        sk = socket.register(sel, 0);
        sk.attach(this);
        sk.interestOps(SelectionKey.OP_READ);
        //select阻塞后，可以用wakeup唤醒；执行wakeup时，如果没有阻塞的select  那么执行完wakeup后下一个执行select就会立即返回。
        sel.wakeup();
    }

    boolean inputIsComplete() {/* 相关处理略... */
        return true;
    }

    boolean outputIsComplete() { /*相关处理略 ... */
        return true;
    }

    void process() {
    }

    public void run() {
        try {
            if (state == READING)
                read();
            else if (state == SENDING)
                send();
        } catch (IOException ex) { /* ... */ }
    }

    void read() throws IOException {
        socket.read(input);
        if (inputIsComplete()) {
            process();
            // 处理完成后设置发送状态
            state = SENDING;
            //注册写事件
            sk.interestOps(SelectionKey.OP_WRITE);
        }
    }

    void send() throws IOException {
        socket.write(output);
        if (outputIsComplete())
            sk.cancel();
    }
}
