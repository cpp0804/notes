# SQL语句分析

## select
ODPS的数据都是全量更新的，比如根据日期分区，那么最新一天分区的数据是包含所有的数据
```sql
--odps sql 
--********************************************************************--
--author:孚申
--create time:2020-08-05 19:22:35
--********************************************************************--
CREATE TABLE IF NOT EXISTS message_send_count 
(
    
    id BIGINT COMMENT '消息模板ID'
    ,name STRING COMMENT '消息模板名称'
    ,site_id BIGINT COMMENT '站点ID'
    ,site_name STRING COMMENT '站点名称'
    ,description STRING COMMENT '模板描述'
    ,total BIGINT COMMENT '该模板下的消息总量'
    ,isread BIGINT COMMENT '已读消息总量'
    ,unread BIGINT COMMENT '未读消息总量'
    ,mark BIGINT COMMENT '收藏总量'
    ,mid BIGINT COMMENT '该消息模板下最近的一条消息ID'
)
COMMENT "消息模板发送量统计"
PARTITIONED BY 
(
    ds STRING COMMENT '每天更新'
)
LIFECYCLE 7
;
INSERT OVERWRITE TABLE message_send_count PARTITION(ds='${bizdate}')
SELECT  t2.id
        ,t2.name
        ,t2.site_id
        ,IF(t3.site_name is NULL ,'全站点',t3.site_name)
        ,t2.description
        ,t1.total
        ,t1.isread
        ,t1.unread
        ,t1.mark
        ,t1.mid
FROM    (
            SELECT /* + MAPJOIN(define) */
                    define.id define_id
                    ,COUNT(1) total
                    ,SUM(IF (is_read=2,1,0)) isread
                    ,SUM(IF (is_read=1,1,0)) unread
                    ,SUM(IF (mark=1,1,0)) mark
                    ,MAX(msg.id) mid
            FROM    (
                        SELECT  *
                        FROM    tmc.tmc_msg_define
                        WHERE   ds = MAX_PT('tmc.tmc_msg_define')
                        AND     type = 0
                        AND     channel_ids LIKE '%2%'
                    ) define
            JOIN    (
                        SELECT  *
                        FROM    tmc.s_ju_msg
                        WHERE   ds = MAX_PT('tmc.s_ju_msg')
                    ) msg
            ON      define.id = msg.define_id
            GROUP BY define.id
        ) t1
JOIN    (
            SELECT  *
            FROM    tmc.tmc_msg_define
            WHERE   ds = MAX_PT('tmc.tmc_msg_define')
        ) t2
ON      t1.define_id = t2.id
LEFT JOIN    (
            SELECT  *
            FROM    ju_tech.s_site_info_ju_seller
            WHERE   ds = MAX_PT('ju_tech.s_site_info_ju_seller')
        ) t3
ON      t2.site_id = t3.id 
;
```




第一步：从tmc_msg_define查出想要的数据得到define，一定要指定分区 WHERE   ds = MAX_PT('tmc.tmc_msg_define')
```sql
                		(
                        SELECT  *
                        FROM    tmc.tmc_msg_define
                        WHERE   ds = MAX_PT('tmc.tmc_msg_define')
                        AND     type = 0
                        AND     channel_ids LIKE '%2%'
                    ) define
```




第二步：从s_ju_msg中查出想要的数据得到msg
```sql
                   (
                        SELECT  *
                        FROM    tmc.s_ju_msg
                        WHERE   ds = MAX_PT('tmc.s_ju_msg')
                    ) msg
```


第三步：结合define、msg查出聚合数据得到t1
```sql
        (
            SELECT /* + MAPJOIN(define) */
                    define.id define_id
                    ,COUNT(1) total
                    ,SUM(IF (is_read=2,1,0)) isread
                    ,SUM(IF (is_read=1,1,0)) unread
                    ,SUM(IF (mark=1,1,0)) mark
                    ,MAX(msg.id) mid
            FROM    (
                        SELECT  *
                        FROM    tmc.tmc_msg_define
                        WHERE   ds = MAX_PT('tmc.tmc_msg_define')
                        AND     type = 0
                        AND     channel_ids LIKE '%2%'
                    ) define
            JOIN    (
                        SELECT  *
                        FROM    tmc.s_ju_msg
                        WHERE   ds = MAX_PT('tmc.s_ju_msg')
                    ) msg
            ON      define.id = msg.define_id
            GROUP BY define.id
        ) t1
```


第三步：结合t1、t2查询group by字段外的数据
```sql
SELECT  t2.id
        ,t2.name
        ,t2.site_id
        ,t2.description
        ,t1.total
        ,t1.isread
        ,t1.unread
        ,t1.mark
        ,t1.mid
FROM    (
            SELECT /* + MAPJOIN(define) */
                    define.id define_id
                    ,COUNT(1) total
                    ,SUM(IF (is_read=2,1,0)) isread
                    ,SUM(IF (is_read=1,1,0)) unread
                    ,SUM(IF (mark=1,1,0)) mark
                    ,MAX(msg.id) mid
            FROM    (
                        SELECT  *
                        FROM    tmc.tmc_msg_define
                        WHERE   ds = MAX_PT('tmc.tmc_msg_define')
                        AND     type = 0
                        AND     channel_ids LIKE '%2%'
                    ) define
            JOIN    (
                        SELECT  *
                        FROM    tmc.s_ju_msg
                        WHERE   ds = MAX_PT('tmc.s_ju_msg')
                    ) msg
            ON      define.id = msg.define_id
            GROUP BY define.id
        ) t1
JOIN    (
            SELECT  *
            FROM    tmc.tmc_msg_define
            WHERE   ds = MAX_PT('tmc.tmc_msg_define')
        ) t2
ON      t1.define_id = t2.id
```


第四部：结合s_site_info_ju_seller表查出站点名称。因为tmc_msg_define会有站点ID为0的数据代表全站点，所以要使用left join
```sql
CREATE TABLE IF NOT EXISTS message_send_count 
(
    
    id BIGINT COMMENT '消息模板ID'
    ,name STRING COMMENT '消息模板名称'
    ,site_id BIGINT COMMENT '站点ID'
    ,site_name STRING COMMENT '站点名称'
    ,description STRING COMMENT '模板描述'
    ,total BIGINT COMMENT '该模板下的消息总量'
    ,isread BIGINT COMMENT '已读消息总量'
    ,unread BIGINT COMMENT '未读消息总量'
    ,mark BIGINT COMMENT '收藏总量'
    ,mid BIGINT COMMENT '该消息模板下最近的一条消息ID'
)
COMMENT "消息模板发送量统计"
PARTITIONED BY 
(
    ds STRING COMMENT '每天更新'
)
LIFECYCLE 7
;
INSERT OVERWRITE TABLE message_send_count PARTITION(ds='${bizdate}')
SELECT  t2.id
        ,t2.name
        ,t2.site_id
        ,IF(t3.site_name is NULL ,'全站点',t3.site_name)
        ,t2.description
        ,t1.total
        ,t1.isread
        ,t1.unread
        ,t1.mark
        ,t1.mid
FROM    (
            SELECT /* + MAPJOIN(define) */
                    define.id define_id
                    ,COUNT(1) total
                    ,SUM(IF (is_read=2,1,0)) isread
                    ,SUM(IF (is_read=1,1,0)) unread
                    ,SUM(IF (mark=1,1,0)) mark
                    ,MAX(msg.id) mid
            FROM    (
                        SELECT  *
                        FROM    tmc.tmc_msg_define
                        WHERE   ds = MAX_PT('tmc.tmc_msg_define')
                        AND     type = 0
                        AND     channel_ids LIKE '%2%'
                    ) define
            JOIN    (
                        SELECT  *
                        FROM    tmc.s_ju_msg
                        WHERE   ds = MAX_PT('tmc.s_ju_msg')
                    ) msg
            ON      define.id = msg.define_id
            GROUP BY define.id
        ) t1
JOIN    (
            SELECT  *
            FROM    tmc.tmc_msg_define
            WHERE   ds = MAX_PT('tmc.tmc_msg_define')
        ) t2
ON      t1.define_id = t2.id
LEFT JOIN    (
            SELECT  *
            FROM    ju_tech.s_site_info_ju_seller
            WHERE   ds = MAX_PT('ju_tech.s_site_info_ju_seller')
        ) t3
ON      t2.site_id = t3.id 
;
```




## 单库和逻辑库
一个表如果数据量很大， 会把这个表分表，比如把一张分成0000-0255张表，那逻辑库是对这255张表的一个抽象，不然在单库中就要指定哪张表查询，通过逻辑库可以把它当做没有分表的表。




## 将行转换成列
```sql
SELECT  strategy_id
                            ,MAX(conditionSellerSceneId) AS conditionSellerSceneId
                            ,MAX(conditionItemSceneId) AS conditionItemSceneId
                            ,MAX(scopeActIds) AS scopeActIds
                            ,MAX(scopeUnitedActIds) AS scopeUnitedActIds
                            ,MAX(actionActId) AS actionActId
                            ,MAX(undoActionAddTag) AS undoActionAddTag
                            ,MAX(undoActionRemoveTag) AS undoActionRemoveTag
                            ,MAX(doneActionAddTag) AS doneActionAddTag
                            ,MAX(doneActionRemoveTag) AS doneActionRemoveTag
                            ,MAX(bizLabel) AS bizLabel
                    FROM    (
                                SELECT  strategy_id
                                        ,CASE    WHEN code="conditionSellerSceneId" THEN value 
                                         END AS conditionSellerSceneId
                                        ,CASE    WHEN code="conditionItemSceneId" THEN value 
                                         END AS conditionItemSceneId
                                        ,CASE    WHEN code="scopeActIds" THEN value 
                                         END AS scopeActIds
                                        ,CASE    WHEN code="scopeUnitedActIds" THEN value 
                                         END AS scopeUnitedActIds
                                        ,CASE    WHEN code="actionActId" THEN value 
                                         END AS actionActId
                                        ,CASE    WHEN code="undoActionAddTag" THEN value 
                                         END AS undoActionAddTag
                                        ,CASE    WHEN code="undoActionRemoveTag" THEN value 
                                         END AS undoActionRemoveTag
                                        ,CASE    WHEN code="doneActionAddTag" THEN value 
                                         END AS doneActionAddTag
                                        ,CASE    WHEN code="doneActionRemoveTag" THEN value 
                                         END AS doneActionRemoveTag
                                        ,CASE    WHEN code="bizLabel" THEN value 
                                         END AS bizLabel
                                FROM    tmc.s_campaign_strategy_ext_tmc_business
                                WHERE   ds = max_pt('tmc.s_campaign_strategy_ext_tmc_business')
                            )  te GROUP BY strategy_id
```


# 查出在A表中没有B表有的数据，并和A表合并

- 方法一：使用union all

先将A B表做join，然后挑出A表没有的数据( where i2.item_id is null)，再和A表做union
```sql
 select 
        item_id
        ,output_scene_ids
        ,bind_act_ids
        ,signed_bind_act_ids
        ,unsigned_bind_act_ids
    from tmc.temp_item_output_sign_stat 
    union all
    select 
        i1.item_id
        ,'' AS output_scene_ids
        ,'' AS bind_act_ids
        ,'' AS signed_bind_act_ids
        ,'' AS unsigned_bind_act_ids
    from (
        select distinct item_id as item_id
        from tmc.item_startegy_relation_hh
        where ds=MAX_PT('tmc.item_startegy_relation_hh')
    ) i1
    left outer join (
        select DISTINCT item_id as item_id
        from tmc.temp_item_output_sign_stat
    ) i2
    on i1.item_id=i2.item_id
    where i2.item_id is null
```


- 方法二：使用full outer join

对A B表使用全连接，如果B表有而A表没有，就取B表的数据
```sql
    select 
        if(i1.item_id is null, i2.item_id, i1.item_id) as item_id
        ,if(i1.item_id is null, '', i1.output_scene_ids) as output_scene_ids
        ,if(i1.item_id is null, '', i1.bind_act_ids) as bind_act_ids
        ,if(i1.item_id is null, '', i1.signed_bind_act_ids) as signed_bind_act_ids
        ,if(i1.item_id is null, '', i1.unsigned_bind_act_ids) as unsigned_bind_act_ids
    from temp_item_output_sign_stat i1
    full outer join (
        --补全没有进选品集但是被策略命中的商品
        select distinct item_id as item_id
        from tmc.item_startegy_relation_hh
        where ds=MAX_PT('tmc.item_startegy_relation_hh')
    ) i2
    on i1.item_id=i2.item_id
```
