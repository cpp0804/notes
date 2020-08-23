# Arthas

# 参考文档
**arthas idea plugin 使用文档**
[https://www.yuque.com/docs/share/fa77c7b4-c016-4de6-9fa3-58ef25a97948?spm=a2c6h.12873639.0.0.3053734926QLTA#pwJKx](https://www.yuque.com/docs/share/fa77c7b4-c016-4de6-9fa3-58ef25a97948?spm=a2c6h.12873639.0.0.3053734926QLTA#pwJKx)


**爱上Java诊断利器Arthas之Arthas idea plugin 的前世今生**
[https://www.yuque.com/docs/share/01217521-2fdb-4261-8904-ef6e20d4f5ea?#pmAZQ](https://www.yuque.com/docs/share/01217521-2fdb-4261-8904-ef6e20d4f5ea?#pmAZQ)


**官网**
[https://alibaba.github.io/arthas/watch.html](https://alibaba.github.io/arthas/watch.html)


# 快速使用

- 安装
```java
curl -sk https://arthas.gitee.io/arthas-boot.jar -o ~/.arthas-boot.jar  && echo "alias as.sh='java -jar ~/.arthas-boot.jar --repo-mirror aliyun --use-http'" >> ~/.bashrc && source ~/.bashrc
```


- 启动
```java
cd /opt/taobao/install/ajdk-8_0_0-b60/bin

./java -jar /home/admin/.arthas-boot.jar
```


# 常用命令
### watch
查看方法入参、返回值和异常


使用插件右键方法名，在Arthas Command中选择watch，然后粘贴到命令行中。在方法被调用后会显示出{params,returnObj,throwExp}的值
```java
watch com.alibaba.porsche.web.apply.impl.StrategyItemFetcher fetchItemList '{params,returnObj,throwExp}' -n 5 -x 3 '1==1'
```


### trace 查看调用链
```java
trace com.alibaba.porsche.web.apply.impl.StrategyItemFetcher fetchItemList -n 5 '1==1' --skipJDKMethod false
```






# 插件下载
[https://plugins.jetbrains.com/plugin/13581-arthas-idea](https://plugins.jetbrains.com/plugin/13581-arthas-idea)，直接搜索arthas 即可下载
