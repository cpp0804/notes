##### 参考博文
[mysql表分区详解](https://www.jianshu.com/p/1cdd3e3c5b3c)
[MySQL 分区表](https://my.oschina.net/jasonultimate/blog/548745)
[MySQL分区表使用方法](https://www.cnblogs.com/huchong/p/10231719.html)
[MySQL · 最佳实践 · 分区表基本类型](http://mysql.taobao.org/monthly/2017/11/09/)


[TOC]

## 1.1 分区表
### 1.1.1 什么是分区表
如果一张表非常大，如商品表，那么查询这张表就会很慢。可以将这张表在物理存储上由一个文件分成三个文件，那么在查找时只要知道去哪个文件中找就行了，不用对所有数据进行查找。(优化器会根据分区定义过滤不包含目标数据的分区)

分区表就是对用户来说在逻辑上是一个表，但在底层物理上会根据条件分成多个表存储。

如某用户表的记录超过了600万条，那么就可以根据入库日期将表分区，也可以根据所在地将表分区。当然也可根据其他的条件分区。

一个表最多有1024个分区，分区表不能使用外键。

MySQL只有在使用分区表达式的列本身进行比较时才能过滤分区，而不能根据表达式，即使表达式就是分区表达式。就和索引一样，必须使用独立的列。

以下查询不能使用year(day)过滤分区。
```SQL
explain partitions select * from sales where year(day)=2010;

id:1
select_type:simple
table:sales
patitions:p_2010,p_2011,p_2012
```
修改成如下:
```SQL
explain partitions select * from sales where day between '2010-01-01' and '2010-12-31';

id:1
select_type:simple
table:sales
patitions:p_2010
```

### 1.1.2 分区表原理
分区表底层由多个物理表组成，这些物理表在实现中由句柄对象表示。对分区表的请求都会通过句柄对象转化成对存储引擎的调用。

分区表的索引是在各个底层表加上相同的索引。存储引擎管理底层表的方式和普通表一样，无需区分。

对分区表的操作如下：
>>1. select
当查询分区表时，分区层先打开并锁定所有底层表，优化器会根据条件过滤部分分区，然后再请求存储引擎访问各个分区的数据。
>>2. insert
当向分区表插入一条记录时，分区层先打开并锁定所有底层表，然后确定将数据插入哪个分区，最后将记录写入对应的底层表
>>3. delete
当向分区表删除一条记录时，分区层先打开并锁定所有底层表，然后确定删除哪个分区的数据，最后删除对应的底层表数据
>>4. update
当向分区表更新一条记录时，分区层先打开并锁定所有底层表，然后确定要更新的数据位于哪个分区，然后取出并更新。再确定新数据应放入哪个分区，再将数据放入对应的底层表，最后将旧数据从所在的底层表中删除。

### 1.1.3 分区表类型
#### range分区
根据分区表达式的范围进行分区，每个分区存储某个范围的数据。

分区表达式可以是列，也可以是包含列的表达式。表达式返回的值必须是一个明确的整数，但不能是一个常数。

分区要连续且不能重叠，且要从低到高顺序定义。

如果插入数据的分区值没有对应的分区将会报错，如果插入的是null将被当做最小值处理。

```SQL
partition by range(exp)( //exp可以为列名或者表达式，比如to_date(created_date)
    partition p0 values less than(num)
)

create table employees (
    id int not null,
    fname varchar(30),
    lname varchar(30),
    hired date not null default '1970-01-01',
    separated date not null default '9999-12-31',
    job_code int not null,
    store_id int not null
) partition by range (store_id) (
    partition p0 values less than (6), //存储1-5
    partition p1 values less than (11), //存储6-10
    partition p2 values less than (16),
    partition p3 values less than (21)，
    partition p3 values less than maxvalue
);
```
#### list分区
建立离散值集合划分分区。要匹配的值必须要在离散值集合中找到，离散值必须是整数

如果有分区列null值，必须出现在离散值集合中
```SQL
create table employees (
    id int not null,
    fname varchar(30),
    lname varchar(30),
    hired date not null default '1970-01-01',
    separated date not null default '9999-12-31',
    job_code int not null,
    store_id int not null
) partition by list(store_id)
    partition pNorth values in (3,5,6,9,17),
    partition pEast values in (1,2,10,11,19,20),
    partition pWest values in (4,12,13,14,18),
    partition pCentral values in (7,8,15,16)
)；
```
如果要删除pNorth的记录，那么可以直接使用alter table drop partition pNorth就可以删除所有相关的记录

#### columns分区
有range columns分区和 list columns分区，可以支持整数，日期时间，字符串三大数据类型的分区以及支持多列分区
```SQL  
partition by range columns(order date) 
```

#### hash分区
主要用于分散热点读，使数据在定义好的分区中均匀分布，分区值只能是整数。

null值被当作0处理

如果不分区，文件存储如下：
```
customer_login_log.frm    # 存储表原数据信息
customer_login_log.ibd    # Innodb数据文件
```

如果分区了，将会有多个ibd文件：
```
customer_login_log.frm    
customer_login_log#P#p0.ibd
customer_login_log#P#p1.ibd
customer_login_log#P#p2.ibd
customer_login_log#P#p3.ibd
```

1. 常规hash分区：使用取模算法

partition 4将分成4个分区，将对store_id进行mod(store_id,4)运算决定放在哪个分区。例如store_id = 234的记录，MOD(234,4)=2,所以会被存储在第二个分区。
```SQL
partition by hash(store_id) partitions 4;
//如果没有包括一个“PARTITIONS num”子句，那么分区的数量将默认为1
```
常规分区适合分区不变动的场景，但如果增加分区，那么取模之后得到的分区会不一样

2. 线性hash分区：使用线性的2的幂的运算法则
```SQL
partition by LINER hash(store_id) partitions 4;
//如果没有包括一个“PARTITIONS num”子句，那么分区的数量将默认为1。
```
在分区的维护上(添加、删除、合并、拆分)更加方便，但与常规hash比，数据的分布没有那么均匀

#### key分区
类似Hash分区，Hash分区允许使用用户自定义的表达式，但Key分区不允许使用用户自定义的表达式。Hash仅支持整数分区，而Key分区支持除了Blob和text的其他类型的列作为分区键

null值被当作0处理

```SQL
//exp可以为空，如果为空，则默认使用主键作为分区键，没有主键的时候，会选择非空惟一键作为分区键
partition by key(exp) partitions 4;//exp是零个或多个字段名的列表
```

### 1.1.4 对分区表的操作
```SQL
1. 增加分区
//range分区
alter table table_name add partition (partition p0 values less than(exp))

//list分区
alter table table_name add partition (partition p0 values in(exp))

//hash分区、key分区
//8指的是新增8个分区
alter table table_name add partition partitions 8;


2. 删除分区：删除了分区，同时也将删除该分区中的所有数据。同时，如果删除了分区导致分区不能覆盖所有值，那么插入数据的时候会报错。
//range分区、list分区
//p0为要删除的分区名称
alter table table_name drop partition p0; 

//hash分区、key分区
//将分区减到2个
alter table table_name coalesce partition 2; //将分区缩减到2个
```