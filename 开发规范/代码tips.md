1. 使用decimal，如果是将double类型创给构造函数，要转换成String类型

```java
new BigDecimal(liveRoomResource.getBuyCount().toString())
```


2. 任何可能为null的地方都要判空


3. 所有的实体类都最好加上toString()方法

