# dubbo

# 1. dubbo负载均衡

## 1.1 负载均衡算法类
![负载均衡算法类](./pic/dubbo_负载均衡算法类.png)

## 1.2 provider节点
![provider节点](./pic/dubbo_provider节点.png)


## 1.3 URL Uniform Resource Locator(不可变的、线程安全的)
![URL](./pic/dubbo_URL.png)

```java
private volatile transient String identity;
private final String protocol;
private final String username;
private final String password;
private final String host;
private final int port;
private final String path;
private volatile transient String ip;
private volatile transient String full;

//从numbers或者parameters中获取某个方法的某个参数值
public int getMethodParameter(String method, String key, int defaultValue) {
    String methodKey = method + "." + key;
    Number n = getNumbers().get(methodKey);
    //如果numbers中参数存在该方法的某个参数
    if (n != null) {
        return n.intValue();   
    }
    //如果numbers中参数不存在该方法的某个参数，就去parameters中取
    String value = getMethodParameter(method, key);
    //如果parameters中也没有，就返回默认值
    if (value == null || value.length == 0) {
        return defaultValue;   
    }
    int i = Integer.parseInt(value);
    //把从parameters中取出的，numbers中不存在的参数放入numbers
    getNumbers().put(methodKey, i);
    
    return i;
}

//存放每个方法的参数以及参数对应的值: Map<方法+参数名， 参数值>
private volatile transient Map<String, Number> numbers;
private Map<String, Number> getNumbers() {
    if (numbers == null) { // 允许并发重复创建
        numbers = new ConcurrentHashMap<String, Number>();
    }
    return numbers;
}

private final Map<String, String> parameters;
public String getMethodParameter(String method, String key) {
    String value = parameters.get(method + "." + key);
    if (value == null || value.length == 0) {
        return getParameter(key);   
    }
    return value;
}

//如果parameters中都没有这个参数，那么返回默认值
public String getParameter(String key) {
    String value = parameter.get(key);
    if (value == null || value.length() == 0) {
        value = parameter.get(Constants.DEFAULT_KEY_PREFIX + key);   
    }
    return value;
}

//和getMethodParameter(String method, String key, int defaultValue)相似
public long getParameter(String key, long defaultValue) {
    //如果numbers存在这个参数，就返回
    Number n = getNumbers().get(key);
    if (n != null) {
        return n.longValue();   
    }
    //如果numbers不存在，就去parameters中找
    String value = getParameter(key);
    //如果numbers和parameters中都没有，就返回默认值
    if (value == null || value.length() ==0) {
        return defaultValue;   
    }
    long l = Long.parseLong(value);
    getNumbers().put(key, l);
    return l;
}

public String toFullString() {
	if (full != null) {
	    return full;
	}
    return full = buildString(true, true);
}

public String toIdentityString() {
    if (identity != null) {
        return identity;   
    }
    return identity = buildString(true, false);
}

private buildString(boolean appendUser, boolean appendParameter, String... parameters) {
    return buildString(appendUser, appendParameter, false, false, parameters);   
}

//    protocal://username:password@host:port/path
private buildString(boolean appendUser, boolean appendParameter, boolean userIP, boolean userService, String... parameters) {
    StringBuilder buf = new StringBuilder();
    if (protocal != null && protocal.length() > 0) {
        buf.append(protocal);
        buf.append("://");
    }
    if (appendUser && username != null && username.length > 0) {
        buf.append(username);
        if (password != null && password.length() > 0) {
            buf.append(":");
            buf.append(password);
        }
        buf.append("@");
    }
    String host;
    if (userIP) {
        host = getIp();   
    } else {
        host = getHost();   
    }
    if (host != null && host.length() > 0) {
        buf.append(host);
        if (port > 0) {
            buf.append(":");
            buf.append(port);
        }
    }
    String path;
    if (userService) {
        path = getServiceKey();   
    } else {
        path = getPath();
    }
    
    if (path != null && paht.length() > 0) {
        buf.append("/");
        buf.append(path);
    }
    if(appendParameter) {
        buildParameters(buf, true, parameters);   
    }
    return buf.toString();
}


```

## 1.4 Invocation
![Invocation](./pic/dubbo_Invocation.png)


## 1.5 AbstractLoadBalance

```java
public <T> Invoker<T> select(List<Invoker<T>> invokers, URL url, Invocation invocation) {
    if (invokers == null || invokers.size() == 0)
        return null;
    if (invokers.size() == 1)
        return invokers.get(0);
    return doSelect(invokers, url, invocation);
}

//获取某个节点的权重
protected getWeight(Invoker<?> invoker, Invocation invocation) {
    //获取该节点的权重
    int weight = invoker.getUrl().getMethodParameter(invocation.getMethodName(), Constants.WEIGHT_KEY, Constants.DEFAULT_WEIGHT);
    if (weight > 0) {
        //1.获取服务提供者启动时间戳
        long timestamp = invoker.getUrl().getParameter(Constants.TIMESTAMP_KEY, 0L);
        if (timestamp > 0L) {
            //2.计算provider运行时长=当前时间-启动时间
            int uptime = (int) (System.currentTimeMillis() - timestamp);
            //3.获取服务预热时间 默认10分钟 
            int warmup = invoker.getUrl().getParameter(Constants.WARMUP_KEY, Constants.DEFAULT_WARMUP);
            //4.如果服务运行时间小于预热时间，即服务启动未到达10分钟，就重新计算权重
            if (uptime > 0 && uptime < warmup) {
                weight = calculateWarmupWeight(uptime, warmup, weight);   
            }
        }
    }
    return weight;
}

static int calculateWarmupWeight(int uptime, int warmup, int weight) {
    // 进度百分比*权重 = (uptime / warmup) * weight
    int ww = (int) ((float) uptime / ((float) warmup / (float) weight));
    //如果ww<1,返回1;如果ww>1{如果ww大于权重，返回权重，否则返回ww}
    return ww < 1 ? 1 : (ww > weight ? weight : ww);
}

/*假设把权重设置为100，预热时间为10分钟。那么：
运行时长       公式         计算后权重
1分钟      1/10 * 100        10
2分钟      2/10 * 100        20
5分钟      5/10 * 100        50
10分钟     10/10 * 100       100

由此可见，在未达到服务预热时间之前，权重都被降级了。Dubbo为什么要这样做呢？
主要用于保证当服务运行时长小于服务预热时间时，对服务进行降权，避免让服务在启动之初就处于高负载状态。
服务预热是一个优化手段，与此类似的还有 JVM 预热。
主要目的是让服务启动后“低功率”运行一段时间，使其效率慢慢提升至最佳状态
*/
```

## 1.6 Constants

```java
public class Constants {
    
    public static final int     DEFAULT_WEIGHT                     = 100;
    
    public static final String  WEIGHT_KEY                         = "weight";
    
    public static final String  TIMESTAMP_KEY                      = "timestamp";   
    
    public static final String  WARMUP_KEY                         = "warmup";
    
    public static final int     DEFAULT_WARMUP                     = 10 * 60 * 1000;
    
    public static final String  DEFAULT_KEY_PREFIX                 = "default.";
    
    public static final Pattern COMMA_SPLIT_PATTERN                = Pattern.compile("\\s*[,]+\\s*");
}
```

## 1.7 RpcStatus

```java
//ConcurrentMap<uri, ConcurrentMap<String, RpcStatus>>
private static final ConcurrentMap<String, ConcurrentMap<String, RpcStatus>> METHOD_STATISTICS = new ConcurrentHashMap<String, ConcurrentMap<String, RpcStatus>>();
//活跃数
private final AtomicInteger active = new AtomicInteger();

public static RpcStatus getStatus(URL url, String methodName) {
    String uri = url.toIdentityString();   
    //从METHOD_STATISTICS根据uri获取ConcurrentMap<String, RpcStatus>
    ConcurrentMap<String, RpcStatus> map = METHOD_STATISTICS.get(url);
    if (map == null) {
        METHOD_STATISTICS.putIfAbsent(url, new ConcurrentHashMap<String, RpcStatus>())   
        map = METHOD_STATISTICS.get(uri);
    }
    //从ConcurrentMap<String, RpcStatus>根据methodName获取RpcStatus
    RpcStatus status = map.get(methodName);
    if (status == null) {
        map.putIfAbsent(methodName, new RpcStatus());
        status = map.get(methodName);
    }
    return status;
}

public int getActive() {
    return active.get();
}
```


## 1.8 负载均衡算法
### 1.8.1 随机负载均衡

RandomLoadBalance：随机负载均衡算法，Dubbo默认的负载均衡策略

- 选择方法：权重不同的，按找权重随机选择；权重都相同的，直接随机选择

```java
protected <T> Invoker<T> doSelect (List<Invoker<T>> invokers, URL url, Invocation invocation) {
    // provider总个数
    int length = invokers.size();
    
    //provider总权重
    int totalWeight = 0;
    
    //假设权重都一样
    boolean sameWeight = true;
    
    //计算总权重，判断各节点权重是否相同
    for (int i = 0; i < length; i++) {
        //得到各节点的权重
        int weight = getWeight(invokers.get(i), invocation);
        totalWeight += weight;
        //判断所有权重是否一样，如果sameWeight已经是false了或者i=0表示是第一个节点，就无需判断
        if (sameWeight && i > 0 && weight != getWeight(invokers.get(i - 1), invocation)) {
            sameWeight = false;  
        }
    }
   
    if (totalWeight > 0 && ! sameWeight) {
         //如果权重不同并且权重大于0，则按总权重随机选择
         int offset = random.nextInt(totalWeight);
         //确定随机值落在哪个片段上
         for (int i = 0; i < length; i++) {
             offset -= getWeight(invokers.get(i), invocation);
             if (offset < 0) {
                 return incokers.get(i);   
             }
         }
    }
    //如果各节点权重相同或者权重为0，则随机选择
    return invokers.get(random.nextInt(length));
}


```

### 1.8.2 最小活跃数算法

LeastActiveLoadBalance：活跃数越小，表明该服务提供者效率越高，单位时间内可处理更多的请求，此时应优先将请求分配给该服务提供者

dubbo为每个Invoker分配一个活跃数大小active。在被调用前active+1，被调用后active-1。active越小，表明越活跃，应该被优先调用

- 选择方法：活跃数有最小且唯一的，直接选择这个唯一的活跃数最小的。活跃数最小且有相同的多个的，按照随机负载均衡选择

```java
protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers, URL url, Invocation invocaiton) {
    int length = invokers.size();
    //最小的活跃数值
    int leastActive = -1;
    //相同最小活跃数的个数，也是leastIndexs的下标
    int leastCount = 0;
    //记录具有相同最小活跃数的provider在invokers中的下标
    int [] leastIndexs = new int[length];
    //有相同最小活跃数的provider的总权重
    int totalWeight = 0;
    //第一个遇到的具有最小活跃数的provider的权重
    int firstWeight = 0;
    //判断具有相同最小活跃数的provider的权重是否都相同
    boolean sameWeight = true;
    
    for (int i = 0; i < length; i++) {
        Invoker<T> invoker = invokers.get(i);
        //得到当前invoker的活跃数
        int active = RpcStatus.getStatus(invoker.getUrl(), invocation.getMethodName()).getActive();
        //得到当前invoker的权重
        int weight = invoker.getUrl().getMethodParameter(invocation.getMethodName(), Constants.WEIGHT_KEY, Constants.DEFAULT_WEIGHT);
        //如果leastActive = -1(表示是第一个provider) 或者当前provider的活跃数比leastActive小,对新的最小活跃数进行统计
        if (leastActive == -1 || active < leastActive) {
            leastActive = active;
            leastCount = 1;
            leastIndexs[0] = i;
            totalWeight += weight;
            firstWeight = weight;
            sameWeight = true;
        } else if (active == leastActive) {
            leastIndexs[leastCount ++] = active;
            totalWeight += weight;
            if (sameWeight && i>0 && weight != firstWeight) {
                sameWeight = false;   
            }
        }
    }
    //如果只有一个活跃数最小的，直接返回这个provider
    if (leastCount == 1) {
        return invokers.get(leastIndexs[0]);   
    }
    
    //如果具有相同活跃数的provider权重不相同并且总权重大于0
    if (! sameWeight && totalWeight > 0) {
        int offsetWeight = random.nextInt(totalWeight);
        for (int i = 0; i < leastCount; i++) {
            int leastIndex = leastIndexs[i];
            offsetWeight -= getWeight(invokers.get(leastIndex), invocation);
            if (offsetWeight < 0) {
                return invokers.get(leastIndex);   
            }
        }
    }
    // 如果权重相同或权重为0则均等随机
    return invokers.get(leastIndexs[random.nextInt(leastCount)]);
}
```


### 1.8.3 Hash一致性算法

ConsistentHashLoadBalance：先构造长度为2的32次的整数环(一致性hash环)，根据provider名称的hash值将provider节点放在环上，然后根据数据的key得到他对应的hash值，在环上根据hash值顺时针查找离数据hash值最近的provider的hash值，完成从key到provider节点的查找

- 选择方法：选择哪个provider只受参数值的影响，具有相同参数值的请求将被分配给同一个provider

- 一致性hash算法

```java
//每个invokers列表中的provider放在一个ConsistentHashSelector中
private final ConcurrentMap<String, ConsistentHashSelector<?>> selectors = new ConcurrentHashMap<String, ConsistentHashSelector<?>>();

protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers, URL url, Invocation invocation) {
	//请求类名+方法名，比如：com.viewscenes.netsupervisor.service.InfoUserService.sayHello
    String key = invokers.get(0).getUrl().getServiceKey() + "." + invocation.getMethodName();
    
    //对当前的invokers进行hash取值
    int identityHashCode = System.identityHashCode(invokers);
    ConsistentHashSelector<T> selector = (ConsistentHashSelector<T>) selectors.get(key);
    
    //如果服务提供者列表发生变化，那么它们两次的HashCode取值会不同，此时会重新创建ConsistentHashSelector对象
    if (selector == null || selector.getIdentityHashCode() != identityHashCode) {
        selectors.put(key, new ConsistentHashSelector<T>(invokers, invocation.getMethodName(), identityHashCode));
        selector = (ConsistentHashSelector<T>) selectors.get(key);
    }
    return selector.select(invocation);
}

public static final class ConsistentHashSelector<T> {
    //存储Invoker虚拟节点 invoker1-1,invoker1-2,...,invoker2-1,invoker2-2,...
    private final TreeMap<Long, Invoker<T>> virtualInvokers;
    //虚拟节点数量，默认160
    private final int replicaNumber;
    //服务提供者列表invokers的hash值
    private final int identityHashCode;
    //参数下标
    private final int[] argumentIndex;
    
    public ConsistentHashSelector(List<Invoker<T>> invokers, String methodName, int identityHashCode) {
        this.virtualInvokers = new TreeMap<Long, Invoker<T>>();
        //System.identityHashCode默认返回hashCode()的值
        this.identityHashCode = System.identityHashCode(invokers);
        URL url = invokers.get(0).getUrl();
        //获取虚拟节点数
        this.replicaNumber = url.getMethodParameter(methodName, "hash.nodes", 160);
        //默认对第一个参数进行hash取值
        String[] index = Constants.COMMA_SPLIT_PATTERN.split(url.getMethodParameter(methodName, "hash.arguments", "0"));
        argumentIndex = new int[index.length];
        for (int i = 0; i< index.length; i++) {
            argumentIndex[i] = Integer.parseInt(index[i]);   
        }
        //循环创建虚拟节点Invoker
        for (Invoker<T> invoker : invokers) {
             for (int i = 0; i < replicaNumber /4; i++) {
                 //获取通信服务器地址address，再对address+i进行mdb运算得到一个数组
                 byte[] digest = md5(invoker.getUrl().toFullString() + i);
                 //对数组中的字节进行4次hash运算得到long值，将long值作为key,invoker作为value存入TreeMap中
                 //如果有3个provider,那么虚拟节点一共有3*40*4=480个
                 for (int h = 0; h < 4; h++) {
                     long m = hash(digest, h);
                     virtualInvokers.put(m, invoker);   
                 }
             }
        } 
    }
    
    private long hash(byte[] digest, int number) {
         return (((long) (digest[3 + number * 4] & 0xFF) << 24)
                | ((long) (digest[2 + number * 4] & 0xFF) << 16)
                | ((long) (digest[1 + number * 4] & 0xFF) << 8) 
                | (digest[0 + number * 4] & 0xFF)) 
                & 0xFFFFFFFFL;
    }
    
    public select(Invocation invocation) {
        //获取参数列表，通过toKey转换为字符串
        String key = toKey(invocation.getArguments());
        byte[] digest = md5(key);
        Invoker<T> invoker = selectForKey(hash(digest, 0));
        return invoker;
    }
    
    private String toKey(Object[] args) {
        StringBuilder buf = new StringBuilder();
        for (int i :argumentIndex) {
            if (i >= 0 && i < args.length) {
                buf.append(args[i]);   
            }
        }
        return buf.toString();
    }
    
    private byte[] md5(String value) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        md5.reset();
        byte[] bytes = null;
        try {
            bytes = value.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        md5.update(bytes);
        return md5.digest();
    }
    
    //选择哪个provider只受参数值的影响，具有相同参数值的请求将被分配给同一个provider
    private Invoker<T> selectForKey(long hash) {
        Invoker<T> invoker;
        Long key = hash;
        if (! virtualInvokers.containsKey(key)) {
            //返回virtualInvokers中键值大于等于key的，并且取第一个
            SortedMap<Long, Invoker<T>> tailMap = virtualInvokers.tailMap(key);   
            if (tailMap.isEmpty()) {
                key = virtualInvokers.firstKey();   
            } else {
                key = tailMap.firstKey();   
            }
        }
        invoker = virtualInvokers.get(key);
        return invoker;
    }
}
```

### 1.8.4 加权轮询算法

RoundRobinLoadBalance：轮询是一个个顺序来，但是简单的轮询对服务器性能没有考虑，小服务器会扛不住大压力，应该降权。加权轮询是根据服务器的不同处理能力，给每个服务器分配不同权值，使其能接受相应权值数的服务请求

加权轮询的结果是生成一个服务器序列，序列中包含n个provider（n是所有provider的权重之和），每个provider出现的次数是他的权值。每当请求进来时，从序列中取出下一个服务器处理请求。生成的序列中，服务器的分布应该尽可能均匀

例如：server a weight = 1;
          server b weight = 2;
    server c weight = 3;
生成的序列是：{c, c, b, c, a, b, c}

生成服务器序列方法：每个provider对应两个权重weight和currentWeight，weight是自身固定的权重，currentWeight是动态调整的权重。初始时currentWeight=0。每当有新请求进来，对每个provider，都让他的currentWeight+weight,选出最大的currentWeight就是要挑选出的provider,然后再让这个被挑出的provider的currentWeight-totalWeight(providers的总权重)。

例如：[A,B,C]=weight[5,1,1]  currentWeight[0,0,0]
          totalWeight=7

| 请求编号 | currentWeight[] | 选择结果 | 减去totalWeight后的currentWeight[] |
| --- | --- | --- | --- |
| 1 | [5, 1, 1] | A | [-2, 1, 1] |
| 2 | [3, 2, 2] | A | [-4, 2, 2] |
| 3 | [1, 3, 3] | B | [1, -4, 3] |
| 4 | [6, -3, 4] | A | [-1, -3, 4] |
| 5 | [4, -2, 5] | C | [4, -2, -2] |
| 6 | [9, -1, -1] | A | [2, -1, -1] |
| 7 | [7, 0, 0] | A | [0, 0, 0] |


```java
protected static class WeightedRoundRobin {
    
    private int weight;
    //currentWeight
    private AtomicLong current = new AtomicLong(0);
    
    public int getWeight() {
        return weight;
    }
    public void setWeight(int weight) {
        this.weight = weight;
        current.set(0);
    }
    //currentWeight+weight
    public long increaseCurrent() {
        return current.addAndGet(weight);
    }
    //currentWeight-totalWeight
    public void sel(int total) {
        current.addAndGet(-1 * total);
    }
    public long getLastUpdate() {
        return lastUpdate;
    }
    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}

/*
{
    "UserService.query":{
        "url1":WeightedRoundRobin@123,
        "url2":WeightedRoundRobin@456
    },
    "UserService.update":{
        "url1":WeightedRoundRobin@123,
        "url2":WeightedRoundRobin@456
    }
}
*/
//最外层key是服务名+方法名,第二层key是provider的url，第二层是url到每个provider的WeightedRoundRobin映射关系
private ConcurrentMap<String, ConcurrentMap<String, WeightedRoundRobin>> methodWeightMap = new ConcurrentHashMap<>();

//原子更新锁，用于去除长久没被更新的provider时
private AtomicBoolean updateLock = new AtomicBoolean();

private static int RECYCLE_PERIOD = 60000;

protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers, URL url, Invocation invocation) {
    //全限定类名+"."+方法名,例如com.xxx.DemoService.sayHello
    String key = invokers.get(0).getUrl().getServiceKey() + "." + invocation.getMethodName();
    ConcurrentMap<String, WeightedRoundRobin> map = methodWeightMap.get(key);
    if (map == null) {
        methodWeightMap.putIfAbsent(key, new ConcurrentHashMap<String, WeightedRoundRobin>());
        map = methodWeightMap.get(key);
    }
    
    int totalWeight = 0;
    long maxCurrent = Long.MIN_VALUE;
    long now = System.currentTimeMillis();
    Invoker<T> selectedInvoler = null;
    WeightedRoundRobin selectedWRR = null;
    
    for (Invoker<T> invoker : invokers) {
        String identifyString = invoker.getUrl().toIdentityString();
        WeightedRoundRobin weightedRoundRobin = map.get(identifyString);
        //获取当前provider的权重
        int weight = getWeight(invoker, invocation);
        if (weight < 0) {
            weight = 0;   
        }
        //如果当前provider不在列表中，加入列表
        if (weightedRoundRobin == null) {
            weightedRoundRobin = new WeightedRoundRobin();
            weightedRoundRobin.setWeight(weight);
            map.putIfAbsent(identifyString, weightedRoundRobin);
            weightedRoundRobin = map.get(identifyString);
        }
        
        //如果provider权重变了，则更新
        if (weiht != weightedRoundRobin.getWeight()) {
            weightedRoundRobin.setWeight(weight);   
        }
        
        //current += weight
        long cur = weightedRoundRobin.increaseCurrent();
        //表示近期更新过
        weighttedRoundRobin.setLastUpdate(now);
        //找到currentWeight最大的provider
        if (cur > maxCurrent) {
            maxCurrent = cur;
            selectedInvoker = invoker;
            selectedWRR = weightedRoundRobin;
        }
        totalWeight += weight;
    }
    
    if (!updateLock.get() && invokers.size() != map.size()) {
        if (updateLock.compareAndSet(false, true)) {
            try{
                ConcurrentMap<String, WeightedRoundRobin> newMap = new ConcurrentHashMap<String, WeightedRoundRobin>();
                newMap.putAll(map);
                Iterator<Entry<String, WeightedRoundRobin>> it = newMap.entrySet().iterator();
                while(it.hasNext()) {
                    Entry<String, WeightedRoundRobin> item = it.next();
                    //如果某个provider超过60s没被更新，则过滤。他可能挂掉了
                    if (now - item.getValue().getLastUpdate() > RECYCLE_PERIOD) {
                        it.remove();
                    }
                }
                methodWeightMap.put(key, newMap);
            } finally {
                updateLock.set(false);   
            }
        }
    }
    if (selectedInvoker != null) {
        //currentWeight -= totalWeight;
        selectedWRR.sel(totalWeight);
        return selectedInvoker
    }
    invokers.get(0);
}
```


# 2. 架构
## 2.1 应用架构的演变
![应用架构的演变](./pic/dubbo_应用架构的演变.png)


- 单一应用架构：ORM

一开始，应用的流量较小，为了节约部署成本（服务器），一个项目的所有功能都放在一个应用中，部署在一个机子上，那提高对数据库增删改查的效率就跟重要，数据访问框架ORM就是关键

- 垂直应用架构：MVC

当访问量变大，所有功能都放在一个服务器上，服务器压力太大，那么就将一个应用根据不同部分分割成几个垂直应用，部署在不同的服务器上，那么MVC框架是关键

- 分布式服务架构：RPC

当垂直应用越来越多，各应用之间的交互也难免会变得越来越多，就将各应用间共同的部分或者核心部分抽取出来变成独立的应用。那么分布式服务RPC是关键

- 流动计算架构：SOA

当服务器越来越多，各服务器间的性能可能有差异，那么就需要有个角色来根据服务器的性能管理调度服务，那么资源调度和治理中心SOA是关键


## 2.2 dubbo架构
![dubbo架构](./pic/dubbo_dubbo架构.png)



