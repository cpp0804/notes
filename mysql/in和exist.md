## 参考博文
[不要再问我 in，exists 走不走索引了](https://segmentfault.com/a/1190000023825926)


[TOC]

# 执行流程
## in 
先会执行子查询(t2)，再把结果和外表(t1)做笛卡尔积，再根据条件筛选(name 是否相等)
```sql
select * from t1 
where name in 
(select name from t2);

--伪代码
for(x in A){
    for(y in B){
        if(condition is true) {
            result.add();
        }
    }
}
```

## exists
exists返回的结果是布尔值，而不是结果集。外层查询只关心true或false，不管具体数据

先遍历外表(t1)，再匹配内表执行后的exists是否为true(是否存在 name 相等的数据)
```sql
select * from t1 
where name exists 
(select 1 from t2);

--伪查询
for(x in A){
    if(exists condition is true) {
        result.add();
    }
}
```

如 id=1001时，张三存在于 t2 表中，则返回 true，把 t1 中张三的这条记录加入到结果集，继续下次循环。 id=1002 时，李四不在 t2 表中，则返回 false，不做任何操作，继续下次循环。直到遍历完整个 t1 表


# 执行效率
外层大表内层小表，用in; 外层小表内层大表

t1 表数据量为 100W， t2 表数据量为 200W 

实验一：外表小、内表大
```sql
--1.3s
select * from t1 where id in (select id from t2);
--3.4s
select * from t1 where exists (select 1 from t2 where t1.id=t2.id);

--会被转换成join where语句
select `test`.`t1`.`id` AS `id`,`test`.`t1`.`name` AS `name`,`test`.`t1`.`address` AS `address` from `test`.`t2` join `test`.`t1` where (`test`.`t2`.`id` = `test`.`t1`.`id`)
```

实验二：外表大、内表小
```sql
--1.8s
select * from t2 where id in (select id from t1);
--10s
select * from t2 where exists (select 1 from t1 where t1.id=t2.id);

--会被转换成join where语句
select `test`.`t2`.`id` AS `id`,`test`.`t2`.`name` AS `name`,`test`.`t2`.`address` AS `address` from `test`.`t1` join `test`.`t2` where (`test`.`t2`.`id` = `test`.`t1`.`id`)
```

结论：对in来说，内外表调换顺序并无太大差异；对exists来说差异巨大，因为exists不管怎么样都会遍历外表，对于内表有索引的就走索引


# join的嵌套循环
### 简单嵌套循环连接SNLJ(Simple Nested-Loop Join )
inner join的情况下，用双层循环遍历两张表

使用小表作为驱动表
```java
for(id1 in A){
    for(id2 in B){
        if(id1==id2){
            result.add();
        }
    }
}
```

### 索引嵌套循环连接 INLJ(Index Nested-Loop Join )
要求内层表的列要有索引,外层表直接和内层表的索引进行匹配，这样就不需要遍历整个内层表了

```java
for(id1 in A){
    if(id1 matched B.id){
        result.add();
    }
}
```

### 块索引嵌套连接 BNLJ(Block Nested-Loop Join)
通过缓存外层表的数据到 join buffer 中，然后 buffer 中的数据批量和内层表数据进行匹配，从而减少内层循环的次数

以外层循环100次为例，正常情况下需要在内层循环读取外层数据100次。如果以每10条数据存入缓存buffer中，并传递给内层循环，则内层循环只需要读取10次(100/10)就可以了。这样就降低了内层循环的读取次数