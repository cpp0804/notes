# 参考博文
[深入解析HashMap原理（基于JDK1.8）](https://blog.csdn.net/qq_37113604/article/details/81353626)
[HashMap底层原理详解(JDK1.8)](https://blog.csdn.net/qq_40604437/article/details/107806395?utm_medium=distribute.pc_relevant.none-task-blog-baidujs_title-0&spm=1001.2101.3001.4242)
[hashmap为什么是2的倍数_关于HashMap你需要知道的一些细节](https://blog.csdn.net/weixin_42534103/article/details/112423009)

# 底层结构
![底层结构](./pic/HashMap_底层结构.jpeg)

## Node
数组+链表+红黑树

主体是一个Node数组(1.7中是Entry数组)，存储的是以Node为节点的链表，Node结构如下：
Node包含这个hash值、key、value和他的下一个节点
```java
static class Node<K,V> implements Map.Entry<K,V> {
        final int hash;
        final K key;
        V value;
        Node<K,V> next;
 
        Node(int hash, K key, V value, Node<K,V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
 
        public final int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }
 
        public final boolean equals(Object o) {
            if (o == this)
                return true;
            if (o instanceof Map.Entry) {
                Map.Entry<?,?> e = (Map.Entry<?,?>)o;
                if (Objects.equals(key, e.getKey()) &&
                    Objects.equals(value, e.getValue()))
                    return true;
            }
            return false;
        }
}
```
![Node](./pic/HashMap_Node.jpeg)


## 构造函数
- 重要字段
```java
/**
默认初始容量为16
0000 0001 右移4位 0001 0000为16
必须为2的倍数
*/
static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; 
 
//最大容量为int的最大值除2
static final int MAXIMUM_CAPACITY = 1 << 30;
 
//默认加载因子为0.75
static final float DEFAULT_LOAD_FACTOR = 0.75f;
 
//阈值，如果主干数组上的链表的长度大于8，链表转化为红黑树
 static final int TREEIFY_THRESHOLD = 8;
 
//hash表扩容后，如果发现某一个红黑树的长度小于6，则会重新退化为链表
 static final int UNTREEIFY_THRESHOLD = 6;
 
//当hashmap容量大于64时，链表才能转成红黑树
 static final int MIN_TREEIFY_CAPACITY = 64;
 
//临界值=主干数组容量*负载因子
```

- 构造方法
```java
//initialCapacity为初始容量，loadFactor为负载因子
public HashMap(int initialCapacity, float loadFactor) {
        //初始容量小于0，抛出非法数据异常
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " +
                                               initialCapacity);
        //初始容量最大为MAXIMUM_CAPACITY
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        //负载因子必须大于0，并且是合法数字
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " +
                                               loadFactor);
        
        this.loadFactor = loadFactor;
        //将初始容量转成2次幂
        this.threshold = tableSizeFor(initialCapacity);
    }
 
    //tableSizeFor的作用就是，如果传入A，当A大于0，小于定义的最大容量时，
  //  如果A是2次幂则返回A，否则将A转化为一个比A大且差距最小的2次幂。  
    //例如传入7返回8，传入8返回8，传入9返回16
  static final int tableSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }
 
 
    //调用上面的构造方法，自定义初始容量，负载因子为默认的0.75
 public HashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }
 
 
    //默认构造方法，负载因子为0.75，初始容量为DEFAULT_INITIAL_CAPACITY=16，初始容量在第一次put时才会初始化
 public HashMap() {
        this.loadFactor = DEFAULT_LOAD_FACTOR; // all other fields defaulted
    }
 
 
    //传入一个MAP集合的构造方法
 public HashMap(Map<? extends K, ? extends V> m) {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        putMapEntries(m, false);
}
```

# 冲突解决方法
指对于一个待插入哈希表的数据元素，若按给定的哈希函数求得的哈希地址已经被占用，那么就按一定规则求下一哈希地址。如此重复，直到找到一个可用的地址保存该元素

## 1. 开放地址法

令H(i)=(H(key)+d(i))%m,i=1,2,...,m-1,其中H(key)为哈希函数，m为哈希表长，d(i)为增量序列

若取d(i)=1,2,3,...,m-1,则称线性探测再散列
若取d(i)=1^2 , -1^2 , 2^2 , -2^2 ,..., ±k^2,则称二次探测再散列
若取d(i)=伪随机数序列，则称伪随机探测再散列


## 2. 链地址法

将所有按给定的哈希函数求得的哈希地址相同的关键字存储在同一线性链表中，且使链表按关键字有序

HashMap就是采用链地址法。在node数组中，如果那个位置已经有node了，就会在哪个位子形成一个链表。将当前元素的key和链表中的数据key做比较，如果没有就插入链表，否则替换。

当链表过长时，查找效率就会降低，所以当链表长度大于8就会转换成红黑树

## 3. 公共溢出区

若关键字对应的哈希地址已经被占用，则保存到公共溢出区中


# put过程
![put1](./pic/HashMap_put1.png)
![put2](./pic/HashMap_put2.png)

```java
pulic V put(K key, V value) {
    putVal(hash(key), key, value, false, true);
}

public static final int(Object key) {
    int h;
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
}

//onlyIfAbsent是true的话，不要改变现有的值
//evict为true的话，表处于创建模式 
final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                   boolean evict) {
        Node<K,V>[] tab; Node<K,V> p; int n, i;
//如果主干上的table为空，长度为0，调用resize方法，调整table的长度（resize方法在下图中）
        if ((tab = table) == null || (n = tab.length) == 0)
            /* 这里调用resize，其实就是第一次put时，对数组进行初始化。
               如果是默认构造方法会执行resize中的这几句话：
               newCap = DEFAULT_INITIAL_CAPACITY;  新的容量等于默认值16
               newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);            
               threshold = newThr;   临界值等于16*0.75
               Node<K,V>[] newTab = (Node<K,V>[])new Node[newCap]; 
               table = newTab; 将新的node数组赋值给table，然后return newTab
                
                如果是自定义的构造方法则会执行resize中的： 
                int oldThr = threshold;   
                newCap = oldThr;   新的容量等于threshold，这里的threshold都是2的倍数，原因在    
                于传入的数都经过tableSizeFor方法，返回了一个新值，上面解释过
                float ft = (float)newCap * loadFactor; 
                newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ?
                (int)ft : Integer.MAX_VALUE); 
                 threshold = newThr; 新的临界值等于 (int)(新的容量*负载因子)
                Node<K,V>[] newTab = (Node<K,V>[])new Node[newCap];
                table = newTab; return newTab;
            */
            n = (tab = resize()).length;  //将调用resize后构造的数组的长度赋值给n
        if ((p = tab[i = (n - 1) & hash]) == null) //将数组长度与计算得到的hash值比较
            tab[i] = new Node(hash, key, value, null);//位置为空，将i位置上赋值一个node对象
        else {  //位置不为空
            Node<K,V> e; K k;
            if (p.hash == hash &&  // 如果这个位置的old节点与new节点的key完全相同
                ((k = p.key) == key || (key != null && key.equals(k)))) 
                e = p;             // 则e=p
            else if (p instanceof TreeNode) // 如果p已经是树节点的一个实例，既这里已经是树了
                e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
            else {  //p与新节点既不完全相同，p也不是treenode的实例
                for (int binCount = 0; ; ++binCount) {  //一个死循环
                    if ((e = p.next) == null) {   //e=p.next,如果p的next指向为null
                        p.next = newNode(hash, key, value, null);  //指向一个新的节点
                        if (binCount >= TREEIFY_THRESHOLD - 1) // 如果链表长度大于等于8
                            treeifyBin(tab, hash);  //将链表转为红黑树
                        break;
                    }
       if (e.hash == hash &&  //如果遍历过程中链表中的元素与新添加的元素完全相同，则跳出循环
                        ((k = e.key) == key || (key != null && key.equals(k))))
                        break;
                    p = e; //将p中的next赋值给p,即将链表中的下一个node赋值给p，
                           //继续循环遍历链表中的元素
                }
            }
            if (e != null) { //这个判断中代码作用为：如果添加的元素产生了hash冲突，那么调用                
                             //put方法时，会将他在链表中他的上一个元素的值返回
                V oldValue = e.value;
                if (!onlyIfAbsent || oldValue == null)  //判断条件成立的话，将oldvalue替换        
                //为newvalue，返回oldvalue；不成立则不替换，然后返回oldvalue
                    e.value = value;
                afterNodeAccess(e);  //这个方法在后面说
                return oldValue;
            }
        }
        ++modCount;  //记录修改次数
        if (++size > threshold)   //如果元素数量大于临界值，则进行扩容
            resize();   //下面说
        afterNodeInsertion(evict);  
        return null;
}
```

# resize()
![resize1](./pic/HashMap_resize1.png)
![resize2](./pic/HashMap_resize2.png)

```java
final Node<K,V>[] resize() {
        Node<K,V>[] oldTab = table;
        int oldCap = (oldTab == null) ? 0 : oldTab.length;
        int oldThr = threshold;
        int newCap, newThr = 0;
        if (oldCap > 0) {  //扩容肯定执行这个分支
            if (oldCap >= MAXIMUM_CAPACITY) {   //当容量超过最大值时，临界值设置为int最大值
                threshold = Integer.MAX_VALUE;
                return oldTab;
            }
            else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                     oldCap >= DEFAULT_INITIAL_CAPACITY) //扩容容量为2倍，临界值为2倍
                newThr = oldThr << 1;
        }
        else if (oldThr > 0) // 不执行
            newCap = oldThr;
        else {                // 不执行
            newCap = DEFAULT_INITIAL_CAPACITY;
            newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
        }
        if (newThr == 0) {  // 不执行
            float ft = (float)newCap * loadFactor;
            newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ?
                      (int)ft : Integer.MAX_VALUE);
        }
        threshold = newThr;    //将新的临界值赋值赋值给threshold
        @SuppressWarnings({"rawtypes","unchecked"})
            Node<K,V>[] newTab = (Node<K,V>[])new Node[newCap];
        table = newTab;   //新的数组赋值给table
 
        //扩容后，重新计算元素新的位置
        if (oldTab != null) {   //原数组
            for (int j = 0; j < oldCap; ++j) {   //通过原容量遍历原数组
                Node<K,V> e;
                if ((e = oldTab[j]) != null) {   //判断node是否为空，将j位置上的节点
                //保存到e,然后将oldTab置为空，这里为什么要把他置为空呢，置为空有什么好处吗？？
                //难道是吧oldTab变为一个空数组，便于垃圾回收？？ 这里不是很清楚
                    oldTab[j] = null;
                    if (e.next == null)          //判断node上是否有链表
                        newTab[e.hash & (newCap - 1)] = e; //无链表，确定元素存放位置，
//扩容前的元素地址为 (oldCap - 1) & e.hash ,所以这里的新的地址只有两种可能，一是地址不变，
//二是变为 老位置+oldCap
                    else if (e instanceof TreeNode)
                        ((TreeNode<K,V>)e).split(this, newTab, j, oldCap);
                    else { // preserve order
                        Node<K,V> loHead = null, loTail = null;
                        Node<K,V> hiHead = null, hiTail = null;
                        Node<K,V> next;
 
                      
/* 这里如果判断成立，那么该元素的地址在新的数组中就不会改变。因为oldCap的最高位的1，在e.hash对应的位上为0，所以扩容后得到的地址是一样的，位置不会改变 ，在后面的代码的执行中会放到loHead中去，最后赋值给newTab[j]；
如果判断不成立，那么该元素的地址变为 原下标位置+oldCap，也就是lodCap最高位的1，在e.hash对应的位置上也为1，所以扩容后的地址改变了，在后面的代码中会放到hiHead中，最后赋值给newTab[j + oldCap]
             举个栗子来说一下上面的两种情况：
            设：oldCap=16 二进制为：0001 0000
                oldCap-1=15 二进制为：0000 1111
                e1.hash=10 二进制为：0000 1010
                e2.hash=26 二进制为：0101 1010
            e1在扩容前的位置为：e1.hash & oldCap-1  结果为：0000 1010 
            e2在扩容前的位置为：e2.hash & oldCap-1  结果为：0000 1010 
            结果相同，所以e1和e2在扩容前在同一个链表上，这是扩容之前的状态。
            
    现在扩容后，需要重新计算元素的位置，在扩容前的链表中计算地址的方式为e.hash & oldCap-1
    那么在扩容后应该也这么计算呀，扩容后的容量为oldCap*2=32 0010 0000 newCap=32，新的计算
    方式应该为
    e1.hash & newCap-1 
    即：0000 1010 & 0001 1111 
    结果为0000 1010与扩容前的位置完全一样。
    e2.hash & newCap-1 
    即：0101 1010 & 0001 1111 
    结果为0001 1010,为扩容前位置+oldCap。
    而这里却没有e.hash & newCap-1 而是 e.hash & oldCap，其实这两个是等效的，都是判断倒数第五位
    是0，还是1。如果是0，则位置不变，是1则位置改变为扩容前位置+oldCap。
            再来分析下loTail loHead这两个的执行过程（假设(e.hash & oldCap) == 0成立）：
            第一次执行：
            e指向oldTab[j]所指向的node对象，即e指向该位置上链表的第一个元素
            loTail为空,所以loHead指向与e相同的node对象，然后loTail也指向了同一个node对象。
            最后，在判断条件e指向next，就是指向oldTab链表中的第二个元素
            第二次执行：
            lotail不为null，所以lotail.next指向e，这里其实是lotail指向的node对象的next指向e，
            也可以说是，loHead的next指向了e，就是指向了oldTab链表中第二个元素。此时loHead指向        
            的node变成了一个长度为2的链表。然后lotail=e也就是指向了链表中第二个元素的地址。
            第三次执行：
            与第二次执行类似，loHead上的链表长度变为3，又增加了一个node，loTail指向新增的node
               ......
            hiTail与hiHead的执行过程与以上相同，这里就不再做解释了。
            由此可以看出，loHead是用来保存新链表上的头元素的，loTail是用来保存尾元素的，直到遍            
            历完链表。   这是(e.hash & oldCap) == 0成立的时候。
            (e.hash & oldCap) == 0不成立的情况也相同，其实就是把oldCap遍历成两个新的链表，
            通过loHead和hiHead来保存链表的头结点，然后将两个头结点放到newTab[j]与 
            newTab[j+oldCap]上面去      
*/
                              do {
                                next = e.next;
                            if ((e.hash & oldCap) == 0) {  
                                if (loTail == null)
                                    loHead = e;
                                else
                                    loTail.next = e;
                                loTail = e;
                            }
                            else {
                                if (hiTail == null)
                                    hiHead = e;
                                else
                                    hiTail.next = e;
                                hiTail = e;
                            }
                        } while ((e = next) != null);
                        if (loTail != null) {
                            loTail.next = null;   //尾节点的next设置为空
                            newTab[j] = loHead;
                        }
                        if (hiTail != null) {
                            hiTail.next = null;   //尾节点的next设置为空
                            newTab[j + oldCap] = hiHead;
                        }
                    }
                }
            }
        }
        return newTab;
}
```

# 1.7 VS 1.8
1. new HashMap();底层没创建一个长度为16的数组
2. jdk 8底层的数组是：Node[],而非Entry []
3. 首次调用put()方法时，底层创建长度为16的数组
4. jdk7底层结构只：数组+链表。jdk8中底层结构：数组+链表+红黑树。
5. 形成链表时，七上八下(jdk7:新的元素指向旧的元素,头插。jdk8：旧的元素指向新的元素，尾插)
6. 当数组的某一个索引位置上的元素以链表形式存在的数据个数 > 8 且当前数组的长度 > 64时，此时此索引位置上的所数据改为使用红黑树存储。