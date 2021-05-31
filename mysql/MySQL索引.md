## 参考博文

[B树和B+树的插入、删除图文详解](https://www.cnblogs.com/nullzx/p/8729425.html)
[Mysql最左匹配原则](https://blog.csdn.net/sinat_41917109/article/details/88944290)
[MySQL 覆盖索引详解](https://juejin.im/post/5da5d1966fb9a04e252c94bf)
[MYSQL索引：对聚簇索引和非聚簇索引的认识](https://blog.csdn.net/alexdamiao/article/details/51934917)
[什么是索引下推](https://juejin.im/post/5deef343e51d455819022033)
[浅析MySQL中的Index Condition Pushdown (ICP 索引条件下推)和Multi-Range Read（MRR 索引多范围查找）查询优化](https://yq.aliyun.com/articles/259696)
[MYSQL索引条件下推的简单测试](https://cloud.tencent.com/developer/article/1068845)
[MySQL5.6之Index Condition Pushdown(ICP,索引条件下推)](http://mdba.cn/2014/01/21/index-condition-pushdownicp%E7%B4%A2%E5%BC%95%E6%9D%A1%E4%BB%B6%E4%B8%8B%E6%8E%A8/)
[MySQL索引类型](https://www.cnblogs.com/luyucheng/p/6289714.html)
[干货篇：一篇文章让你——《深入解析MySQL索引原理 》](https://zhuanlan.zhihu.com/p/70898706)⚠️
[MySQL——索引实现原理](https://www.lagou.com/lgeduarticle/66657.html)
[数据存储 1：数据库索引的原理及使用策略](https://crazyfzw.github.io/2018/07/18/RDBMS-INDEX/)
[探究InnoDB数据页内部行的存储方式](https://www.jianshu.com/p/aa67d757e591)⚠️
[B+树：MySQL数据库索引是如何实现的？](https://shouliang.github.io/2019/01/16/%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84%E4%B8%8E%E7%AE%97%E6%B3%95/48%20%7C%20B+%E6%A0%91%EF%BC%9AMySQL%E6%95%B0%E6%8D%AE%E5%BA%93%E7%B4%A2%E5%BC%95%E6%98%AF%E5%A6%82%E4%BD%95%E5%AE%9E%E7%8E%B0%E7%9A%84%EF%BC%9F/)
[深入理解了MySQL，你才能说熟悉数据库](https://zhuanlan.zhihu.com/p/66352669)


[TOC]
# 缓冲池、顺序读取与随机读取
## 缓冲池
读取数据时，基于磁盘的数据库会先判断磁盘中是否有数据，如果有就直接返回。如果没有就从磁盘读取数据页放入缓冲池中。
写数据时，先从磁盘读取数据页放入缓冲池中，在缓冲池对页进行修改，再异步写入磁盘中。

MySQL数据库的缓冲池是基于存储引擎的

##  顺序读取与随机读取
顺序读取（sequntial read）是指顺序地读取磁盘上的页。

随机读取（random read）是指访问的页不是连续的，需要磁盘的磁头不断移动

顺序读取指的是逻辑上的顺序，InnoDB使用区管理页，一个区64页。在读取数据库时，可以保证这64个页的读取是顺序的。区和区之间的页可能是顺序的，也可能不是顺序的


# 数据结构与算法
## 二分查找法
将记录按有序化（递增或递减）排列，查找过程中采用跳跃式方式查找，即先以有序数列的中点位置为比较对象，如果要找的元素值小于该中点元素，则将待查序列缩小为左半部分，否则为右半部分。通过一次比较，将查找区间缩小一半

在B+树索引中，B+树索引只能找到某条记录所在的页(索引树的遍历到叶子节点)，需再根据二分查找法来进一步找到记录所在页的具体位置

## 二叉查找树和平衡二叉树
在二叉查找树中，左子树的键值总是小于根的键值，右子树的键值总是大于根的键值，因此可以通过中序遍历得到键值的排序输出

平衡二叉树的定义如下：首先符合二叉查找树的定义，其次必须满足任何节点的两棵子树的高度最大差为1

## B+树
[B树和B+树](../数据结构和算法/B树和B+树.md)

# cardinality
如果某个字段的取值范围很广，几乎没有重复，就是高选择性的，那么此时使用B+树索引是最适合的

通过SHOW INDEX语句中的Cardinality列来观察索引是否是高选择性的。Cardinality是个采样值
Cardinality/n_rows_in_table应尽可能接近1这个索引才有高选择性

```SQL
mysql> show index from account;
+---------+------------+----------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+
| Table   | Non_unique | Key_name | Seq_in_index | Column_name | Collation | Cardinality | Sub_part | Packed | Null | Index_type | Comment | Index_comment |
+---------+------------+----------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+
| account |          0 | PRIMARY  |            1 | id          | A         |           2 |     NULL | NULL   |      | BTREE      |         |               |
+---------+------------+----------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+
1 row in set (0.01 sec)
```
# 1. 索引存储类型
在存储引擎层实现

存储引擎使用索引的方式：先在索引中找到匹配的索引记录，然后返回包含该索引值的数据行。

## 1.1 B+树索引
B+树索引可以分为[聚簇索引(InnoDB)和非聚簇索引(MyISAM)](#3-聚簇索引innodb和非聚簇索引myisam)

在数据库中，B+树的高度一般都在2～4层，这意味着查找某一键值最多只需要2到4次IO操作

##### 扫描
B+树索引避免了全表扫描，而是从索引根节点开始搜索，不断向下寻找(左指针指向值更小的节点，右指针指向值更大的节点)

##### 排序
对多个值进行排序的规则是依照create table语句中定义索引时列的顺序。

##### 可以使用B+树索引的查询类型
1. 全值匹配：和索引中所有列匹配

在用到所有索引列的情况下，where语句中索引列的顺序不影响
```SQL
select * from table_name where a = '1' and b = '2' and c = '3' 
select * from table_name where b = '2' and a = '1' and c = '3' 
select * from table_name where c = '3' and b = '2' and a = '1' 
```
2. 最左前缀匹配：针对索引key(a, b,c)，其实创建了(a)、(a,b)、(a,b,c)三个索引
```SQL
//都可以进行索引匹配
select * from table_name where a = '1' 
select * from table_name where a = '1' and b = '2'  
select * from table_name where a = '1' and b = '2' and c = '3'
```
```SQL
//会进行全表扫描
select * from table_name where  b = '2' 
select * from table_name where  c = '3'
select * from table_name where  b = '1' and c = '3'
```
```SQL
//只用到了a的索引
select * from table_name where a = '1' and c = '3' 
```
假如创建一个联合索引(a,b),他的索引树如图所示。a的值是有序的(1,1,2,2,3,3)，b的值是无序的(1,2,1,4,1,2)。只有在a相等的情况下，才会使用b排序。
![最左前缀匹配](./pic/MySQL索引_最左前缀匹配.png)
最左匹配原则在遇上范围查询(>、<、between、like)就会停止，后面的字段不使用索引。例如a>1 and b>2的情况下，a可以使用索引而b不可以使用，因为b是无序的。所以在优化查询时，如果范围查询的列值是有限的，可以用多个等于条件代替范围查询。

3. 列前缀匹配：针对第一个索引列：索引列如果是字符型的，比较顺序是先比较第一个字母，然后再比较第二个字母。
```SQL
select * from table_name where a like 'As%'; //前缀都是排好序的，走索引查询
select * from table_name where a like '%As'//全表查询
select * from table_name where a like '%As%'//全表查询
```
4. 范围值匹配：针对第一个索引列
```SQL
//b是无序的，只有a能范围匹配
select * from table_name where  a > 1 and a < 3 and b > 1;
```
5. 精确匹配某一列，范围匹配另一列：针对第一个索引列精确匹配，第二个索引列范围匹配
```SQL
//a=1的情况下，b是有序的
select * from table_name where  a = 1 and b > 3;
```
6. 排序
MySQL有两种方式可以生成有序的结果：
```
1. 通过排序操作 explain的type列是filesort
2. 按索引顺序扫描 explain的type列是index
```

- 通过排序操作过程

先根据查询条件获取结果集，然后在内存中对这个结果集进行排序。如果结果集数量特别大，还需要将结果集写入到多个文件里，然后单独对每个文件里的数据进行排序，然后在文件之间进行归并，排序完成后在进行 limit 操作
```sql
CREATE TABLE `person` (
  `id` int(11) NOT NULL,
  `city` varchar(16) NOT NULL,
  `name` varchar(16) NOT NULL,
  `age` int(11) NOT NULL,
  `addr` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `city` (`city`)
) ENGINE=InnoDB;

select city,name,age from person where city='武汉' order by name limit 100  ;

使用 explain 发现该语句会使用 city 索引，并且会有 filesort。我们分析下该语句的执行流程：
1.初始化 sortbuffer用来存放结果集
2.找到 city 索引，定位到 city 等于武汉的第一条记录，获取主键索引ID
3.根据 ID 去主键索引上找到对应记录，取出 city,name,age 字段放入 sortbuffer
4.在 city 索引取下一个 city 等于武汉的记录的主键ID
5.重复上面的步骤，直到所有 city 等于武汉的记录都放入 sortbuffer
6.对 sortbuffer 里的数据根据 name 做快速排序

如果查询的字段较多，则多个列如果都放入 sortbuffer 将占有大量内存空间
```


- 通过索引操作过程

如果 name 字段上有索引，由于索引在构建的时候已经是有序的了，所以就不需要进行额外的排序流程只需要在查询的时候查出指定的条数就可以了
```sql
alter table person add index city_user(city, name);

这样查询过程如下：
1.根据 city,name 联合索引定位到 city 等于武汉的第一条记录，获取主键索引ID
2.根据 ID 去主键索引上找到对应记录，取出 city,name,age 字段作为结果集返回
3.继续重复以上步骤直到 city 不等于武汉，或者条数大于 1000
```


使用索引排序要求非常苛刻，必须满足如下几个条件才能使用索引做排序
1、索引的列顺序和order by子句的顺序完全一致

3、如果是联表查询，order by中的字段全部在关联表的第一张表中

```SQL
//b+树索引的数据本身就是有序的，可以直接返回
select * from table_name order by a,b,c limit 10;
```
```SQL
//没有用到索引，order by后面的列也必须满足最左匹配原则
select * from table_name order by b,c,a limit 10;
```
```SQL
//用到(a)、(a,b)索引
select * from table_name order by a limit 10;
select * from table_name order by a,b limit 10;
```
```SQL
a=1的情况下，b,c可以作为索引
select * from table_name where a =1 order by b,c limit 10;
```

对于以下语句，只要有(col1,col2,col3)的索引就可以直接使用索引排序。
如果想达到根据主键id排序的效果，col3可以为gmt_create，达到类似的效果
```SQL
select * from table
where col1 = ? and col2 = ?
order by col3
```





## 1.2 哈希索引
哈希索引基于哈希表实现，只能用于精确匹配所有列。存储引擎对所有索引列计算哈希值存在索引中，值存储指向每个数据行的指针。

- 不能使用索引值避免读取行
- 索引数据不按照索引列的值排序，不可以用于排序
- 不支持部分索引列匹配查找
- 只支持等值查询，不支持范围查询
举例如下：
```SQL
create table testhash(
    fname varchar(50) not null,
    lname varchar(50) not null,
    key using hash(fname),
)engine=memory
```
fname|lname
---|---|
a|aa
b|bb
c|cc
d|dd

哈希值：
```
f('a')=2323
f('b')=7437
f('c')=8784
f('d')=2458
```
哈希索引数据结构：每个slot是顺序排序的，但是针对表记录不是
槽(slot)|值(value)
---|---|
2323|指向第1行的指针
2458|指向第4行的指针
7437|指向第2行的指针
8784|指向第3行的指针

首先计算c的哈希值，f('c')=8784，在索引中找到8784，然后根据记录的第三行指针寻找数据。
```SQL
select lname from testhash where fname='c';
```

## 1.3 空间数据索引(R-Tree)

## 1.4 全文索引

# 2. 索引优化策略
#### 2.1 独立的列
索引列不可以是表达式的一部分，也不可以作为函数参数
```MYSQL
//以下两种都不行
select actor_id from actor where actor+1=5;
select...where to_days(current_date)-to_days(date_col)<=10;
```
#### 2.2 前缀索引
针对值很长的索引可以索引开始的部分字符，要选择哪些选择性高的索引。选择性高指的是不重复的索引值和数据表中记录总数的比值。选择性越高，越能筛选不符合条件的记录，唯一索引的选择性就为1。
```
alter table city add key(city(7));
```
但是无法使用前缀索引做order by和group by，还有无法做覆盖扫描

#### 2.3 多列索引
为每个列创建单个索引并不是好策略

#### 2.4 选择合适的索引列顺序

# 3. 聚簇索引(InnoDB)和非聚簇索引(MyISAM)
```SQL
create table test(
    col1 int not null,
    col2 int not null,
    primary key(col1),
    key(col2)
)
```
![例子数据表](./pic/MySQL索引_例子数据表.jpeg)
## 3.1 聚簇索引
聚簇索引是为每张表的主键构造一颗B+树，树的叶子节点存放表的数据，每个节点也叫数据页，数据页之间的是双向链表，按照主键的大小顺序排序。

InnoDB中每个页的大小都为16KB，且不能更改

非叶节点又叫索引页，存放索引值和指向数据页的物理地址或下一个非叶节点的物理地址。

InnoDB主键索引(聚簇索引)如下图所示：每个叶子节点包含主键值、事务ID、DB_ROLL_PTR以及剩余的列值。数据文件本身就是索引文件
![主键索引](./pic/MySQL索引_InnoDB主键索引.jpeg)

主键索引的叶子节点会存储数据行，辅助索引只会存储主键值。
![主键索引](./pic/MySQL索引_主键索引.png)

InnoDB二级索引(辅助索引、非聚簇索引)如下图所示：每个叶子节点包含二级索引列值、主键值，InnoDB在更新时无需更新二级索引中的指针。如果索引的是二级索引，那么需要先从二级索引树中找到对应的主键，然后从主键的索引树中找出记录。
![二级索引](./pic/MySQL索引_InnoDB二级索引.jpeg)

二级索引包含唯一索引和普通索引

- 提高了I/O密集型应用的性能
- 插入速度依赖于插入顺序，==对于按主键顺序插入的记录效率很高==
- 更新索引列值的代价很高，因为索引列更新了会强制移动到新的位置
- 插入新行或者主键被更新可能导致页分裂问题，页分裂占用磁盘空间更大

避免使用uuid作为主键：
1. 写入的目标也可能已经刷回磁盘并从缓存中删除，需要频繁的从磁盘加载目标页到内存
2. uuid是乱序的，可能导致大量的页分裂，或许会造成每个页数据都很稀疏的现象

```SQL
CREATE TABLE t(￼
a INT NOT NULL ,￼
b VARCHAR(8000),
c INT NOT NULL,
PRIMARY KEY (a),￼
KEY idx_c (c)
)ENGINE=INNODB;￼

//repeate构造一个有7000个a的字符串
INSERT INTO t SELECT 1,REPEAT('a',7000),-1;￼
INSERT INTO t SELECT 2,REPEAT('a',7000),-2;
INSERT INTO t SELECT 3,REPEAT('a',7000),-3;￼
INSERT INTO t SELECT 4,REPEAT('a',7000),-4;
```
![聚集索引与辅助索引之间的联系](./pic/MySQL索引_聚集索引与辅助索引之间的联系.jpeg)


## 3.2 非聚簇索引
MyISAM所有的行数据都存放于MYD文件中,索引数据存放于MYI文件中。

索引页的大小默认为1KB

非聚簇索引的叶子节点存放主键值和存放数据的物理地址指针。

MyISAM主键索引(非聚簇索引)如下图所示：叶子节点存储主键值和指向记录的指针
![非聚簇索引](./pic/MySQL索引_MyISAM非聚簇索引.jpeg)

MyISAM的二级索引的B+树和主键索引相同，叶子节点中存储二级索引列值和指向记录的指针

InnoDB的索引B+树 VS MyISAM的索引B+树
![InnoDB的索引B+树 VS MyISAM的索引B+树](./pic/MySQL索引_聚簇VS非聚簇.jpeg)

## 3.3 覆盖索引
覆盖索引是指一个非主键索引包含查询和条件需要的字段，只能使用B+树实现。例如当有一个索引(a,b),并且只访问这两列时，就是一个覆盖索引

使用覆盖索引的好处是辅助索引不包含整行记录的所有信息，故其大小要远小于聚集索引，因此可以减少大量的IO操作

当发起一个覆盖索引的请求时，explain的extra列会显示"using index"，key中代表的就是覆盖索引。

例子：
```SQL
SELECT age FROM student WHERE name = '小李'；
```
![覆盖索引](./pic/MySQL索引_覆盖索引.jpg)

再比如对以下语句：
如果我们在建立 name 字段的索引的时候，不是使用单一索引，而是使用联合索引（name，sex）这样的话再执行这个查询语句根据辅助索引查询到的结果就可以获取当前语句的完整数据
```sql
select id,name,sex from user where name ='zhangsan';
```

## 3.4 使用索引扫描做排序
- order by的顺序和索引顺序一致(但如果第一列指定成常量条件，剩余的索引列仍可以做排序)
- where中的列和order by中的列的组合需要满足最左匹配原则

# 4. Multi-Range Read
MRR优化的目的就是减少磁盘的随机访问，并且将随机访问转化为较为顺序的数据访问

MRR优化适用于range、ref和eq_ref类型的查询
```
range：有限制的索引范围扫描

ref：索引查找，返回匹配某个值的多行。适用于非唯一索引或唯一索引的非唯一前缀

eq_ref：索引查找，只返回一条符合记录的行。适用于唯一索引
```

MRR的工作方式：
1. 通过where筛选出辅助索引后，将辅助索引放入缓存中，此时数据排序是根据辅助索引键值排的
2. 将缓存中的数据根据主键索引排序
3. 根据主键索引顺序访问实际的数据文件

MRR还可以将某些范围查询拆分为键值对，以此来完成批量的数据查询。这样做的好处是可以在拆分过程中，直接过滤一些不符合查询条件的数据：
1. 如果没有MRR，查询的类型是range。先根据key_part1 >= 1000 AND key_part1 < 2000￼查出所有符合条件的数据，在取出行后再根据key_part2 = 10000条件过滤。但是这样会导致大量无用的数据行被取出
2. 如果有了MRR，会先拆分查询条件，然后再查询数据。优化器会将查询条件拆分为（1000，10000），（1001，10000），（1002，10000），...，（1999，10000），最后再根据这些拆分出的条件进行数据查询
```SQL
//t有（key_part1，key_part2）的联合索引
SELECT ＊ FROM t￼
WHERE key_part1 >= 1000 AND key_part1 < 2000￼
AND key_part2 = 10000;
```



# 5. 索引条件下推(ICP)
当不使用ICP时，存储引擎会根据索引检索出数据，然后将数据返回给MYSQL服务器，由服务器根据where条件对数据进行过滤。当使用ICP时，如果条件中存在索引列，服务器将索引列的判断条件也交给存储引擎，存储引擎根据索引列条件过滤不满足的数据，然后交给服务器。这可以减少存储引擎和磁盘间的I/O和存储引擎和服务器间的数据传输量。

只有在inner join中可以直接使用

ICP优化支持range、ref、eq_ref和ref_or_null类型的查询，当前支持MyISAM和InnoDB存储引擎

使用场景：
1. InndoDB的二级索引，因为聚簇索引直接会把数据也加载到内存。二级索引会先去找聚簇索引，然后加载数据。如果二级索引不符合条件，就不用加载对应的聚簇索引和数据到内存了。
2. 需要范围查找的情况
3. 二级索引必须是组合索引，并且满足最左匹配原则，当前面的索引列进行范围索引查找，剩余的索引列不能用于索引查找后，经过ICP剩余的索引列将用来当条件过滤查找出来的数据。

### 不使用ICP的过程
explain的extra会显示“using where”
①服务器发起读取数据的命令，通过handle接口调取索引读或者全表读(此处将使用索引读)
②调用索引读，存储引擎将读取索引树
③在索引树上使用满足最左匹配原则的二级索引列查找叶子数据(不能被索引的列将不被使用)
④找到叶子数据后，根据数据中的主键去磁盘表中加载数据到内存
⑤⑥存储引擎将数据返回给服务器
⑦⑧服务器收到数据后，进行where条件过滤
![不使用ICP](./pic/MySQL索引_noneICP.jpg)


### 使用ICP的过程
explain的extra会显示“using index condition”
```SQL
set optimizer_switch='index_condition_pushdown=off/on'
```

![使用ICP](./pic/MySQL索引_ICP.jpg)
①②同上
③对查出来的数据进行条件过滤，将不能被利用的索引列当条件过滤数据，不满足条件的将不去磁盘加载数据，因此减少了I/O
④⑤⑥同上
⑦⑧收到的数据将是少量的

### 例子
order_id = 10900是可以直接进行索引查找的，虽然product_name也包含在复合索引中，但是product_name like '%00163e0496af%'是无法使用索引的。利用ICP就是：在利用第一个条件 order_id = 10900 进行索引查找的过程中，同时使用product_name like '%00163e0496af%'这个无法直接使用索引查找的条件进行过滤。
```SQL
create index idx_orderid_productname on test_orderdetail(order_id,product_name);

select * from test_orderdetail where order_id = 10900 and product_name like '%00163e0496af%';
```


# 6. 索引的应用类型
```SQL
CREATE TABLE table_name[col_name data type]
[unique|fulltext][index|key][index_name](col_name[length])[asc|desc]
```
## 普通索引index | key
作为基本索引，没有限制
```SQL
CREATE INDEX index_name ON table(column(length));

ALTER TABLE table_name ADD INDEX index_name ON (column(length));

CREATE TABLE `table` (
    `id` int(11) NOT NULL AUTO_INCREMENT ,
    `title` char(255) CHARACTER NOT NULL ,
    `content` text CHARACTER NULL ,
    `time` int(10) NULL DEFAULT NULL ,
    PRIMARY KEY (`id`),
    INDEX|key index_name (title(length))
);

DROP INDEX index_name ON table;

mysql> show index from account;
+---------+------------+----------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+
| Table   | Non_unique | Key_name | Seq_in_index | Column_name | Collation | Cardinality | Sub_part | Packed | Null | Index_type | Comment | Index_comment |
+---------+------------+----------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+
| account |          0 | PRIMARY  |            1 | id          | A         |           5 |     NULL | NULL   |      | BTREE      |         |               |
+---------+------------+----------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+
1 row in set (0.00 sec)
```

## 唯一索引unique
列值必须唯一且可以为空，如果是组合索引，那么组合值必须唯一
```SQL
CREATE UNIQUE INDEX(key) indexName ON table(column(length));

ALTER TABLE table_name ADD UNIQUE indexName ON (column(length));

CREATE TABLE `table` (
    `id` int(11) NOT NULL AUTO_INCREMENT ,
    `title` char(255) CHARACTER NOT NULL ,
    `content` text CHARACTER NULL ,
    `time` int(10) NULL DEFAULT NULL ,
    UNIQUE indexName (title(length))
);;
```
## 主键索引
一个表只能有一个主键索引，主键索引值唯一且不能为空
```SQL
CREATE TABLE `table` (
    `id` int(11) NOT NULL AUTO_INCREMENT ,
    `title` char(255) NOT NULL ,
    PRIMARY KEY (`id`)
);
```
## 组合索引
在多个字段上创建索引，需要满足最左匹配原则
```SQL
ALTER TABLE `table` ADD INDEX name_city_age (name,city,age); 
```

## 全文索引fulltext
- 只能应用于char、varchar、text，配合match against使用
- 先将数据插入没有fulltext的表，再创建fulltext索引，比将数据插入有fulltext索引的表速度快
```SQL
CREATE TABLE `table` (
    `id` int(11) NOT NULL AUTO_INCREMENT ,
    `title` char(255) CHARACTER NOT NULL ,
    `content` text CHARACTER NULL ,
    `time` int(10) NULL DEFAULT NULL ,
    PRIMARY KEY (`id`),
    FULLTEXT (content)
);

ALTER TABLE article ADD FULLTEXT index_content(content);

CREATE FULLTEXT INDEX index_content ON article(content);

SELECT * FROM `student` WHERE MATCH(`name`) AGAINST('聪')
```


# 索引查找构成举例
## 主键索引
```sql
select * from user_innodb where id = 28;
```

![主键索引查找过程](./pic/MySQL索引_主键索引查找过程.png)
```
先在主键树中从根节点开始检索，将根节点加载到内存，比较28<75，走左路。（1次磁盘IO）

将左子树节点加载到内存中，比较16<28<47，向下检索。（1次磁盘IO）

检索到叶节点，将节点加载到内存中遍历，比较16<28，18<28，28=28。查找到值等于28的索引项，直接可以获取整行数据。将改记录返回给客户端。（1次磁盘IO）

磁盘IO数量：3次
```

## 二级索引
### 单索引
age是一个二级索引

底层叶子节点的按照（age，id）的顺序排序，先按照age列从小到大排序，age列相同时按照id列从小到大排序。

使用辅助索引需要检索两遍索引：首先检索辅助索引获得主键，然后使用主键到主索引中检索获得记录。

```sql
select * from t_user_innodb where age=19;
```
![单二级索引查找过程](./pic/MySQL索引_单二级索引查找过程.png)

### 组合二级索引
```sql
CREATE TABLE `abc_innodb`
(
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `a`  int(11)     DEFAULT NULL,
  `b`  int(11)     DEFAULT NULL,
  `c`  varchar(10) DEFAULT NULL,
  `d`  varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_abc` (`a`, `b`, `c`)
) ENGINE = InnoDB;
```

```sql
select * from abc_innodb where a = 13 and b = 16 and c = 4;
```
![组合二级索引查找过程](./pic/MySQL索引_组合二级索引查找过程.png)
