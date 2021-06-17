## 参考博文
[SQL优化2020最全干货总结---MySQL](https://developer.aliyun.com/article/779151)


[TOC]

# 一、避免不走索引的场景
### like查询时避免在开头使用%，会导致全表扫描
```sql
SELECT * FROM t WHERE username LIKE '%陈%'
```

优化：
1. 尽量在后面使用%
```sql
SELECT * FROM t WHERE username LIKE '陈%'
```
2. 使用MySQL内置函数INSTR(str,substr) 来匹配，作用类似于java中的indexOf()
3. 使用搜索引擎

### 尽量避免使用in和not in，会导致引擎走全表扫描
```sql
SELECT * FROM t WHERE id IN (2,3)` 
```

优化：
1. 如果是连续数值，可以用between代替
```sql
SELECT * FROM t WHERE id BETWEEN 2 AND 3
```
2. 如果是子查询，可以用exists代替。如下：
```sql
-- 不走索引
select * from A where A.id in (select id from B);
-- 走索引
select * from A where exists (select * from B where B.id = A.id);
```

### 尽量避免使用 or，会导致数据库引擎放弃索引进行全表扫描
```sql
SELECT * FROM t WHERE id = 1 OR id = 3
```
优化：
1. 可以用union代替or
```sql
SELECT * FROM t WHERE id = 1
UNION
SELECT * FROM t WHERE id = 3
```

### 尽量避免进行null值的判断，会导致数据库引擎放弃索引进行全表扫描
```sql
SELECT * FROM t WHERE score IS NULL
```

优化：
1. 可以给字段添加默认值0，对0值进行判断
```sql
SELECT * FROM t WHERE score = 0` 
```

### 尽量避免在where条件中等号的左侧进行表达式、函数操作，会导致数据库引擎放弃索引进行全表扫描
```sql
-- 全表扫描
SELECT * FROM T WHERE score/10 = 9
```

优化：
1. 将表达式、函数操作移动到等号右侧
```sql
-- 走索引
SELECT * FROM T WHERE score = 10*9
```

### 查询条件不能用 <> 或者 !=
使用索引列作为条件进行查询时，需要避免使用<>或者!=等判断条件。如确实业务需要，使用到不等于符号，需要在重新评估索引建立，避免在此字段上建立索引，改由查询条件中其他索引字段代替

### 隐式类型转换造成不使用索引
```sql
--由于索引对列类型为varchar，但给定的值为数值，涉及隐式类型转换，造成不能正确走索引
select col1 from table where col_varchar=123;
```

# 二、SELECT语句其他优化
### 避免出现select *

使用select * 取出全部列，会让优化器无法完成索引覆盖扫描这类优化；

会增加网络带宽消耗，更会带来额外的I/O,内存和CPU消耗

### 避免出现不确定结果的函数
针对主从复制的场景，从库复制的是主库的SQL语句，对于now()、rand()、sysdate()、current_user()等不确定结果的函数很容易导致主库与从库相应的数据不一致

并且不确定结果的函数产生的数据不能被缓存


### 多表关联查询时，小表在前，大表在后
在MySQL中，执行 from 后的表关联查询是从左往右执行的（Oracle相反），第一张表会涉及到全表扫描，所以将小表放在前面，再扫描后面的大表，或许只扫描大表的前100行就符合返回条件并return了

### 调整Where字句中的连接顺序
MySQL采用从左往右，自上而下的顺序解析where子句。根据这个原理，应将过滤数据多的条件往前放，最快速度缩小结果集

# 三、增删改 DML 语句优化
### 大批量插入数据
如果同时执行大量的插入，建议使用多个值的INSERT语句(方法二)。这比使用分开INSERT语句快（方法一）

原因如下：
1. 减少SQL语句解析的操作，采用方法二，只需要解析一次就能进行数据的插入操作
2. 在特定场景可以减少对DB连接次数
3. SQL语句较短，可以减少网络传输的IO

方法一：
```sql
insert into T values(1,2); 
insert into T values(1,3); 
insert into T values(1,4);
```

方法二：
```sql
insert into T values(1,2),(1,3),(1,4);
```

### 避免重复查询更新的数据
例如某行频繁更新，又要查询更改的这行
```sql
Update t1 set time=now() where col1=1; 
Select time from t1 where id =1;
```

使用变量存储更改的数据,避免了再次访问数据表
```sql
Update t1 set time=now () where col1=1 and @now: = now (); 
Select @now;
```


# 四、查询条件优化
### 优化group by语句
MySQL 会对GROUP BY分组的所有值进行排序，如 “GROUP BY col1，col2，....;” 

如果不想排序，可以指定 ORDER BY NULL禁止排序
```sql
SELECT col1, col2, COUNT(*) FROM table GROUP BY col1, col2 ORDER BY NULL ;
```

### 优化join语句
假设要将所有没有订单记录的用户取出来
```sql
SELECT col1 FROM customerinfo WHERE CustomerID NOT in (SELECT CustomerID FROM salesinfo )
```

使用join会比使用子查询效率更高，因为MySQL不用在内存创建临时表了
```sql
SELECT col1 FROM customerinfo 
   LEFT JOIN salesinfoON customerinfo.CustomerID=salesinfo.CustomerID 
      WHERE salesinfo.CustomerID IS NULL
```

### 优化union查询
MySQL通过创建并填充临时表的方式来执行union查询

如果不需要消除重复的行，建议使用union all

如果没有all，会对整个临时表加上distinct字段，会对整个表的数据做唯一性校验

### 拆分复杂SQL为多个小SQL，避免大事务


### 使用truncate代替delete
删除全表中记录时，使用delete语句的操作会被记录到undo块中，删除记录也记录binlog，当确认需要删除全表时，会产生很大量的binlog并占用大量的undo数据块，此时既没有很好的效率也占用了大量的资源。

使用truncate替代，不会记录可恢复的信息，数据不能被恢复。也因此使用truncate操作有其极少的资源占用与极快的时间。另外，使用truncate可以回收表的水位，使自增字段值归零。

# 五、建表优化
### 在表中建立索引，优先考虑where、order by使用到的字段

### 将只含数值型的字段设计成数字类型，尽量不设计成字符类型

### 用varchar/nvarchar 代替 char/nchar
变长字段存储空间小

比如：char(100) 型，在字段建立时，空间就固定了， 不管是否插入值（NULL也包含在内），都是占用 100个字符的空间的，如果是varchar这样的变长字段， null 不占用空间
