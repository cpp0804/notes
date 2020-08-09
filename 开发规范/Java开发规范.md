[TOC]

# OOP规约
```java
【强制】Object的equals方法容易抛空指针异常，应使用常量或确定有值的对象来调用equals。
正例："test".equals(object);
反例：object.equals("test");
说明：推荐使用JDK7引入的工具类java.util.Objects#equals(Object a, Object b)
```

```java
【强制】所有整型包装类对象之间值的比较，全部使用equals方法比较。
说明：对于Integer var=?在-128至127之间的赋值，Integer对象是在IntegerCache.cache产生，会复用已有对象，这个区间内的Integer值可以直接使用==进行判断，但是这个区间之外的所有数据，都会在堆上产生，并不会复用已有对象，这是一个大坑，推荐使用equals方法进行判断。
```

```java
【强制】浮点数之间的等值判断，基本数据类型不能用==来比较，包装数据类型不能用equals来判断。
说明：浮点数采用“尾数+阶码”的编码方式，类似于科学计数法的“有效数字+指数”的表示方式。二进制无法精确表示大部分的十进制小数

正例：
(1) 指定一个误差范围，两个浮点数的差值在此范围之内，则认为是相等的

    float a = 1.0f - 0.9f;
    float b = 0.9f - 0.8f;
    float diff = 1e-6f;

    if (Math.abs(a - b) < diff) {
        System.out.println("true");
    }
(2) 使用BigDecimal来定义值，再进行浮点数的运算操作

    BigDecimal a = new BigDecimal("1.0");
    BigDecimal b = new BigDecimal("0.9");
    BigDecimal c = new BigDecimal("0.8");

    BigDecimal x = a.subtract(b);
    BigDecimal y = b.subtract(c);

    if (x.compareTo(y) == 0) {
        System.out.println("true");
    }
```

```java
【强制】BigDecimal(double)的值的比较使用compareTo()方法，而不是equals()方法。
说明：equals()方法会比较值和精度，而compareTo()则会忽略精度。

    BigDecimal a = new BigDecimal("1.0");
    BigDecimal b = new BigDecimal("1.000");

    if (a.equals(b)) {
        System.out.println("true");
    } else {
        // 比较精度时就返回为false
        System.out.println("false");
    }

    if (a.compareTo(b) == 0) {
        // 忽略精度scale，输出为true
        System.out.println("true");
    } else { 
        System.out.println("false");
    }
```

```java
【强制】禁止使用构造方法BigDecimal(double)的方式把double值转化为BigDecimal对象。
说明：BigDecimal(double)存在精度损失风险，在精确计算或值比较的场景中可能会导致业务逻辑异常。如：BigDecimal g = new BigDecimal(0.1f); 实际的存储值为：0.100000001490116119384765625
正例：优先推荐入参为String的构造方法，或使用BigDecimal的valueOf方法，此方法内部其实执行了Double的toString，而Double的toString按double的实际能表达的精度对尾数进行了截断。

BigDecimal recommend1 = new BigDecimal("0.1");
BigDecimal recommend2 = BigDecimal.valueOf(0.1);
```

```java
【推荐】使用索引访问用String的split方法得到的数组时，需做最后一个分隔符后有无内容的检查，否则会有抛IndexOutOfBoundsException的风险。
说明：

String str = "a,b,c,,";
String[] ary = str.split(",");
// 预期大于3，结果是3
System.out.println(ary.length);
```

# 日期时间
```java
【强制】日期格式化时，传入pattern中表示年份统一使用小写的y。
说明：日期格式化时，yyyy表示当天所在的年，而大写的YYYY代表是week in which year（JDK7之后引入的概念），意思是当天所在的周属于的年份，一周从周日开始，周六结束，只要本周跨年，返回的YYYY就是下一年。
正例：表示日期和时间的格式如下所示：

new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
```

```java
【强制】在日期格式中分清楚大写的M和小写的m，大写的H和小写的h分别指代的意义。
说明：日期格式中的这两对字母表意如下：

表示月份是大写的M；
表示分钟则是小写的m；
24小时制的是大写的H；
12小时制的则是小写的h。
```

```java
【强制】获取当前毫秒数：System.currentTimeMillis(); 而不是new Date().getTime();
说明：如果想获取更加精确的纳秒级时间值，使用System.nanoTime的方式。在JDK8中，针对统计时间等场景，推荐使用Instant类
```


```java
【强制】不允许在程序任何地方中使用：1）java.sql.Date 2）java.sql.Time 3）java.sql.Timestamp
说明：第1个不记录时间，getHours()抛出异常；第2个不记录日期，getYear()抛出异常；第3个在构造方法super((time/1000)*1000)，在Timestamp 属性fastTime和nanos分别存储秒和纳秒信息。
反例：java.util.Date.after(Date)进行时间比较时，当入参是java.sql.Timestamp时，会触发JDK BUG(JDK9已修复)，可能导致比较时的意外结果
```

```java
【强制】不要在程序中写死一年为365天，避免在公历闰年时出现日期转换错误或程序逻辑错误。
正例：

// 获取今年的天数
int daysOfThisYear = LocalDate.now().lengthOfYear();

// 获取指定某年的天数
LocalDate.of(2011, 1, 1).lengthOfYear();
```

```java
【推荐】使用枚举值来指代月份。如果使用数字，注意Date，Calendar等日期相关类的月份month取值在0-11之间。
说明：参考JDK原生注释：Month value is 0-based. e.g., 0 for January.
正例： Calendar.JANUARY，Calendar.FEBRUARY，Calendar.MARCH等来指代相应月份来进行传参或比较
```

# 集合处理
```java
【强制】关于hashCode和equals的处理，遵循如下规则：
 1） 只要覆写equals，就必须覆写hashCode。
 2） 因为Set存储的是不重复的对象，依据hashCode和equals进行判断，所以Set存储的对象必须覆写这两个方法。
 3） 如果自定义对象作为Map的键，那么必须覆写hashCode和equals。
```

```java
【强制】使用Map的方法keySet()/values()/entrySet()返回集合对象时，不可以对其进行添加元素操作，否则会抛出UnsupportedOperationException异常
```

```java
【强制】判断所有集合内部的元素是否为空，使用isEmpty()方法，而不是size()==0的方式。
说明：在某些集合中，前者的时间复杂度为O(1)，而且可读性更好。
正例：

Map<String, Object> map = new HashMap<>();
if(map.isEmpty()) {
    System.out.println("no element in this map.");
}
```

```java
【强制】Collections类返回的对象，如：emptyList()/singletonList()等都是immutable list，不可对其进行添加或者删除元素的操作。

反例：某二方库的方法中，如果查询无结果，返回Collections.emptyList()空集合对象，调用方一旦进行了添加元素的操作，就会触发UnsupportedOperationException异常
```

```java
【强制】ArrayList的subList结果不可强转成ArrayList，否则会抛出ClassCastException异常：java.util.RandomAccessSubList cannot be cast to java.util.ArrayList;
说明： subList()返回的是ArrayList的内部类SubList，并不是 ArrayList本身，而是ArrayList 的一个视图，对于SubList的所有操作最终会反映到原列表上。
```


```java
【强制】在subList场景中，高度注意对父集合元素的增加或删除，均会导致子列表的遍历、增加、删除产生ConcurrentModificationException 异常。
```



```java
【强制】使用集合转数组的方法，必须使用集合的toArray(T[] array)，传入的是类型完全一致、长度为0的空数组。
反例：直接使用toArray无参方法存在问题，此方法返回值只能是Object[]类，若强转其它类型数组将出现ClassCastException错误。
正例：

List<String> list = new ArrayList<>(2);
list.add("guan");
list.add("bao");

array = list.toArray(new String[0]);
说明：使用toArray带参方法，数组空间大小的length：
1）等于0，动态创建与size相同的数组，性能最好。
2）大于0但小于size，重新创建大小等于size的数组，增加GC负担。
3）等于size，在高并发情况下，数组创建完成之后，size正在变大的情况下，负面影响与2相同。
4）大于size，空间浪费，且在size处插入null值，存在NPE隐患。
```


```java
【强制】在使用Collection接口任何实现类的addAll()方法时，都要对输入的集合参数进行NPE判断。
说明：在ArrayList#addAll方法的第一行代码即Object[] a = c.toArray(); 其中c为输入集合参数，如果为null，则直接抛出异常
```


```java
【强制】使用工具类Arrays.asList()把数组转换成集合时，不能使用其修改集合相关的方法，它的add/remove/clear方法会抛出UnsupportedOperationException异常。
说明：asList的返回对象是一个Arrays内部类，并没有实现集合的修改方法。Arrays.asList体现的是适配器模式，只是转换接口，后台的数据仍是数组。

String[] str = new String[] { "a", "b" };
List list = Arrays.asList(str);
第一种情况：list.add("c"); 运行时异常。
第二种情况：str[0]= "changed"; 那么list.get(0)也会随之修改，反之亦然
```



```java
【强制】不要在foreach循环里进行元素的remove/add操作。remove元素请使用Iterator方式，如果并发操作，需要对Iterator迭代器对象加锁。


反例：
List<String> list = new ArrayList<>();
list.add("1");
list.add("2");

for (String item : list) {
    if ("1".equals(item)) {
        list.remove(item);
    }
}



正例：
Iterator<String> iterator = list.iterator();
while (iterator.hasNext()) {
    String item = iterator.next();
    if (删除元素的条件) {
        iterator.remove();
    }
}

在并发情况下需要通过以下两种方式解决：
1.使用Iterator遍历时在Iterator上加锁。
2.使用并发容器CopyOnWriteArrayList代替ArrayList或Vector, 该容器内部会对Iterator进行加锁操作。
```


```java
【强制】在JDK7版本以上，Comparator要满足如下三个条件，不然Arrays.sort，Collections.sort会抛IllegalArgumentException异常。
说明：
 1） x，y的比较结果和y，x的比较结果相反。
 2） x>y，y>z，则x>z。
 3） x=y，则x，z比较结果和y，z比较结果相同。
反例：下例中没有处理相等的情况，交换两个对象判断结果并不互反，不符合第一个条件，在实际使用中可能会出现异常。

new Comparator<Student>() {
    @Override
    public int compare(Student o1, Student o2) {
        return o1.getId() > o2.getId() ? 1 : -1;
    }
}
```


```java
【推荐】集合初始化时，指定集合初始值大小。
说明：HashMap使用如下构造方法进行初始化，如果暂时无法确定集合大小，那么指定默认值（16）即可：

public HashMap(int initialCapacity) {
    this(initialCapacity, DEFAULT_LOAD_FACTOR);
}

反例：HashMap需要放置1024个元素，由于没有设置容量初始大小，随着元素增加而被迫不断扩容，resize()方法总共会调用8次，反复重建哈希表和数据迁移。当放置的集合元素个数达千万级时会影响程序性能
```



```java
【推荐】使用entrySet遍历Map类集合KV，而不是keySet方式进行遍历。
说明：keySet其实是遍历了2次，一次是转为Iterator对象，另一次是从hashMap中取出key所对应的value。而entrySet只是遍历了一次就把key和value都放到了entry中，效率更高。如果是JDK8，使用Map.forEach方法。

正例：values()返回的是V值集合，是一个List集合对象；keySet()返回的是K值集合，是一个Set集合对象；entrySet()返回的是K-V值组合集合
```


```java
【推荐】高度注意Map类集合K/V能不能存储null值的情况，如下表格：
```
集合类	|Key	|Value	|Super	|说明|
---|---|---|---|---|
Hashtable	|不允许为null	|不允许为null	|Dictionary	|线程安全
ConcurrentHashMap	|不允许为null	|不允许为null	|AbstractMap|	锁分段技术（JDK8:CAS)
TreeMap	|不允许为null	|允许为null	|AbstractMap	|线程不安全
HashMap	|允许为null	|允许为null	|AbstractMap	|线程不安全



```java
【参考】合理利用好集合的有序性(sort)和稳定性(order)，避免集合的无序性(unsort)和不稳定性(unorder)带来的负面影响。
说明：有序性是指遍历的结果是按某种比较规则依次排列的。稳定性指集合每次遍历的元素次序是一定的。

如：ArrayList是order/unsort；HashMap是unorder/unsort；TreeSet是order/sort
```




# 并发处理
```java
【强制】线程池不允许使用Executors去创建，而是通过ThreadPoolExecutor的方式，这样的处理方式让写的同学更加明确线程池的运行规则，规避资源耗尽的风险。

说明：Executors返回的线程池对象的弊端如下：
1）FixedThreadPool和SingleThreadPool:
  允许的请求队列长度为Integer.MAX_VALUE，可能会堆积大量的请求，从而导致OOM。
2）CachedThreadPool:
  允许的创建线程数量为Integer.MAX_VALUE，可能会创建大量的线程，从而导致OOM
```


```java
【强制】SimpleDateFormat 是线程不安全的类，一般不要定义为static变量，如果定义为static，必须加锁，或者使用DateUtils工具类。
正例：注意线程安全，使用DateUtils。亦推荐如下处理：

private static final ThreadLocal<DateFormat> df = new ThreadLocal<DateFormat>() {
    @Override
    protected DateFormat initialValue() {
        return new SimpleDateFormat("yyyy-MM-dd");
    }
};
```


```java
【强制】必须回收自定义的ThreadLocal变量，尤其在线程池场景下，线程经常会被复用，如果不清理自定义的 ThreadLocal变量，可能会影响后续业务逻辑和造成内存泄露等问题。尽量在代码中使用try-finally块进行回收。
正例：

objectThreadLocal.set(userInfo);
try {
    ...
} finally {
    objectThreadLocal.remove();
}
```


```java
【强制】对多个资源、数据库表、对象同时加锁时，需要保持一致的加锁顺序，否则可能会造成死锁。
说明：线程一需要对表A、B、C依次全部加锁后才可以进行更新操作，那么线程二的加锁顺序也必须是A、B、C，否则可能出现死锁。
```


```java
【强制】在使用阻塞等待获取锁的方式中，必须在try代码块之外，并且在加锁方法与try代码块之间没有任何可能抛出异常的方法调用，避免加锁成功后，在finally中无法解锁。
说明一：如果在lock方法与try代码块之间的方法调用抛出异常，那么无法解锁，造成其它线程无法成功获取锁。
说明二：如果lock方法在try代码块之内，可能由于其它方法抛出异常，导致在finally代码块中，unlock对未加锁的对象解锁，它会调用AQS的tryRelease方法（取决于具体实现类），抛出IllegalMonitorStateException异常。
说明三：在Lock对象的lock方法实现中可能抛出unchecked异常，产生的后果与说明二相同。

正例：
Lock lock = new XxxLock();
// ...
lock.lock();
try {
    doSomething();
    doOthers();
} finally {
    lock.unlock();
}

反例：
Lock lock = new XxxLock();
// ...
try {
    // 如果此处抛出异常，则直接执行finally代码块
    doSomething();
    // 无论加锁是否成功，finally代码块都会执行
    lock.lock();
    doOthers();

} finally {
    lock.unlock();
}
```


```java
【强制】在使用尝试机制来获取锁的方式中，进入业务代码块之前，必须先判断当前线程是否持有锁。锁的释放规则与锁的阻塞等待方式相同。
说明：Lock对象的unlock方法在执行时，它会调用AQS的tryRelease方法（取决于具体实现类），如果当前线程不持有锁，则抛出IllegalMonitorStateException异常。
正例：

Lock lock = new XxxLock();
// ...
boolean isLocked = lock.tryLock();
if (isLocked) {
    try {
        doSomething();
        doOthers();
    } finally {
        lock.unlock();
    }
}
```


```java
【强制】并发修改同一记录时，避免更新丢失，要么在应用层加锁，要么在缓存加锁，要么在数据库层使用乐观锁，使用version作为更新依据。
说明：如果每次访问冲突概率小于20%，推荐使用乐观锁，否则使用悲观锁。乐观锁的重试次数不得小于3次。

正例：集团很多业务使用TairManager方法：incr(namespace, lockKey, 1, 0, expireTime); 判断返回步长是否为1，实现分布式锁
```


```java
【强制】多线程并行处理定时任务时，Timer运行多个TimeTask时，只要其中之一没有捕获抛出的异常，其它任务便会自动终止运行，使用ScheduledExecutorService则没有这个问题。

反例：阿里云平台产品技术部，域名更新具体产品信息保存到tair，Timer产生了RunTimeExcetion异常后，定时任务不再执行，通过检查日志发现原因，改为ScheduledExecutorService方式
```


```java
【推荐】资金相关的金融敏感信息，使用悲观锁策略。
说明：乐观锁在获得锁的同时已经完成了更新操作，校验逻辑容易出现漏洞，另外，乐观锁对冲突的解决策略有较复杂的要求，处理不当容易造成系统压力或数据异常，所以资金相关的金融敏感信息不建议使用乐观锁更新。
正例：悲观锁遵循一锁二判三更新四释放的原则
```


```java
【推荐】使用CountDownLatch进行异步转同步操作，每个线程退出前必须调用countDown方法，线程执行代码注意catch异常，确保countDown方法被执行到，避免主线程无法执行至await方法，直到超时才返回结果。

说明：注意，子线程抛出异常堆栈，不能在主线程try-catch到。
反例：在“马可波罗平台”的翻译同步转异步多线程时，由于翻译过程抛出异常，导致countDown方法失败，经常超时才返回
```


```java
【推荐】避免Random实例被多线程使用，虽然共享该实例是线程安全的，但会因竞争同一seed 导致的性能下降。
说明：Random实例包括java.util.Random 的实例或者 Math.random()的方式。

正例：在JDK7之后，可以直接使用API ThreadLocalRandom；而在JDK7前，需要编码保证每个线程持有一个单独的Random实例
```



```java
【推荐】通过双重检查锁（double-checked locking）（在并发场景）实现延迟初始化的优化问题隐患(可参考 The "Double-Checked Locking is Broken" Declaration)推荐解决方案中较为简单一种（适用于JDK5及以上版本），将目标属性声明为 volatile型（比如修改helper的属性声明为private volatile Helper helper = null;）；

反例：
public class LazyInitDemo {
    private Helper helper = null;

    public Helper getHelper() {
        if (helper == null) {
            synchronized (this) {
                if (helper == null) {
                    helper = new Helper();
                }
            }
        }
        return helper;
    }
    // other methods and fields...
}
```



```java
【强制】在一个switch块内，每个case要么通过continue/break/return等来终止，要么注释说明程序将继续执行到哪一个case为止；在一个switch块内，都必须包含一个default语句并且放在最后，即使它什么代码也没有
```


```java
【强制】当switch括号内的变量类型为String并且此变量为外部参数时，必须先进行null判断。

反例：如下的代码输出是什么？
public class SwitchString {
    public static void main(String[] args) {
        method(null);
    }

    public static void method(String param) {
        switch (param) {
            // 肯定不是进入这里
            case "sth":
                System.out.println("it's sth");
                break;
            // 也不是进入这里
            case "null":
                System.out.println("it's null");
                break;
            // 也不是进入这里
            default:
                System.out.println("default");
        }
    }
}

Exception in thread "main" java.lang.NullPointerException
```


```java
【强制】三目运算符condition? 表达式1 : 表达式2中，高度注意表达式1和2在涉及算术计算或数据类型转换时，可能抛出因自动拆箱导致的NPE异常。
说明：以下两种场景会触发类型对齐的拆箱操作：
 1） 表达式1或表达式2的值只要有一个是原始类型。
 2） 表达式1或表达式2的值的类型不一致，会强制拆箱升级成表示范围更大的那个类型。

反例：
Integer a = 1;
Integer b = 2;
Integer c = null;
Boolean flag = false;
// a*b的结果是int类型，那么c会强制拆箱成int类型，抛出NPE异常
Integer result = (flag? a*b : c);
```



```java
【强制】在高并发场景中，避免使用“等于”判断作为中断或退出的条件。
说明：如果并发控制没有处理好，容易产生等值判断被“击穿”的情况，使用大于或小于的区间判断条件来代替。

反例：某营销活动发奖，判断剩余奖品数量等于0时，终止发放奖品，但因为并发处理错误导致奖品数量瞬间变成了负数，活动无法终止，产生资损
```



```java
【推荐】表达异常分支时，少用if-else方式，这种方式可以改写成：

if (condition) {
    ...
    return obj;
}
// 接着写else的业务逻辑代码;


说明：如果非得使用if()...else if()...else...方式表达逻辑，【强制】请勿超过3层，超过请使用状态设计模式。
正例：超过3层的 if-else 的逻辑判断代码可以使用卫语句、策略模式、状态模式等来实现，其中卫语句示例如下：

public class GuardSatementsDemo {
    public void findBoyfriend(Man man) {
        if (man.isBadTemper()) {
            System.out.println("月球有多远，你就给我滚多远.");
            return;
        }

        if (man.isShort()) {
            System.out.println("我不需要武大郎一样的男友.");
            return;
        }

        if (man.isPoor()) {
            System.out.println("贫贱夫妻百事哀.");
            return;
        }

        System.out.println("可以先交往一段时间看看.");
    }
}
```


```java
【推荐】避免采用取反逻辑运算符。
说明：取反逻辑不利于快速理解，并且取反逻辑写法一般都存在对应的正向逻辑写法。

正例：使用if (x < 628)来表达 x 小于628。
反例：使用if (!(x >= 628))来表达 x 小于628
```


```java
【强制】对于需要使用超大整数的场景，服务端一律使用String字符串类型返回，禁止使用Long类型。

说明：Java服务端如果直接返回Long整型数据给前端，JS会自动转换为Number类型（注：此类型为双精度浮点数，表示原理与取值范围等同于Java中的Double）。Long类型能表示的最大值是2的63次方-1，在取值范围之内，超过2的53次方 (9007199254740992)的数值转化为JS的Number时，有些数值会有精度损失。扩展说明，在Long取值范围内，任何2的指数次整数都是绝对不会存在精度损失的，所以说精度损失是一个概率问题。若浮点数尾数位与指数位空间不限，则可以精确表示任何整数，但很不幸，双精度浮点数的尾数位只有52位。

反例：通常在订单号或交易号大于等于16位，大概率会出现前后端单据不一致的情况，比如，"orderId": 362909601374617692，前端拿到的值却是: 362909601374617660
```


```java
【强制】避免用Apache Beanutils进行属性的copy。
说明：Apache BeanUtils性能较差，可以使用其他方案比如Spring BeanUtils, Cglib BeanCopier，注意均是浅拷贝
```


```java
【强制】注意 Math.random() 这个方法返回是double类型，注意取值的范围 0≤x<1（能够取到零值，注意除零异常），如果想获取整数类型的随机数，不要将x放大10的若干倍然后取整，直接使用Random对象的nextInt或者nextLong方法
```


```java
【强制】不要在finally块中使用return。
说明：try块中的return语句执行成功后，并不马上返回，而是继续执行finally块中的语句，如果此处存在return语句，则在此直接返回，无情丢弃掉try块中的返回点。

反例：
private int x = 0;
public int checkReturn() {
    try {
        // x等于1，此处不返回
        return ++x;
    } finally {
        // 返回的结果是2
        return ++x;
    }
}
```


# MYSQL规约
```java
【强制】表达是与否概念的字段，必须使用is_xxx的方式命名，数据类型是unsigned tinyint（1表示是，0表示否），此规则同样适用于odps建表。

说明：任何字段如果为非负数，必须是unsigned。
注意：POJO类中的任何布尔类型的变量，都不要加is前缀，所以，需要在<resultMap>设置从is_xxx到xxx的映射关系。数据库表示是与否的值，使用tinyint类型，坚持is_xxx的命名方式是为了
```


```java
【强制】表名、字段名必须使用小写字母或数字，字段命名可参考附2；禁止出现数字开头，禁止两个下划线中间只出现数字。数据库字段名的修改代价很大，因为无法进行预发布，所以字段名称需要慎重考虑。
说明：MySQL在Windows下不区分大小写，但在Linux下默认是区分大小写。因此，数据库名、表名、字段名，都不允许出现任何大写字母，避免节外生枝。

正例：getter_admin，task_config，level3_name
反例：GetterAdmin，taskConfig，level_3_name
```


```java
【强制】表名不使用复数名词。

说明：表名应该仅仅表示表里面的实体内容，不应该表示实体数量，对应于DO类名也是单数形式，符合表达习惯
```


```java
【强制】小数类型为decimal，禁止使用float和double。

说明：在存储的时候，float 和 double 都存在精度损失的问题，很可能在比较值的时候，得到不正确的结果。如果存储的数据范围超过 decimal 的范围，建议将数据拆成整数和小数并分开存储
```


```java
【强制】如果存储的字符串长度几乎相等，使用CHAR定长字符串类型。
```

```java
【强制】表必备三字段：id, gmt_create, gmt_modified。

说明：其中id必为主键，类型为bigint unsigned、单表时自增、步长为1；分表时改为从TDDL Sequence取值，确保分表之间的全局唯一。gmt_create, gmt_modified的类型均为date_time类型，前者现在时表示主动式创建，后者过去分词表示被动式更新
```


```java
【推荐】单表行数超过500万行或者单表容量超过2GB，才推荐进行分库分表。
说明：如果预计三年后的数据量根本达不到这个级别，请不要在创建表时就分库分表。
```


```java
【强制】不要使用count(列名)或count(常量)来替代count(*)，count(*)就是SQL92定义的标准统计行数的语法，跟数据库无关，跟NULL和非NULL无关。

说明：count(*)会统计值为NULL的行，而count(列名)不会统计此列为NULL值的行
```


```java
【强制】count(distinct col) 计算该列除NULL之外的不重复数量。注意 count(distinct col1, col2) 如果其中一列全为NULL，那么即使另一列有不同的值，也返回为0
```

```java
【强制】当某一列的值全是NULL时，count(col)的返回结果为0，但sum(col)的返回结果为NULL，因此使用sum()时需注意NPE问题。

正例：可以使用如下方式来避免sum的NPE问题：SELECT IFNULL(SUM(column), 0) FROM table
```


```java
【强制】使用ISNULL()来判断是否为NULL值。
说明：NULL与任何值的直接比较都为NULL。
 1） NULL<>NULL的返回结果是NULL，而不是false。
 2） NULL=NULL的返回结果是NULL，而不是true。
 3） NULL<>1的返回结果是NULL，而不是true。

反例：在SQL语句中，如果在null前换行，影响可读性。select * from table where column1 is null and column3 is not null; 而ISNULL(column)是一个整体，简洁易懂。从性能数据上分析，ISNULL(column)执行效率更快一些
```


```java
【强制】对于数据库中表记录的查询和变更，只要涉及多个表，都需要在列名前加表的别名（或表名）进行限定。
说明：对多表进行查询记录、更新记录、删除记录时，如果对操作列没有限定表的别名（或表名），并且操作列在多个表中存在时，就会抛异常。

正例：select t1.name from table_first as t1 , table_second as t2 where t1.id=t2.id;
反例：在飞猪某业务中，由于多表关联查询语句没有加表的别名（或表名）的限制，正常运行两年后，最近在某个表中增加一个同名字段，在预发布环境做数据库变更后，线上查询语句出现出1052异常：Column 'name' in field list is ambiguous，导致票务交易下跌
```


```java
【强制】不得使用外键与级联，一切外键概念必须在应用层解决。

说明：（概念解释）学生表中的student_id是主键，那么成绩表中的student_id则为外键。如果更新学生表中的student_id，同时触发成绩表中的student_id更新，则为级联更新。外键与级联更新适用于单机低并发，不适合分布式、高并发集群；级联更新是强阻塞，存在数据库更新风暴的风险；外键影响数据库的插入速度
```


```java
【参考】TRUNCATE TABLE 比 DELETE 速度快，且使用的系统和事务日志资源少，但TRUNCATE无事务且不触发trigger，有可能造成事故，故不建议在开发代码中使用此语句。

说明：TRUNCATE TABLE 在功能上与不带 WHERE 子句的 DELETE 语句相同
```

```java
【强制】不允许直接拿HashMap与HashTable作为查询结果集的输出。

反例：某同学为避免写一个xxx，直接使用HashTable来接收数据库返回结果，结果出现日常是把bigint转成Long值，而线上由于数据库版本不一样，解析成BigInteger，导致线上问题
```



# 索引规约

```java
【强制】超过三个表禁止join。需要join的字段，数据类型保持绝对一致；多表关联查询时，保证被关联的字段需要有索引。

说明：即使双表join也要注意表索引、SQL性能
```


```java
【强制】在varchar字段上建立索引时，必须指定索引长度，没必要对全字段建立索引，根据实际文本区分度决定索引长度。

说明：索引的长度与区分度是一对矛盾体，一般对字符串类型数据，长度为20的索引，区分度会高达90%以上，可以使用count(distinct left(列名, 索引长度))/count(*)的区分度来确定
```


```java
【推荐】如果有order by的场景，请注意利用索引的有序性。order by 最后的字段是组合索引的一部分，并且放在索引组合顺序的最后，避免出现file_sort的情况，影响查询性能。

正例：where a=? and b=? order by c; 索引：a_b_c
反例：索引中有范围查找，那么索引有序性无法利用，如：WHERE a>10 ORDER BY b; 索引a_b无法排序
```


```java
【推荐】利用覆盖索引来进行查询操作，来避免回表操作。
说明：如果一本书需要知道第11章是什么标题，会翻开第11章对应的那一页吗？目录浏览一下就好，这个目录就是起到覆盖索引的作用。

正例：IDB能够建立索引的种类分为【主键索引、唯一索引、普通索引】，而覆盖索引是一种查询的一种效果，用explain的结果，extra列会出现：using index
```

```java
【推荐】利用延迟关联或者子查询优化超多分页场景。
说明：MySQL并不是跳过offset行，而是取offset+N行，然后返回放弃前offset行，返回N行，那当offset特别大的时候，效率就非常的低下，要么控制返回的总页数，要么对超过特定阈值的页数进行SQL改写。

正例：先快速定位需要获取的id段，然后再关联：
SELECT a.* FROM 表1 a, (select id from 表1 where 条件 LIMIT 100000,20 ) b where a.id=b.id
反例：“服务市场”某交易分页超过1000页，用户点击最后一页时，数据库基本处于半瘫痪状态
```


```java
【推荐】SQL性能优化的目标：至少要达到 range 级别，要求是ref级别，如果可以是const最好。
说明：
 1）const 单表中最多只有一个匹配行（主键或者唯一索引），在优化阶段即可读取到数据。
 2）ref 指的是使用普通的索引。（normal index）
 3）range 对索引进行范围检索。
反例：explain表的结果，type=index，索引物理文件全扫描，速度非常慢，这个index级别比较range还低，与全表扫描是小巫见大巫。
```


```java
【推荐】建组合索引的时候，区分度最高的在最左边。
正例：如果where a=? and b=? ，a列的几乎接近于唯一值，那么只需要单建idx_a索引即可。

说明：存在非等号和等号混合判断条件时，在建索引时，请把等号条件的列前置。如：where c>? and d=? 那么即使c的区分度更高，也必须把d放在索引的最前列，即建立组合索引idx_d_c
```




# 工程规约
```java
【参考】分层领域模型规约：
DO（Data Object）：与数据库表结构一一对应，通过DAO层向上传输数据源对象。
DTO（Data Transfer Object）：数据传输对象，Service或Manager向外传输的对象。
BO（Business Object）：业务对象。可以由Service层输出的封装业务逻辑的对象。
Query：数据查询对象，各层接收上层的查询请求。注：超过2个参数的查询封装，禁止使用Map类来传输。
VO（View Object）：显示层对象，通常是Web向模板渲染引擎层传输的对象
```


```java
【强制】二方库版本号命名方式：主版本号.次版本号.修订号
 1） 主版本号：产品方向改变，或者大规模API不兼容，或者架构不兼容升级。
 2） 次版本号：保持相对兼容性，增加主要功能特性，影响范围极小的API不兼容修改。
 3） 修订号：保持完全兼容性，修复BUG、新增次要功能特性等。
反例：仓库内某二方库版本号从1.0.0.0开始，一直默默“升级”成1.0.0.64，完全失去版本的语义信息。

说明：集团任何中间件、中台业务、二方包都必须遵守此版本约定。起始版本号统一为：1.0.0，而不是0.0.1
```

```java
【强制】调用远程操作必须有超时设置。HSF默认自动超时是3秒，类似于HttpClient的超时设置需要自己明确去设置Timeout。
反例：根据经验表明，无次数的故障都是因为没有设置超时
```

```java
【推荐】给JVM环境参数设置-XX:+HeapDumpOnOutOfMemoryError参数，让JVM碰到OOM场景时输出dump信息。

说明：OOM的发生是有概率的，甚至相隔数月才出现一例，出错时的堆内信息对解决问题非常有帮助。
```
