1. 使用decimal，如果是将double类型创给构造函数，要转换成String类型

```java
new BigDecimal(liveRoomResource.getBuyCount().toString())
```

2. 任何可能为null的地方都要判空

3. 所有的实体类都最好加上toString()方法

4. 执行SQL报错可以贴到idb中执行一下看看

5. 要看懂报错的信息，near -50,50其实已经告诉我哪里有问题了

6. 复制别人的SQL要看一下假如把它拼出来了是不是有语法问题

7. 和其他表做join的时候要注意到一对多的关系

8. 左表和右表join的时候如果字段数据类型不一样会出错，所以多个join中不能只关注自己的表，要所有表都执行看看