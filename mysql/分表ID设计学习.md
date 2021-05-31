# 分表ID设计学习

# 业务场景


在高并发场景下，当多个商家在同一时刻进行商品报名时，需要保证商品报名记录的id(juId)是全局唯一的。并且要对商品报名记录进行分表。


查询场景：

1. 根据id查
1. 根据sellerId查



# 分表路由原理
分表查询其实就是分桶（表），将id通过分桶（表）规则均匀的分布到某个桶（表）里，然后在此桶（表）里查询此ID。
满足此分桶规则的方法主要有 “hash”和 “取模运算”：
```
1、Long型的hash算法是高32位和低32位异或操作，将Long型转化为Int型，hash方法复杂程度高，再设计比较困难
2、取模运算，即 % 运算，实现方便，在递增id中可以均匀分桶，再设计比较简单
```

举个例子
market_item表分成32个表，分别对应表market_item_[0000-0031]，market_item表分别列为id，路由规则为#id % 32#。
如果查询id=33的记录,id % 32=1，那么此次查询就在market_item_0001表中




# 分表设计方案
### 方案一 使用id=sequenceId分表
使id=sequenceId，使用id分表


- 缺点

不能满足根据sellerId查


### 方案二 使用sellerId分表
使用sellerId分表，使用ID查询的时候通用需要加sellerID


- 优点

由于sellerId作为最基本字段，很容易获取，因此此方案基本满足两种索引场景


- 缺点

在只知道id的场景下将不可用，或者需要上下文获取sellerID，增加了业务方实现成本




### 方案三 创建两个分表，分别满足两种索引场景
创建两个分表，分别以id和sellerId分表，用精卫任务做数据同步


- 优点

完全满足两种索引场景


- 缺点

1. 数据同步有延迟
2. 数据量冗余一份
3. 需要按照索引场景路由到不同表，增加系统复杂程度


### 方案四 使用id=组合id分表
使id=组合id，使用id分表

组合id必须满足使用id或sellerId进行路由能得到相同的结果，来保证可以使用id或sellerId查询


组合id需要满足两个条件：
```
1、使得id和sellerId按照规则转换后值相等
2、使得id全局唯一
```


最终设计的组合ID组装方案为：必须分表成512张表
```
id = sequenceId * 1000 + sellerId % 512
```


在表中添加indexId字段，使得用id和sellerId进行某种计算后能得到同一个indexId
```
indexId = id % 1000 = sellerId % 512
```


当根据id查询时，根据id % 1000求出index，然后根据index取模得到分表
当根据sellerId查询时，根据sellerId % 512求出index，然后根据index取模得到分表


# 组合ID验证
```
id = sequenceId * 1000 + sellerId % 512
```


### 唯一性
两个组合id(n1,n2)相同的唯一条件是
```
n1.sequenceId == n2.sequenceId && n1.sellerId == n2.sellerId
```


由于sequenceId是通过sequence生成的，可以保证全局唯一性，所以n1不可能等于n2，故组合Id全局唯一


### 局限性
通过组合ID的计算方式可知，id的值是sequence*1000，那么对于id来说可使用的范围就变小了

如果sequence > Long.MAX_VALUE/1000 = 9223372036854775，组合ID即为负数，组合ID会失效！
不过临界数字足够大，足以支撑几年的使用
