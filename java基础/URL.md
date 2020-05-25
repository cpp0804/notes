## 参考博文
[Java URL处理](https://www.runoob.com/java/java-url-processing.html)



# 1. URL分析

```
protocol://host:port/path?query#fragment

http://www.runoob.com/index.html?language=cn#j2se

协议为(protocol)：可以是 HTTP、HTTPS、FTP 和 File。http
主机为(host:port)：www.runoob.com
端口号为(port): 80 ，以上URL实例并未指定端口，因为 HTTP 协议默认的端口号为 80。
文件路径为(path)：/index.html
请求参数(query)：language=cn
定位位置(fragment)：j2se，定位到网页中 id 属性为 j2se 的 HTML 元素位置
```

# 2. URL类
## 构造方法
```java
//通过给定的参数(协议、主机名、端口号、文件名)创建URL。
public URL(String protocol, String host, int port, String file) throws MalformedURLException.

//使用指定的协议、主机名、文件名创建URL，端口使用协议的默认端口。
public URL(String protocol, String host, String file) throws MalformedURLException

//通过给定的URL字符串创建URL
public URL(String url) throws MalformedURLException

//使用基地址和相对URL创建
public URL(URL context, String url) throws MalformedURLException
```

## 方法
```java
//返回URL路径部分
public String getPath()

//返回URL查询部分
public String getQuery()

//获取此 URL 的授权部分
public String getAuthority()

//返回URL端口部分
public int getPort()

//返回协议的默认端口号
public int getDefaultPort()

//返回URL的协议
public String getProtocol()

//返回URL的主机
public String getHost()

//返回URL文件名部分
public String getFile()

//获取此 URL 的锚点（也称为"引用"）
public String getRef()

//打开一个URL连接，并运行客户端访问资源
public URLConnection openConnection() throws IOException
```

## 实例
- URL方法测试
```java
package javaBase;

import java.net.MalformedURLException;
import java.net.URL;

public class URLTest {

    public static void main(String[] args) throws MalformedURLException {
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
}
/*
URL 为：https://www.runoob.com/java/java-url-processing.html
协议为：https
验证信息：www.runoob.com
文件名及请求参数：/java/java-url-processing.html
主机名：www.runoob.com
路径：/java/java-url-processing.html
端口：-1
默认端口：443
请求参数：null
定位位置：null
*/
```



- 可以直接从URL中读取字节流数据
```java
package javaBase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class URLTest {

    public static void main(String[] args) throws IOException {
        urlStreamTest();
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
}
/*
<!DOCTYPE html>
<!--STATUS OK--><html> <head><meta http-equiv=content-type content=text/html;charset=utf-8><meta http-equiv=X-UA-Compatible content=IE=Edge><meta content=always name=referrer><link rel=stylesheet type=text/css href=http://s1.bdstatic.com/r/www/cache/bdorz/baidu.min.css><title>百度一下，你就知道</title></head> <body link=#0000cc> <div id=wrapper> <div id=head> <div class=head_wrapper> <div class=s_form> <div class=s_form_wrapper> <div id=lg> <img hidefocus=true src=//www.baidu.com/img/bd_logo1.png width=270 height=129> </div> <form id=form name=f action=//www.baidu.com/s class=fm> <input type=hidden name=bdorz_come value=1> <input type=hidden name=ie value=utf-8> <input type=hidden name=f value=8> <input type=hidden name=rsv_bp value=1> <input type=hidden name=rsv_idx value=1> <input type=hidden name=tn value=baidu><span class="bg s_ipt_wr"><input id=kw name=wd class=s_ipt value maxlength=255 autocomplete=off autofocus></span><span class="bg s_btn_wr"><input type=submit id=su value=百度一下 class="bg s_btn"></span> </form> </div> </div> <div id=u1> <a href=http://news.baidu.com name=tj_trnews class=mnav>新闻</a> <a href=http://www.hao123.com name=tj_trhao123 class=mnav>hao123</a> <a href=http://map.baidu.com name=tj_trmap class=mnav>地图</a> <a href=http://v.baidu.com name=tj_trvideo class=mnav>视频</a> <a href=http://tieba.baidu.com name=tj_trtieba class=mnav>贴吧</a> <noscript> <a href=http://www.baidu.com/bdorz/login.gif?login&amp;tpl=mn&amp;u=http%3A%2F%2Fwww.baidu.com%2f%3fbdorz_come%3d1 name=tj_login class=lb>登录</a> </noscript> <script>document.write('<a href="http://www.baidu.com/bdorz/login.gif?login&tpl=mn&u='+ encodeURIComponent(window.location.href+ (window.location.search === "" ? "?" : "&")+ "bdorz_come=1")+ '" name="tj_login" class="lb">登录</a>');</script> <a href=//www.baidu.com/more/ name=tj_briicon class=bri style="display: block;">更多产品</a> </div> </div> </div> <div id=ftCon> <div id=ftConw> <p id=lh> <a href=http://home.baidu.com>关于百度</a> <a href=http://ir.baidu.com>About Baidu</a> </p> <p id=cp>&copy;2017&nbsp;Baidu&nbsp;<a href=http://www.baidu.com/duty/>使用百度前必读</a>&nbsp; <a href=http://jianyi.baidu.com/ class=cp-feedback>意见反馈</a>&nbsp;京ICP证030173号&nbsp; <img src=//www.baidu.com/img/gs.gif> </p> </div> </div> </div> </body> </html>

Process finished with exit code 0

*/
```

## URLConnections
URLConnection 类来表示与 URL 建立的通信连接

URLConnection 类的对象使用 URL 类的 openConnection() 方法获得

如果你连接HTTP协议的URL, openConnection() 方法返回 HttpURLConnection 对象。

如果你连接的URL为一个 JAR 文件, openConnection() 方法将返回 JarURLConnection 对象

- 方法
```java
//检索URL链接内容
Object getContent()

//检索URL链接内容
Object getContent(Class[] classes)

//返回头部 content-encoding 字段值
String getContentEncoding()

//返回头部 content-length字段值
int getContentLength()

//返回头部 content-type 字段值
String getContentType()

//返回头部 last-modified 字段值
int getLastModified()

//返回头部 expires 字段值
long getExpiration()

//返回对象的 ifModifiedSince 字段值
long getIfModifiedSince()

//URL 连接可用于输入和/或输出。如果打算使用 URL 连接进行输入，则将 DoInput 标志设置为 true；如果不打算使用，则设置为 false。默认值为 true
public void setDoInput(boolean input)

//URL 连接可用于输入和/或输出。如果打算使用 URL 连接进行输出，则将 DoOutput 标志设置为 true；如果不打算使用，则设置为 false。默认值为 false。
public void setDoOutput(boolean output)

//返回URL的输入流，用于读取资源
public InputStream getInputStream() throws IOException

//返回URL的输出流, 用于写入资源
public OutputStream getOutputStream() throws IOException

//返回 URLConnection 对象连接的URL
public URL getURL()
```



- 例子
```java
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
    /*
    <!DOCTYPE HTML PUBLIC "-//IETF//DTD HTML 2.0//EN"><html><head><title>301 Moved Permanently</title></head><body bgcolor="white"><h1>301 Moved Permanently</h1><p>The requested resource has been assigned a new permanent URI.</p><hr/>Powered by Tengine</body></html>
    */
```