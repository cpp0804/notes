
## maven包冲突问题
### 解决
如果在引入一个新的包之后，发现某个类找不到了或者某个方法找不到了，大概率是包冲突了。
```java
Caused by:java.lang.NoSuchMethodError
Caused by: java.lang.ClassNotFoundException
```


可以做以下排查：

- 方法一
1. 看这个类是在哪个包里面

如果是某个方法找不到了就去看服务器上的版本是什么，去看这个类里面是不是有这个方法


2. 可以按情况选择升级包版本、引入新包、排包



3. 看这个包的maven结构

看看这个包是被哪个包引进来的，把它排掉
```
mvn dependency:tree>tree.log
```


不要使用snapshot的包




- 方法二：使用maven helper

[https://blog.csdn.net/noaman_wgs/article/details/81137893](https://blog.csdn.net/noaman_wgs/article/details/81137893)


### 原理
假设有如下依赖关系：
```java
A->B->C->D1(log 15.0)：A中包含对B的依赖，B中包含对C的依赖，C中包含对D1的依赖，假设是D1是日志jar包，version为15.0
    
E->F->D2(log 16.0)：E中包含对F的依赖，F包含对D2的依赖，假设是D2是同一个日志jar包，version为16.0
```
当pom.xml文件中引入A、E两个依赖后，根据Maven传递依赖的原则，D1、D2都会被引入，而D1、D2是同一个依赖D的不同版本。


当我们在调用D2中的method1()方法，而D1中是15.0版本（method1可能是D升级后增加的方法），可能没有这个方法，这样JVM在加载A中D1依赖的时候，找不到method1方法，就会报`NoSuchMethodError`的错误，此时就产生了jar包冲突。


注：
如果在调用method2()方法的时候，D1、D2都含有这个方法（且升级的版本D2没有改动这个方法，这样即使D有多个版本，也不会产生版本冲突的问题。）


Maven 解析 pom.xml 文件时，同一个 jar 包只会保留一个，那么面对多个版本的jar包，maven处理方式如下：
```java
1. 最短路径优先
Maven 面对 D1 和 D2 时，会默认选择最短路径的那个 jar 包，即 D2。E->F->D2 比 A->B->C->D1 路径短

2. 最先声明优先
如果路径一样的话，如： A->B->C1, E->F->C2 ，两个依赖路径长度都是 2，那么就选择最先声明
```


## aone版本冲突问题
假如在tmc-business上有三个分支有A B C三个版本,Porsche上引入了这A B C三个版本,那么在部署的时候就会造成冲突的问题。


解决方案是：

1. 在IDEA中切换到集成分支，将集成分支的包版本改成A，部署
1. 在aone上“提交二方库发布”一起打包

![aone打包](./pic/maven问题_aone打包.png)

3. Porsche上所有的版本都选择A版本




## 命令
### 查看某个类
```shell
//查看项目下的jar包
cd tmc-apply/target/tmc-apply/BOOT-INF/lib/

//查看包含某一个关键字的包
ll | grep servlet

//查看包里面的内容
vim javax.servlet-api-3.1.0.jar

//搜索某一个文件,搜索到了之后再次回车就能看见里面的内容
/ServletContext
```


### 4个命令
##### compile
编译maven


#### package
编译并打包maven




#### install
编译并打包maven，然后将jar包安装到本地repository
```shell
//-Dmaven.test.skip=true代表跳过测试类
mvn install -Dmaven.test.skip=true
```


#### deploy
编译并打包maven，然后将jar包安装到远程repository

### 删除某个包
```shell
rm -rf 包名
```
