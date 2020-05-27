
## 参考博文
[Java使用InetAddress类获取主机名和IP地址](https://blog.csdn.net/u012561176/article/details/48183181)


InetAddress:用于表示网络上的硬件资源，即 IP 地址； 


```java
//返回本地主机
InetAddress inetAddress=InetAddress.getLocalHost();

//在给定原始IP地址得情况下，返回一个InetAddress对象
InetAddress inetAddress=InetAddress.getByAddress(b);

//在给定主机名的情况下确定主机的 IP 地址
InetAddress inetAddress=InetAddress.getByName("Lc");
```

```java
public class InetAddressTest {

    public static void main(String[] args) throws IOException {
        InetAddress inetAddress=InetAddress.getLocalHost();

        //返回 IP 地址字符串（以文本表现形式）
        System.out.println(inetAddress.getHostAddress());

        //获取此 IP 地址的主机名
        System.out.println(inetAddress.getHostName());

        //获取此 IP 地址的完全限定域名
        System.out.println(inetAddress.getCanonicalHostName());

        //测试是否可以达到该地址
        System.out.println(inetAddress.isReachable(100));

        //返回此 InetAddress 对象的原始 IP 地址。如果将此字节数组输出在控制台下，会出现一个存放此字节数组的内存地址
        System.out.println(inetAddress.getAddress());

        byte[] address=inetAddress.getAddress();//获取原始IP地址
        System.out.println("原始IP地址为："+address[0]+"."+address[1]+"."+address[2]+"."+ address[3]);

    }
}

```