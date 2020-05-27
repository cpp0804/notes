package javaBase.socket;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * BIO客户端
 */
public class BIOClient {

    public static void main(String[] args) throws IOException {

        Socket socket = new Socket(InetAddress.getLocalHost().getHostAddress(), 2006);
        OutputStream outputStream = socket.getOutputStream();
        InputStream inputStream = socket.getInputStream();
        //获取键盘输入
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        PrintStream printStream = new PrintStream(outputStream);
        //接受服务端消息
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        boolean flag = true;
        while (flag) {
            String str = input.readLine();

            //发给服务端
            printStream.println(str);
            if ("bye".equals(str)) {
                flag = false;
            } else {
                String echo = bufferedReader.readLine();
                System.out.println(echo);
            }
        }
        input.close();
        if (socket != null) {
            socket.close();
        }

    }
}
