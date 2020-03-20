##### 参考博文
[MySQL中的各种join](http://wxb.github.io/2016/12/15/MySQL%E4%B8%AD%E7%9A%84%E5%90%84%E7%A7%8Djoin.html)
[Mysql Join语法解析与性能分析](https://www.cnblogs.com/beginman/p/3754322.html)
[关于 MySQL LEFT JOIN 你可能需要了解的三点](https://www.oschina.net/question/89964_65912)
[Mysql - JOIN详解](https://segmentfault.com/a/1190000015572505)
[Mysql多表连接查询的执行细节（一）](https://blog.csdn.net/qq_27529917/article/details/87904179) ⚠️


```SQL
mysql> select A.id,A.name,B.name from A,B where A.id=B.id;
+----+-----------+-------------+
| id | name       | name             |
+----+-----------+-------------+
|  1 | Pirate       | Rutabaga      |
|  2 | Monkey    | Pirate            |
|  3 | Ninja         | Darth Vader |
|  4 | Spaghetti  | Ninja             |
+----+-----------+-------------+
4 rows in set (0.00 sec)
```

# 内连接
inner join | join：返回满足on的列条件的行，只筛选两个表同时满足条件的行
![join](./pic/MySQL中的join_join.png)
```SQL
mysql> select * from A inner join B on A.name = B.name;
+----+--------+----+--------+
| id | name   | id | name   |
+----+--------+----+--------+
|  1 | Pirate |  2 | Pirate |
|  3 | Ninja  |  4 | Ninja  |
+----+--------+----+--------+
```

# 外连接
## 左外连接
left join | left outer join：返回左表所有行，右表满足条件的行，右表不满足条件的返回null
![left_join_结果](./pic/MySQL中的join_leftjoin.png)
```SQL
mysql> select * from A left join B on A.name = B.name;
#或者：select * from A left outer join B on A.name = B.name;

+----+-----------+------+--------+
| id | name      | id   | name   |
+----+-----------+------+--------+
|  1 | Pirate    |    2 | Pirate |
|  2 | Monkey    | NULL | NULL   |
|  3 | Ninja     |    4 | Ninja  |
|  4 | Spaghetti | NULL | NULL   |
+----+-----------+------+--------+
4 rows in set (0.00 sec)
```
使用where从A中筛选出不包含B的记录：
![A-B](./pic/MySQL中的join_A-B.png)
```SQL
mysql> select * from A left join B on A.name=B.name where A.id is null or B.id is null;
+----+-----------+------+------+
| id | name      | id   | name |
+----+-----------+------+------+
|  2 | Monkey    | NULL | NULL |
|  4 | Spaghetti | NULL | NULL |
+----+-----------+------+------+
2 rows in set (0.00 sec)
```
使用左外连接实现内连接
```SQL
mysql> select * from A left join B on A.name=B.name where A.id is not null and B.id is not null;
+----+--------+------+--------+
| id | name   | id   | name   |
+----+--------+------+--------+
|  1 | Pirate |    2 | Pirate |
|  3 | Ninja  |    4 | Ninja  |
+----+--------+------+--------+
2 rows in set (0.00 sec)
```
## 右外连接
right join | right outer join：返回右表所有行，左表满足条件的行，左表不满足条件的返回null
```SQL
mysql> select * from A right join B on A.name = B.name;
+------+--------+----+-------------+
| id   | name   | id | name        |
+------+--------+----+-------------+
| NULL | NULL   |  1 | Rutabaga    |
|    1 | Pirate |  2 | Pirate      |
| NULL | NULL   |  3 | Darth Vader |
|    3 | Ninja  |  4 | Ninja       |
+------+--------+----+-------------+
4 rows in set (0.00 sec)
```
# 笛卡尔积
cross join：得到两个表的乘积

inner join 不指定on得到的结果和cross join相同
```SQL
mysql> select * from A cross join B;
+----+-----------+----+-------------+
| id | name      | id | name        |
+----+-----------+----+-------------+
|  1 | Pirate    |  1 | Rutabaga    |
|  2 | Monkey    |  1 | Rutabaga    |
|  3 | Ninja     |  1 | Rutabaga    |
|  4 | Spaghetti |  1 | Rutabaga    |
|  1 | Pirate    |  2 | Pirate      |
|  2 | Monkey    |  2 | Pirate      |
|  3 | Ninja     |  2 | Pirate      |
|  4 | Spaghetti |  2 | Pirate      |
|  1 | Pirate    |  3 | Darth Vader |
|  2 | Monkey    |  3 | Darth Vader |
|  3 | Ninja     |  3 | Darth Vader |
|  4 | Spaghetti |  3 | Darth Vader |
|  1 | Pirate    |  4 | Ninja       |
|  2 | Monkey    |  4 | Ninja       |
|  3 | Ninja     |  4 | Ninja       |
|  4 | Spaghetti |  4 | Ninja       |
+----+-----------+----+-------------+
16 rows in set (0.00 sec)

#再执行：mysql> select * from A inner join B; 试一试
```

# on VS where
```SQL
mysql> CREATE TABLE `product` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `amount` int(10) unsigned default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=latin1
 
mysql> CREATE TABLE `product_details` (
  `id` int(10) unsigned NOT NULL,
  `weight` int(10) unsigned default NULL,
  `exist` int(10) unsigned default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1
 
mysql> INSERT INTO product (id,amount)
       VALUES (1,100),(2,200),(3,300),(4,400);
Query OK, 4 rows affected (0.00 sec)
Records: 4  Duplicates: 0  Warnings: 0
 
mysql> INSERT INTO product_details (id,weight,exist)
       VALUES (2,22,0),(4,44,1),(5,55,0),(6,66,1);
Query OK, 4 rows affected (0.00 sec)
Records: 4  Duplicates: 0  Warnings: 0
 
mysql> SELECT * FROM product;
+----+--------+
| id | amount |
+----+--------+
|  1 |    100 |
|  2 |    200 |
|  3 |    300 |
|  4 |    400 |
+----+--------+
4 rows in set (0.00 sec)
 
mysql> SELECT * FROM product_details;
+----+--------+-------+
| id | weight | exist |
+----+--------+-------+
|  2 |     22 |     0 |
|  4 |     44 |     1 |
|  5 |     55 |     0 |
|  6 |     66 |     1 |
+----+--------+-------+
4 rows in set (0.00 sec)
 
mysql> SELECT * FROM product LEFT JOIN product_details
       ON (product.id = product_details.id);
+----+--------+------+--------+-------+
| id | amount | id   | weight | exist |
+----+--------+------+--------+-------+
|  1 |    100 | NULL |   NULL |  NULL |
|  2 |    200 |    2 |     22 |     0 |
|  3 |    300 | NULL |   NULL |  NULL |
|  4 |    400 |    4 |     44 |     1 |
+----+--------+------+--------+-------+
4 rows in set (0.00 sec)
```
A left join B on中的on用于在B中检索出满足条件的行，如果B中没有满足条件的，将额外生成全为null的。检索出所有的行后，再去对行根据where条件进行过滤

on条件和where条件的不同:on条件对不满足条件的右表返回null，where则过滤不满足条件的

但对于inner join，on和where没区别
```SQL
mysql> SELECT * FROM product LEFT JOIN product_details
       ON (product.id = product_details.id)
       AND product_details.id=2;
+----+--------+------+--------+-------+
| id | amount | id   | weight | exist |
+----+--------+------+--------+-------+
|  1 |    100 | NULL |   NULL |  NULL |
|  2 |    200 |    2 |     22 |     0 |
|  3 |    300 | NULL |   NULL |  NULL |
|  4 |    400 | NULL |   NULL |  NULL |
+----+--------+------+--------+-------+
4 rows in set (0.00 sec)

mysql> SELECT * FROM product LEFT JOIN product_details
       ON (product.id = product_details.id)
       WHERE product_details.id=2;
+----+--------+----+--------+-------+
| id | amount | id | weight | exist |
+----+--------+----+--------+-------+
|  2 |    200 |  2 |     22 |     0 |
+----+--------+----+--------+-------+
1 row in set (0.01 sec)
```
# 执行过程分析
```SQL
CREATE TABLE `user_info` (
  `userid` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  UNIQUE `userid` (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4

CREATE TABLE `user_account` (
  `userid` int(11) NOT NULL,
  `money` bigint(20) NOT NULL,
 UNIQUE `userid` (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4

select * from user_info;
+--------+------+
| userid | name |
+--------+------+
|   1001 | x    |
|   1002 | y    |
|   1003 | z    |
|   1004 | a    |
|   1005 | b    |
|   1006 | c    |
|   1007 | d    |
|   1008 | e    |
+--------+------+
8 rows in set (0.00 sec)

select * from user_account;
+--------+-------+
| userid | money |
+--------+-------+
|   1001 |    22 |
|   1002 |    30 |
|   1003 |     8 |
|   1009 |    11 |
+--------+-------+
4 rows in set (0.00 sec)

SELECT i.name, a.money 
  FROM user_info as i 
    LEFT JOIN user_account as a 
      ON i.userid = a.userid 
        WHERE a.userid = 1003;
```
1. 执行from进行笛卡尔积操作
得到虚拟表t1
```SQL
SELECT * FROM user_info as i LEFT JOIN user_account as a ON 1;
+--------+------+--------+-------+
| userid | name | userid | money |
+--------+------+--------+-------+
|   1001 | x    |   1001 |    22 |
|   1002 | y    |   1001 |    22 |
|   1003 | z    |   1001 |    22 |
|   1004 | a    |   1001 |    22 |
|   1005 | b    |   1001 |    22 |
|   1006 | c    |   1001 |    22 |
|   1007 | d    |   1001 |    22 |
|   1008 | e    |   1001 |    22 |
|   1001 | x    |   1002 |    30 |
|   1002 | y    |   1002 |    30 |
|   1003 | z    |   1002 |    30 |
|   1004 | a    |   1002 |    30 |
|   1005 | b    |   1002 |    30 |
|   1006 | c    |   1002 |    30 |
|   1007 | d    |   1002 |    30 |
|   1008 | e    |   1002 |    30 |
|   1001 | x    |   1003 |     8 |
|   1002 | y    |   1003 |     8 |
|   1003 | z    |   1003 |     8 |
|   1004 | a    |   1003 |     8 |
|   1005 | b    |   1003 |     8 |
|   1006 | c    |   1003 |     8 |
|   1007 | d    |   1003 |     8 |
|   1008 | e    |   1003 |     8 |
|   1001 | x    |   1009 |    11 |
|   1002 | y    |   1009 |    11 |
|   1003 | z    |   1009 |    11 |
|   1004 | a    |   1009 |    11 |
|   1005 | b    |   1009 |    11 |
|   1006 | c    |   1009 |    11 |
|   1007 | d    |   1009 |    11 |
|   1008 | e    |   1009 |    11 |
+--------+------+--------+-------+
32 rows in set (0.00 sec)
```

2. 执行on过滤数据
ON i.userid = a.userid  得到虚拟表t2
```SQL
+--------+------+--------+-------+
| userid | name | userid | money |
+--------+------+--------+-------+
|   1001 | x    |   1001 |    22 |
|   1002 | y    |   1002 |    30 |
|   1003 | z    |   1003 |     8 |
+--------+------+--------+-------+
```

3. 执行join
(1)left join：扫描左表，将左表没出现的数据插入t2，右表的数据相应为null，生成虚拟表t3
(2)right join：扫描右表，将右表没出现的数据插入t2，左表的数据相应为null，生成虚拟表t3
(2)inner join：不执行该步骤
```SQL
+--------+------+--------+-------+
| userid | name | userid | money |
+--------+------+--------+-------+
|   1001 | x    |   1001 |    22 |
|   1002 | y    |   1002 |    30 |
|   1003 | z    |   1003 |     8 |
|   1004 | a    |   NULL |  NULL |
|   1005 | b    |   NULL |  NULL |
|   1006 | c    |   NULL |  NULL |
|   1007 | d    |   NULL |  NULL |
|   1008 | e    |   NULL |  NULL |
+--------+------+--------+-------+
```
4. 执行where过滤行
WHERE a.userid = 1003  生成虚拟表t4
```SQL
+--------+------+--------+-------+
| userid | name | userid | money |
+--------+------+--------+-------+
|   1003 | z    |   1003 |     8 |
+--------+------+--------+-------+
```

5. 执行select
SELECT i.name, a.money  生成虚拟表t5并返回
```SQL
+------+-------+
| name | money |
+------+-------+
| z    |     8 |
+------+-------+
```
# 性能分析
TODO