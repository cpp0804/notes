package javaBase.socket;

import com.sun.security.ntlm.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * BIO客户端
 */
public class BIOServer {

    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 15, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(2006);
        while (true) {
            Socket socket = serverSocket.accept();
            executor.execute(new ServerThread(socket));
        }
    }
}
