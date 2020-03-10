[TOC]

# 第一章 MYSQL架构与历史
## 1.1 MySQL逻辑架构

#### 1. 客户端
#### 2. 核心服务器层

所有和具体存储引擎无关的功能都在这一层实现：存储过程、触发器、函数等，这一层屏蔽不同存储引擎之间的差异

(1) 连接/线程处理：

每个客户端连接都在MYSQL服务器进程中有一个线程(服务器会缓存线程分配给客户端)，这个客户端所有的query都在这个线程中执行(解析-优化-执行)

(2) 解析器

解析器会对query进行解析，并创建解析树

(3) 优化器

优化器对query进行优化(重写查询、决定表的读取顺序、选择合适的索引)

(4) 查询缓存

对于select语句在解析之前，MYSQL会先查询缓存中有没有对应的语句，有的话直接返回缓存。
#### 3. 存储引擎

存储引擎定义了数据怎么存储和提取，包含了十几个底层函数。MYSQL服务器通过存储引擎API访问存储引擎，这些API屏蔽存储引擎之间的差异。
## 1.2 并发控制
### 1.2.1 读写锁
#### 共享锁(读锁、shared lock)
#### 排它锁(写锁 exclusive lock)

### 1.2.2 锁粒度
锁的开销和读写安全之间的平衡

#### 表锁(table lock)
MYSQL服务器为ALTER TABLE语句添加表锁
### 行级锁(row lock)
由存储引擎自己实现


##### for update(排它锁)
将数据库中的某行添加行锁(独占锁)，当某个事务使用for update添加了锁，那么其他事务只可以读，不可以更新这行也不可以加任何锁，直到持有锁的事务commit

```
select * from tb_test where id = 1 for update;
```
##### lock in share mode(共享锁)
加了lock in share mode锁的事务，其他事务可以读或加共享锁，但不可以更新或加排它锁

可能会造成死锁

```
select * from tb_test where id = 1 lock in share mode;
```

## 1.3 事务
由存储引擎实现
### 1.3.1 事务特性
1. 原子性(atomicicy)

一个事务中的操作要么都成功，要么都失败

2. 一致性(consistency)

数据库总是从一个一致性状态转移到另一个一致性状态，如果一个事务失败了，他的修改不会提交到数据库

3. 隔离性(isolation)

一个事务在提交之前，他所做的更改不会被其他事务看见

4. 持久性(durability)

一个事务提交了更改，如果数据库崩溃了，更改也不会丢失

### 1.3.2 隔离级别
在存储引擎层面实现的
1. READ UNCOMMITTED(未提交读)

指的是事务能看见其他事务还未提交的修改，容易导致脏读
2. READ COMMITTED(提交读)

指的是事务只能看见其他事务已经提交的修改，满足了事务隔离性的定义，容易导致不可重复读(一个事务两次查询读到的数据不一致)
3. REPEATABLE READ(可重复读)

一个事务两次查询的结果一样，InnoDB的默认隔离级别
4. SERIALIZABLE(可串行化)

强制使所有事务串行执行，在读取的每一行上加锁，解决幻读的问题(事务A在读，事务B插入一行，事务A再次读发现多了一行)

ANSI SQL隔离级别：
隔离级别|脏读可能性|不可重复读可能性|幻读可能性|加锁读|
---|---|---|---|---|
未提交读|yes|yes|yes|no|
提交读|no|yes|yes|no
可重复读|no|no|yes|no
可串行化|no|no|no|yes

### 1.3.3 死锁

两个或多个资源同时占有资源，并且请求对方的资源。

由存储引擎自己决定如何处理：死锁检测和死锁超时机制


### 1.3.4 事务日志
当修改数据时，经历以下过程(预写式日志)：
1. 修改缓存中的数据
2. 将修改行为记录到硬盘中的事务日志，事务日志的写入采用追加写的方式
3. 硬盘中的数据根据事务日志慢慢的修改

即使在执行第3步前，系统崩溃了，重新启动后还是能根据第2步中的事务日志继续修改

### 1.3.5 MYSQL中的事务
#### 自动提交
当autocommit=1 每个查询都被当做一个事务执行。但对MyISAM这种非事务型表不会有影响，对InnoDB这种事务型数据库才有作用。

当autocommit=0 所有查询都在一个事务中执行，直到显示执行commit或者rollback，才开启下一个事务。

对于alter table 、lock table语句，会强制执行事务的提交操作。


```
mysql> show variables like 'autocommit';
//1=ON;0=OFF;
mysql> set autocommit = 1;
```
```
//改变数据库的隔离级别，session只改变当前会话的隔离级别
mysql> set (session) transaction isolation level read committed;
```

#### 两阶段锁定协议
一个事务中分为加锁阶段和解锁阶段：加锁阶段能加锁、操作数据，但不能解锁；解锁阶段能解锁、操作数据，但不能加锁。

InnoDB实现两阶段锁定协议：在事务开始执行过程中能随意加锁，当事务commit、rollback后一次性释放所有锁。

- 性能分析
```
方案1:
begin;
// 扣减库存
update t_inventory set count=count-5 where id=${id} and count >= 5;
// 锁住用户账户表
select * from t_user_account where user_id=123 for update;
// 插入订单记录
insert into t_trans;
commit;
```
```
方案2:
begin;
// 锁住用户账户表
select * from t_user_account where user_id=123 for update;
// 插入订单记录
insert into t_trans;
// 扣减库存
update t_inventory set count=count-5 where id=${id} and count >= 5;
commit;
```
两者锁库存时间都为update到commit，但是方案1的时间比方案2更长，并发性更差。因此越热点的记录应放到事务的后面

---
```
方案1：
 begin:
 int count = select count from t_inventory for update;
 if count >= 5:
 	update t_inventory set count=count-5 where id =123
 	commit 
 else
 	rollback
```
```
方案2：
 begin:
 	int rows = update t_inventory set count=count-5 where id =123 and count >=5
	if rows > 0:
		commit;
	ele 
		rollback;
```
方案1在select for update就加锁，经历了update和commit才释放；方案2在update加锁，经历commit就释放，性能更好



## 1.4 多版本并发控制(MVCC)
保存数据库某时间的快照，一个事务从开始到结束读的内容都是一样的。做到不加锁也能处理读写冲突。行锁的变种。

通过3个隐式字段，undo日志，read view实现。
### 1.4.1 两种读
##### 1. 当前读
类似select in share mode(共享锁), select for update、update、insert、delete(排它锁)等都是当前读，读的都是最新的数据，读取时不允许其他事务更改数据。

##### 2. 快照读
类似普通select，在读取时不加锁。读到的不一定是最新数据，可能是历史数据。MVCC就是解决快照读的问题。

下面介绍InnoDB简化的MVCC实现：
1. 在每行记录后添加两个隐藏列，一列记录行的创建时间，一列记录行的删除时间。时间指的是系统版本号
2. 每开始一个新事务，系统版本号都加1，事务开始时的系统版本号作为对应事务的版本号。
3. 只在REPEATABLE READ和READ COMMITTED两个级别实现

- select

根据两个条件查找记录：

(1) 只返回创建系统版本号小于等于当前事务版本号的(确保读取的行出现在事务以前或被该事务操作过)

(2) 只返回删除系统版本号为undefined或者大于当前事务版本号的(确读取的行在事务开始前没被删除)

- insert

将当前系统版本号作为创建时间

- delete

将当前系统版本号作为删除时间

- update

update其实是插入新的一行，将当前系统版本号作为新行的创建时间以及作为旧行的删除时间

### 1.4.2 三个隐式字段
(1)DB_TRX_ID

最近修改事务ID：最后一次修改该记录的事务ID

(2)DB_ROLL_PTR

回滚指针：指向rollback segment(undo log)中上一个版本的地址

(3)DB_ROW_ID

隐藏的自增ID：如果数据表没有主键，InnodDB自动以DB_ROW_ID作为聚簇索引

举例：

1. 首先事务1插入一条记录

操作事务ID|name|age|DB_ROW_ID|DB_TRX_ID|DB_ROLL_PTR
---|---|---|---|---|---|
1|jerry|24|1|1|null

2. 事务2将名字修改为tom：首先事务2修改这行时，数据库会对该行加排它锁。然后将这行复制到undo log中。拷贝完毕后，修改name为tom，将DB_TRX_ID修改为2，将DB_ROLL_PTR指向复制到undo log中的拷贝记录。最后提交记录，释放锁。

操作事务ID|name|age|DB_ROW_ID|DB_TRX_ID|DB_ROLL_PTR
---|---|---|---|---|---|
2|tom|24|1|2|0x124465

undo log：
位置|name|age|DB_ROW_ID|DB_TRX_ID|DB_ROLL_PTR
---|---|---|---|---|---|
0x124465|tom|24|1|1|null

3. 事务3将年龄改为30：首先事务3修改这行时，数据库会对该行加排它锁。然后将这行复制到undo log中，发现这行记录已经有log了，就将这条log插入原有log的前面作为链头。拷贝完毕后，修改age为30，将DB_TRX_ID修改为3，将DB_ROLL_PTR指向复制到undo log中的拷贝记录。最后提交记录，释放锁。

操作事务ID|name|age|DB_ROW_ID|DB_TRX_ID|DB_ROLL_PTR
---|---|---|---|---|---|
3|tom|30|1|3|0x124469

undo log：同一记录的undo log会形成一个版本链表，最新的记录在表头
位置|name|age|DB_ROW_ID|DB_TRX_ID|DB_ROLL_PTR
---|---|---|---|---|---|
0x124469|tom|24|1|2|0x124465

位置|name|age|DB_ROW_ID|DB_TRX_ID|DB_ROLL_PTR
---|---|---|---|---|---|
0x124465|tom|24|1|1|null

### 1.4.3 read view(读视图)
read view是当某个事务执行快照读的时候，会生成数据库的当前快照，维护当前活跃的事务。将根据read view来判断事务能读到哪个版本的数据(可能是最新的，也可能是undo log里的)。判断方法是拿read view中被访问记录的DB_TRX_ID和read view中其他事务的事务ID作比较，如果满足可见性条件，就可以访问。如果不满足，则拿记录的DB_ROLL_PTR取undo log中取，直到满足可见性条件。

![avatar](./pic/readview示意图.jpg)
ms
read view有四个字段：
```
1. m_ids：表示在生成ReadView时当前系统中活跃的读写事务的事务id列表。
2. min_trx_id：表示在生成ReadView时当前系统中活跃的读写事务中最小的事务id，也就是m_ids中的最小值。
3. max_trx_id：表示生成ReadView时系统中应该分配给下一个事务的id值。
4. creator_trx_id：表示生成该ReadView的事务的事务id。
```
1. 当要访问记录的DB_TRX_ID等于creator_trx_id，说明当前事务要访问被他最后一次修改过的记录，该版本记录可以被访问。
2. 当要访问记录的DB_TRX_ID小于min_trx_id，说明

## 1.5 MySQL的存储引擎
表的定义有MYSQL服务层统一处理，存储在和表同名的.frm文件中；数据和索引由存储引擎存储。
### 1.5.1 查看数据库信息的命令行
```
//查看有哪些数据库
mysql> show databases;
+--------------------+
| Database           |
+--------------------+
| information_schema |
| gym                |
| java_class         |
| mysql              |
| performance_schema |
| sand_simulation    |
| spring-class       |
| spring-mvc-class   |
| stock              |
| sys                |
+--------------------+
10 rows in set (0.00 sec)

//具体选择某个数据库查看
mysql> use spring-class;

//查看表
mysql>show tables;
+------------------------+
| Tables_in_spring-class |
+------------------------+
| account                |
| item                   |
| order_detail           |
| orders                 |
| t_user                 |
| user                   |
+------------------------+
6 rows in set (0.00 sec)

//查看account表的数据
mysql> select * from account
    -> ;
+----+----------+-------+
| id | username | money |
+----+----------+-------+
|  1 | jack     |   200 |
|  2 | mary     | 10900 |
+----+----------+-------+
2 rows in set (0.00 sec)

//查看表信息
mysql> show table status like 'account';
+---------+--------+---------+------------+------+----------------+-------------+-----------------+--------------+-----------+----------------+---------------------+-------------+------------+-------------+----------+----------------+---------+
| Name    | Engine | Version | Row_format | Rows | Avg_row_length | Data_length | Max_data_length | Index_length | Data_free | Auto_increment | Create_time         | Update_time | Check_time | Collation   | Checksum | Create_options | Comment |
+---------+--------+---------+------------+------+----------------+-------------+-----------------+--------------+-----------+----------------+---------------------+-------------+------------+-------------+----------+----------------+---------+
| account | InnoDB |      10 | Dynamic    |    2 |           8192 |       16384 |               0 |            0 |         0 |              3 | 2018-10-30 15:22:44 | NULL        | NULL       | utf8mb4_bin |     NULL |                |         |
+---------+--------+---------+------------+------+----------------+-------------+-----------------+--------------+-----------+----------------+---------------------+-------------+------------+-------------+----------+----------------+---------+
1 row in set (0.00 sec)

//查看表中的列
mysql> desc account;
mysql> show columns from account;
mysql> describe account;
+----------+-------------+------+-----+---------+----------------+
| Field    | Type        | Null | Key | Default | Extra          |
+----------+-------------+------+-----+---------+----------------+
| id       | int(11)     | NO   | PRI | NULL    | auto_increment |
| username | varchar(50) | YES  |     | NULL    |                |
| money    | int(255)    | YES  |     | NULL    |                |
+----------+-------------+------+-----+---------+----------------+
3 rows in set (0.00 sec)

//查看表的创建语句
mysql> show create table account;
+---------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| Table   | Create Table                                                                                                                                                                                                                                                   |
+---------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| account | CREATE TABLE `account` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL,
  `money` int(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin |
+---------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
1 row in set (0.00 sec)

//检查表的错误
mysql> check table account;
+----------------------+-------+----------+----------+
| Table                | Op    | Msg_type | Msg_text |
+----------------------+-------+----------+----------+
| spring-class.account | check | status   | OK       |
+----------------------+-------+----------+----------+
1 row in set (0.01 sec)

mysql> repair table account;
```

### 1.5.1 InnoDB VS MyISAM
比较|InnoDB|MyISAM
---|---|---|
类型|事务型|非事务型
存储|表空间:将每个表的数据和索引存放在单独文件中|数据文件.MYD;索引文件.MYI
锁|支持行级锁|不支持行级锁，对整张表加锁。读时加共享锁，写时加排它锁
事务|崩溃后可以安全恢复|崩溃后只能手动恢复

### 1.5.2 转换表的引擎
#### 1. alter table 
```
mysql> alter table mytable engin=InnoDB;
```
MYSQL会将数据从原表复制到新表，执行速度很慢，会丢失原引擎的特性

#### 2. 导入和导出
使用mysqldump工具将数据导出到文件，然后修改create table中引擎的选择，还要修改表名

#### 3. 创建和查询(create和select)
```
mysql> create table newtable like oldtable;
mysql> alter table newtable engine=InnoDB;
mysql> insert into newtable select * from oldtable;
```
如果数据量大可以分批处理
```
mysql> start transaction;
mysql> insert into newtable select * from oldtable where id betwenn x and y;
mysql> commit;
```

# 1.6 SQL语句加锁分析


# 第二章 MySQL基准测试
## 基准测试目的


## 基准测试策略


## 基准测试方法


## 基准测试指标
