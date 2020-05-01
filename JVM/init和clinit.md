## 参考博文
[深入理解jvm--Java中init和clinit区别完全解析](https://blog.csdn.net/u013309870/article/details/72975536)


[TOC]

这两个方法都是在编译时的字节码生成阶段添加到语法树当中

# clinit
类构造器在加载过程中的初始化阶段执行，对static变量和static代码块进行赋值

1. clinit是由编译器按语句在源文件中的顺序收集static变量和static代码块产生的。对于static代码块，只能访问定义在它之前的static变量，对于定义在他后面的只能赋值不能访问
```java
public class Test{
static{
i=0；//给变量赋值可以正常编译通过
System.out.print（i）；//这句编译器会提示"非法向前引用"
}
static int i=1；
}
```

2. JVM会保证父类的clinit一定在子类的clinit执行之前执行。所以有以下执行顺序。
>1. 父类静态代变量、
>2. 父类静态代码块、
>3. 子类静态变量、
>4. 子类静态代码块、
>5. 父类非静态变量（父类实例成员变量）、
>6. 父类构造函数、
>7. 子类非静态变量（子类实例成员变量）、
>8. 子类构造函数



# init
实例构造器，在通过new调用类的构造方法实例化时执行，对非static变量进行赋值


