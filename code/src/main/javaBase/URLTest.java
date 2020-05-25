package javaBase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class URLTest {

    public static void main(String[] args) throws IOException {
        urlTest();
    }

    public static void urlTest() throws MalformedURLException {
        URL url = new URL("https://www.runoob.com/java/java-url-processing.html");
        System.out.println("URL 为：" + url.toString());
        System.out.println("协议为：" + url.getProtocol());
        System.out.println("验证信息：" + url.getAuthority());
        System.out.println("文件名及请求参数：" + url.getFile());
        System.out.println("主机名：" + url.getHost());
        System.out.println("路径：" + url.getPath());
        System.out.println("端口：" + url.getPort());
        System.out.println("默认端口：" + url.getDefaultPort());
        System.out.println("请求参数：" + url.getQuery());
        System.out.println("定位位置：" + url.getRef());
    }

    public static void urlStreamTest() throws IOException {
        URL url = new URL("http://www.baidu.com");

        //字节流
        InputStream inputStream = url.openStream();

        //转换成字符流
        InputStreamReader reader = new InputStreamReader(inputStream, "utf-8");

        //提供缓存功能
        BufferedReader br = new BufferedReader(reader);

        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }

        br.close();
    }

    public static void urlConnectionsTest() throws IOException {
        URL url = new URL("http://www.runoob.com");
        URLConnection connection = url.openConnection();
        HttpURLConnection httpconnection = null;

        if (connection instanceof HttpURLConnection) {
            httpconnection = (HttpURLConnection) connection;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(httpconnection.getInputStream()));
        String urlString = "";
        String current;
        while ((current = reader.readLine()) != null) {
            urlString += current;
        }
        System.out.println(urlString);
    }
}
