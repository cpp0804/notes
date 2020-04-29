[TOC]

# 1. 源码编译过程
一次编写，到处运行的意思是说：
不管使用什么语言的文件，只要用对应的编译器编译成字节码文件(.class)交给JVM，JVM就能把.class文件翻译成对应机器的机器语言。

对于Java源文件(.java)的编译过程会分成两部分
1. 前端编译器(javac)：在编译期将.java文件编译成.class文件。这部分是与机器无关的部分，.class文件作为中间代码
2. 后端编译器(JIT)：作为热点代码的优化手段，在JVM运行过程中将字节码文件编译成机器指令存下来

[源码编译过程](./源码编译过程.md)

# 2. 类加载过程



# 3. 类执行过程


# 语法糖
### 泛型与类型擦除
在编译后的字节码中，将泛型中的具体类型替换成了原生类型E，并在相应的地方做了类型强转

下面的两个重载方法无法编译通过，因为经过泛型擦除之后都变成了List<E>
```java
public void fanxing(List<String> list){
        
    }

    public void fanxing(List<Integer> list){

    }
```

### 自动装箱、拆箱与遍历循环
Arrays.asList中的参数是可变参数，被编译成了数组类型的参数，并且把1，2，3，4调用了Integer.valueOf()方法包装成Integer

在遍历循环中，for循环被还原成迭代器的实现，所以被遍历的类要实现Iterable接口。并且每个数值又被调用intValue()方法拆箱成int
```java
    public void pack(){
        List<Integer> list = Arrays.asList(1,2,3,4);
        int sum = 0;
        for (int i : list) {
            sum += i;
        }
        System.out.println(sum);
    }
    /**
     * 经编译之后的代码
     */
    public void packJavac(){
        List list = Arrays.asList(new Integer[] {
                Integer.valueOf(1),
                Integer.valueOf(2),
                Integer.valueOf(3),
                Integer.valueOf(4)
        });
        int sum = 0;
        for (Iterator localIterator = list.iterator(); localIterator.hasNext();) {
            int i = ((Integer)localIterator.next()).intValue();
            sum += i;
        }
        System.out.println(sum);
    }
```
对于Integer的valueOf，JVM会在常量池中缓存-128到127之间的整数，所以任何在-128到127之间的整数的地址都是相同的，都是同一个引用。Integer、Short、Byte、Character、Long这几个类的valueOf方法的实现是类似的。

而Double、Float的valueOf方法的实现是类似的，他们不会有缓存

包装类的==在不遇到算数的情况下都不会自动拆箱，equals方法不处理数据类型转换
```java
public static void integerTest() {
        Integer a = 1;
        Integer b = 2;
        Integer c = 3;
        Integer d = 3;
        Integer e = 321;
        Integer f = 321;
        Long g = 3L;
        System.out.println(c == d);//T
        System.out.println(e == f);//F
        System.out.println(c == (a + b));//T
        System.out.println(c.equals(a + b));//T
        System.out.println(g == (a + b));//T
        System.out.println(g.equals(a + b));//F
    }
```

### 条件编译
只适用于条件为常量的if语句，对于其他例如while(true)就不能编译
```java
public void ifTest() {
        if (true) {
            System.out.println("t");
        } else {
            System.out.println("f");
        }
}

//会被编译成
public void ifTest() {
    System.out.println("t");
}
```