[TOC]

# 编译javac
默认会将.class文件和.java文件放一起，使用-d指定路径
```
cd myProject
javac -d classes source/MyClass.java
```


# 运行java
-D代表创建系统变量cmdProp，并给他赋值cmdVal，然后运行TestProps
```
java -DcmdProp=cmdVal TestProps
```

```java
import java.util.*; 

public class TestProps {
    public static void main(String[] args) { 
        Properties p = System.getProperties(); 
        p.setProperty("myProp", "myValue"); 
        p.list(System.out);
    } 
}
/*
os.name=Mac OS X
myProp=myValue
...
java.specification.vendor=Sun Microsystems Inc. user.language=en
java.version=1.5.0_02 ...
cmdProp=cmdVal
*/
```


执行CmdArgs，并传递两个参数x和1
```
java CmdArgs x 1
```

```java
public class CmdArgs {
    public static void main(String[] args) {
        int x = 0; 
        for(String s : args)
            System.out.println(x++ + " element = " + s); 
    }
}
/*
0 element = x 
1 element = 1
*/
```


# 打包jar
```
test 
|
|--UseStuff.java 
|--ws
    |
    |--(create MyJar.jar here) 
    |--myApp
        | 
        |--utils 
        |   |
        |   |--Dates.class
        |   |--Conversions.class " " 
        |
            |--engine
                | 
                |--rete.class   (package myApp.engine;)
                |--minmax.class  " "


cd ws
jar -cf MyJar.jar myApp


jar -tf MyJar.jar
META-INF/ META-INF/MANIFEST.MF
myApp/
myApp/.DS_Store
myApp/utils/ myApp/utils/Dates.class myApp/utils/Conversions.class myApp/engine/ myApp/engine/rete.class myApp/engine/minmax.class
```