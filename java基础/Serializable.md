[TOC]

# 1. 例子
```java
public class Cat implements Serializable {
}

public static void serializeTest() {
        Cat c = new Cat(); // 2
        try {
            FileOutputStream fs = new FileOutputStream("testSer.ser");
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(c); // 3
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            FileInputStream fis = new FileInputStream("testSer.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            c = (Cat) ois.readObject(); // 4
            ois.close();
            System.out.println(c.getClass());
        } catch (Exception e) {
            e.printStackTrace();
        }
}
/*
class SCJP.Cat
*/
```

- 对象引用的序列化
```java
//Dog和Collar都要实现Serializable
public class Dog implements Serializable {

    private Collar theCollar;
    private int dogSize;

    public Dog(Collar collar, int size) {
        theCollar = collar;
        dogSize = size;
    }

    public Collar getCollar() {
        return theCollar;
    }
}

//如果不能修改Collar的代码，那么我们可以创建一个Collar的子类，让子类实现Serializable
public class Collar implements Serializable {
    private int collarSize;

    public Collar(int size) {
        collarSize = size;
    }

    public int getCollarSize() {
        return collarSize;
    }
}

 public static void serializeTest2() {
        Collar c = new Collar(3);
        Dog d = new Dog(c, 5);
        System.out.println("before: collar size is " + d.getCollar().getCollarSize());
        try {
            FileOutputStream fs = new FileOutputStream("testSer.ser");
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(d);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            FileInputStream fis = new FileInputStream("testSer.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            d = (Dog) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("after: collar size is " + d.getCollar().getCollarSize());
    }
/*
before: collar size is 3
after: collar size is 3
*/
```

# 2. transient
如果不需要Collar被序列化，可以定义他为transient
```java
private transient Collar theCollar;
```

但是这样当Dog被序列化后，Collar的信息在反序列化中就丢失了。那么需要在Dog中重写这两个方法。这个两个方法将在Dog被序列化/反序列化过程中自动调用来保存想要保存的Collar信息，然后根据这些信息重新构造Collar对象

如果没有定义这两个方法，当某个对象被反序列化后，它拥有的transient将被赋初始值(基本类型)或null(引用类型)，即使声明时赋值了也不会起作用
```java
private void writeObject(ObjectOutputStream os) {
    // your code for saving the Collar variables
}
private void readObject(ObjectInputStream os) {
    // your code to read the Collar state, create a new Collar, // and assign it to the Dog
}
```

```java
class Dog implements Serializable {
    
    private transient Collar theCollar; // we can't serialize this
    private int dogSize;

    public Dog(Collar collar, int size) {
        theCollar = collar;
        dogSize = size; 
    }
    public Collar getCollar() { 
        return theCollar; 
    }

    private void writeObject(ObjectOutputStream os) {
        try {
            os.defaultWriteObject();
            os.writeInt(theCollar.getCollarSize());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readObject(ObjectInputStream is) {
        try {
            is.defaultReadObject();
            theCollar = new Collar(is.readInt());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```


# 3. 继承关系的序列化
如果一个子类实现了 Serializable，父类没有实现Serializable。那么在反序列化的过程中。子类的构造函数和自身属性的赋值语句不会被执行，因为想要的是保留后面修改过的值。而父类的构造函数和自身属性的赋值语句将被执行,所以只能保留子类本身属性的值
```java
public class Animal { //// not serializable !
    int weight = 42;
}

public class Bird extends Animal implements Serializable {
    String name;

    Bird(int w, String n) {
        weight = w;
        name = n;
    }
}

  public static void extendSerializeTest() {
        Bird d = new Bird(35, "Fido");
        System.out.println("before: " + d.name + " " + d.weight);

        try {
            FileOutputStream fs = new FileOutputStream("testSer.ser");
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(d);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            FileInputStream fis = new FileInputStream("testSer.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            d = (Bird) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("after: " + d.name + " " + d.weight);
    }

/*
before: Fido 35
after: Fido 42
*/
```

