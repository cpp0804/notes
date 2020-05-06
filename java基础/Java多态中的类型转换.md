[TOC]

# 1. 引用类型转换
## 向上转换(自动/隐式类型转换)
小类型转换成大类型，例如将子类对象赋值给父类引用变量

向上转换后，子类中的独有变量将不能被访问。因为编译期间，只能使用变量的静态类型(即引用变量的类型)，实际类型(即new对象的类型)只有在运行期间才确定([方法调用](../JVM/方法调用.md))。

如果想要访问子类中的独有变量，必须在使用引用变量时强制将他转换成实际类型



## 向下转换(强制类型转化)
大类型转换成小类型，例如将父类对象赋值给子类引用变量，会存在转换风险


# 2. 规避转换风险（instanceof)

```java
//创建子类对象
Dog dog = new Dog();

//向上类型转换(类型自动提升),不存在风险
Animal animal = dog;

//风险演示  
//animal指向Dog类型对象，没有办法转化成Cat对象，编辑阶段不会报错，但是运行会报错
//对于cat变量，编译阶段使用的是静态类型即Cat;在运行阶段才使用实际类型Dog,也就是说在运行阶段才发现类型不匹配
Cat cat = (Cat)animal;
```


可以使用instanceof来规避运行时发生的类型转换风险,用instanceof来判断当前引用的实际类型是否匹配某一类型，如果是才进行向下转换
```java
//创建子类对象
Dog dog = new Dog();

//向上类型转换(类型自动提升),不存在风险
Animal animal = dog;

// 向下类型转换(存在一定风险),我们可以强制转换
if(animal instanceof Dog){
    Dog dog2 = (Dog)animal; 
    System.out.println("转化类型成功");
}
else{
    System.out.println("向下类型转化失败"+animal.getClass());
}

//规避风险 (instanceof运算符)
if(animal instanceof Cat){
    Cat cat  = (Cat)animal;
    System.out.println("转化类型成功");
}
else{
    System.out.println("转化类型失败"+animal.getClass());
}
```
