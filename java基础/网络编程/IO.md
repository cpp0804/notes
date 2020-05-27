## 参考博文
[Java IO - 分类(传输，操作)](https://www.pdai.tech/md/java/io/java-io-basic-category.html)
[Java笔记-字节流，字符流，转换流，缓冲流](https://blog.csdn.net/Coco_liukeke/article/details/80410700)
[java I/O流详解](https://juejin.im/post/5d4ee73ae51d4561c94b0f9d)


[TOC]


# 1. 框架
流是对输入输出的抽象，数据在两个设备间的传输就是流

## 1.1 分类
根据操作对象的类型是字符还是字节可分为两大类： 字符流和字节流

1. 字节是给计算机看的，字符是给人看的。字符流可以看作是特殊的二进制文件，使用了某种编码让人可以阅读
2. 字节流直接输出；字符流先输出到缓冲区，在调用close()关闭缓冲区时才输出，如果需要未关闭缓冲区就输出可以调用flush()
3. 字节流可以处理所有类型的数据，字符流只能处理字符类型

![数据操作分类](../pic/IO_数据操作分类.png)


分类|字节输入流|字节输出流|字符输入流|字符输出流
---|---|---|---|---|
抽象基类|InputStream|OutputStream|Reader|Writer
访问文件|FileInputStream|FileOutputStream|FileReader|FileWriter
访问数据|ByteArrayInputStream|ByteArrayOutputStream|CharArrayReader|CharArrayWriter
访问管道|PipedInputStream|PipedOutputStream|PipedReader|PipedWriter
访问字符串|||StringReader|StringWriter
缓冲流|BufferedInputStream|BufferedOutputStream|BufferedReader|BufferedWriter
转换流|||InputStreamReader|OutputStreamWriter
对象流|ObjectInputStream|ObjectOutputStream|||
对象流|FilterInputStream|FilterOuputStream|FilterReader|FilterWriter
打印流||PrintStream||PrintWriter
推回输入流|PishbackInputStream||PushBackReader||
特殊流|DataInputStream|DataOutputStream|||
## 字节流
![字节流](../pic/IO_字节流.png)

- 数据以字节为单位
- 用来处理二进制文件(图片、MP3、视频文件)

## 字符流
![字符流](../pic/IO_字符流.png)

- 数据以字符为单位。Java中的字符是Unicode编码，一个字符char占用两个字节
- 用来处理文本文件

# 2. 字节流
InputStream是输入字节流的父类

OutputStream是输出字节流的父类

## 2.1 InputStream
![继承关系](../pic/IO_InputStream继承关系.png)

- 基本方法
```java
/*
每次调用读取一个字节，并返回读到的数据，如果返回-1，表示读到了输入流的末尾
由子类实现
*/
public abstract int read() throws IOException 

/*
将数据读入一个字节数组，同时返回实际读取的字节数。
如果返回-1，表示读到了输入流的末尾。
其实是调用的read(b, 0 ,b.length)
*/
public int read(byte[]b) throws IOException

/*
将数据读入一个字节数组，同时返回实际读取的字节数。
如果返回-1，表示读到了输入流的末尾。
off指定在数组b中存放数据的起始偏移位置；len指定读取的最大字节数。
读取字节调用的就是read()
*/
public int read(byte[]b,int off,int len) throws IOException

//读取完，关闭流，释放资源
public void close() throws IOException

// 跳过指定个数的字节不读取，想想看电影跳过片头片尾
public long skip(long n) 

// 返回可读的字节数量
public int available() 

// 标记读取位置，下次还可以从这里开始读取，使用前要看当前流是否支持，可以使用 markSupport() 方法判断
public synchronized void mark(int readlimit) 

// 重置读取位置为上次 mark 标记的位置
public synchronized void reset() 

// 判断当前流是否支持标记流，和上面两个方法配套使用
public boolean markSupported() 

```


- 源码
```java
public abstract class InputStream implements Closeable {
    private static final int SKIP_BUFFER_SIZE = 2048;  //用于skip方法，和skipBuffer相关
    private static byte[] skipBuffer;    // skipBuffer is initialized in skip(long), if needed.
    
    //从输入流中读取下一个字节，
    //正常返回0-255，到达文件的末尾返回-1
    //在流中还有数据，但是没有读到时该方法会阻塞(block)
    //Java IO和New IO的区别就是阻塞流和非阻塞流
    //抽象方法！不同的子类不同的实现！
    public abstract int read() throws IOException;  
    
    //将流中的数据读入放在byte数组的第off个位置先后的len个位置中
    //放回值为放入字节的个数。
    //这个方法在利用抽象方法read，某种意义上简单的Templete模式。
    public int read(byte b[], int off, int len) throws IOException {
        //检查输入是否正常。一般情况下，检查输入是方法设计的第一步
        if (b == null) {    
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > b.length - off) {
             throw new IndexOutOfBoundsException();
        } else if (len == 0) {
             return 0;
        }        
        //读取下一个字节
        int c = read();
        //到达文件的末端返回-1
        if (c == -1) {    return -1;   }
        //放回的字节downcast                           
        b[off] = (byte)c;
        //已经读取了一个字节                                                   
        int i = 1;                                                                        
        try {
            //最多读取len个字节，所以要循环len次
            for (; i < len ; i++) {
                //每次循环从流中读取一个字节
                //由于read方法阻塞，
                //所以read(byte[],int,int)也会阻塞
                c = read();
                //到达末尾，理所当然放回-1                                       
                if (c == -1) {            break;           } 
                //读到就放入byte数组中
                b[off + i] = (byte)c;
            }
        } catch (IOException ee) {     }
        return i;
    }

     //利用上面的方法read(byte[] b)
    public int read(byte b[]) throws IOException {
        return read(b, 0, b.length);
     }                          
    //方法内部使用的、表示要跳过的字节数目，
     public long skip(long n) throws IOException {
        long remaining = n;    
        int nr;
        if (skipBuffer == null)
        //初始化一个跳转的缓存
        skipBuffer = new byte[SKIP_BUFFER_SIZE];                   
        //本地化的跳转缓存
        byte[] localSkipBuffer = skipBuffer;          
        //检查输入参数，应该放在方法的开始                            
        if (n <= 0) {    return 0;      }                             
        //一共要跳过n个，每次跳过部分，循环       
        while (remaining > 0) {                                      
            nr = read(localSkipBuffer, 0, (int) Math.min(SKIP_BUFFER_SIZE, remaining));
            //利用上面的read(byte[],int,int)方法尽量读取n个字节  
            //读到流的末端，则返回
            if (nr < 0) {  break;    }
            //没有完全读到需要的，则继续循环
            remaining -= nr;                                       
        }       
        return n - remaining;//返回时要么全部读完，要么因为到达文件末端，读取了部分
    }
    //查询流中还有多少可以读取的字节
    //该方法不会block。在java中抽象类方法的实现一般有以下几种方式: 
    //1.抛出异常(java.util)；2.“弱”实现。像上面这种。子类在必要的时候覆盖它。
    //3.“空”实现。
    public int available() throws IOException {           
        return 0;
    }
    //关闭当前流、同时释放与此流相关的资源
    public void close() throws IOException {}

    //markSupport可以查询当前流是否支持mark
    public synchronized void mark(int readlimit) {}

    //对mark过的流进行复位。只有当流支持mark时才可以使用此方法。
    public synchronized void reset() throws IOException {

                   throw new IOException("mark/reset not supported");

}
    //查询是否支持mark
    //绝大部分不支持，因此提供默认实现，返回false。子类有需要可以覆盖。
    public boolean markSupported() {           
        return false;
    }
}
```

## 2.2 OutputStream
```java
public abstract class OutputStream implements Closeable, Flushable {
    /* 写入一个字节，可以看到这里的参数是一个 int 类型，对应上面的读方法，int 类型的 32 位，只有低 8 位才写入，高 24 位将舍弃
    由子类实现
    */
    public abstract void write(int b) throws IOException;

    /*
    将数组中的所有字节写入，和上面对应的 read() 方法类似，实际调用的也是write(b, 0, b.length)
    */
    public void write(byte b[]) throws IOException {
        write(b, 0, b.length);
    }

    /*
    将 byte 数组从 off 位置开始，len 长度的字节写入
    */
    public void write(byte b[], int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        } else if ((off < 0) || (off > b.length) || (len < 0) ||
                   ((off + len) > b.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return;
        }
        for (int i = 0 ; i < len ; i++) {
            write(b[off + i]);
        }
    }

    //强制刷新，将缓冲中的数据写入。只有字符流需要
    public void flush() throws IOException {
    }

    //关闭输出流，流被关闭后就不能再输出数据了。只有字节流需要
    public void close() throws IOException {
    }
}
```

# 3. 字符流
## 3.1 Reader
```java
public abstract class Reader implements Readable, Closeable {

    protected Object lock;

    protected Reader() {
        this.lock = this;
    }

    protected Reader(Object lock) {
        if (lock == null) {
            throw new NullPointerException();
        }
        this.lock = lock;
    }

    public int read(java.nio.CharBuffer target) throws IOException {
        int len = target.remaining();
        char[] cbuf = new char[len];
        int n = read(cbuf, 0, len);
        if (n > 0)
            target.put(cbuf, 0, n);
        return n;
    }

    //读取一个字符，返回值为读取的字符
    public int read() throws IOException {
        char cb[] = new char[1];
        if (read(cb, 0, 1) == -1)
            return -1;
        else
            return cb[0];
    }

    //读取一系列字符到数组cbuf[]中，返回值为实际读取的字符的数量
    public int read(char cbuf[]) throws IOException {
        return read(cbuf, 0, cbuf.length);
    }

    //读取len个字符，从数组cbuf[]的下标off处开始存放，返回值为实际读取的字符数量，该方法必须由子类实现
    abstract public int read(char cbuf[], int off, int len) throws IOException;

    private static final int maxSkipBufferSize = 8192;

    private char skipBuffer[] = null;

    public long skip(long n) throws IOException {
        if (n < 0L)
            throw new IllegalArgumentException("skip value is negative");
        int nn = (int) Math.min(n, maxSkipBufferSize);
        synchronized (lock) {
            if ((skipBuffer == null) || (skipBuffer.length < nn))
                skipBuffer = new char[nn];
            long r = n;
            while (r > 0) {
                int nc = read(skipBuffer, 0, (int)Math.min(r, nn));
                if (nc == -1)
                    break;
                r -= nc;
            }
            return n - r;
        }
    }

    public boolean ready() throws IOException {
        return false;
    }

    public boolean markSupported() {
        return false;
    }

    public void mark(int readAheadLimit) throws IOException {
        throw new IOException("mark() not supported");
    }

    public void reset() throws IOException {
        throw new IOException("reset() not supported");
    }

     abstract public void close() throws IOException;

}
```

## 3.2 Writer
```java
public abstract class Writer implements Appendable, Closeable, Flushable {

    private char[] writeBuffer;

    private static final int WRITE_BUFFER_SIZE = 1024;

    protected Object lock;

    protected Writer() {
        this.lock = this;
    }

    protected Writer(Object lock) {
        if (lock == null) {
            throw new NullPointerException();
        }
        this.lock = lock;
    }

    //将整型值c的低16位写入输出流
    public void write(int c) throws IOException {
        synchronized (lock) {
            if (writeBuffer == null){
                writeBuffer = new char[WRITE_BUFFER_SIZE];
            }
            writeBuffer[0] = (char) c;
            write(writeBuffer, 0, 1);
        }
    }

    //将字符数组cbuf[]写入输出流
    public void write(char cbuf[]) throws IOException {
        write(cbuf, 0, cbuf.length);
    }

    //将字符数组cbuf[]中的从索引为off的位置处开始的len个字符写入输出流
    abstract public void write(char cbuf[], int off, int len) throws IOException;

    //将字符串str中的字符写入输出流
    public void write(String str) throws IOException {
        write(str, 0, str.length());
    }

    //将字符串str中从索引off开始处的len个字符写入输出流
    public void write(String str, int off, int len) throws IOException {
        synchronized (lock) {
            char cbuf[];
            if (len <= WRITE_BUFFER_SIZE) {
                if (writeBuffer == null) {
                    writeBuffer = new char[WRITE_BUFFER_SIZE];
                }
                cbuf = writeBuffer;
            } else {    // Don't permanently allocate very large buffers.
                cbuf = new char[len];
            }
            str.getChars(off, (off + len), cbuf, 0);
            write(cbuf, 0, len);
        }
    }

    public Writer append(CharSequence csq) throws IOException {
        if (csq == null)
            write("null");
        else
            write(csq.toString());
        return this;
    }

    public Writer append(CharSequence csq, int start, int end) throws IOException {
        CharSequence cs = (csq == null ? "null" : csq);
        write(cs.subSequence(start, end).toString());
        return this;
    }

    public Writer append(char c) throws IOException {
        write(c);
        return this;
    }

    abstract public void flush() throws IOException;

    abstract public void close() throws IOException;

}
```

# 4. 字符和字节间的转换
## 4.1 字节解码成字符流InputSreamReader
将字节解码成字符

使用的字符集可以由名称指定或显式给定，或者可以接受平台默认的字符集

### 源码
```java
public class InputStreamReader extends Reader {

    private final StreamDecoder sd;

    public InputStreamReader(InputStream in) {
        super(in);
        try {
            sd = StreamDecoder.forInputStreamReader(in, this, (String)null); // ## check lock object
        } catch (UnsupportedEncodingException e) {
            // The default encoding should always be available
            throw new Error(e);
        }
    }

    public InputStreamReader(InputStream in, String charsetName)
        throws UnsupportedEncodingException
    {
        super(in);
        if (charsetName == null)
            throw new NullPointerException("charsetName");
        sd = StreamDecoder.forInputStreamReader(in, this, charsetName);
    }

    public InputStreamReader(InputStream in, Charset cs) {
        super(in);
        if (cs == null)
            throw new NullPointerException("charset");
        sd = StreamDecoder.forInputStreamReader(in, this, cs);
    }

    public InputStreamReader(InputStream in, CharsetDecoder dec) {
        super(in);
        if (dec == null)
            throw new NullPointerException("charset decoder");
        sd = StreamDecoder.forInputStreamReader(in, this, dec);
    }

    public String getEncoding() {
        return sd.getEncoding();
    }

    public int read() throws IOException {
        return sd.read();
    }

    public int read(char cbuf[], int offset, int length) throws IOException {
        return sd.read(cbuf, offset, length);
    }

    public boolean ready() throws IOException {
        return sd.ready();
    }

    public void close() throws IOException {
        sd.close();
    }
}
```

### 例子
```java
    //将字节转换成字符
    public static void inputStreamReaderTest() throws IOException {
        //创建读取文件的字节流对象
        InputStream in = new FileInputStream("/Users/chenpeipei/Documents/炸鸡/code/src/main/javaBase/io.txt");

        //创建转换流对象
        InputStreamReader reader = new InputStreamReader(in, "utf-8");
        int ch = 0;
        while ((ch = reader.read()) != -1) {
            System.out.println((char)ch);
        }
        reader.close();
    }
    /*
    你好！
    hello！
    */
```

## 4.2 字符流编码成字节流OutputStreamWriter
将要写入流中的字符编码成字节

### 源码
```java
public class OutputStreamWriter extends Writer {

    private final StreamEncoder se;

    public OutputStreamWriter(OutputStream out, String charsetName)
        throws UnsupportedEncodingException
    {
        super(out);
        if (charsetName == null)
            throw new NullPointerException("charsetName");
        se = StreamEncoder.forOutputStreamWriter(out, this, charsetName);
    }

    public OutputStreamWriter(OutputStream out) {
        super(out);
        try {
            se = StreamEncoder.forOutputStreamWriter(out, this, (String)null);
        } catch (UnsupportedEncodingException e) {
            throw new Error(e);
        }
    }

    public OutputStreamWriter(OutputStream out, Charset cs) {
        super(out);
        if (cs == null)
            throw new NullPointerException("charset");
        se = StreamEncoder.forOutputStreamWriter(out, this, cs);
    }

    public OutputStreamWriter(OutputStream out, CharsetEncoder enc) {
        super(out);
        if (enc == null)
            throw new NullPointerException("charset encoder");
        se = StreamEncoder.forOutputStreamWriter(out, this, enc);
    }

    public String getEncoding() {
        return se.getEncoding();
    }

    void flushBuffer() throws IOException {
        se.flushBuffer();
    }

    public void write(int c) throws IOException {
        se.write(c);
    }

    public void write(char cbuf[], int off, int len) throws IOException {
        se.write(cbuf, off, len);
    }

    public void write(String str, int off, int len) throws IOException {
        se.write(str, off, len);
    }

    public void flush() throws IOException {
        se.flush();
    }

    public void close() throws IOException {
        se.close();
    }
}

```


### 例子
在OutputStreamWriter流中维护自己的缓冲区，当我们调用OutputStreamWriter对象的write方法时，会拿着字符到指定的码表中进行查询，把查到的字符编码值转成字节数存放到OutputStreamWriter缓冲区中。然后再调用刷新功能，或者关闭流，或者缓冲区存满后会把缓冲区中的字节数据使用字节流写到指定的文件中
```java
    //将字符转换成字节
    public static void outputStreamWriterTest() throws IOException {
        //创建与文件关联的字节输出流对象
        OutputStream out = new FileOutputStream("/Users/chenpeipei/Documents/炸鸡/code/src/main/javaBase/ioOut.txt");

        //创建可以把字符转换成字节的对象
        OutputStreamWriter writer = new OutputStreamWriter(out, "utf-8");

        writer.write("你好");
        writer.close();
    }
```

## 4.3 FileWriter和FileReader
FileWriter和FileReader是 OutputStreamWriter和InputStreamReader的子类
```java
    InputStreamReader  isr = new InputStreamReader(new FileInputStream("a.txt"));//默认字符集。

    InputStreamReader  isr = new InputStreamReader(newFileInputStream("a.txt"),"GBK");//指定GBK字符集。

    FileReader fr =new FileReader("a.txt");
```

# 5. File
File 类可以用于表示文件和目录的信息，但是它不表示文件的内容
```java
public static void listAllFiles(File dir) {
    if (dir == null || !dir.exists()) {
        return;
    }
    if (dir.isFile()) {
        System.out.println(dir.getName());
        return;
    }
    for (File file : dir.listFiles()) {
        listAllFiles(file);
    }
}
```

# 6. 例子
## 字节流
```java
public static void copyFile(String src, String dist) throws IOException {

    FileInputStream in = new FileInputStream(src);
    FileOutputStream out = new FileOutputStream(dist);
    byte[] buffer = new byte[20 * 1024];

    // read() 最多读取 buffer.length 个字节
    // 返回的是实际读取的个数
    // 返回 -1 的时候表示读到 eof，即文件尾
    while (in.read(buffer, 0, buffer.length) != -1) {
        out.write(buffer);
    }

    in.close();
    out.close();
}
  
```

## 实现逐行输出文本文件的内容
```java
public static void readFileContent(String filePath) throws IOException {

    FileReader fileReader = new FileReader(filePath);
    BufferedReader bufferedReader = new BufferedReader(fileReader);

    String line;
    while ((line = bufferedReader.readLine()) != null) {
        System.out.println(line);
    }

    // 装饰者模式使得 BufferedReader 组合了一个 Reader 对象
    // 在调用 BufferedReader 的 close() 方法时会去调用 Reader 的 close() 方法
    // 因此只要一个 close() 调用即可
    bufferedReader.close();
}
```

# 6. 常见类
## Serializable


## transient
transient 关键字可以使一些属性不会被序列化。 ArrayList 中存储数据的数组 elementData 是用 transient 修饰的，因为这个数组是动态扩展的，并不是所有的空间都被使用，因此就不需要所有的内容都被序列化。通过重写序列化和反序列化方法，使得可以只序列化数组中有内容的那部分数据。

```java
private transient Object[] elementData;
```
