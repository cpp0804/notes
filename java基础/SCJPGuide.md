[TOC]

# 第一章 声明和访问控制(Declarations and Access Control)
## 1.1 标识符和JavaBean(Identifiers & JavaBeans)
### 命名规范


# 第三章 赋值
方法内定义的变量最好都给赋初始值
```java
public class TestLocal {
    public static void main(String [] args) {
        int x;
        if (args[0] != null) { // assume you know this will always be true
            x = 7;  // compiler can't tell that this
                    // statement will run
        } 
        int y = x;  // the compiler will choke here
          
    }
} 

The compiler will produce an error something like this:
TestLocal.java:9: variable x might not have been initialized
```
