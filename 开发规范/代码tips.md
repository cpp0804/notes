[TOC]

# 代码篇
1. 使用decimal，如果是将double类型创给构造函数，要转换成String类型

```java
new BigDecimal(liveRoomResource.getBuyCount().toString())
```

2. 所有的实体类都加上toString()方法

3. 如果一个应用要给另一个应用开一个查询接口，那这个接口是不能随便乱开的，要注意设计的合理性。比如策略-策略详情的关系，我不能直接开一个策略详情的接口给别人用，展现给别人的应该是策略。应该将策略返回，然后让别人通过策略查询策略详情。如果乱开策略详情的接口，不能控制这个接口会被怎么样调用

4. 如果返回给别人的是一个List，那么应该返回一个空列表而不是返回null。因为对别人来说查询列表的结果要么是有数据，要么是没数据，而不应该是null

5. 打log日志
```java
log.warn("setLiveRoomPriceAndTips error!itemId={}, activityId={}", itemPriceAndCountVO.getItemId(), activity.getId(), e);
```

4. 所有被远程调用的实体类都要实现serializable接口

5. 所有接口的返回都不要返回抽象类，这样别人远程调用的时候有可能会出现不能序列化的问题。比如有一个抽象类A，他有一个实现B。接口返回了A，别人在调用的时候如果没有引入B所在的包，那么虽然得到的对象是A类型，但是实际返回的B却不能序列化。


6. 使用Joiner将List中的值变成string返回
```java
//on()中指定分隔符
List<String> stringList = new ArrayList();
Joiner.on("</br>").join(stringList);
```


# 异常篇
1. 任何可能为null的地方都要判空

2. 要看懂报错的信息，near -50,50其实已经告诉我哪里有问题了

3. 如果是自己在某个接口中添加了一段代码，那最好给这段代码加上try catch不要影响原有业务

4. 在manager层，对于异常应该直接抛出，并在service层捕获。service返回的Result的success应该是指接口调用的成功或失败，里面的data只反应业务数据。在service中捕获到异常后，应该设置Result的success为false。
对于调用方来说接收到的都应该是Result，而不应该接收到异常

```java
//service层
@Override
    public Result<Void> cancelMarketContent(Long marketContentId, AppInfo appInfo, Operator operator) {
        Result<Void> result = Result.createSucc();
        try {
            marketContentManager.cancelMarketContent(marketContentId, appInfo, operator);
            return result;
        }  catch (BaseException baseException) {
            logger.warn("cancelMarketContent biz exception, msg={}", baseException.getMessage());
            return result.fail(baseException.getMessage());
        } catch (Throwable t) {
            logger.error("cancelMarketContent system exception", t);
            return result.fail(ErrorMessageEnum.SYSTEM_ERROR.getMessage());
        }
}

//manager层
@Override
    public void cancelMarketContent(Long marketContentId, AppInfo appInfo, Operator operator) {
        EnableCommonTO enableResult = enableCancelMarketContent(marketContentId, appInfo, operator);

        if (enableResult == null) {
            throw new BaseException("判断能否取消内容报名记录返回结果为空");
        }

        if (!enableResult.enable()) {
            throw new BaseException("判断能否取消内容报名记录失败，失败原因：" + enableResult.getReason());
        }

        ResultDO<Void> cancelResult = marketContentWriteService.cancelApply(marketContentId, new BizContext("取消内容报名记录"), operator, appInfo);
        if (cancelResult == null) {
            throw new BaseException("取消内容报名记录返回结果为空，marketContentId: " + marketContentId);
        }

        if (!cancelResult.isSuccess()) {
            throw new BaseException("取消内容报名记录失败，marketContentId: " + marketContentId + ", 失败原因:" + cancelResult.getErrorMessage());
        }
}
```


# SQL篇
1. 执行SQL报错可以贴到idb中执行一下看看

2. 复制别人的SQL要看一下假如把它拼出来了是不是有语法问题

3. 和其他表做join的时候要注意到一对多的关系

```
join：只返回两个表中满足on条件的行

left join：返回左表中的所有行，右表只返回符合条件的行

right join：返回右表中所有的行，左表只符合返回条件的行

full join：不仅只返回两个表中只满足on的行，两个表中的所有行都会返回，
```

4. 左表和右表join的时候如果字段数据类型不一样会出错，所以多个join中不能只关注自己的表，要所有表都执行看看

4. 在sql_map里面的查询字段，一定要注意索引和分表字段的命中。对于有些字段必须限制要传，不能在sqlmap里面判断<if id!=null>，而是应该在代码层面就检查这个字段是否为null
