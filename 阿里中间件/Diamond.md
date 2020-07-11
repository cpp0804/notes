# Diamond

# 1. Brief Introduction

Diamond is distributed configuration center, provide the service of pushing real time configuration

# 2. Use Constraints

- configuration center only save the newest status of data,not the changing process of it(it may ignore data's middle change like combining ). But it guarantee the same order of receive and send.

e.g.  sender sequential send three data A B C, subcriber may receive A B C, or A C, or just C

- the main effect of center is release Meta-data but not store data. The released data should smaller than 100k.

- center may send repeat data to subscriber.

- center doesn't promise the receive order of subscribers.

- center doesn't promise read-write consistency


# 3. Quick Start

- add maven reliance

```java
<dependency>
  <groupId>com.taobao.diamond</groupId>
  <artifactId>diamond-client</artifactId>
  <version>3.8.3</version>
</dependency>
```


- example code 

```java
import java.io.IOException;

import com.taobao.diamond.client.Diamond;
import com.taobao.diamond.manager.ManagerListenerAdapter;

/**
 * 配置中心，管理所有动态配置;基本用法
 * 
 * @author ConfigCenter
 *
 */
public class ConfigCenter {
    // 属性/开关
    private static String config = "";

    private static void initConfig() {
        // 启动只用一次场景，直接get获取配置值
        try {
            String configInfo = Diamond
                    .getConfig("yanlin", "yanlin", 1000);
            System.out.println("dataId+group:" + configInfo);
        } catch (IOException e1) {

        }

        // 启动用，并且变化需要立即推送最新值
        Diamond.addListener("yanlin", "yanlin",
                new ManagerListenerAdapter() {
                    public void receiveConfigInfo(String configInfo) {
                        try {
                            config = configInfo;
                            System.out.println(configInfo);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public static void main(String[] args) throws IOException {
        // 如果使用spring，此类等同于init方法
        initConfig();
        // 测试让主线程不退出，因为订阅配置是守护线程，主线程退出守护线程就会退出，实际代码中不需要。
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
    }
    // 通过get接口把配置值暴露出去使用
    public static String getConfig() {
        return config;
    }
}
```



# 4. Diamond-client API

Diamond-client API is provided by static functions in class Diamond (Diamond.XXX) .

services that api provides:

- release: create update delete
- obtain: synchronized obtain configuration and asynchronized listen the change of server

## 4.1 Three Main Developing Interfaces

- Diamond: get access to diamond in same environment

- DiamondUnitSite:get access to dimanond across environment

- DiamondAdvance: implements multi-tenant interface


## 4.2 Diamond

- get data 

```java
/**
* @param dataId   configId[unique、value:package.class、small letter、only contains letter and . : - _ ]
* @param group    config Group unique
*/
static public String getConfig(String dataId, String group, long timeoutMs) throws IOException
```

```java
try {
    String content = Diamond.getConfig("yanlin", "yanlin", 3000);
        System.out.println(content);
} catch (Exception e1) {
    // TODO Auto-generated catch block
    e1.printStackTrace();
}
```


- listen configuration, push configuration change 

```java
//single
static public void addListener(String dataId, String group, ManagerListener listener)

//batch
static public void addListeners(String dataId, String group, List<ManagerListener> listeners)
```

ManagerListener will listen data change in server, and report change to client

```java
public interface ManagerListener {    
   /**
    * return a thread pool
    */
    public Executor getExecutor();


    /**
     * receive configuration information, handle new data   
     * a callback method,it will be packaged in Runnable object 
     * @param configInfo
     */
    public void receiveConfigInfo(final String configInfo);
}



try {
    Diamond.addListener("yanlin", "yanlin", new ManagerListener() {

        public void receiveConfigInfo(String configInfo) {
            // 应用方的回调方法逻辑是快速调用，即执行耗时比较少。否则会阻塞其他通知线程。有阻塞任务需实现以下自定义执行线程
            System.out.println(configInfo);
        }

        public Executor getExecutor() {
            // 如果回调线程调用耗时或者有阻塞情况，自己实现独立回调线程，不要影响主线程。
            return null;
        }
    });
} catch (Exception e) {
    e.printStackTrace();
}
```


if you don't need a thread pool, you can extend ManagerListenerAdapter

```java
public class MyListener extends ManagerListenerAdapter{
    public void receiveConfigInfo(String configInfo) {
        if(configInfo == null)
            System.out.println("in listener data remove");        
        else
            System.out.println("in listener data update：" + configInfo);        
    }
}

/**
* add listener
*/
static public void addListener(){
        String dataId = "my_diamond_test";
        String group = "DEFAULT_GROUP";
        Diamond.addListener(dataId, group, new MyListener());
        System.out.println("add listener success");
    }
```


- delete lisenter

```java
//single
static public void removeListener(String dataId, String group, ManagerListener listener)

//batch
static public List<ManagerListener> getListeners(String dataId, String group)
```


- push data

```java
static public boolean publishSingle(String dataId, String group, String content)

/**
* @param appName  the owner of content
*/
static public boolean publishSingle(String dataId, String group, String appName, String content)
```


- delete data 

```java
static public boolean remove(String dataId, String group)
```


- push aggregation data 

aggregation data is like <key - 子key, value>，multi data is published in separate, and diamond backstage will combine it to one data.

```java
/**
* @param datumId  child configId
*/
static public boolean publishAggr(String dataId, String group, String datumId, String content)
    
static public boolean publishAggr(String dataId, String group, String datumId, String appName, String content)    
    
try {
    Diamond.publishAggr("pingwei.test", "testGroup", "datumId1", "ip1");
    Diamond.publishAggr("pingwei.test", "testGroup", "datumId2", "ip2");
    Thread.sleep(3000);
    String content = Diamond.getConfig("pingwei.test", "testGroup", 3000);
    System.out.println(content);
} catch (Exception e) {
    e.printStackTrace();
}
```


- delete aggregation data

```java
static public boolean removeAggr(String dataId, String group, String datumId)
```



- assign IP to create Diamond object 

```java
/**
* @param serverIps  targeted server Ip
* @return DiamondEnv   use this object to CRUD diamond
*/
static public DiamondEnv getTargetEnv(String... serverIps)


try {
    DiamondEnv diamond = Diamond.getTargetEnv("100.67.0.17");
    String content = diamond.getConfig("yanlin", "yanlin", 1000);
    System.out.println(content);
} catch (Exception e1) {
    // TODO Auto-generated catch block
    e1.printStackTrace();
}
```



## 4.3 DiamondUnitSite

change between center diamond cluster and default diamond cluster

- get diamond instance in certain environment

```java
/**
* @param unitName environment tag
*/
static public DiamondEnv getDiamondUnitEnv(String unitName)


try {
    DiamondEnv diamond = DiamondUnitSite.getDiamondUnitEnv("daily");
    String content = diamond.getConfig("yanlin", "yanlin", 1000);
    System.out.println(content);
} catch (Exception e1) {
    // TODO Auto-generated catch block
    e1.printStackTrace();
}
```


- push data to current environment and same group environment

```java
static public void publishToAllUnit(String dataId, String group, String content)

static public void publishToAllUnit(String dataId, String group, String appName, String content) throws IOException
```


- delete data in current environment and same group environment

```java
static public void removeToAllUnit(String dataId, String group) throws IOException
```


- get diamon instance in all environment

```java
static public List<DiamondEnv> getUnitList()
```


- push or delete aggregation data in all environment

there not exits interface that directly implements the function, but you can combine other interface to implements the function

```java
public void tesPulishAggr_聚合数据多单元推送() throws Exception{
      String dataId= "NS_DIAMOND_SUBSCRIPTION_TOPIC_chenwztest";
      String group = "DEFAULT_GROUP";
      String datumId = "somebody-pub-test";
      String content = "xxxx";
      List units = DiamondUnitSite.getUnitList();
      for(DiamondEnv env: units){
          env.publishAggr(dataId, group, datumId, content);
      }
  }
```


- judge the current diamond is pointed to center diamond cluster or not

```java
static public boolean isInCenterUnit()
```


- switch to center diamond cluster

```java
static public void switchToCenterUnit()
```


- switch to default diamond cluster

```java
static public void switchToLocalUnit()
```


- get center diamond environment 

```java
static public DiamondEnv getCenterUnitEnv()
```


- get default diamond environment 

```java
static public DiamondEnv getLocalUnitEnv()
```



## 4.4 Gated Launch

Controll the speed and range of launch, choose a certain range of people to launch not all of them.A/B testing is a kind of gated launch. Let a part of user continue to user productA, and the other use productB. If productB can satisfy user, then gradually migrate all user to use productB.


diamond supports three kind of gated launch:

1. gated launch based on IP (beta)

Appoint several ip, only let new configuration in these ip. This kind can be used before official release.



2. gated launch based on VipServer



3. gated launch in batches

diamond choose ip in random according to batches




## 4.5 Annotation

Basic annotations:

- [@PropertySource](#) and PropertySources

```java

@PropertySources({ 
    @PropertySource(value = "classpath:serverCfg.properties", ignoreResourceNotFound = true),
    @PropertySource(value = "diamond:///kunyu_test/com.kunyu.datasource.yaml", ignoreResourceNotFound = true) 
 })
public interface AppConfig {

     @Value("${server.port}")
     int port();

     @Value("${server.listenaddress}")
     String listenAddress();

     @Value("${database.jdbc.url}")
     String getDbUrl();

     @Value("${database.username}")
    String getUserName();
}
```



- 

Sometimes in a big configuration file, we will configure different prefix to different module
```java
...
# EMBEDDED MONGODB (EmbeddedMongoProperties)
spring.mongodb.embedded.features=SYNC_DELAY 
spring.mongodb.embedded.version=2.6.10

# REDIS (RedisProperties)
spring.redis.host=localhost
spring.redis.port=6379
spring.redis.pool.max-active=8 
spring.redis.pool.max-idle=8
...
    
    
@Prefix("spring.redis")
@PropertySource("diamond:///kunyu_group/com.spring.boot.properties")
public interface RedisAppConfig {

     @Value("${host}")
     public String getRedisHost();

    @Value("${port}")
     public int getRedisPort();

    @Value("${pool.max-active}")
    public int getRedisPoolMaxActive();

    @Value("${pool.max-idle}")
    public int getRedisPoolMaxIdle();

}
```


- 

application can use a default configuration when start in first time

```java
@PropertySource("diamond:///kunyu_group/com.spring.defaultvalue.properties")
public interface RedisAppConfig {

     @Value("${host}")
     @DefaultValue("warning")
     public String logLevel();

     @Value(${max.thread.count:15})
     public String maxThreadsCount()

}
```


1. non spring application

- maven dependency

```java
    <dependency>
        <groupId>com.taobao.diamond</groupId>
        <artifactId>diamond-client-annotation</artifactId>
        <version>0.0.1</version>
    </dependency>
```


- configure appName and group

```java
application.name=myFirstApp  //must same with Aone application
application.group=com.alibaba.kunyu.group
application.diamond.group=tenant_kunyu
```



- create configuration file

```java
port = 80
listenAddress = 192.168.1.151
maxThreads = 100
logDebugEnabled = false
connectorPortMap = server1 = 8001, server2 = 8002
```



- custom configuration interface

```java
public interface AppConfig {
    int port();
    String listenAddress();
    int maxThreads();
    boolean logDebugEnabled();
    Map<String,Integer> connectorPortMap();
}
```




- configure in program

```java
PropertyProvider provider = new PropertyProviderBuilder()
          .withPropertySource(source)
          .withEnvironment(new StandardEnvironment())
          .withReloadStrategy(new PeriodicalReloadStrategy(5, TimeUnit.SECONDS))
          .build();
AppConfig appConfig = provider.bind("appCfg", AppConfig.class);

 // now use the config freely in your app!
System.out.println(appConfig.listenAddress()+":" + appConfig.port());
if(appConfig.logDebugEnabled()) {
        System.out.println(appConfig.connectorPortMap());
}
```



2. spring framework application

- maven dependency

```java
 	<dependency>
        <groupId>com.taobao.diamond</groupId>
        <artifactId>diamond-client-spring</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>
```


- configure appName and group

```java
spring.application.name=myFirstSpringApp
spring.application.group=com.alibaba.kunyu.springgroup
spring.diamond.group=tenant_kunyu
```


- create configuration file

```java
port = 80
listenAddress = 192.168.1.151
maxThreads = 100
logDebugEnabled = false
connectorPortMap = server1 = 8001, server2 = 8002
```


- configure in spring

```java
import com.taobao.diamond.spring.Diamond;

@Configuration
//import diamond configuration support
@Import({Diamond.class})    
public class AppConfig {

   // @Bean application customized beans

}


import org.springframework.beans.factory.annotation.Value;

@Component(value = "oneAppBean")
public class OneBean {

    //use @Value to inject configuration value
    @Value("${port}")
    private int port;

    @Value("${connectorPortMap}")
    private Map<String, Integer> connectorPortMap;

    public int getPort() {
        return port;
    }

    public Map<String, Integer> getConnectorPortMap(){
         return connectorPortMap;
    }
}



@PropertySource(value = "diamond:///kunyu_test/com.kunyu.dbtest.properties", ignoreResourceNotFound = true) })
public class AnotherBean {
    @Reloadable
    @Value("${db.url.0}")
    private String dbUrl = null;
}



//  "diamond:// Resource" protocal
//method one
@PropertySource("diamond://envid/group/com.test.dataid")
public interface DiamondResourceAppConfig {

    @Value("${host}")
    public String host();

}

//method two:directly use bare Resource
package com.kunyu.diamond.atest;

import java.net.URL;
import java.util.Properties;

public class DiamondResourceSample {

    public static void main(String[] args) throws Exception {

        Properties properties = new Properties();

        URL diamondResource = new URL("diamond:///tenant_kunyu/com.alibaba.kunyu.group:myFirstApp.properties");

        properties.load(diamondResource.openStream());

        System.out.println(properties);
    }
}

```



3. spring boot application

- maven dependency

```java
<dependencyManagement>
    <dependencies>
        <dependency>
            <!-- Import dependency management from Spring Boot -->
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>1.5.20.RELEASE</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        <dependency>
            <groupId>com.taobao.pandora</groupId>
            <artifactId>pandora-boot-starter-bom</artifactId>
            <version>2019-04-stable</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
	<dependency>
    	<groupId>com.alibaba.boot</groupId>
    	<artifactId>pandora-diamond-spring-boot-starter</artifactId>
	</dependency>
</dependencies>
```


- configure

```java
//in application.properties,configuration will be loaded when application started
spring.diamond.data-id=com.taobao.middleware:test.properties
# 可以不用配置，默认值是 DEFAULT_GROUP
spring.diamond.group-id=
    
//several diamond needs to configure 
spring.diamonds[0].data-id=
spring.diamonds[0].group-id=

spring.diamonds[1].data-id=
spring.diamonds[1].group-id=
    
    
//use @DiamondPropertySource
@SpringBootApplication
@PropertySource("classpath:application.properties")
@DiamondPropertySource(dataId = "com.taobao.middleware:test.properties")
public class Application {
...
}


@DiamondPropertySources({ @DiamondPropertySource(dataId = "com.alibaba.boot:demo-application.properties"),
        @DiamondPropertySource(dataId = "com.taobao.middleware:test.properties") })
public class Application {
...
}

//use @PropertySource
@SpringBootApplication
@PropertySource({"classpath:application.properties", "diamond:/com.taobao.middleware:test.properties"})
public class Application {

    public static void main(String[] args) throws IOException {
        PandoraBootstrap.run(args);
        ApplicationContext context = SpringApplication.run(Application.class, args);
        ...
    }
}
```


- DiamondListener

 It will listen the change of configuration and execute callback method (received), but not update configuration automatically. If you want new configuration updated automatically, you can use switchcenter

```java
@DiamondListener(dataId = "com.taobao.middleware:configFromListener.properties")
public class DiamondDataCallbackDemo implements DiamondDataCallback {

    @Autowired
    ConfigFromListener configFromListener;

    @Override
    public void received(String data) {
        try {
            System.out.println("Before listener, configFromListener: " + configFromListener);

            Properties properties = new Properties();
            properties.load(new InputStreamReader(new ByteArrayInputStream(data.getBytes())));
            System.out.println("Rreceived from diamond listener: " + properties);

            // 把properties的值注入到 configFromListener 里。配置有前缀的用 RelaxedDataBinder(Object target, String namePrefix)
            new RelaxedDataBinder(configFromListener).bind(new MutablePropertyValues(properties));

            System.out.println("After listener, configFromListener: " + configFromListener);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
```



