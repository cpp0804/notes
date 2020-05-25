package javaBase.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * socket服务端
 */
public class SocketServer {

    public static void main(String[] args) throws IOException {
        cicleSocket();
    }

    //双向通信
    public static void dualCommunication() throws IOException {
        int port = 5533;
        ServerSocket server = new ServerSocket(port);
        System.out.println("server将一直等待连接的到来");
        //// server将一直等待连接的到来
        Socket socket = server.accept();

        // 建立好连接后，从socket中获取输入流，并建立缓冲区进行读取
        InputStream inputStream = socket.getInputStream();
        byte[] bytes = new byte[1024];
        int len;
        StringBuilder sb = new StringBuilder();
        while ((len = inputStream.read(bytes)) != -1) {
            //注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
            sb.append(new String(bytes, 0, len, "UTF-8"));
        }

        System.out.println("get message from client: " + sb);

        //双向通信
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write("Hello Client,I get the message.".getBytes("UTF-8"));

        inputStream.close();
        outputStream.close();
        socket.close();
        server.close();
    }

    //服务端多线程处理多个socket
    public static void cicleSocket() throws IOException {
        // 监听指定的端口
        int port = 5533;
        ServerSocket server = new ServerSocket(port);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 15, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));

        while (true) {
            //每来一个socket请求，就从线程池中取出线程来处理。然后主线程继续监听端口
            Socket socket = server.accept();
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    InputStream stream = null;
                    try {
                        stream = socket.getInputStream();
                        byte[] bytes = new byte[1024];
                        int len;
                        StringBuilder sb = new StringBuilder();
                        while ((len = stream.read(bytes)) != -1) {
                            // 注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
                            sb.append(new String(bytes, 0, len, "UTF-8"));
                        }
                        System.out.println("get message from client: " + sb);
                        stream.close();
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });

        }

    }
}
