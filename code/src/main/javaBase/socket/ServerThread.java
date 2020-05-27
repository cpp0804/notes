package javaBase.socket;

import java.io.*;
import java.net.Socket;

/**
 * BIO线程任务
 */
public class ServerThread implements Runnable {

    private Socket socket;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();

            PrintStream printStream = new PrintStream(outputStream);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            boolean flag = true;
            while (flag) {
                String string = bufferedReader.readLine();
                if (string == null || "".equals(string) || "bye".equals(string)) {
                    flag = false;
                } else {
                    System.out.println("receive from client:" + string);
                    //告知客户端信息收到了
                    printStream.println("echo:" + string);
                }
            }

            printStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
