[TOC]

# ==
比较的是栈中的变量存放的地址值是否相同，即比较变量是否指向同一个对象。

对于基本数据类型变量来说，他的变量名和变量值都存在虚拟机栈中，值是变量间共享的，所以两个基本类型的变量如果值相同，那他们==比较结果也相同。 [值传递和引用传递](./值传递和引用传递.md)

对于引用类型变量来说，他的变量名存在虚拟机栈中，变量值是指向堆内存的地址，即使两个变量值在内容上来看是一样的，他们如果不是同一个堆地址，那他们==比较结果也是不同的

# equals
比较两个对象的内容是否相同。但是没有重写Object的equals方法，那默认还是使用==比较
```java
public boolean equals(Object obj) {
    //this - s1
    //obj - s2
    return (this == obj);
}
```

所以String、Integer、Date等都重写了equals方法
```java
//String的equals方法实现
public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof String) {
            String anotherString = (String)anObject;
            int n = value.length;
            if (n == anotherString.value.length) {
                char v1[] = value;
                char v2[] = anotherString.value;
                int i = 0;
                while (n-- != 0) {
                    if (v1[i] != v2[i])
                        return false;
                    i++;
                }
                return true;
            }
        }
        return false;
}
```
```java
public class test1 {
    public static void main(String[] args) {
        // a 为一个引用
        String a = new String("ab"); 
        // b为另一个引用,对象的内容一样
        String b = new String("ab");
        // 放在常量池中
        String aa = "ab"; 
        // 从常量池中查找
        String bb = "ab"; 
        if (aa == bb) // true
            System.out.println("aa==bb");
        if (a == b) // false，非同一对象
            System.out.println("a==b");
        if (a.equals(b)) // true
            System.out.println("aEQb");
        if (42 == 42.0) { // true
            System.out.println("true");
        }
    }
}
```