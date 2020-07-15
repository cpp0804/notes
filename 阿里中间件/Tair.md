# Tair

# 1. 简介

tair是一个高性能、分布式、可扩展、高可靠的Key-Value结构存储系统。分为非持久化存储和持久化存储两种，非持久化存储可以看成是一个分布式缓存，持久化存储可以将数据存储在磁盘中


# 2. 总体架构      

图中有点错误，rdb也是有持久化的                 

![总体架构](./pic/Tair_总体架构.png)


# 3. Tair mdb
## 3.1 概述

- 内存存储服务

- 特别适用容量小（一般在M级别，50G之内），读写QPS高（万级别）的缓存场景


## 3.2 典型应用场景

- 缓存：降低对后端数据库的访问压力

- 临时数据存储：分钟级别后失效，偶尔数据丢失不会对业务产生较大影响

- 读多写少：读QPS达到万级别以上

## 3.4 规范及限制

- 产品选型

1. 只支持缓存，无数据可靠性。当容量超出业务申请量时会淘汰数据

2. 不支持事务

3. 不支持模糊查询

4. 不支持全量遍历，不支持类似于redis的keys全量遍历功能，无法知晓某个实例组中具体有哪些key。必须要指定key查询

5. 不支持单元间同步


- key名设计

1. 业务名：表名：id

2. key长度<1k，>1k的接口将返回错误

3. Pkey个数<5120
## 

- value设计

1. value长度<1KB , >1M的接口将返回错误

2. 控制key的生命周期，建议对所有数据写入时用expiretime参数设置合理的过期时间
## 

- API使用

1. 所有接口都必须判读返回码，非ResultCode.SUCCESS的返回码需要打印出来，用于异常情况快速定位问题

2. mget/prefixgets等批量接口个数强制限制在1024个之内，建议控制在100个之内。大批量操作会进入慢查询队列

## 3.5 version

- 用途：为了解决并发更新的问题

version相当于一个版本号，当client传入的version和tair中的version一致时，才会更新。类似于CAS。

version=1--16

version=0 强行覆盖数据

- 用法

1. get接口：返回的DataEntry对象中包含version.通过DataEntry中的getVersion()获得

2. put接口：在put时将版本号作为入参

具体：

如果应用有10个client会对key进行并发put，那么操作过程如下

1. get key:若成功，进入2.若失败(数据不存在),进入3

2. put key:将get key返回的version传入put,。服务端根据version是否匹配来返回client是否put成功。

3. put新数据：此时传入的version必须不是0和1，其他的值都可以（例如1000，要保证所有client是一套逻辑）。不传入0是因为tair会认为强制覆盖；不传入1是因为会出现两个client都写入成功的情况，不能防止并发


## 3.6 MDB 容量管理策略 - 淘汰与回收

- 统计实例组的使用容量

Dataserver针对每次写操作和删除操作统计用户实际的使用容量

- 淘汰

1. 采用LRU的方式淘汰数据

2. 以slab为内存单位(1~64M)


## 3.7 JAVA SDK接入

- maven依赖

```
<dependency>
   <groupId>com.taobao.rdb</groupId>
   <artifactId>rdb-client2</artifactId>
   <version>最新版本</version>
</dependency>
```

- 初始化SDK

1. 三种初始化模式：集团标准模式、集团VIPServer直连模式、开源直连模式




2. 初始化两个核心参数(连接RDB实例的参数)：instanceID(实例ID)、password
3. 两种初始化方式：每种方式都可以同时支持 同步模式 和 异步模式

     1）使用接口初始化   

     
```
public class RdbTest {
    private static final String INSTANCE = "xxx";
    private static final String PASSWORD = "xxx";
    
    // 一个实例对应初始化一个 rdbSmartApi，不需要初始化多个
    static RdbSmartApi rdbSmartApi = RdbSmartFactory.getClientManager(INSTANCE);
    static RedisAsyncApi redisAsyncApi; // 异步接口
    static RedisSyncApi redisSyncApi; // 同步接口

    public static void main(String[] args) {
        rdbSmartApi.setPassWord(PASSWORD);

        //设置超时时间为3秒(默认：2秒),超时要在init之前设置才生效
        //RdbConfig rdbConfig = new RdbConfig();
        //rdbConfig.setSoTimeout(3000);
        //rdbSmartApi.setRdbConfig(rdbConfig);

        if (!rdbSmartApi.init().equals("ok")) {
            return;
        }
        redisSyncApi = rdbSmartApi.sync();
        redisAsyncApi = rdbSmartApi.async();
        
        try {
            redisSyncApi.set("key".getBytes(), "value".getBytes());
            System.out.println(new String(redisSyncApi.get("key".getBytes())));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```


     2）通过spring bean 初始化

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
    <bean id="rclientManager" class="com.taobao.rdb2.factory.RdbSmartFactory" factory-method="getClientManager"
          init-method="init">
        <property name='instance'>
            <value>307197b3-acc5-4bbf-ab7e-dc792bcd8b3a</value>
        </property>
        <property name='passWord'>
            <value>307197b3-acc5-4bbf-ab7e-dc792bcd8b3a</value>
        </property>
    </bean>
</beans>
```

```
ApplicationContext ac = new FileSystemXmlApplicationContext("src/test/java/base/init.xml");
RdbSmartApi rclientManager = (RdbSmartApi)ac.getBean("rclientManager");
RedisSyncApi redisSyncApi = rclientManager.sync();

redisSyncApi.set("key".getBytes(), "12345".getBytes());
Assert.assertEquals("12345", new String(redisSyncApi.get("key".getBytes())));
```


- 调用SDK

1) 使用String类型读写(同步模式)

```
public class HelloRdbStringClient {
    private static final String INSTANCE = "xxx";
    private static final String PASSWORD = INSTANCE;

    static RdbSmartApi rdbSmartApi = RdbSmartFactory.getClientManager(INSTANCE);
    static RedisSyncApi redisSyncApi;

    // 定义一个Api的Warp
    static RedisSyncApiWrap<String, String> apiWrap;

    public static void main(String[] args) throws Exception {
        String result;

        rdbSmartApi.setPassWord(PASSWORD);
        result = rdbSmartApi.init();
        if (!"ok".equals(result)) {
            System.out.println("init fail, ret is: " + result);
            return;
        }
        redisSyncApi = rdbSmartApi.sync();

        /* 传入redisSyncAPi，返回的API是定义类型，在RedisSyncApiWrap
         * 内部负责序列化和反序列化 */
        apiWrap = RedisSyncApiWrap.wrap(redisSyncApi);

        // 之后的API均为String类型的
        apiWrap.set("StringKey", "StringValue");
        System.out.println(apiWrap.get("StringKey"));
    }
}
```


2）使用自定义类型读写(同步模式)

首先自定义自己的encode和decode方法，需要implements RdbCodec，以<String，Long>为例
```
public class MyCodec implements RdbCodec<String, Long> {
    // 反序列化Key
    public String decodeKey(byte[] bytes) throws Exception {
        return new String(bytes);
    }
    // 反序列化Value
    public Long decodeValue(byte[] bytes) throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(bytes, 0, 8);
        return buffer.getLong();
    }
    // 序列化Key
    public byte[] encodeKey(String a) throws Exception {
        return a.getBytes();
    }
    // 序列化Value
    public byte[] encodeValue(Long number) throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(0, number);
        return buffer.array();
    }
}
```

使用方法如下：

```
public class HelloRdbUserDefineClient {
    private static final String INSTANCE = "xxx";
    private static final String PASSWORD = INSTANCE;
    static RdbSmartApi rdbSmartApi = RdbSmartFactory.getClientManager(INSTANCE);
    static RedisSyncApi redisSyncApi;
    // 定义一个Api的Warp
    static RedisSyncApiWrap<String, Long> apiWrap;
    public static void main(String[] args) throws Exception {
        String result;
        rdbSmartApi.setPassWord(PASSWORD);
        result = rdbSmartApi.init();
        if (!"ok".equals(result)) {
            System.out.println("init fail, ret is: " + result);
            return;
        }
        redisSyncApi = rdbSmartApi.sync();
        
        /* 使用自定义的codec方法，并且传入 */
        MyCodec myCodec = new MyCodec();
        apiWrap = RedisSyncApiWrap.wrap(redisSyncApi, myCodec);
        // 之后的API均为<String, Long>类型的
        apiWrap.set("StringKey", (long) 1);
        System.out.println(apiWrap.get("StringKey"));
    }
}
```
# 
# 4. Tair RDB

## 4.1 概述

- 与Redis兼容的完全托管式NoSQL服务

- 基于高可靠双机热备架构及可平滑扩展的集群架构

- 可缓存和持久化

## 4.2 规范与限制

- 产品选型

1. 标准版实例组最大容量：16G        集群版实例组最大容量：10T

2. 不支持类似于MySQL like的模糊查询

3. 适合高并发，不适合大吞吐量场景：>10k的value就是bigValue


- key名设计

1. <256byte,  >1k接口将返回错误

2. 避免GodKey(容易引发数据倾斜和热点问题)，List，Hash，Set等复杂结构操作大于3000个skey就是godkey

3. 不包含特殊字符(空格、换行、单双引号以及其他转义字符、**{}、**二进制及复杂encoding)

4. 建议对每个key设置超时时间


- value设计

<1KB,   >1M接口将返回错误


## 4.3 JAVA SDK接入

同mdb


# 5. Tair LDB

## 5.1 概述

- 持久化存储

- 基于开源的LevelDB引擎以及可扩展的靠可靠分布式架构

- 读写QPS比较高的场景(比MDB还是差10来倍，所以不适合当缓存，这种场景使用MDB+DB)

- 支持key-value结构，支持分层key结构：Pkey-{Skey:Value,Skey:Value}。 一个 Key 对应的 Skey 建议最多不要超过 1W 个

- 在支持的数据结构上和MDB相同


## 5.2 典型应用场景

- 存储黑白单数据，读QPS很高，DB无法承载

- 计数器功能，更新频繁，且数据不可丢失


## 5.3 线上部署方式

- 双机房主备集群(互为主备)

- 一个集群的数据会异步同步到另一个集群

- 机房容灾时可以由一个集群来承载


## 5.4 SDK

- maven 

```
<dependency>
		<groupId>com.taobao.tair</groupId>
		<artifactId>tair-mc-client</artifactId>
		<version>4.1.6</version>
</dependency>
```

- 初始化连接: 单个实例组初始化

spring
```
<bean id="tairManager" class="com.taobao.tair.impl.mc.MultiClusterTairManager" initmethod="init">
         <!-- 日常，预发和生产配置全部相同 -->
  <property name='userName'>
      <value>1ba2b4d61d7040ed</value>
  </property>
  <!--如果需要跨单元访问，比如线上中心机器访问Tair的深圳单元集群，则可以指定unit。
  这种操作会产生跨机房延迟，同时也就强依赖单元环境，请务必谨慎！！！-->
   <property name='unit'>
       <value>unsz</value>
   </property>
   <!--需要指定超时时间的应用-->
   <property name="timeout">
       <value>500</value>  <!-- 单位为 ms，默认 2000 ms -->
   </property>
</bean>
```


java jdk
```
// mdb/ldb引擎
public void testInitMultiCluster() {
     MultiClusterTairManager mcTairManager = new MultiClusterTairManager();
     mcTairManager.setUserName("申请到的 实例组名称");
     // mcTairManager.setTimeout(500);  // 单位为 ms，默认 2000 ms
     mcTairManager.init();
     ...
}
```


## 5.5 接口demo

- kv 接口demo

```
1. ResultCode put(int namespace, Object key, Serializable value, int version, int expireTime)
参数：
namespace - 申请时分配的namespace。
key - key，不超过1k。
value - 可序列化对象value，接口限制1M，超过1M将返回错误。基于性能考虑，建议在1k之内。
version - 为了解决并发更新同一个数据而设置的参数。当version为0时，表示强制更新。当传入get value返回的版本号，则若此key未发生更新时，返回正常，否则返回version error。
expireTime - 数据过期时间，单位为秒，可设相对时间或绝对时间(Unix时间戳)。expireTime = 0，表示数据永不过期。expireTime > 0，表示设置过期时间。若expireTime>当前时间的时间戳，则表示使用绝对时间，否则使用相对时间。expireTime < 0，表示不关注过期时间，若之前设过过期时间，则已之前的过期时间为准，若没有，则作为永不过期处理。
返回值：
ResultCode对象。ResultCode.SUCCESS表示写成功，其他表示写失败。



2. Result get(int namespace, Object key)
参数
namespace - 申请时分配的namespace
key - key，不超过1k。
返回值
Result对象，可用isSuccess()方法判断请求是否成功，再用getRc()方法获取到ResultCode。isSuccess有两种情况，ResultCode.SUCCESS表示读成功，ResultCode.DATANOTEXSITS表示数据不存在。其余情况皆表示读取失败。


3. ResultCode delete(int namespace, Object key)
参数
namespace - 申请时分配的namespace
key - key，不超过1k
返回值
ResultCode对象，ResultCode.SUCCESS表示删除成功。
```



```java
@Test
public void put_get_normal() {
    String key = "key_" + UUID.randomUUID().toString();
    String value = "val_" + UUID.randomUUID().toString();

    // delete if exist
    tairManager.delete(namespace, key);

    // put
    ResultCode resultCode = tairManager.put(namespace, key, value);
    assertTrue(resultCode.isSuccess());
    assertEquals(ResultCode.SUCCESS, resultCode);
    logger.info("put key: " + key);

    // get
    Result<DataEntry> result = tairManager.get(namespace, key);
    assertTrue(result.isSuccess());
    assertEquals(ResultCode.SUCCESS, result.getRc());
    assertEquals(value, (result.getValue()).getValue());
    assertEquals(1, (result.getValue()).getVersion());
    assertEquals(key, (result.getValue()).getKey());
    logger.info("get key: " + key + "  value: " + result.getValue().getValue());
}

@Test
public void put_delete_get() {
    String key = "key_" + UUID.randomUUID().toString();
    String value = "val_" + UUID.randomUUID().toString();

    tairManager.delete(namespace, key);

    // put
    ResultCode resultCode = tairManager.put(namespace, key, value);
    assertTrue(resultCode.isSuccess());
    assertEquals(ResultCode.SUCCESS, resultCode);
    logger.info("put key: " + key);

    ResultCode code = tairManager.delete(namespace, key);
    assertTrue(code.isSuccess());
    logger.info("delete key: " + key);

    // get
    Result<DataEntry> result = tairManager.get(namespace, key);
    assertTrue(result.isSuccess());
    assertEquals(ResultCode.DATANOTEXSITS, result.getRc());
    logger.info("get key result: " + result.getRc().toString());
}

@Test
public void put_with_version_get_normal() {
    String key = "key_" + UUID.randomUUID().toString();
    String value = "val_" + UUID.randomUUID().toString();

    // delete if exist
    tairManager.delete(namespace, key);

    // put
    ResultCode resultCode = tairManager.put(namespace, key, value, 1);
    assertTrue(resultCode.isSuccess());
    assertEquals(ResultCode.SUCCESS, resultCode);
    logger.info("put key: " + key);

    // get
    Result<DataEntry> result = tairManager.get(namespace, key);
    assertTrue(result.isSuccess());
    assertEquals(ResultCode.SUCCESS, result.getRc());
    assertEquals(value, (result.getValue()).getValue());
    assertEquals(1, (result.getValue()).getVersion());
    assertEquals(key, (result.getValue()).getKey());
    logger.info("get key: " + key + "  value: " + result.getValue().getValue()
            + " version: " + result.getValue().getVersion());

    value = "val_" + UUID.randomUUID().toString();

    // put with wrong version
    resultCode = tairManager.put(namespace, key, value, 10);
    assertFalse(resultCode.isSuccess());
    assertEquals(ResultCode.VERERROR, resultCode);
    logger.info("put key with wrong version: " + resultCode.toString());

    // put with right version
    resultCode = tairManager.put(namespace, key, value, 1);
    assertTrue(resultCode.isSuccess());
    assertEquals(ResultCode.SUCCESS, resultCode);
    logger.info("put key with right version: " + resultCode.toString());

    // get
    result = tairManager.get(namespace, key);
    assertTrue(result.isSuccess());
    assertEquals(ResultCode.SUCCESS, result.getRc());
    assertEquals(value, (result.getValue()).getValue());
    assertEquals(2, (result.getValue()).getVersion());
    assertEquals(key, (result.getValue()).getKey());
    logger.info("get key: " + key + "  value: " + result.getValue().getValue()
            + " version: " + result.getValue().getVersion());
}

@Test
public void put_with_expire_time_get_normal() {
    String key = "key_" + UUID.randomUUID().toString();
    String value = "val_" + UUID.randomUUID().toString();

    // delete if exist
    tairManager.delete(namespace, key);

    // put whit 2s expire time
    ResultCode resultCode = tairManager.put(namespace, key, value, 0, 2);
    assertTrue(resultCode.isSuccess());
    assertEquals(ResultCode.SUCCESS, resultCode);
    logger.info("put key: " + key);

    // get
    Result<DataEntry> result = tairManager.get(namespace, key);
    assertTrue(result.isSuccess());
    assertEquals(ResultCode.SUCCESS, result.getRc());
    assertEquals(value, (result.getValue()).getValue());
    assertEquals(1, (result.getValue()).getVersion());
    assertEquals(key, (result.getValue()).getKey());
    logger.info("get key: " + key + "  value: " + result.getValue().getValue());

    logger.info("wait for 3s ...");
    SleepForSeconds(3);

    // get
    result = tairManager.get(namespace, key);
    assertTrue(result.isSuccess());
    assertEquals(ResultCode.DATANOTEXSITS, result.getRc());
    logger.info("get key result: " + result.getRc().toString());

}

@Test
public void put_with_abs_expire_time_get_normal() {
    String key = "key_" + UUID.randomUUID().toString();
    String value = "val_" + UUID.randomUUID().toString();

    // delete if exist
    tairManager.delete(namespace, key);

    long now_time = System.currentTimeMillis() / 1000;

    // put whit 2s expire time
    ResultCode resultCode = tairManager.put(namespace, key, value, 0, (int)now_time + 2);
    assertTrue(resultCode.isSuccess());
    assertEquals(ResultCode.SUCCESS, resultCode);
    logger.info("put key: " + key);

    // get
    Result<DataEntry> result = tairManager.get(namespace, key);
    assertTrue(result.isSuccess());
    assertEquals(ResultCode.SUCCESS, result.getRc());
    assertEquals(value, (result.getValue()).getValue());
    assertEquals(1, (result.getValue()).getVersion());
    assertEquals(key, (result.getValue()).getKey());
    logger.info("get key: " + key + "  value: " + result.getValue().getValue());

    logger.info("wait for 3s ...");
    SleepForSeconds(3);

    // get
    result = tairManager.get(namespace, key);
    assertTrue(result.isSuccess());
    assertEquals(ResultCode.DATANOTEXSITS, result.getRc());
    logger.info("get key result: " + result.getRc().toString());

}

@Test
public void put_with_version_max_get_normal() {
    String key = "key_" + UUID.randomUUID().toString();
    String value = "val_" + UUID.randomUUID().toString();

    // delete if exist
    tairManager.delete(namespace, key);

    // put
    ResultCode resultCode = tairManager.put(namespace, key, value);
    assertTrue(resultCode.isSuccess());
    assertEquals(ResultCode.SUCCESS, resultCode);
    logger.info("put key: " + key);

    logger.info("update key for 32767 times...");
    for (int i = 0; i < 32767; i++) {
        resultCode = tairManager.put(namespace, key, value);
        assertTrue(resultCode.isSuccess());
        assertEquals(ResultCode.SUCCESS, resultCode);
    }

    // get
    Result<DataEntry> result = tairManager.get(namespace, key);
    assertTrue(result.isSuccess());
    assertEquals(ResultCode.SUCCESS, result.getRc());
    assertEquals(value, (result.getValue()).getValue());
    assertEquals(-32768, (result.getValue()).getVersion());
    assertEquals(key, (result.getValue()).getKey());
    logger.info("get key: " + key + "  value: " + result.getValue().getValue()
            + " version: " + result.getValue().getVersion());
}

@Test
public void put_with_error_version_get_normal() {
    String key = "key_" + UUID.randomUUID().toString();
    String value = "val_" + UUID.randomUUID().toString();

    // delete if exist
    tairManager.delete(namespace, key);

    // put
    ResultCode resultCode = tairManager.put(namespace, key, value);
    assertTrue(resultCode.isSuccess());
    assertEquals(ResultCode.SUCCESS, resultCode);
    logger.info("put key: " + key);

    // put
    resultCode = tairManager.put(namespace, key, value, -100);
    assertFalse(resultCode.isSuccess());
    assertEquals(ResultCode.VERERROR, resultCode);
    logger.info("put key(version: -100) result:" + resultCode.toString());
}

@Test
public void inc_test() {
    logger.warn("\n\nBegin Test(" + _FILE_() + _FUNC_() + "):\n");

    String key = "key_" + UUID.randomUUID().toString();

    // delete if exist
    tairManager.delete(namespace, key);

    // inc
    Result<Integer> result = tairManager.incr(namespace, key, 1, 0, 0);
    assertTrue(result.isSuccess());
    assertEquals(ResultCode.SUCCESS, result.getRc());
    assertEquals(result.getValue().intValue(), 1);
    logger.info("incr key: " + key + "  value: " + result.getValue().intValue());

    for (int i = 0; i < 10; ++i) {
        result = tairManager.incr(namespace, key, 1, 0, 0);
        assertTrue(result.isSuccess());
        assertEquals(ResultCode.SUCCESS, result.getRc());
        assertEquals(result.getValue().intValue(), 2 + i);
        logger.info("incr key: " + key + "  value: " + result.getValue().intValue());
    }
}

@Test
public void inc_with_bound_test() {
    logger.warn("\n\nBegin Test(" + _FILE_() + _FUNC_() + "):\n");

    String key = "key_" + UUID.randomUUID().toString();

    // delete if exist
    tairManager.delete(namespace, key);

    // dec
    Result<Integer> result = tairManager.incr(namespace, key, 1, 0, 0, 0, 5);
    assertTrue(result.isSuccess());
    assertEquals(ResultCode.SUCCESS, result.getRc());
    assertEquals(result.getValue().intValue(), 1);
    logger.info("incr key: " + key + "  value: " + result.getValue().intValue());

    for (int i = 0; i < 4; ++i) {
        result = tairManager.incr(namespace, key, 1, 0, 0, 0, 5);
        assertEquals(ResultCode.SUCCESS, result.getRc());
        assertTrue(result.isSuccess());
        assertEquals(result.getValue().intValue(), 2 + i);
        logger.info("incr key: " + key + "  value: " + result.getValue().intValue());
    }

    result = tairManager.incr(namespace, key, 1, 0, 0, 0, 5);
    assertFalse(result.isSuccess());
    assertEquals(ResultCode.COUNTER_OUT_OF_RANGE, result.getRc());
    logger.info("incr key(over bound) result: " + result.getRc().toString());
}

@Test
public void dec_test() {
    logger.warn("\n\nBegin Test(" + _FILE_() + _FUNC_() + "):\n");

    String key = "key_" + UUID.randomUUID().toString();

    // delete if exist
    tairManager.delete(namespace, key);

    // inc
    Result<Integer> result = tairManager.decr(namespace, key, 1, 10, 0);
    assertTrue(result.isSuccess());
    assertEquals(ResultCode.SUCCESS, result.getRc());
    assertEquals(result.getValue().intValue(), 9);
    logger.info("incr key: " + key + "  value: " + result.getValue().intValue());

    for (int i = 0; i < 10; ++i) {
        result = tairManager.decr(namespace, key, 1, 0, 0);
        assertTrue(result.isSuccess());
        assertEquals(ResultCode.SUCCESS, result.getRc());
        assertEquals(result.getValue().intValue(), 8 - i);
        logger.info("incr key: " + key + "  value: " + result.getValue().intValue());
    }
}

@Test
public void dec_with_bound_test() {
    logger.warn("\n\nBegin Test(" + _FILE_() + _FUNC_() + "):\n");

    String key = "key_" + UUID.randomUUID().toString();

    // delete if exist
    tairManager.delete(namespace, key);

    // dec
    Result<Integer> result = tairManager.decr(namespace, key, 1, 10, 0, 0, 10);
    assertTrue(result.isSuccess());
    assertEquals(ResultCode.SUCCESS, result.getRc());
    assertEquals(result.getValue().intValue(), 9);
    logger.info("incr key: " + key + "  value: " + result.getValue().intValue());

    for (int i = 0; i < 9; ++i) {
        result = tairManager.decr(namespace, key, 1, 10, 0, 0, 10);
        assertEquals(ResultCode.SUCCESS, result.getRc());
        assertTrue(result.isSuccess());
        assertEquals(result.getValue().intValue(), 8 - i);
        logger.info("incr key: " + key + "  value: " + result.getValue().intValue());
    }

    result = tairManager.decr(namespace, key, 1, 10, 0, 0, 10);
    assertFalse(result.isSuccess());
    assertEquals(ResultCode.COUNTER_OUT_OF_RANGE, result.getRc());
    logger.info("incr key(over bound) result: " + result.getRc().toString());
}

@Test
public void set_count_test() {
    logger.warn("\n\nBegin Test(" + _FILE_() + _FUNC_() + "):\n");

    String key = "key_" + UUID.randomUUID().toString();

    // delete if exist
    tairManager.delete(namespace, key);

    // inc -> 1
    Result<Integer> result = tairManager.incr(namespace, key, 1, 0, 0);
    assertTrue(result.isSuccess());
    assertEquals(ResultCode.SUCCESS, result.getRc());
    assertEquals(result.getValue().intValue(), 1);
    logger.info("incr key: " + key + "  value: " + result.getValue().intValue());

    // incr -> 2
    result = tairManager.incr(namespace, key, 1, 0, 0);
    assertTrue(result.isSuccess());
    assertEquals(ResultCode.SUCCESS, result.getRc());
    assertEquals(result.getValue().intValue(), 2);
    logger.info("incr key: " + key + "  value: " + result.getValue().intValue());

    // set count -> 10
    ResultCode resultCode = tairManager.setCount(namespace, key, 10);
    assertTrue(resultCode.isSuccess());
    logger.info("Set value is 10");

    // incr -> 11
    result = tairManager.incr(namespace, key, 1, 0, 0);
    assertTrue(result.isSuccess());
    assertEquals(ResultCode.SUCCESS, result.getRc());
    assertEquals(result.getValue().intValue(), 11);
    logger.info("incr key: " + key + "  value: " + result.getValue().intValue());

}

@Test
public void set_count_with_version_expire_test() {
    logger.warn("\n\nBegin Test(" + _FILE_() + _FUNC_() + "):\n");

    String key = "key_" + UUID.randomUUID().toString();

    // delete if exist
    tairManager.delete(namespace, key);

    // inc -> 1
    Result<Integer> result = tairManager.incr(namespace, key, 1, 0, 0);
    assertTrue(result.isSuccess());
    assertEquals(ResultCode.SUCCESS, result.getRc());
    assertEquals(result.getValue().intValue(), 1);
    logger.info("incr key: " + key + "  value: " + result.getValue().intValue());

    // set count -> 10
    ResultCode resultCode = tairManager.setCount(namespace, key, 10, 0, 2);
    assertTrue(resultCode.isSuccess());
    logger.info("Set value is 10, expire time is 2s");

    logger.info("wait for 3s ...");
    SleepForSeconds(3);

    // incr (default value is 1)
    result = tairManager.incr(namespace, key, 1, 0, 0);
    assertTrue(result.isSuccess());
    assertEquals(ResultCode.SUCCESS, result.getRc());
    assertEquals(result.getValue().intValue(), 1);
    logger.info("incr key: " + key + "  value: " + result.getValue().intValue());
}

@Test
public void set_count_try_put_is_ok_test() {
    logger.warn("\n\nBegin Test(" + _FILE_() + _FUNC_() + "):\n");

    String key = "key_" + UUID.randomUUID().toString();

    // delete if exist
    tairManager.delete(namespace, key);

    // inc -> 1
    Result<Integer> result = tairManager.incr(namespace, key, 1, 0, 0);
    assertTrue(result.isSuccess());
    assertEquals(ResultCode.SUCCESS, result.getRc());
    assertEquals(result.getValue().intValue(), 1);
    logger.info("incr key: " + key + "  value: " + result.getValue().intValue());

    ResultCode resultCode = tairManager.put(namespace, key, "test_value");
    assertTrue(resultCode.isSuccess());
    assertEquals(ResultCode.SUCCESS, resultCode);

}

@Test
public void put_try_incr_test() {
    logger.warn("\n\nBegin Test(" + _FILE_() + _FUNC_() + "):\n");

    String key = "key_" + UUID.randomUUID().toString();

    // delete if exist
    tairManager.delete(namespace, key);

    ResultCode resultCode = tairManager.put(namespace, key, "test_value");
    assertTrue(resultCode.isSuccess());
    assertEquals(ResultCode.SUCCESS, resultCode);
    logger.info("put key: " + key);

    // inc
    Result<Integer> result = tairManager.incr(namespace, key, 1, 0, 0);
    assertFalse(result.isSuccess());
    assertEquals(ResultCode.CANNT_OVERRIDE, result.getRc());
}
```


# 6. 区别

### ldb

- 适用于确实有**持久化需求**，**读写QPS较高**（万级别）的应用场景
- LDB 虽然可以抗较高的 QPS，但是比起MDB来说还是差十来倍，因此 LDB 不适用当缓存来用，这种场景要用 MDB + DB 来替代
- 不支持事务
- LDB 不支持类似于mysql like类的查询功能，因此比如key包含部分固定前缀的，是无法查询的。必须要指定key查询
- LDB 适合高并发访问场景，但并不适合大吞吐量场景（单KV 百k级别及以上），大吞吐量的场景完全无法发挥LDB的高性能高并发能力，对整体集群性能影响大
- 分布式锁 version
- LDB数据同步默认单元全量同步到中心，因此开通单元同步指开通中心到单元的同步。开通单元同步后，中心和单元之间就开始了双向同步（仅以下环境支持）

| **BU** | **环境标** | **环境名** |
| --- | --- | --- |
| 集团 | center | 线上中心（张北中心） |
|  | unsh | 上海单元 |
|  | unsz | 深圳单元 |
| 优酷 | zbyk | 张北优酷 |
|  | aliyun-vpc-hk | 香港VPC |


### mdb

- MDB特别适用**容量小**（一般在M级别，50G之内），**读QPS高**（万级别）的缓存场景, 读多写少
- 无法保证数据的安全性
- 不支持事务、模糊查询、单元间同步

### rdb

- RDB有两种存储需求供选择：缓存和持久化
- 标准版实例组最大容量限制为16G，集群版实例组最大容量限制为10
- 不支持类似于mysql like类的查询功能
- RDB适合高并发场景，但不适合大吞吐场景
- 支持单元间数据同步（同ldb）
