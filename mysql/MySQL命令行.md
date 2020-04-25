[TOC]
# 1. 查看数据库信息
```SQL
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
mysql> select * from account;
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
# 2. 转换表的引擎
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

# 3. MySQL部分命令
```SQL
//手动启动MySQL
sudo -u _mysql /usr/local/mysql/bin/mysqld
```

