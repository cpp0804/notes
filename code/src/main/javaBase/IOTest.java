package javaBase;

import java.io.*;
import java.net.InetAddress;

/**
 * 测试Java的字节流和字符流
 */
public class IOTest {

    public static void main(String[] args) throws IOException {
        inputStreamReaderTest();

    }

    //将字节转换成字符
    public static void inputStreamReaderTest() throws IOException {
        //创建读取文件的字节流对象
        InputStream in = new FileInputStream("/Users/chenpeipei/Documents/炸鸡/code/src/main/javaBase/io.txt");
        //创建转换流对象
//        Reader r = new BufferedReader(new FileReader("/Users/chenpeipei/Documents/炸鸡/code/src/main/javaBase/io.txt"));
        InputStreamReader reader = new InputStreamReader(in, "utf-8");
        int ch = 0;
        while ((ch = reader.read()) != -1) {
            System.out.print((char)ch);
        }
        reader.close();
    }

    //将字符转换成字节
    public static void outputStreamWriterTest() throws IOException {
        //创建与文件关联的字节输出流对象
        OutputStream out = new FileOutputStream("/Users/chenpeipei/Documents/炸鸡/code/src/main/javaBase/ioOut.txt");

        //创建可以把字符转换成字节的对象
        OutputStreamWriter writer = new OutputStreamWriter(out, "utf-8");

        writer.write("你好");
        writer.close();
    }
}
