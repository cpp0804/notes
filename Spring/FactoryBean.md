# FactoryBean

[https://juejin.im/post/6844903954615107597](https://juejin.im/post/6844903954615107597)


FactoryBean是一种特殊的bean,它可以向容器中注册两个Bean，一个是它本身，一个是FactoryBean.getObject()方法返回值所代表的Bean


```java
@Component
public class CustomerFactoryBean implements FactoryBean<UserService> {
    @Override
    public UserService getObject() throws Exception {
        return new UserService();
    }

    @Override
    public Class<?> getObjectType() {
        return UserService.class;
    }
}
```


```java
public class UserService {

    public UserService(){
        System.out.println("userService construct");
    }
}
```


通过applicationContext获取UserService和customerFactoryBean得到的其实是同一个实例，这是因为通过getBean方法获取customerFactoryBean其实返回的是getObject方法中返回的实例。
如果想获取customerFactoryBean本身，需要在前面加一个&
```java
public class MainApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        System.out.println("容器启动完成");
        UserService userService = applicationContext.getBean(UserService.class);
        System.out.println(userService);
        Object customerFactoryBean = applicationContext.getBean("customerFactoryBean");
        System.out.println(customerFactoryBean);
        
        CustomerFactoryBean rawBean = (CustomerFactoryBean) applicationContext.getBean("&customerFactoryBean");
        System.out.println(rawBean);
    }
}
```
![FactoryBean_示例结果.png](./pic/FactoryBean_示例结果.png)
