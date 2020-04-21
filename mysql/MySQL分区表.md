## 参考博文
[mysql表分区详解](https://www.jianshu.com/p/1cdd3e3c5b3c)
[MySQL 分区表](https://my.oschina.net/jasonultimate/blog/548745)
[MySQL分区表使用方法](https://www.cnblogs.com/huchong/p/10231719.html)
[MySQL · 最佳实践 · 分区表基本类型](http://mysql.taobao.org/monthly/2017/11/09/)


[TOC]


## 1. 什么是分区表
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

通过show plugins命令查看是否开启了分区功能
```SQL
mysql> show plugins;
+----------------------------+----------+--------------------+-----------------+---------+
| Name                       | Status   | Type               | Library         | License |
+----------------------------+----------+--------------------+-----------------+---------+
| keyring_file               | ACTIVE   | KEYRING            | keyring_file.so | GPL     |
| binlog                     | ACTIVE   | STORAGE ENGINE     | NULL            | GPL     |
| mysql_native_password      | ACTIVE   | AUTHENTICATION     | NULL            | GPL     |
| sha256_password            | ACTIVE   | AUTHENTICATION     | NULL            | GPL     |
| CSV                        | ACTIVE   | STORAGE ENGINE     | NULL            | GPL     |
| MEMORY                     | ACTIVE   | STORAGE ENGINE     | NULL            | GPL     |
| InnoDB                     | ACTIVE   | STORAGE ENGINE     | NULL            | GPL     |
| INNODB_TRX                 | ACTIVE   | INFORMATION SCHEMA | NULL            | GPL     |
| INNODB_LOCKS               | ACTIVE   | INFORMATION SCHEMA | NULL            | GPL     |
| INNODB_LOCK_WAITS          | ACTIVE   | INFORMATION SCHEMA | NULL            | GPL     |
| INNODB_CMP                 | ACTIVE   | INFORMATION SCHEMA | NULL            | GPL     |
| INNODB_CMP_RESET           | ACTIVE   | INFORMATION SCHEMA | NULL            | GPL     |
| INNODB_CMPMEM              | ACTIVE   | INFORMATION SCHEMA | NULL            | GPL     |
| INNODB_CMPMEM_RESET        | ACTIVE   | INFORMATION SCHEMA | NULL            | GPL     |
| INNODB_CMP_PER_INDEX       | ACTIVE   | INFORMATION SCHEMA | NULL            | GPL     |
| INNODB_CMP_PER_INDEX_RESET | ACTIVE   | INFORMATION SCHEMA | NULL            | GPL     |
| INNODB_BUFFER_PAGE         | ACTIVE   | INFORMATION SCHEMA | NULL            | GPL     |
| INNODB_BUFFER_PAGE_LRU     | ACTIVE   | INFORMATION SCHEMA | NULL            | GPL     |
| INNODB_BUFFER_POOL_STATS   | ACTIVE   | INFORMATION SCHEMA | NULL            | GPL     |
| INNODB_TEMP_TABLE_INFO     | ACTIVE   | INFORMATION SCHEMA | NULL            | GPL     |
| INNODB_METRICS             | ACTIVE   | INFORMATION SCHEMA | NULL            | GPL     |
| INNODB_FT_DEFAULT_STOPWORD | ACTIVE   | INFORMATION SCHEMA | NULL            | GPL     |
| INNODB_FT_DELETED          | ACTIVE   | INFORMATION SCHEMA | NULL            | GPL     |
| INNODB_FT_BEING_DELETED    | ACTIVE   | INFORMATION SCHEMA | NULL            | GPL     |
| INNODB_FT_CONFIG           | ACTIVE   | INFORMATION SCHEMA | NULL            | GPL     |
| INNODB_FT_INDEX_CACHE      | ACTIVE   | INFORMATION SCHEMA | NULL            | GPL     |
| INNODB_FT_INDEX_TABLE      | ACTIVE   | INFORMATION SCHEMA | NULL            | GPL     |
| INNODB_SYS_TABLES          | ACTIVE   | INFORMATION SCHEMA | NULL            | GPL     |
| INNODB_SYS_TABLESTATS      | ACTIVE   | INFORMATION SCHEMA | NULL            | GPL     |
| INNODB_SYS_INDEXES         | ACTIVE   | INFORMATION SCHEMA | NULL            | GPL     |
| INNODB_SYS_COLUMNS         | ACTIVE   | INFORMATION SCHEMA | NULL            | GPL     |
| INNODB_SYS_FIELDS          | ACTIVE   | INFORMATION SCHEMA | NULL            | GPL     |
| INNODB_SYS_FOREIGN         | ACTIVE   | INFORMATION SCHEMA | NULL            | GPL     |
| INNODB_SYS_FOREIGN_COLS    | ACTIVE   | INFORMATION SCHEMA | NULL            | GPL     |
| INNODB_SYS_TABLESPACES     | ACTIVE   | INFORMATION SCHEMA | NULL            | GPL     |
| INNODB_SYS_DATAFILES       | ACTIVE   | INFORMATION SCHEMA | NULL            | GPL     |
| INNODB_SYS_VIRTUAL         | ACTIVE   | INFORMATION SCHEMA | NULL            | GPL     |
| MyISAM                     | ACTIVE   | STORAGE ENGINE     | NULL            | GPL     |
| MRG_MYISAM                 | ACTIVE   | STORAGE ENGINE     | NULL            | GPL     |
| PERFORMANCE_SCHEMA         | ACTIVE   | STORAGE ENGINE     | NULL            | GPL     |
| ARCHIVE                    | ACTIVE   | STORAGE ENGINE     | NULL            | GPL     |
| BLACKHOLE                  | ACTIVE   | STORAGE ENGINE     | NULL            | GPL     |
| FEDERATED                  | DISABLED | STORAGE ENGINE     | NULL            | GPL     |
| partition                  | ACTIVE   | STORAGE ENGINE     | NULL            | GPL     |
| ngram                      | ACTIVE   | FTPARSER           | NULL            | GPL     |
+----------------------------+----------+--------------------+-----------------+---------+
45 rows in set (0.00 sec)
```

# 2. 分区表原理
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

如果表中存在主键或唯一索引时，分区列必须是唯一索引的一个组成部分，不需要整个唯一索引列都是分区列
```SQL
mysql> create table m1(
    col1 int not null,
    col2 date not null,
    col3 int not null,
    col4 int not null,
    unique key(col1,col2)
    )partition by hash(col3) partitions 4;
ERROR 1503 (HY000): A PRIMARY KEY must include all columns in the table‘s partitioning function;

mysql> create table m2(
    col1 int not null,
    col2 date not null,
    col3 int not null,
    col4 int not null,
    unique key(col1,col2,col3,col4)
    )partition by hash(col3) partitions 4;
Query OK, 0 rows affected (0.02 sec)
```

当建表时没有指定主键和唯一索引时，可以指定任何一个列为分区列
```SQL
CREATE TABLE t1 (
    col1 INT   NULL,￼
    col2 DATE NULL,￼
    col3 INT NULL,￼
    col4 INT NULL￼
    )engine=innodb￼
    PARTITION BY HASH(col3)
    PARTITIONS 4;￼
    
CREATE TABLE t1 (
    col1 INT   NULL,￼
    col2 DATE NULL,￼
    col3 INT NULL,
    col4 INT NULL,
    key (col4)￼
    )engine=innodb
    PARTITION BY HASH(col3)
    PARTITIONS 4;
```

查看表在磁盘上的物理文件，启用分区之后，表不再由一个ibd文件组成，而是由建立分区时的各个分区ibd文件组成
```SQL
chenpeipeideMacBook-Pro:~ chenpeipei$ sudo chmod 777 /usr/local/mysql/data/spring@002dclass/ 
chenpeipeideMacBook-Pro:~ chenpeipei$ cd /usr/local/mysql/data/spring@002dclass/
chenpeipeideMacBook-Pro:spring@002dclass chenpeipei$ ls -lh m2*
-rw-r-----  1 _mysql  _mysql    96K  4 20 12:57 m2#P#p0.ibd
-rw-r-----  1 _mysql  _mysql    96K  4 20 12:57 m2#P#p1.ibd
-rw-r-----  1 _mysql  _mysql    96K  4 20 12:57 m2#P#p2.ibd
-rw-r-----  1 _mysql  _mysql    96K  4 20 12:57 m2#P#p3.ibd
-rw-r-----  1 _mysql  _mysql   8.4K  4 20 12:57 m2.frm

//不修改文件夹权限
//改变执行MySQL客户端的身份，指定Linux执行MySQL的身份是_mysql，创建文件时都是以这个用户身份执行的
chenpeipeideMacBook-Pro:~ chenpeipei$ sudo -u _mysql /usr/local/mysql/bin/mysql -uroot -p
Enter password: 
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 6
Server version: 5.7.23 MySQL Community Server (GPL)

Copyright (c) 2000, 2018, Oracle and/or its affiliates. All rights reserved.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

mysql> system ls -lh /usr/local/mysql/data/spring@002dclass/m2*
-rw-r-----  1 _mysql  _mysql    96K  4 20 12:57 /usr/local/mysql/data/spring@002dclass/m2#P#p0.ibd
-rw-r-----  1 _mysql  _mysql    96K  4 20 12:57 /usr/local/mysql/data/spring@002dclass/m2#P#p1.ibd
-rw-r-----  1 _mysql  _mysql    96K  4 20 12:57 /usr/local/mysql/data/spring@002dclass/m2#P#p2.ibd
-rw-r-----  1 _mysql  _mysql    96K  4 20 12:57 /usr/local/mysql/data/spring@002dclass/m2#P#p3.ibd
-rw-r-----  1 _mysql  _mysql   8.4K  4 20 12:57 /usr/local/mysql/data/spring@002dclass/m2.frm
```

可以通过查询information_schema架构下的PARTITIONS表来查看每个分区的具体信息
```SQL
//TABLE_ROWS列反映了每个分区中记录的数量
mysql> select * from information_schema.partitions where table_schema=database() and table_name='m2';
+---------------+--------------+------------+----------------+-------------------+----------------------------+-------------------------------+------------------+---------------------+----------------------+-------------------------+-----------------------+------------+----------------+-------------+-----------------+--------------+-----------+---------------------+-------------+------------+----------+-------------------+-----------+-----------------+
| TABLE_CATALOG | TABLE_SCHEMA | TABLE_NAME | PARTITION_NAME | SUBPARTITION_NAME | PARTITION_ORDINAL_POSITION | SUBPARTITION_ORDINAL_POSITION | PARTITION_METHOD | SUBPARTITION_METHOD | PARTITION_EXPRESSION | SUBPARTITION_EXPRESSION | PARTITION_DESCRIPTION | TABLE_ROWS | AVG_ROW_LENGTH | DATA_LENGTH | MAX_DATA_LENGTH | INDEX_LENGTH | DATA_FREE | CREATE_TIME         | UPDATE_TIME | CHECK_TIME | CHECKSUM | PARTITION_COMMENT | NODEGROUP | TABLESPACE_NAME |
+---------------+--------------+------------+----------------+-------------------+----------------------------+-------------------------------+------------------+---------------------+----------------------+-------------------------+-----------------------+------------+----------------+-------------+-----------------+--------------+-----------+---------------------+-------------+------------+----------+-------------------+-----------+-----------------+
| def           | spring-class | m2         | p0             | NULL              |                          1 |                          NULL | HASH             | NULL                | col3                 | NULL                    | NULL                  |          0 |              0 |       16384 |            NULL |            0 |         0 | 2020-04-20 12:57:13 | NULL        | NULL       |     NULL |                   | default   | NULL            |
| def           | spring-class | m2         | p1             | NULL              |                          2 |                          NULL | HASH             | NULL                | col3                 | NULL                    | NULL                  |          0 |              0 |       16384 |            NULL |            0 |         0 | 2020-04-20 12:57:13 | NULL        | NULL       |     NULL |                   | default   | NULL            |
| def           | spring-class | m2         | p2             | NULL              |                          3 |                          NULL | HASH             | NULL                | col3                 | NULL                    | NULL                  |          0 |              0 |       16384 |            NULL |            0 |         0 | 2020-04-20 12:57:13 | NULL        | NULL       |     NULL |                   | default   | NULL            |
| def           | spring-class | m2         | p3             | NULL              |                          4 |                          NULL | HASH             | NULL                | col3                 | NULL                    | NULL                  |          0 |              0 |       16384 |            NULL |            0 |         0 | 2020-04-20 12:57:13 | NULL        | NULL       |     NULL |                   | default   | NULL            |
+---------------+--------------+------------+----------------+-------------------+----------------------------+-------------------------------+------------------+---------------------+----------------------+-------------------------+-----------------------+------------+----------------+-------------+-----------------+--------------+-----------+---------------------+-------------+------------+----------+-------------------+-----------+-----------------+
4 rows in set (0.01 sec)
```

# 3. 分区表类型
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

# 4. 对分区表的操作
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

# 5. 子分区
子分区（subpartitioning）是在分区的基础上再进行分区，有时也称这种分区为复合分区（composite partitioning）。MySQL数据库允许在RANGE和LIST的分区上再进行HASH或KEY的子分区
```SQL
//subpartition每个子分区的数量必须相同
//subpartition子分区的名字必须是唯一的
//如果在一个分区表的任何分区上使用SUBPARTITION来明确定义任何子分区，那么所有分区都要定义相同数量的子分区
mysql> create table ts(
    -> a int,
    -> b date)
    -> partition by range(year(b))
    -> subpartition by hash(to_days(b))(
    -> partition p0 values less than (1990)(
    -> subpartition s0,
    -> subpartition s1
    -> ),
    -> partition p1 values less than (2000)(
    -> subpartition s2,
    -> subpartition s3
    -> ),
    -> partition p2 values less maxvalue(
    -> subpartition s4,
    -> subpartition s5
    -> )
    -> );
Query OK, 0 rows affected (0.02 sec)

//以下等价
mysql> create table ts(
    -> a int,
    -> b date)
    -> partition by range(year(b))
    -> subpartition by hash(to_days(b))
    -> subpartitions 2(
    -> partition p0 values less than (1990),
    -> partition p1 values less than (2000),
    -> partition p2 values less than maxvalue
    -> );
Query OK, 0 rows affected (0.02 sec)

mysql> system ls -lh /usr/local/mysql/data/spring@002dclass/pc*
-rw-r-----  1 _mysql  _mysql    96K  4 20 13:46 /usr/local/mysql/data/spring@002dclass/pc#P#p0#SP#p0sp0.ibd
-rw-r-----  1 _mysql  _mysql    96K  4 20 13:46 /usr/local/mysql/data/spring@002dclass/pc#P#p0#SP#p0sp1.ibd
-rw-r-----  1 _mysql  _mysql    96K  4 20 13:46 /usr/local/mysql/data/spring@002dclass/pc#P#p1#SP#p1sp0.ibd
-rw-r-----  1 _mysql  _mysql    96K  4 20 13:46 /usr/local/mysql/data/spring@002dclass/pc#P#p1#SP#p1sp1.ibd
-rw-r-----  1 _mysql  _mysql    96K  4 20 13:46 /usr/local/mysql/data/spring@002dclass/pc#P#p2#SP#p2sp0.ibd
-rw-r-----  1 _mysql  _mysql    96K  4 20 13:46 /usr/local/mysql/data/spring@002dclass/pc#P#p2#SP#p2sp1.ibd
-rw-r-----  1 _mysql  _mysql   8.4K  4 20 13:46 /usr/local/mysql/data/spring@002dclass/pc.frm
```