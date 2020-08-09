[TOC]
# exploded war VS war
## war
称为packaged模式，将工程以打包的形式部署到应用服务器中
## exploded war
称为exploded模式，将工程以文件夹包含需要的内容的方式（含有打包war解压后的内容）部署到应用服务器中

exploded war的特点：部署更快且支持热部署（可以修改代码实时反应），eclipse中的tomcat插件基本都支持这种技术





<br>
<br>
<br>

        

# GET和POST
GET|POST
---|--- 
获取资源|提交数据
参数包含在URL|通过request body传参
浏览器回退时无害|浏览器回退时会再次提交请求
产生的URL地址可以被Bookmark|产生的URL地址不可以被Bookmark
GET请求会被浏览器主动cache|POST请求不会被浏览器主动cache
GET请求只能进行url编码|POST支持多种编码方式
GET请求参数会被完整保留在浏览器历史记录里|POST中的参数不会被保留
对参数的数据类型，GET只接受ASCII字符|没有限制
- GET和POST本质上就是TCP链接，并无差别。但是由于HTTP的规定和浏览器/服务器的限制，导致他们在应用过程中体现出一些不同
  - 在我大万维网世界中，TCP就像汽车，我们用TCP来运输数据，它很可靠，从来不会发生丢件少件的现象。但是如果路上跑的全是看起来一模一样的汽车，那这个世界看起来是一团混乱，送急件的汽车可能被前面满载货物的汽车拦堵在路上，整个交通系统一定会瘫痪。为了避免这种情况发生，交通规则HTTP诞生了。HTTP给汽车运输设定了好几个服务类别，有GET, POST, PUT, DELETE等等，HTTP规定，当执行GET请求的时候，要给汽车贴上GET的标签（设置method为GET），而且要求把传送的数据放在车顶上（url中）以方便记录。如果是POST请求，就要在车上贴上POST的标签，并把货物放在车厢里。当然，你也可以在GET的时候往车厢内偷偷藏点货物，但是这是很不光彩；也可以在POST的时候在车顶上也放一些数据，让人觉得傻乎乎的。HTTP只是个行为准则，而TCP才是GET和POST怎么实现的基本。 
  - 在我大万维网世界中，还有另一个重要的角色：运输公司。不同的浏览器（发起http请求）和服务器（接受http请求）就是不同的运输公司。 虽然理论上，你可以在车顶上无限的堆货物（url中无限加参数）。但是运输公司可不傻，装货和卸货也是有很大成本的，他们会限制单次运输量来控制风险，数据量太大对浏览器和服务器都是很大负担。业界不成文的规定是，（大多数）浏览器通常都会限制url长度在2K个字节，而（大多数）服务器最多处理64K大小的url。超过的部分，恕不处理。如果你用GET服务，在request body偷偷藏了数据，不同服务器的处理方式也是不同的，有些服务器会帮你卸货，读出数据，有些服务器直接忽略，所以，虽然GET可以带request body，也不能保证一定能被接收到哦。
 - GET产生一个TCP数据包；POST产生两个TCP数据包
   - 对于GET方式的请求，浏览器会把http header和data一并发送出去，服务器响应200（返回数据
   - 对于POST，浏览器先发送header，服务器响应100 continue，浏览器再发送data，服务器响应200 ok（返回数据）
   
   


 
<br>
<br>
<br>

        
        
# 转发（forward）VS 重定向（redirect）
## 详细
- RequestDispatcher.forward方法只能将请求转发给同一个WEB应用中的组件。HttpServletResponse.sendRedirect 方法不仅可以重定向到当前应用程序中的其他资源，还可以重定向到同一个站点上的其他应用程序中的资源，甚至是使用绝对URL重定向到其他站点的资源
- 如果传递给HttpServletResponse.sendRedirect 方法的相对URL以“/”开头，它是相对于整个WEB站点的根目录；如果调用RequestDispatcher.forward 方法时指定的相对URL以“/”开头，它是相对于当前WEB应用程序的根目录。
- HttpServletResponse.sendRedirect方法对浏览器的请求直接作出响应，响应的结果就是告诉浏览器去重新发出对另外一个URL的访问请求。这个过程好比有个绰号叫“浏览器”的人写信找张三借钱，张三回信说没有钱，让“浏览器”去找李四借，并将李四现在的通信地址告诉给了“浏览器”。于是，“浏览器”又按张三提供通信地址给李四写信借钱，李四收到信后就把钱汇给了“浏览器”。可见，“浏览器”一共发出了两封信和收到了两次回复，
 “浏览器”也知道他借到的钱出自李四之手
- RequestDispatcher.forward方 法在服务器端内部将请求转发给另外一个资源，浏览器只知道发出了请求并得到了响应结果，并不知道在服务器程序内部发生了转发行为。这个过程好比绰号叫“浏览器”的人写信找张三借钱，张三没有钱，于是张三找李四借了一些钱，甚至还可以加上自己的一些钱，然后再将这些钱汇给了“浏览器”。可见，“浏览器”只发 出了一封信和收到了一次回复，他只知道从张三那里借到了钱，并不知道有一部分钱出自李四之手
- RequestDispatcher.forward方法的调用者与被调用者之间共享相同的request对象和response对象，它们属于同一个访问请求和响应过程。HttpServletResponse.sendRedirect方法调用者与被调用者使用各自的request对象和response对象，它们属于两个独立的访问请求和响应过程。
- 对于同一个WEB应用程序的内部资源之间的跳转，特别是跳转之前要对请求进行一些前期预处理，并要使用HttpServletRequest.setAttribute方法传递预处理结果，那就应该使用RequestDispatcher.forward方法。不同WEB应用程序之间的重定向，特别是要重定向到另外一个WEB站点上的资源的情况，都应该使用HttpServletResponse.sendRedirect方法。

## 小结
转发（forward）|重定向（redirect)
---|---
浏览器URL的地址栏不变|浏览器URL的地址栏改变
服务器行为|客户端行为
浏览器只做了一次访问请求|浏览器做了至少两次的访问请求的
转发2次跳转之间传输的信息不会丢失|重定向2次跳转之间传输的信息会丢失（request范围）

## 例子
```
//@WebServlet("/user/loginServlet")
public class loginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public loginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        doPost(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        String  username=request.getParameter("login");
        String  password=request.getParameter("password");
        userInfo us=new userInfo();
        us.setUsername(username);
        us.setPassword(password);
        if (username.equals("11")&&password.equals("22")) {
            HttpSession session=request.getSession();
            session.setAttribute("user", us);
            //测试重定向
            response.sendRedirect("../main.jsp"); 
            //测试转发
            request.getRequestDispatcher("../main.jsp").forward(request, response); 
        }
        else {
            response.sendRedirect("../index.jsp");
        }
    }

}
```
## 选择
- 重定向的速度比转发慢，因为浏览器还得发出一个新的请求，所以如果在使用转发和重定向都无所谓的时候建议使用转发
- 因为转发只能访问当前WEB的应用程序，所以不同WEB应用程序之间的访问，特别是要访问到另外一个WEB站点上的资源的情况，这个时候就只能使用重定向了

## 应用场景
- 避免在用户重新加载页面时两次调用相同的动作

我们提交产品表单的时候，执行保存的方法将会被调用，并执行相应的动作；这在一个真实的应用程序中，很有可能将表单中的所有产品信息加入到数据库中。但是如果在提交表单后，重新加载页面，执行保存的方法就很有可能再次被调用。同样的产品信息就将可能再次被添加，为了避免这种情况，提交表单后，你可以将用户重定向到一个不同的页面，这样的话，这个网页任意重新加载都没有副作用；

- 将属性添加到Model

使用重定向不能把值传给目标页面。转发则可以简单的把属性添加到model让目标页面访问。

在Spring3.1版本以后，我们可以通过Flash属性，解决重定向时传值丢失的问题。

要使用Flash属性，必须在Spring MVC的配置文件中添加一个<annotation-driven/>。然后，还必须再方法上添加一个新的参数类型：org.springframework.web.servlet.mvc.support.RedirectAttributes。
```
@RequestMapping(value="saveProduct",method=RequestMethod.POST)
public String saveProduct(ProductForm productForm,RedirectAttributes redirectAttributes){      
    //执行产品保存的业务逻辑等       

    //传递参数       
    redirectAttributes.addFlashAttribute("message","The product is saved successfully");        
    //执行重定向      
    return "redirect:/……";
}

```

# Spring
## 概述
- 轻量级 
  - 依赖资源少
  - 消耗资源少
- 分层、full-stack一站式（为每层都提供了解决方案）
  - web层：Struts、springMVC
  - service层:spring
  - dao层：hibernate、mybatis、jdbcTemplate
- 核心
  - 控制反转IoC:将创建对象的控制权反转给spring
  - 面向切面编程AOP
- 方便解耦，简化开发
  - spring是一个工厂（容器），用于生产、维护bean
## 体系结构

第0层
- Test:整合junit

第一层
- core container
  - Beans:管理bean
  - Core:核心
  - Context:上下文（配置文件）
  - Expression Language:SpEL(表达式语言)

第二层
- AOP
- Aspects 

第三层

- Data Access/Integration
  - JdbcTemplate:整合jdbc
  - ORM:整合hibernate
  - Transaction:事务管理（tx)
- Web

## 入门案例IoC
### 导入jar包
4+1:4个核心+1个依赖
```
    <dependencies>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-core</artifactId>
            <version>LATEST</version>
        </dependency>

        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-beans</artifactId>
            <version>1.2.6</version>
        </dependency>

        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>1.2.6</version>
        </dependency>

        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-expression</artifactId>
            <version>5.0.9.RELEASE</version>
        </dependency>

        <dependency>
            <groupId>commons-logging</groupId>
            <artifactId>commons-logging</artifactId>
            <version>1.2</version>
        </dependency>
    </dependencies>
```
### 目标类
- 提供UserService接口和实现类
- 获得UserService实现类的实例：由spring创建对象实例->IoC控制反转，之后需要实例对象时，从spring工厂中获得
```
public interface UserService {
    public void addUser();
}


public class UserServiceImpl implements UserService {
    public void addUser() {
        System.out.println("add user");
    }
}
```

### 配置文件
- 位置：classpath（src)的resources下
- 名称：任意，但开发中常用applicationContext.xml
- 内容：添加schema约束
- schema命名空间
1. 命名空间声明：
- 默认：xmlns="" <标签名> 如<bean>
- 显示：xmlns:别名="" <别名:标签名> 如<context:annotation-config>
2. 确定schema:xsd文件位置(内容都是成对的)
xsi:schemaLocation="名称 位置"

beans.xml
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans 
                           http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!--配置service
    <bean>配置需要创建的对象
        id:用于之后从spring容器获得实例时使用的
        class:需要创建的实例的全限定类名
    -->
    <bean id="userService" class="service.UserServiceImpl">
    </bean>
</beans>
```

### 测试
```
public class UserServiceImplTest {

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: addUser()
     */
    @Test
    public void testAddUser() throws Exception {
        //1.获得容器
        String xmlPath="beans.xml";
        ApplicationContext applicationContext=new ClassPathXmlApplicationContext(xmlPath);

        //2.获得内容--不需要自己new，都是从spring容器获得
        UserService userService= (UserService) applicationContext.getBean("userService");
        userService.addUser();
    }
} 
```
## 入门案例DI
### 概念
- 依赖：一个对象需要使用另一对象
has a:
```
class B{
    //B依赖A
    private A a;
}
```
- 注入：通过setter方法进行另一对象实例的设置
```
class BookServiceImpl{
    //之前的开发 接口=实现类(service和dao耦合)
    //private BookDao bookDao=new BookDaoImpl();
    
    //spring(解耦：service使用dao接口，不知道具体的实现类)
    private BookDao bookDao;
    setter方法
}
```
模拟spring执行过程：
1. 创建service实例：BookService    bookService=new BookServiceImpl(); ->IoC <bean>
2. 创建dao实例:BookDao bookDao=new BookDaoImpl();   ->IoC <bean>
3. 将dao设置给service：bookService.setBookDao(bookDao);   ->DI  <property>

- DI（依赖注入dependency injection）


### 目标类
- 创建BookService接口和实现类
- 创建BookDao接口和实现类
```
package dao;

public interface BookDao {
    public void add();
}
```
```
package dao;

public class BookDaoImpl implements BookDao {
    public void add() {
        System.out.println("add book");
    }
}
```
```
package service;

public interface BookService {
    public void add();
}
```
```
package service;

import dao.BookDao;

public class BookServiceImpl implements BookService {

    private BookDao bookDao;

    public void add() {
        bookDao.add();
    }

    public void setBookDao(BookDao bookDao) {
        this.bookDao = bookDao;
    }
}
```
### 配置文件
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
    
    <!--
    <property>用于属性注入
           name:bean的属性名,通过setter方法获得
           ref:另一个bean的id值得引用
    -->
    <bean id="bookService" class="service.BookServiceImpl">
        <property name="bookDao" ref="bookDao"></property>
    </bean>
    
    <bean id="bookDao" class="dao.BookDaoImpl">
    </bean>
</beans>
```

### 测试
```
package service;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * UserServiceImpl Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>十月 20, 2018</pre>
 */
public class UserServiceImplTest {

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: addUser()
     */
    @Test
    public void testAddUser() throws Exception {
        //1.获得容器
        String xmlPath="beans.xml";
        ApplicationContext applicationContext=new ClassPathXmlApplicationContext(xmlPath);

        //2.获得内容--不需要自己new，都是从spring容器获得
        BookService bookService= (BookService) applicationContext.getBean("bookService");
        bookService.add();
    }
} 
```

## 核心API
- BeanFactory
  - 一个工厂，用户生成任意bean
  - 采用延迟加载，当第一次getBean()时才会初始化bean
  ```
  BeanFactory beanFactory=new XmlBeanFactory(new ClassPathResource(xmlPath));
  ```
  
- ApplicationContext
  - BeanFactory的子接口，功能更强大(国际化处理，Bean自动装配...)
  - 配置文件一加载，对象就实例化

- ClassPathXmlApplicationContext
  - ApplicationContext的一个实现，用于加载classpath（类路径、src)下的指定xml
  - 加载xml运行时位置：/WEB-INF/classes/...xml

- FileSystemXmlApplicationContext
  - ApplicationContext的一个实现，用于加载指定盘符下的xml
  - 加载xml运行时位置:通过java web ServletContext.getRealPath()获得具体盘符
  


## 装配bean--基于xml
### bean实例化方法
- 默认构造
```
<bean id="" class="">//必须提供默认构造
```
- 静态工厂

常用于spring整合其他框架（工具）

静态工厂用于生成实例对象，所有方法必须是static

```
<bean id="" class="工厂全限定类名" factory-method="静态方法>
```
```
package factory;

import service.UserService;
import service.UserServiceImpl;

public class MyBeanFactory {

    public static UserService createService() {
        return new UserServiceImpl();
    }
}
```
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!--将静态工厂创建的实例交给spring
        class:确定静态工厂的全限定类名
        factory-method:确定静态方法名
    -->
    <bean id="userService" class="factory.MyBeanFactory" factory-method="createService"></bean>

</beans>
```
```
package factory;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import service.UserService;

/**
 * MyBeanFactory Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>十月 20, 2018</pre>
 */
public class MyBeanFactoryTest {

    @Test
    public void testCreateService() throws Exception {
        UserService userService = MyBeanFactory.createService();
    }
} 
```

- 实例工厂

必须先有工厂的实例对象，然后通过工厂实例对象去创建对象，提供的所有方法都是非静态的

```
package factory;

import service.UserService;
import service.UserServiceImpl;

public class MyBeanFactory {

    public UserService createService() {
        return new UserServiceImpl();
    }
}
```
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!--创建工厂实例-->
    <bean id="beanFactory" class="factory.MyBeanFactory">
    </bean>
    <!--获得userService
        factory-bean:确定工厂实例
        factory-method：确定普通方法
    -->
    <bean id="userService" factory-bean="beanFactory" factory-method="createService">
    </bean>
</beans>
```
```
public class UserServiceImplTest {

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: addUser()
     */
    @Test
    public void testAddUser() throws Exception {
        //1.获得容器
        String xmlPath="beans.xml";
        ApplicationContext applicationContext=new ClassPathXmlApplicationContext(xmlPath);

        //2.获得内容--不需要自己new，都是从spring容器获得
        UserService userService=applicationContext.getBean("userService");
    }
}
```
### bean种类
- 普通bean
```
<bean id="" class="">
```

- FactoryBean
  - 是一个特殊的bean,有工厂生成对象的能力，只能生成特定的对象
  - 必须实现FactoryBean接口，此接口提供getObject()方法，用于获得特定bean
```
<bean id="" class="FB">//先创建FB实例，然后调用getObject()方法，并返回方法的返回值
```
- FactoryBean VS BeanFactory:
  - BeanFactory:工厂，用于生成任意bean
  - FactoryBean：特殊bean,用于生成另一特定的bean,例如ProxyFactoryBean用于生产代理对象
```
<bean id="" class="...ProxyFactoryBean">/获得代理对象实例
```

### 作用域
用于确定spring创建的bean的个数
- singleton单例：默认值
- prototype多例：每执行一次getbean()获得一个实例，例如Struts整合spring，配置action多例子
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
       
    <bean id="userService" class="service.UserServiceImpl" scope="prototype">
    </bean>
</beans>
```
- session
- request
- globalSession

### 生命周期
- 初始化和销毁
  - 有时候在bean初始化之后要执行的初始化方法，以及在bean销毁时执行的方法
```
<bean id="" class="" init-method="myInit" destroy-method ="myDestroy">
</bean>
```
```
package service;

public class UserServiceImpl implements UserService {
    public void addUser() {
        System.out.println("add user");
    }

    public void myInit() {
        System.out.println("init");
    }

    public void myDestroy() {
        System.out.println("destroy");
    }
}
```
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
    <!--
        init-method:配置初始化方法
        destroy-method:配置销毁方法
    -->
    <bean id="userService" class="service.UserServiceImpl" init-method="myInit" destroy-method="myDestroy">
    </bean>
</beans>
```
```
package service;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;

/**
 * UserServiceImpl Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>十月 20, 2018</pre>
 */
public class UserServiceImplTest {

    @Test
    public void testAddUser() throws Exception {
        //1.获得容器
        String xmlPath="beans.xml";
        ApplicationContext applicationContext=new ClassPathXmlApplicationContext(xmlPath);

        //2.获得内容--不需要自己new，都是从spring容器获得
        UserService userService= (UserService) applicationContext.getBean("userService");
        userService.addUser();
        //执行销毁方法容器必须close并且是单例，销毁方法才会执行,而接口ApplicationContext并没有定义close方法，ClassPathXmlApplicationContext定义了
        //applicationContext.getClass().getMethod("close").invoke(applicationContext);
        ((ClassPathXmlApplicationContext) applicationContext).close();
    }
} 
```
- BeanPostProcessor 后处理bean
  - spring提供的一种机制，只要实现了这个接口，并将实现了提供给spring容器（<bean class="">)，spring容器将自动调用执行:
    - 在初始化方法执行前执行postProcessBeforeInitialization(Object bean,String beanName)
    - 在初始化方法执行后执行postProcessAfterInitialization(Object bean,String beanName)
  - 这是spring提供的工厂钩子，用于修改实例对象，可以生成代理对象，是AOP底层
  - 模拟
  - 对容器中所有bean生效
```
A a=new A();
a=B.postProcessBeforeInitialization(a);
a.init();
//传入的a是目标对象，返回的a是代理对象（AOP）
a=B.postProcessAfterInitialization(a);
//执行的是代理对象的addUser()
a.addUser();
a.destroy();
```
```
package processor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class MyBeanPostProcessor implements BeanPostProcessor {
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("post" + beanName);
        return bean;
    }

    public Object postProcessAfterInitialization(final Object bean, String beanName) throws BeansException {
        System.out.println("after" + beanName);
        //生成代理
        return (Object) Proxy.newProxyInstance(MyBeanPostProcessor.class.getClassLoader(), bean.getClass().getInterfaces(), new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("开启事务");
                //执行目标方法
                Object obj=method.invoke(bean,args);
                System.out.println("提交事务");
                return obj;
            }
        });
    }
}
```
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="userService" class="service.UserServiceImpl" init-method="myInit" destroy-method="myDestroy">
    </bean>
    
    <!-- 将后处理的实现类注册给spring-->
    <bean class="processor.MyBeanPostProcessor"></bean>
</beans>
```

### 属性依赖注入

#### 构造方法

#### setter方法
```
package entity;

public class Person {

    private String name;
    private Integer age;
    private Address homeAddr;
    private Address companyAddr;

    @Override
    public String toString() {
        return "{" + name + "," + age + "," + homeAddr + "," + companyAddr + "}";
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setHomeAddr(Address homeAddr) {
        this.homeAddr = homeAddr;
    }

    public void setCompanyAddr(Address companyAddr) {
        this.companyAddr = companyAddr;
    }

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }

    public Address getHomeAddr() {
        return homeAddr;
    }

    public Address getCompanyAddr() {
        return companyAddr;
    }
}
```
```
package entity;

public class Address {

    private String addr;
    private String tel;

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getAddr() {
        return addr;
    }

    public String getTel() {
        return tel;
    }

    @Override
    public String toString() {
        return "Address{" +
                "addr='" + addr + '\'' +
                ", tel='" + tel + '\'' +
                '}';
    }
}
```
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
       
    <bean id="person" class="entity.Person">
        <property name="name" value="jack"></property>
        <property name="age">
            <value>1</value>
        </property>
        <property name="companyAddr" ref="companyAddr"></property>
        <property name="homeAddr">
            <ref bean="homeAddr"></ref>
        </property>
    </bean>

    <bean id="homeAddr" class="entity.Address">
        <property name="addr" value="aa"></property>
        <property name="tel" value="11"></property>
    </bean>
    <bean id="companyAddr" class="entity.Address">
        <property name="addr" value="aa"></property>
        <property name="tel" value="11"></property>
    </bean>
</beans>
```
#### P命令空间
- 对setter方法进行简化

替换了<property name="属性名">,而是<bean p:属性名="普通值" p:属性名-ref="引用值">
- 必须添加命名空间
```
xmlns:p="http://www.springframework.org/schema/p"
```
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:p="http://www.springframework.org/schema/p"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
       
    <bean id="person" class="entity.Person"
          p:name="aa" p:age="1"
          p:companyAddr-ref="companyAddr"
          p:homeAddr-ref="homeAddr">
    </bean>

    <bean id="homeAddr" class="entity.Address"
          p:addr="aa"
          p:tel="11">
    </bean>
    <bean id="companyAddr" class="entity.Address"
          p:addr="oo"
          p:tel="44">
    </bean>

</beans>
```
#### SpEL
对<property>进行统一编程，所有的内容都是要value
```
<property name="" value="#{表达式}">
```
- 常见的
  - 数字 #{123}、#{90.9}
  - 字符串 #{'jack'}
  - bean的引用 #{beanId}
  - bean属性的操作 #{beanId.propName}
  - bean方法的执行 #{beanId.toString()}
  - 引用静态方法、属性 #{T(类).字段|方法}

```
package entity;

public class Customer {
    private String name;

    private Double pi;

    public void setPi(Double pi) {
        this.pi = pi;
    }

    @Override
    public String toString() {
        return "Customer{" +
                ", name='" + name + '\'' +
                ", pi=" + pi +
                '}';
    }

    public Double getPi() {
        return pi;
    }

    public Customer() {
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:p="http://www.springframework.org/schema/p"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!--
         <property name="name" value="#{jack}"></property>
         <property name="name" value="#{customer.name.toUpperCase()}"></property>
         ?.如果对象不为null,将调用方法
         <property name="name" value="#{customer.name?.toUpperCase()}"></property>
    -->
    <bean id="customer" class="entity.Customer">
        <property name="name" value="#{customer.name?.toUpperCase()}"></property>
        <property name="pi" value="#{T(Math).PI}"></property>
    </bean>
</beans>
```
#### 集合注入
```
package entity;

import java.util.*;

public class CollData {
    private String[] arrayData;
    private List<String> listData;
    private Set<String> setData;
    private Map<String, String> mapData;
    private Properties propsData;

    @Override
    public String toString() {
        return "CollData{" +
                "arrayData=" + Arrays.toString(arrayData) +
                "\n, listData=" + listData +
                "\n, setData=" + setData +
                "\n, mapData=" + mapData +
                "\n, propsDatal=" + propsData +
                '}';
    }

    public void setArrayData(String[] arrayData) {
        this.arrayData = arrayData;
    }

    public void setListData(List<String> listData) {
        this.listData = listData;
    }

    public void setSetData(Set<String> setData) {
        this.setData = setData;
    }

    public void setMapData(Map<String, String> mapData) {
        this.mapData = mapData;
    }

    public void setPropsData(Properties propsData) {
        this.propsData = propsData;
    }

    public String[] getArrayData() {
        return arrayData;
    }

    public List<String> getListData() {
        return listData;
    }

    public Set<String> getSetData() {
        return setData;
    }

    public Map<String, String> getMapData() {
        return mapData;
    }

    public Properties getPropsData() {
        return propsData;
    }
}
```
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:p="http://www.springframework.org/schema/p"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
       
    <!--
        集合的注入都是给<property>添加子标签
        数组：<array>
        List:<list>
        Set:<set>
        Map:<map>
        Properties:<props>

        普通数据:<value>
        引用数据:<ref>
    -->
    <bean id="collData" class="entity.CollData">
        <property name="arrayData">
            <array>
                <value>a</value>
                <value>b</value>
                <value>c</value>
            </array>
        </property>
        <property name="listData">
            <list>
                <value>l</value>
                <value>b</value>
                <value>j</value>
            </list>
        </property>
        <property name="setData">
            <set>
                <value>l</value>
                <value>b</value>
                <value>t</value>
            </set>
        </property>
        <property name="mapData">
            <map>
                <entry key="jack" value="jack"></entry>
                <entry>
                    <key><value>rose</value></key>
                    <value>rose</value>
                </entry>
            </map>
        </property>
        <property name="propsData">
            <props>
                <prop key="kk">lll</prop>
                <prop key="pp">pp</prop>
            </props>
        </property>
    </bean>

</beans>
```

## 装配bean--基于注解
注解就是一个类，使用@注解名称

开发中使用注解取代xml配置文件
### 添加命名空间
```
xmlns:context="http://www.springframework.org/schema/context"

xsi:schemaLocation="http://www.springframework.org/schema/beans
                    http://www.springframework.org/schema/beans/spring-beans.xsd
                    http://www.springframework.org/schema/context
                    http://www.springframework.org/schema/context/spring-context.xsd">
```
### @Component
- @Component

取代<bean class="">
- @Component("id")

取代<bean id="" class=""> 
```
@Component
public class UserServiceImpl implements UserService {
    public void addUser() {
        System.out.println("add user");
    }

    public void myInit() {
        System.out.println("init");
    }

    public void myDestroy() {
        System.out.println("mdestroy");
    }
}
```
```
    <!--
        组件扫描，扫描含有注解的类
    -->
    <context:component-scan base-package="service"></context:component-scan>
```
### web开发
1. 提供3个@Component衍生注解(功能一样)
- Repository:dao层
- Service:service层
- Controller:web层

2. 依赖注入
可以给私有字段设置，也可以给setter方法设置
- 普通值：@Value("")
- 引用值
  - 按类型注入：@Autowired
  - 按名称注入
    - @Autowired  @Qualifire("名称")
    - @Resource("名称")
3. 例子
```
package entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import service.StudentService;

@Controller("studentAction")
public class StudentAction {

    @Autowired
    private StudentService studentService;

    public void execute() {
        System.out.println("...........");
        studentService.add();
    }
}
```
```
package service;

public interface StudentService {
    void add();
}
```
```
package service;

import dao.StudentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl implements StudentService {

    private StudentDao studentDao;

    @Autowired
    @Qualifier("studentDao")
    public void setStudentDao(StudentDao studentDao) {
        this.studentDao = studentDao;
    }

    public void add() {
        studentDao.add();
    }
}
```

```
package dao;

public interface StudentDao {
    void add();
}
```

```
package dao;

import org.springframework.stereotype.Repository;

@Repository("studentDao")
public class StudentDaoImpl implements StudentDao {
    public void add() {
        System.out.println("add student");
    }
}
```
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:p="http://www.springframework.org/schema/p"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans

    <!--
        组件扫描，扫描含有注解的类
    -->
    <context:component-scan base-package="dao"></context:component-scan>
    <context:component-scan base-package="service"></context:component-scan>
    <context:component-scan base-package="entity"></context:component-scan>
</beans>
```
```
package service;

import entity.CollData;
import entity.Customer;
import entity.Person;
import entity.StudentAction;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;

/**
 * UserServiceImpl Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>十月 20, 2018</pre>
 */
public class UserServiceImplTest {

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: addUser()
     */
    @Test
    public void testAddUser() throws Exception {
        String xmlPath="beans.xml";
        ApplicationContext applicationContext=new ClassPathXmlApplicationContext(xmlPath);
        StudentAction studentAction= (StudentAction) applicationContext.getBean("studentAction");
        studentAction.execute();
    }
} 
```
### 生命周期
- 初始化 @PostConstruct
- 销毁 @PreDestroy
```
package service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component("userService")
//@Scope("prototype")
public class UserServiceImpl implements UserService {

    public void addUser() {
        System.out.println("add user");
    }

    @PostConstruct
    public void myInit() {
        System.out.println("init");
    }

    @PreDestroy
    public void myDestroy() {
        System.out.println("destroy");
    }
}
```

### 作用域
@Scope("prototype")
```
@Component
@Scope("prototype")
public class UserServiceImpl implements UserService {

    public void addUser() {
        System.out.println("add user");
    }

    public void myInit() {
        System.out.println("init");
    }

    public void myDestroy() {
        System.out.println("mdestroy");
    }
}
```
### 注解和xml混合使用
- 将所有bean配置到xml中
```
<bean id="" class="">
```
- 将所有依赖使用注解
```
@Autowired
```
默认不生效，为了生效需要在xml中配置：
```
<context:annotation-config/>
```
- 比较
  - 两个注解不一起使用，因为只有一个会生效
  - 注解1扫描含有注解（@Component)的类，注入注解自动生效
  - 注解2只在xml和注解（注入）混合使用时，使注入注解生效
```
<context:component-scan base-package="">//1
<context:annotation-config/>//2
```


## AOP
### AOP介绍
- 定义
  - Aspect Orientred Program 面向切面编程
  - 采用横向抽取机制，取代了传统的纵向集成的重复代码
  - 经典应用：事务管理、性能监视、安全检查、缓存、日志
  - spring AOP使用纯java实现，不需要专门的编译过程和类加载器，在运行期通过代理方式向目标类织入增强代码
```
//target
class UserService{
    //joinPoint||pointCut
    addUser();
    
    //joinPoint||pointCut
    updateUser();
}

class UserServiceImpl{
    addUser(){
        开始事务;
        super.addUser();
        提交事务;
    }
    updateUser(){
        开始事务;
        super.updateUser();
        提交事务;
    }
}
```
重复了很多开始事务、提交事务的代码,所以把这些代码抽取出来

```
class A{
    //advice
    before(){
        开始事务；
    }
    //advice
    after(){
        提交事务;
    }
}
```
A和UserService是没有关系的类，由spring容器生成代理类(Proxy)来组合A和UserService

Proxy有和UserService一样的方法

- 实现原理
  - AOP底层将使用代理机制进行实现
  - 接口+实现类：spring采用JDK的动态代理
  - 实现类：spring采用cglib字节码增强

### AOP术语
- target(目标类)

需要被代理的类 UserService

- joinPoint(连接点)

指那些可能被拦截到的方法，例如Useservice的所有方法

- pointCut(切入点)

已经被增强的连接点，例如addUser()

- advice(通知/增强)

增强的代码，例如before()

- weaving(织入)

把增强advice应用到目标对象target来创建新的代理对象proxy的过程

- proxy(代理)


- aspect(切面)

是切入点pointCut和通知advice的结合

### 手动代理
#### JDK动态代理
JDK动态代理是对"装饰者"设计模式的简化，使用前提是必须有接口
- 目标类：接口+实现类
- 切面类MyAspect：用于存放通知
- 工厂类：生成代理
- 测试

```
package service;

public interface UserService {

    public void addUser();

    public void updateUser();

    public void deleteUser();
}
```
```
package service;

public class UserServiceImpl implements UserService {

    public void addUser() {
        System.out.println("add user");
    }

    public void updateUser() {
        System.out.println("update user");
    }

    public void deleteUser() {
        System.out.println("delete user");
    }
}
```
```
package aspect;

public class MyAspect {

    public void before(){
        System.out.println("before");
    }
    public void after(){
        System.out.println("after");
    }
}
```
```
package factory;

import aspect.MyAspect;
import service.UserService;
import service.UserServiceImpl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class MyBeanFactory {

    public static UserService createService() {

        //1.目标类
        final UserService userService = new UserServiceImpl();
        
        //2.切面类
        final MyAspect myAspect = new MyAspect();
        
        /*3.代理类,将目标类(切入点)和切面类(通知)结合得到切面
         * Proxy.newProxyInstance(loader,interfaces,h)
         *      loader:类加载器，动态代理类在运行时创建，任何类都需要类加载器将其加载到内存
         *              一般情况采用：当前类.class.getClassLoader();
         *                          目标类实例.getClass().getClassLoader();
         *      Class[] interfaces:代理类需要实现的所有接口
         *                  方式一：目标类实例.getClass().getInterfaces();//只能获得自己的接口，不能获得父元素接口
         *                  方式二：new Class[]{UserService.class}
         *      InvocationHandler h:处理类接口，一般采用内部匿名类
         *                          提供了invoke方法：代理类的每一个方法执行时都将去调用一次invoke
         *                                  Object proxy：代理对象
         *                                  Method method:代理对象当前执行的方法的描述对象
         *                                      执行的方法名：method.getName()
         *                                      执行方法:method.invoke(对象,实际参数)
         *                                  Object[]args：方法的实际参数
         */
         
        /**
         * 当对代理类调用addUser()时，直接会去调用invoke()
         *          ```
         *          addUser(){
         *              invoke(this,addUser,[]);
         *          }
         *          ```
         */
         
        UserService proxyService = (UserService) Proxy.newProxyInstance(userService.getClass().getClassLoader(), userService.getClass().getInterfaces(), new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //将目标类和切面类结合

                //执行切面类的前方法
                myAspect.before();
                
                //执行目标类的方法
                Object obj = method.invoke(userService, args);
                
                //执行切面类的后方法
                myAspect.after();
                
                return obj;
            }
        });
        return proxyService;
    }
}

```
```
package aspect;

import factory.MyBeanFactory;
import org.junit.Test;
import service.UserService;

public class TestAspect {

    @Test
    public void demo01() {
        UserService userService = MyBeanFactory.createService();
        userService.addUser();
        userService.updateUser();
        userService.deleteUser();
    }
}
```

#### cglib字节码增强框架
没有接口，只有实现类
- 运行原理

在运行时，创建目标类的子类，从而对目标类进行增强

- 导入jar包
  - 手动导包：
     - 核心：cglib-2.2.jar
     - 依赖：asm-3.3.jar
  - spring-core.jar已经整合以上两个内容



```
package service;

public class UserServiceImpl
{

    public void addUser() {
        System.out.println("add user");
    }

    public void updateUser() {
        System.out.println("update user");
    }

    public void deleteUser() {
        System.out.println("delete user");
    }
}
```
```
package aspect;

public class MyAspect {

    public void before(){
        System.out.println("before");
    }
    public void after(){
        System.out.println("after");
    }
}
```
```
package factory;

import aspect.MyAspect;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import service.UserService;
import service.UserServiceImpl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class MyBeanFactory {

    public static UserService createService() {

        //1.目标类
        final UserService userService = new UserServiceImpl();

        //2.切面类
        final MyAspect myAspect = new MyAspect();

        //3.代理类
        //3.1 核心类
        Enhancer enhancer=new Enhancer();
        //3.2 确定父类
        enhancer.setSuperclass(userService.getClass());
        /*3.3 设置回调函数
            MethodInterceptor接口等效JDK的InvocationHandler接口
            intercept等效JDK的invoke()
                前3个参数与invoke一样
                第四个参数methodProxy：方法的代理
         */
        enhancer.setCallback(new MethodInterceptor() {

            public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
               //前方法
                myAspect.before();

                //执行目标类的方法
                Object obj=method.invoke(userService,args);

                //执行代理类的父类，就是执行目标类（目标类和代理类是父子关系）
                //methodProxy.invokeSuper(proxy,args);

                //后方法
                myAspect.after();

                return obj;


            }
        });
        //3.4 创建代理
        UserServiceImpl proxService= (UserServiceImpl) enhancer.create();
        return proxService;
    }

}
```
- JDK返回的代理是 ：$Proxy4
- cglib返回的代理是：UserServiceImpl$$EnhancerByCGLIB$$3cd26195

### AOP联盟增强类型
spring按照通知advice在目标类方法的连接点位置，可分为5类：
- 前置通知(MethodBeforeAdvice)

在目标方法执行前实施增强，可以阻止目标方法的执行(抛出异常)
- 后置通知(AfterReturingAdvice)

在目标方法执行后实施增强,可以获得目标方法的返回值
- 环绕通知(MethodInterceptor)

在目标方法执行前后实施增强
- 异常抛出通知(ThrowsAdvice)

在方法(包括前置、后置、环绕、目标)抛出异常后实施增强
- 引介通知(IntroductionInterceptor)
```
//环绕通知必须手动执行目标方法
try{
    前置通知;
    执行目标方法;
    后置通知;
}catch(){
    异常抛出通知;
}
```
### spring代理(半自动)
让spring去创建代理对象，我们从spring容器中手动去获取代理对象
- 导入jar包
  - 核心：4+1
  - AOP
    - AOP联盟(规范):com.springsource.org-aopalliance-1.0.0.jar
    - aop spring(实现):spring-aop-3.2.0.RELEASE.jar


- 目标类
- 切面类
- 将目标列和切面类整合

```
package service;

public interface UserService {

    public void addUser();

    public void updateUser();

    public void deleteUser();
}
```
```
package service;

public class UserServiceImpl implements UserService {

    public void addUser() {
        System.out.println("add user");
    }

    public void updateUser() {
        System.out.println("update user");
    }

    public void deleteUser() {
        System.out.println("delete user");
    }
}
```
```
package aspect;


import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * 切面类中确定通知，需要实现不同接口，接口就是规范，从而确定方法名称
 * 采用环绕通知 MethodInterceptor
 */

public class MyAspect implements MethodInterceptor {


    public Object invoke(MethodInvocation invocation) throws Throwable {
        //前方法
        System.out.println("before");

        //手动执行目标方法
        Object obj=invocation.proceed();

        //后方法
        System.out.println("after");

        return obj;
    }
}
```
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:p="http://www.springframework.org/schema/p"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
                           http://www.springframework.org/schema/beans/spring-beans.xsd
                           http://www.springframework.org/schema/context
                           http://www.springframework.org/schema/context/spring-context.xsd">

    <!--1.创建目标类-->
    <bean id="userService" class="service.UserServiceImpl">
    </bean>

    <!--2.创建切面类-->
    <bean id="myAspect" class="aspect.MyAspect"></bean>

    <!--3.创建代理类
        使用工厂bean FactoryBean,底层调用getObject()返回特殊bean
        ProxyFactoryBean:用于创建代理的工厂bean,生成特殊代理对象
            interfaces:确定接口,通过array可以设置多个值，只有一个值时直接使用value=""
            target：确定目标类
            interceptorNames:切面类的名称,类型是string[]
            optimize:强制使用cglib
         底层默认机制：
            如果目标类有接口，采用JDK动态代理
            如果没有接口，采用cglib
    -->
    <bean id="proxyUserService" class="org.springframework.aop.framework.ProxyFactoryBean">
        <property name="interfaces" value="service.UserService">
        </property>
        
        <property name="target" ref="userService"></property>

        <property name="interceptorNames" value="myAspect"></property>

        <property name="optimize" value="true"></property>
    </bean>
</beans>
```
```
package aspect;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import service.UserService;

public class TestSemiAop {

    @Test
    public void demo01(){
        String xmlPath="beans.xml";
        ApplicationContext applicationContext=new ClassPathXmlApplicationContext(xmlPath);

        //获得代理类
        UserService userService= (UserService) applicationContext.getBean("proxyUserService");

        userService.addUser();
        userService.updateUser();
        userService.deleteUser();
    }
}
```
### spring  AOP编程(全自动)
- 从spring容器中获得目标类，如果配置了aop,spring将自动生成代理
- 确定目标类:aspectJ的切入点表达式，导入jar包:com.springsource.org-aspectj.weaver-1.6.8.RELEASE.jar
```
<dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjweaver</artifactId>
    <version>1.9.1</version>
</dependency>
```
- 导入命名空间
```
 xmlns:aop="http://www.springframework.org/schema/aop"
 
 xsi:schemaLocation="http://www.springframework.org/schema/beans
                           http://www.springframework.org/schema/beans/spring-beans.xsd
                           http://www.springframework.org/schema/context
                           http://www.springframework.org/schema/context/spring-context.xsd
                           http://www.springframework.org/schema/aop
                           http://www.springframework.org/schema/aop/spring-aop.xsd"
```

```
package service;

public interface UserService {

    public void addUser();

    public void updateUser();

    public void deleteUser();
}
```
```
package service;

public class UserServiceImpl implements UserService {

    public void addUser() {
        System.out.println("add user");
    }

    public void updateUser() {
        System.out.println("update user");
    }

    public void deleteUser() {
        System.out.println("delete user");
    }
}
```
```
package aspect;


import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * 切面类中确定通知，需要实现不同接口，接口就是规范，从而确定方法名称
 * 采用环绕通知 MethodInterceptor
 */

public class MyAspect implements MethodInterceptor {


    public Object invoke(MethodInvocation invocation) throws Throwable {
        //前方法
        System.out.println("before");

        //手动执行目标方法
        Object obj=invocation.proceed();

        //后方法
        System.out.println("after");

        return obj;
    }
}
```
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:p="http://www.springframework.org/schema/p"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
                           http://www.springframework.org/schema/beans/spring-beans.xsd
                           http://www.springframework.org/schema/context
                           http://www.springframework.org/schema/context/spring-context.xsd
                           http://www.springframework.org/schema/aop
                           http://www.springframework.org/schema/aop/spring-aop.xsd">

    <!--1.创建目标类-->
    <bean id="userService" class="service.UserServiceImpl">
    </bean>

    <!--2.创建切面类-->
    <bean id="myAspect" class="aspect.MyAspect"></bean>

    <!--3.aop编程
          3.1 导入命名空间
          3.2 使用<aop:config>进行配置
                proxy-target-class="true":使用cglib代理
                <aop:pointcut>:切入点,从目标对象上获得具体的方法
                <<aop:advisor>:特殊的切面，只有一个通知和一个切入点
                        advice-ref:通知的引用
                        pointcut-ref:切入点的引用
          3.3 切入点表达式
                execution(* service.*.*(..))
             选择方法  返回值任意 包  类名任意 方法名任意 （..）参数任意

          3.4 默认底层
                有接口：jdk
                没有接口：cglib
     -->
    <aop:config proxy-target-class="true">
        <aop:pointcut id="myPointCut" expression="execution(* service.*.*(..))"></aop:pointcut>
        <aop:advisor advice-ref="myAspect" pointcut-ref="myPointCut"></aop:advisor>
    </aop:config>
</beans>
```

```
package aspect;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import service.UserService;

public class TestAop {

    @Test
    public void demo01(){
        String xmlPath="beans.xml";
        ApplicationContext applicationContext=new ClassPathXmlApplicationContext(xmlPath);

        //获得目标类
        UserService userService= (UserService) applicationContext.getBean("userService");

        userService.addUser();
        userService.updateUser();
        userService.deleteUser();
    }
}
```



### AspectJ
#### 介绍
- 基于java语言的AOP框架
- 主要用途：自定义开发，不破坏别人代码的情况下，植入自己的代码

#### 切入点表达式
- execution()用于描述方法
  - 语法：execution(修饰符 返回值 包.类.方法(参数)throws 异常)
    - 修饰符：一般省略
      - public
      - *：任意
    - 返回值：不能省略
      - void
      - String
      - *：任意
    - 包：可以省略
      - com.aa.bb
      - com.aa.bb. * .service
      - com..：com下面所有的子包，含自己
      - com.aa.bb. * .service..：bb包下面任意子包，固定目录service，service目录任意包
    - 类：可以省略
      - UserService
      - *Impl:以Impl结尾的
      - User*:以User开头的
      - *：任意
    - 方法：不能省略
      - addUser:
      - add*:以*开头
      - *Do:以Do结尾
      - *：任意
    - (参数)
      - ():无参
      - (int):一个整型
      - (int,int):两个整型
      - (..):参数任意
    - throws 异常：可省略、一般不写

  - 综合
    - execution(* com.aa.bb. * .service.. * . * (..)):
      - 返回值任意，com.aa.bb下的任意子包的service下的子包的所有类的所有方法，参数任意
    - 匹配任意一个都行 
      - <aop:pointcut expression="execution(省略)||execution(省略)" id="myPointCut"/>
    - execution(* com.aa.bb. * .service..   *Do. * (..))
- within()用于匹配包或子包中的方法

within(com.aa.bb.. *):com.aa.bb的子目录下的任意
- this():匹配实现接口的代理对象中的方法

this(com.aa.bb.user.UserDao)
- target():匹配实现接口的目标对象中的方法

target(com.aa.bb.user.UserDao)
- args():匹配参数格式符合标准的方法

args(int,int)
- bean():对指定的bean中的所有的方法

bean("userService")



#### AspectJ通知类型
aop联盟定义的通知类型，具有特定的接口。我们必须实现接口从而确定方法名称，spring就能去调用

aspectJ的通知类型只定义了类型名称和方法格式

- 前置通知before
  - 在方法执行前执行，应用如校验。如果通知抛出异常则阻止方法运行
  - <aop:before>

- 后置通知afterReturning
  - 方法执行后执行，可以获得方法的返回值，应用如：常规数据处理。如果方法中抛出异常，通知无法执行
  - <aop:after-returning>

- 环绕通知around
  - 方法执行前后分别执行，必须手动执行目标方法
  - <aop:around>

- 抛出异常通知afterThrowing
  - 方法抛出异常后执行，如果方法没有抛出异常则无法执行。应用如：包装异常信息
  - <aop:after-throwing>

- 最终通知after
  - 方法执行完毕后执行，无论方法中是否出现异常。应用如：清理现场
  - <aop:after>
```
try{
    前置通知before
    手动执行目标方法：
    后置通知afterReturning
}catch(){
    抛出异常通知afterThrowing
}finally{
    最终通知after
}
```

#### 导入jar包
- aop 联盟
- spring aop
- aspect 规范
- spring aspect实现:spring-aspects-3.2.0.RELEASE.jar

#### 基于XML
- 目标类：接口+实现
- 切面类：编写多个通知，采用aspectJ通知名任意(方法名任意)
- aop编程：将通知应用到目标类
- 测试


```
package service;

public interface UserService {

    public void addUser();

    public String updateUser();

    public void deleteUser();
}
```
```
package service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class UserServiceImpl implements UserService {

    public void addUser() {
        System.out.println("add user");
    }

    public String updateUser() {
        System.out.println("update user");
        int i=1/0;
        return "update";
    }

    public void deleteUser() {
        System.out.println("delete user");
    }
}

```

```
package aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * 切面类，含有多个通知
 */
public class MyAspectJ {

    public void before(JoinPoint joinPoint) {
        System.out.println("前置通知: " + joinPoint.getSignature().getName());
    }

    public void afterReturning(JoinPoint joinPoint, Object ret) {
        System.out.println("后置通知" + joinPoint.getSignature().getName() + ",--> " + ret);
    }

    /*环绕通知
        返回值：必须是Object
        参数：必须是ProceedingJoinPoint
        抛出异常：必须 throws Throwable
     */
    public Object myAround(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("环绕前");
        //手动执行目标方法
        Object obj = joinPoint.proceed();

        System.out.println("环绕后");
        return obj;
    }

    public void afterThrowing(JoinPoint joinPoint, Throwable e) {
        System.out.println("抛出异常：" + e.getMessage());
    }

    public void after(JoinPoint joinPoint) {
        System.out.println("最终通知");
    }
}
```

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
                           http://www.springframework.org/schema/beans/spring-beans.xsd
                           http://www.springframework.org/schema/aop
                           http://www.springframework.org/schema/aop/spring-aop.xsd">


    <!--创建目标类-->
    <bean id="userService" class="service.UserServiceImpl"></bean>

    <!-- 创建切面类(通知)-->
    <bean id="myAspectJ" class="aspect.MyAspectJ"></bean>

    <!--aop编程
        <aop:aspect>:将切面类声明成切面，从而可以获得通知(方法)
                ref:切面类的引用
        <aop:pointcut>:声明一个切入点，所有的通知都可以使用
                expression:切入点表达式
                id:用于其他通知引用
    -->
    <aop:config>
        <aop:aspect ref="myAspectJ">
            <aop:pointcut expression="execution(* service.*.*(..))" id="myPointCut"></aop:pointcut>
            <!--前置通知
                 <aop:before>
                    method:通知，就是方法名
                    pointCut:切入点表达式，此表达式只能当前通知使用
                    pointcut-ref:切入点的引用，可以与其他通知共享切入点
                 通知方法可以有参数:public void before(JoinPoint joinPoint)
                    org.aspectj.lang.JoinPoint:用于描述连接点(目标方法)，可以获得当前目标方法的方法名
                 例如：
            -->
            <aop:before method="before" pointcut-ref="myPointCut"></aop:before>

            <!--后置通知,目标方法后执行，可以获得返回值
                    returning:通知方法第二个参数的名称
                    通知方法格式：public void afterReturning(JoinPoint joinPoint,Object ret)
                        Object ret:参数名由returning配置,获得的是切入点的返回值
            -->
            <aop:after-returning returning="ret" method="afterReturning"
                                 pointcut-ref="myPointCut"></aop:after-returning>

            <!--环绕通知
                通知方法格式：public Object myAround(ProceedingJoinPoint joinPoint)throws Throwable
                    返回值类型：必须是Object
                    方法名:任意
                    参数：必须是import org.aspectj.lang.ProceedingJoinPoint
                    必须抛出异常
                执行目标方法：Object obj=joinPoint.proceed();
            -->
            <aop:around method="myAround" pointcut-ref="myPointCut"></aop:around>

            <!--抛出异常
                通知方法格式:public void afterThrowing(JoinPoint joinPoint,Throwable e))
                    Throwable e:获得异常信息，类型是Throwable，参数名由throwing="e"配置
                throwing:通知方法的第二个参数名称
            -->
            <aop:after-throwing throwing="e" method="afterThrowing" pointcut-ref="myPointCut"></aop:after-throwing>

            <!--最终通知
            -->
            <aop:after method="after" pointcut-ref="myPointCut"></aop:after>
        </aop:aspect>
    </aop:config>
</beans>
```

```
package aspect;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import service.UserService;

public class TestAspectJ {


    @Test
    public void demo01() {
        String xmlPath = "beansJ.xml";
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(xmlPath);

        //获得目标类
        UserService userService = (UserService) applicationContext.getBean("userService");

        userService.addUser();
        userService.updateUser();
        userService.deleteUser();
    }
}
```


#### 基于注解

基于java语言的AOP框架
- 替换<bean id="" class="">
```
@Service("userService")
public class UserServiceImpl implements UserService {
}
```
```
@Component("myAspectJAnnotation")
public class MyAspectJAnnotation {
}
```
- 扫描注解类
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
                           http://www.springframework.org/schema/beans/spring-beans.xsd
                           http://www.springframework.org/schema/aop
                           http://www.springframework.org/schema/aop/spring-aop.xsd
                           http://www.springframework.org/schema/context
                           http://www.springframework.org/schema/context/spring-context.xsd">

    <!--1.扫描注解类-->
    <context:component-scan base-package="aspect"></context:component-scan>
    <context:component-scan base-package="service"></context:component-scan>
</beans>
```
- 替换AOP

1.进行aspectJ自动代理
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
                           http://www.springframework.org/schema/beans/spring-beans.xsd
                           http://www.springframework.org/schema/aop
                           http://www.springframework.org/schema/aop/spring-aop.xsd
                           http://www.springframework.org/schema/context
                           http://www.springframework.org/schema/context/spring-context.xsd">

    <!--1.扫描注解类-->
    <context:component-scan base-package="aspect"></context:component-scan>
    <context:component-scan base-package="service"></context:component-scan>

    <!--2.确定aop注解生效-->
    <aop:aspectj-autoproxy></aop:aspectj-autoproxy>
</beans>
```
2.声明切面<aop:aspect ref="myAspect">
```
@Component("myAspectJAnnotation")
@Aspect
public class MyAspectJAnnotation {
}
```
- 替换前置通知<aop:before>
```
@Component("myAspectJAnnotation")
@Aspect
public class MyAspectJAnnotation {

    @Before("execution(* service.*.*(..))")
    public void before(JoinPoint joinPoint) {
        System.out.println("前置通知: " + joinPoint.getSignature().getName());
    }
}
```
- 替换公共切入点 <aop:pointcut>
```
package aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component("myAspectJAnnotation")
@Aspect
public class MyAspectJAnnotation {

    //切入点当前有效
    @Before("execution(* service.*.*(..))")
    public void before(JoinPoint joinPoint) {
        System.out.println("前置通知: " + joinPoint.getSignature().getName());
    }

    //声明公共的切入点
    @Pointcut("execution(* service.*.*(..))")
    private void myPointCut(){

    }
}

```
- 替换后置通知<aop:after-returning>
```
package aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component("myAspectJAnnotation")
@Aspect
public class MyAspectJAnnotation {

    //切入点当前有效
    @Before("execution(* service.*.*(..))")
    public void before(JoinPoint joinPoint) {
        System.out.println("前置通知: " + joinPoint.getSignature().getName());
    }

    //声明公共的切入点
    @Pointcut("execution(* service.*.*(..))")
    private void myPointCut() {

    }

    @AfterReturning(value = "myPointCut()", returning = "ret")
    public void afterReturning(JoinPoint joinPoint, Object ret) {
        System.out.println("后置通知" + joinPoint.getSignature().getName() + ",--> " + ret);
    }
}
```
- 替换环绕通知<aop:around>
```
package aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Component("myAspectJAnnotation")
@Aspect
public class MyAspectJAnnotation {

    //切入点当前有效
    @Before("execution(* service.*.*(..))")
    public void before(JoinPoint joinPoint) {
        System.out.println("前置通知: " + joinPoint.getSignature().getName());
    }

    //声明公共的切入点
    @Pointcut("execution(* service.*.*(..))")
    private void myPointCut() {

    }

    @AfterReturning(value = "myPointCut()", returning = "ret")
    public void afterReturning(JoinPoint joinPoint, Object ret) {
        System.out.println("后置通知" + joinPoint.getSignature().getName() + ",--> " + ret);
    }

    /*环绕通知
        返回值：必须是Object
        参数：必须是ProceedingJoinPoint
        抛出异常：必须 throws Throwable
     */
    @Around("myPointCut()")
    public Object myAround(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("环绕前");
        //手动执行目标方法
        Object obj = joinPoint.proceed();

        System.out.println("环绕后");
        return obj;
    }
}
```
- 替换异常抛出通知<aop:after-throwing>
```
package aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Component("myAspectJAnnotation")
@Aspect
public class MyAspectJAnnotation {

    //切入点当前有效
    @Before("execution(* service.*.*(..))")
    public void before(JoinPoint joinPoint) {
        System.out.println("前置通知: " + joinPoint.getSignature().getName());
    }

    //声明公共的切入点
    @Pointcut("execution(* service.*.*(..))")
    private void myPointCut() {

    }

    @AfterReturning(value = "myPointCut()", returning = "ret")
    public void afterReturning(JoinPoint joinPoint, Object ret) {
        System.out.println("后置通知" + joinPoint.getSignature().getName() + ",--> " + ret);
    }

    /*环绕通知
        返回值：必须是Object
        参数：必须是ProceedingJoinPoint
        抛出异常：必须 throws Throwable
     */
    @Around("myPointCut()")
    public Object myAround(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("环绕前");
        //手动执行目标方法
        Object obj = joinPoint.proceed();

        System.out.println("环绕后");
        return obj;
    }

    @AfterThrowing(value = "execution(* service.*.*(..))",throwing = "e")
    public void afterThrowing(JoinPoint joinPoint, Throwable e) {
        System.out.println("抛出异常：" + e.getMessage());
    }
}
```
- 替换最终通知<aop:after>
```
package aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Component("myAspectJAnnotation")
@Aspect
public class MyAspectJAnnotation {

    //切入点当前有效
    @Before("execution(* service.*.*(..))")
    public void before(JoinPoint joinPoint) {
        System.out.println("前置通知: " + joinPoint.getSignature().getName());
    }

    //声明公共的切入点
    @Pointcut("execution(* service.*.*(..))")
    private void myPointCut() {

    }

    @AfterReturning(value = "myPointCut()", returning = "ret")
    public void afterReturning(JoinPoint joinPoint, Object ret) {
        System.out.println("后置通知" + joinPoint.getSignature().getName() + ",--> " + ret);
    }

    /*环绕通知
        返回值：必须是Object
        参数：必须是ProceedingJoinPoint
        抛出异常：必须 throws Throwable
     */
    @Around("myPointCut()")
    public Object myAround(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("环绕前");
        //手动执行目标方法
        Object obj = joinPoint.proceed();

        System.out.println("环绕后");
        return obj;
    }

    @AfterThrowing(value = "execution(* service.*.*(..))", throwing = "e")
    public void afterThrowing(JoinPoint joinPoint, Throwable e) {
        System.out.println("抛出异常：" + e.getMessage());
    }

    @After(value = "myPointCut()")
    public void after(JoinPoint joinPoint) {
        System.out.println("最终通知");
    }
}
```

#### aop注解总结
- 声明切面，修饰切面类，从而获得通知

@Aspect

- 通知
  - 前置 @Before 
  - 后置 @AfterReturning
  - 环绕 @Around
  - 异常抛出 @AfterThrowing
  - 最终 @After
- 切入点

@PointCut,修饰方法 private void xxx()

之后通过方法名获得切入点引用


## Jdbc Template
### 概述
- jdbc 提供的用于操作JDBC的工具类，类似于DBUtil
- 依赖连接池DataSource

### 环境搭建
#### 创建表
新建t_user表


#### 导入jar包
- 核心：4+1
- spring jdbc:spring-jdbc-3.2.0.RELEASE.jar
- 事务tx: spring-tx-3.2.0.RELEASE.jar
- 连接池c3p0:com.springsource.com.mchange.v2.c3p0-0.9.1.2.jar
- dbcp连接池:com.springsource.org.apache.commons.dbcp-1.2.2.osgi.jar
- dbcp的依赖:com.springsource.org.apache.commons.pool-1.5.3.jar
- mysql驱动:mysql-connector-java-5.1.28-bin.jar
```
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-jdbc</artifactId>
            <version>5.0.9.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-tx</artifactId>
            <version>5.0.9.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>c3p0</groupId>
            <artifactId>c3p0</artifactId>
            <version>0.9.1.2</version>
        </dependency>
        <dependency>
            <groupId>commons-dbcp</groupId>
            <artifactId>commons-dbcp</artifactId>
            <version>1.4</version>
        </dependency>
        <dependency>
            <groupId>commons-pool</groupId>
            <artifactId>commons-pool</artifactId>
            <version>1.6</version>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.12</version>
        </dependency>
```
#### javaBean
```
package entity;

public class User {

    private Integer id;
    private String username;
    private String password;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
```


### 使用API
```
package jdbc;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public class TestApi {

    public static void main(String[] args) {

        //1.创建数据源(连接池)dbcp
        BasicDataSource basicDataSource = new BasicDataSource();
        //基本4项
        basicDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        basicDataSource.setUrl("jdbc:mysql://localhost:3306/spring-class");
        basicDataSource.setUsername("root");
        basicDataSource.setPassword("123456");

        //2.创建模板
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(basicDataSource);

        //3.通过api操作
        jdbcTemplate.update("insert into t_user(username,password) values (?,?);","tom","123");

    }
}
```

### 配置DBCP
```
package dao;

import entity.User;
import org.springframework.jdbc.core.JdbcTemplate;

public class UserDao {

    //jdbc模板将由spring注入
    private JdbcTemplate jdbcTemplate;

    public void update(User user) {
        String sql = "update t_user set username=?,password=? where id=?";
        Object[] args = {user.getUsername(), user.getPassword(), user.getId()};
        jdbcTemplate.update(sql, args);
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
```
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">


    <!--配置数据源-->
    <bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource">
        <property name="driverClassName" value="com.mysql.jdbc.Driver"></property>
        <property name="url" value="jdbc:mysql://localhost/spring-class"></property>
        <property name="username" value="root"></property>
        <property name="password" value="123456"></property>
    </bean>

    <!--创建模板，注入数据源-->
    <bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
        <property name="dataSource" ref="dataSource"></property>
    </bean>

    <!--配置dao-->
    <bean id="userDao" class="dao.UserDao">
        <property name="jdbcTemplate" ref="jdbcTemplate"></property>
    </bean>
</beans>
```
```
package jdbc;

import dao.UserDao;
import entity.User;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class TestDBCP {

    @Test
    public void demo() {

        User user = new User();
        user.setId(1);
        user.setUsername("mary");
        user.setPassword("444");

        String xmlPath = "beansDBCP.xml";
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(xmlPath);

        UserDao userDao = (UserDao) applicationContext.getBean("userDao");
        userDao.update(user);
    }
}
```

### 配置C3P0
UserDao不变,Test类不变
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">


    <!--配置数据源C3P0-->
    <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
        <property name="driverClass" value="com.mysql.jdbc.Driver"></property>
        <property name="jdbcUrl" value="jdbc:mysql://localhost/spring-class"></property>
        <property name="user" value="root"></property>
        <property name="password" value="123456"></property>
    </bean>

    <!--创建模板，注入数据源-->
    <bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
        <property name="dataSource" ref="dataSource"></property>
    </bean>

    <!--配置dao-->
    <bean id="userDao" class="dao.UserDao">
        <property name="jdbcTemplate" ref="jdbcTemplate"></property>
    </bean>
</beans>
```


### 使用JdbcDaoSupport
```
package dao;

import entity.User;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.util.List;

public class UserDao extends JdbcDaoSupport {

    public void update(User user) {
        String sql = "update t_user set username=?,password=? where id=?";
        Object[] args = {user.getUsername(), user.getPassword(), user.getId()};
        this.getJdbcTemplate().update(sql, args);
    }

    public List<User> findAll() {
        return this.getJdbcTemplate().query("select * from t_user", new BeanPropertyRowMapper<User>().newInstance(User.class));
    }
}
```
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">


    <!--配置数据源C3P0-->
    <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
        <property name="driverClass" value="com.mysql.jdbc.Driver"></property>
        <property name="jdbcUrl" value="jdbc:mysql://localhost/spring-class"></property>
        <property name="user" value="root"></property>
        <property name="password" value="123456"></property>
    </bean>

    <!--配置dao
        dao继承JdbcDaoSupport,之后只需要注入数据源，底层将自动创建模板
    -->
    <bean id="userDao" class="dao.UserDao">
        <property name="dataSource" ref="dataSource"></property>
    </bean>
</beans>
```


### 配置properties
```
jdbc.driverClass=com.mysql.jdbc.Driver
jdbc.jdbcUrl=jdbc:mysql://localhost/spring-class
jdbc.user=root
jdbc.password=123456
```
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/context
       http://www.springframework.org/schema/context/spring-context.xsd">


    <!--加载配置文件
        classpath前缀表示src下
        在配置文件后通过${key}获得内容
    -->
    <context:property-placeholder location="classpath:properties.properties"></context:property-placeholder>

    <!--配置数据源C3P0-->
    <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
        <property name="driverClass" value="${jdbc.driverClass}"></property>
        <property name="jdbcUrl" value="${jdbc.jdbcUrl}"></property>
        <property name="user" value="${jdbc.user}"></property>
        <property name="password" value="${jdbc.password}"></property>
    </bean>

    <!--创建模板，注入数据源-->
    <bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
        <property name="dataSource" ref="dataSource"></property>
    </bean>

    <!--配置dao-->
    <bean id="userDao" class="dao.UserDao">
        <property name="jdbcTemplate" ref="jdbcTemplate"></property>
    </bean>
</beans>
```

## 事务管理

### 事务
- 事务

一组业务操作ABCD，要么全部成功，要么全部不成功
- 特点ACID
  - 原子性：整体
  - 一致性：完整
  - 隔离性：并发
  - 持久性：结果
- 隔离问题：
  - 脏读：一个事务读到另一事务没有提交的数据
  - 不可重复读：一个事务已经提交的数据(update)
  - 虚读(幻读)：一个事务已经提交的数据(insert)
- 隔离级别
  - read uncommited:读未提交
  - read commited：读已提交
  - repeatable read：可重复读
  - serializable：串行化
- mysql事务操作--简单

ABCD是一个事务
```
Connection conn=null;
try{
    //1.获得连接
    conn=...;
    //2.开启事务
    conn.setAutoCommit(false);
    A;
    B;
    C;
    D;
    //3.提交事务
    conn.commit();
}catch(){
    //4.回滚事务
    conn.rollback();
}
```

- mysql事务操作--SavePoint

AB(必须) CD(可选) 

例如发工资，A是公司扣钱，B表示我加钱，然后工资短信要收费的，C是发短信，D是扣我的短信费。CD成不成功，对我AB没影响。

```
Connection conn=null;
//保存点，记录操作的当前位置，之后可以回滚到指定的位置(可以回滚一部分)
SavePoint savePoint=null；
try{
    //1.获得连接
    conn=...;
    //2.开启事务
    conn.setAutoCommit(false);
    A;
    B;
    //savePoint为null表示AB异常，不为null表示CD异常
    savePoint=conn.setSavePoint();
    C;
    D;
    //3.提交事务
    conn.commit();
}catch(){
    if(savePoint!=null){//CD异常
        //回滚到CD之前
        conn.rollback(savePoint);
        //提交AB
        conn.commit();
    }else{
        //回滚AB
        conn.rollback();
    }
}
```

### 事务管理介绍

#### 三个顶级接口

- PlatformTransactionManager.class(平台事务管理器)

spring管理事务必须使用事务管理器

进行事务配置时，必须配置事务管理器
- TransactionDefinition.class(事务详情、事务属性、事务定义)

spring用于确定事务具体详情的，例如隔离级别、是否只读、超时时间

进行事务配置时，必须配置详情，spring将配置项封装到该对象实例
- TransactionStatus.class(事务状态)

spring用于记录当前事务运行状态，例如是否有保存点、事务是否完成

spring底层根据状态进行相应操作，和我们没关系

#### PlatformTransactionManager
- 导入jar包,需要平台事务管理器的实现类
```
spring-jdbc-3.2.0.RELEASE.jar//jdbc
spring-orm-3.2.0.RELEASE.jar//整合hibernate
spring-tx-3.2.0.RELEASE.jar
```
```
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-jdbc</artifactId>
    <version>5.1.1.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-orm</artifactId>
    <version>5.1.1.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-tx</artifactId>
    <version>5.1.1.RELEASE</version>
</dependency>
```
- 常见的事务管理器
  - jdbc开发时：DataSourceTransactionManager
  - hibernate开发时:HibernateTransactionManager
- API详解
```
//事务管理器通过事务详情，获得事务状态，从而去管理事务
TransactionStatus getTransaction(TransactionDefinition definition)

//根据状态提交
void commit(TransactionStatus status)

//根据状态回滚
void rollback(TransactionStatus status)
```
#### TransactionStatus
```
//是否是新事务
isNewTransaction():boolean

//是否有保存点
hasSavePoint():boolean

//设置回滚
setRollbackOnly():void

//是否回滚过了
isRollbackOnly():boolean

//刷新
flush():void

//是否完成
isCompleted():boolean
```
#### TransactionDefinition
方法
- getName():String

配置事务详情时的名称，一般是方法的名称。例如save、add* 等

- isReadOnly():boolean

是否只读，增删改(读写)，查询(读)

- getTimeout():int

获得超时时间

- getIsolationLevel():int

获得隔离级别

- getPropagationBehavior():int

传播行为

static final字段

- TIMEOUT_DEFAULT

默认超时时间，默认值是-1，表示使用数据库底层的超时时间
- 隔离级别取值
  - ISOLATION_DEFAULT：0
  - ISOLATION_READ_UNCOMMITED：1
  - ISOLATION_READ_COMMITED：2
  - ISOLATION_REPEATABLE_READ：4
  - ISOLATION_SERIALIZABLE：8
- 传播行为：在两个业务之间如何共享事务
  - ==PROPAGATION_REQUIRED==(必须)：支持当前事务，A如果有事务，B将使用该事务。A如果没有事务，B将创建一个新的事务。是默认值
  - PROPAGATION_SUPPORTS(支持)：支持当前事务，A如果有事务，B将使用该事务。A如果没有事务，B将以非事务执行
  - PROPAGATION_MANDATORY(强制)：支持当前事务，A如果有事务，B将使用该事务。A如果没事务，B将抛异常
  - ==PROPAGATION_REQUIRED_NEW==(必须新的)：如果A有事务，将A的事务挂起，B创建一个新的事务。如果A没有事务，B将创建一个新的事务
  - PROPAGATION_NOT_SUPPORTED(不支持)：如果A有事务，将A的事务挂起，B将以非事务执行。如果A没有事务，B将以非事务执行
  - PROPAGATION_NEVER(不)：不支持当前事务，如果A有事务，B将抛异常。如果A没有事务，B将以非事务执行
  - ==PROPAGATION_NESTED==(嵌套)：A和B底层采用保存点机制，形成嵌套事务

### 转账
#### 搭建环境
- 建数据库
```
create table account(
    id int primary key,auto_increment,
    username varchar(50),
    money int
)
```
#### 导入jar包
- 核心：4+1
- AOP：4个(aop联盟、spring aop、aspectJ规范、spring aspect)
- 数据库：2个(jdbc+tx)
- 数据库驱动：MySQL
- 连接池:c3p0

#### 环境搭建
1. service
- transfer(inner,outer,money)
2. dao extends JdbcDaoSupport
- in(inner,money)
- out(outer,money)

```
package dao;

public interface AccountDao {

    public void out(String outer,Integer money);

    public void in(String inner,Integer money);
}
```
```
package dao;

import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class AccountDaoImpl extends JdbcDaoSupport implements AccountDao {

    public void out(String outer, Integer money) {
        this.getJdbcTemplate().update("update account set money=money-? where username=?", money, outer);
    }

    public void in(String inner, Integer money) {
        this.getJdbcTemplate().update("update account set money=money+? where username=?", money, inner);
    }
}
```
```
package service;

public interface AccountService {

    public void transfer(String outer, String inner, Integer money);
}
```
```
package service;

import dao.AccountDao;

public class AccountServiceImpl implements AccountService {

    private AccountDao accountDao;

    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public AccountDao getAccountDao() {
        return accountDao;
    }

    public void transfer(String outer, String inner, Integer money) {
        accountDao.in(inner, money);
        //断点,会只执行in,而不执行out
        int i = 1 / 0;
        accountDao.out(outer, money);
    }
}
```
```
//applicationContext.xml

<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">


    <!--dataSource-->
    <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
        <property name="driverClass" value="com.mysql.jdbc.Driver"></property>
        <property name="jdbcUrl" value="jdbc:mysql://localhost:3306/spring-class"></property>
        <property name="user" value="root"></property>
        <property name="password" value="123456"></property>
    </bean>

    <bean id="accountDao" class="dao.AccountDaoImpl">
        <property name="dataSource" ref="dataSource"></property>
    </bean>


    <bean id="accountService" class="service.AccountServiceImpl">
        <property name="accountDao" ref="accountDao"></property>
    </bean>
</beans>
```
```
package jdbc;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import service.AccountService;

public class TestAccount {

    @Test
    public void demo() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");

        AccountService accountService = (AccountService) applicationContext.getBean("accountService");
        accountService.transfer("jack", "mary", 100);
    }
}
```
#### 手动管理事务
- spring底层使用TransactionTemplate事务模板进行操作
- 操作:
  - service：需要获得TransactionTemplate
  - spring：配置模板，并注入给service
  - 模板需要注入事务管理器
  - 配置事务管理器：DataSourceTransactionManager,需要注入DataSource
 - 修改service
 ```
 package service;

import dao.AccountDao;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

public class AccountServiceImpl implements AccountService {

    private AccountDao accountDao;

    //需要spring注入模板
    private TransactionTemplate transactionTemplate;

    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public AccountDao getAccountDao() {
        return accountDao;
    }

    public void transfer(final String outer, final String inner, final Integer money) {

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                accountDao.in(inner, money);
                //断点
                int i = 1 / 0;
                accountDao.out(outer, money);
            }
        });

    }
}
```
- 修改配置文件
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!--1. dataSource-->
    <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
        <property name="driverClass" value="com.mysql.jdbc.Driver"></property>
        <property name="jdbcUrl" value="jdbc:mysql://localhost:3306/spring-class"></property>
        <property name="user" value="root"></property>
        <property name="password" value="123456"></property>
    </bean>

    <!--2.dao-->
    <bean id="accountDao" class="dao.AccountDaoImpl">
        <property name="dataSource" ref="dataSource"></property>
    </bean>

    <!--3.service-->
    <bean id="accountService" class="service.AccountServiceImpl">
        <property name="accountDao" ref="accountDao"></property>
        <property name="transactionTemplate" ref="transactionTemplate"></property>
    </bean>

    <!--4.创建模板-->
    <bean id="transactionTemplate" class="org.springframework.transaction.support.TransactionTemplate">
        <property name="transactionManager" ref="txManager"></property>
    </bean>

    <!--5.配置事务管理器,管理器需要事务，事务从Connection获得，连接从连接池DataSource获得-->
    <bean id="txManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="dataSource"></property>
    </bean>
</beans> 
```
现在in和out都不会执行了

#### 工厂bean生成代理：半自动

spring提供了一个管理事务的代理工厂bean:TransactionProxyFactoryBean。首先这是一个工厂bean,用于创建一个代理对象，代理对象做的是管理事务

- getBean()获得代理对象
- spring配置一个代理

```
package service;

import dao.AccountDao;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

public class AccountServiceImpl implements AccountService {

    private AccountDao accountDao;
    
    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public AccountDao getAccountDao() {
        return accountDao;
    }

    public void transfer(final String outer, final String inner, final Integer money) {
        accountDao.in(inner, money);
        //断点
        int i = 1 / 0;
        accountDao.out(outer, money);
    }
}
```
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!--1. dataSource-->
    <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
        <property name="driverClass" value="com.mysql.jdbc.Driver"></property>
        <property name="jdbcUrl" value="jdbc:mysql://localhost:3306/spring-class"></property>
        <property name="user" value="root"></property>
        <property name="password" value="123456"></property>
    </bean>

    <!--2.dao-->
    <bean id="accountDao" class="dao.AccountDaoImpl">
        <property name="dataSource" ref="dataSource"></property>
    </bean>

    <!--3.service-->
    <bean id="accountService" class="service.AccountServiceImpl">
        <property name="accountDao" ref="accountDao"></property>
    </bean>

    <!--4.service代理对象
        4.1 proxyInterfaces 接口
        4.2 target 目标类
        4.3 transactionManager 事务管理器
        4.4 transactionAttributes 事务属性(事务详情、事务定义)
                prop.key 确定哪些方法使用当前事务配置
                prop.text 用于配置事务详情
                    格式：PROPAGATION,ISOLATION,readOnly，-Exception,+Exception
                             传播行为  隔离级别   是否只读  出现异常要回滚  出现异常要提交
                    例如：<prop key="transfer">PROPAGATION_REQUIRED,ISOLATION_DEFAULT</prop>默认的传播行为和隔离级别
                         <prop key="transfer">PROPAGATION_REQUIRED,ISOLATION_DEFAULT,readOnly</prop>只读
                         <prop key="transfer">PROPAGATION_REQUIRED,ISOLATION_DEFAULT,+ArithmeticException</prop>有异常仍提交
    -->
    <bean id="proxyAccountService" class="org.springframework.transaction.interceptor.TransactionProxyFactoryBean">
        <property name="proxyInterfaces" value="service.AccountService"></property>
        <property name="target" ref="accountService"></property>
        <property name="transactionManager" ref="txManager"></property>
        <property name="transactionAttributes">
            <props>
                <prop key="transfer">PROPAGATION_REQUIRED,ISOLATION_DEFAULT,+ArithmeticException</prop>
            </props>
        </property>
    </bean>

    <!--5.事务管理器-->
    <bean id="txManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="dataSource"></property>
    </bean>
</beans>
```
```
package jdbc;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import service.AccountService;

public class TestAccount {

    @Test
    public void demo() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");

        AccountService accountService = (AccountService) applicationContext.getBean("proxyAccountService");
        accountService.transfer("jack", "mary", 100);
    }
}
```
#### AOP配置基于XML
在spring xml配置aop自动生成代理，进行事务的管理

- 配置管理器
- 配置事务详情
- 配置aop

导入命名空间
```
xmlns:tx="http://www.springframework.org/schema/tx"

http://www.springframework.org/schema/tx
http://www.springframework.org/schema/tx/spring-tx.xsd
```
修改applicationContext.xml
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/aop
        http://www.springframework.org/schema/aop/spring-aop.xsd
        http://www.springframework.org/schema/tx
        http://www.springframework.org/schema/tx/spring-tx.xsd">

    <!--1. dataSource-->
    <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
        <property name="driverClass" value="com.mysql.jdbc.Driver"></property>
        <property name="jdbcUrl" value="jdbc:mysql://localhost:3306/spring-class"></property>
        <property name="user" value="root"></property>
        <property name="password" value="123456"></property>
    </bean>

    <!--2.dao-->
    <bean id="accountDao" class="dao.AccountDaoImpl">
        <property name="dataSource" ref="dataSource"></property>
    </bean>

    <!--3.service-->
    <bean id="accountService" class="service.AccountServiceImpl">
        <property name="accountDao" ref="accountDao"></property>
    </bean>

    <!--4.配置事务管理-->
    <!--4.1 事务管理器-->
    <bean id="txManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="dataSource"></property>
    </bean>
    <!--4.2 事务详情(事务通知),在aop筛选的基础上，对ABC三个方法确定使用什么样的事务。例如AC读写，B只读
        <tx:attributes>:配置事务详情
            <tx:method name="transfer"/>:详情具体配置
                propagation:传播行为 （REQUIRED必须的，REQUIRES_NEW必须是新的）
                isolation：隔离级别 DEFAULT
    -->
    <tx:advice id="txAdvice" transaction-manager="txManager">
        <tx:attributes>
            <tx:method name="transfer" propagation="REQUIRED" isolation="DEFAULT"/>
        </tx:attributes>
    </tx:advice>

    <!--4.3 AOP编程，目标类有ABCD四个方法，相当于4个连接点，切入点表达式确定需要增强的连接点，从而获得切入点ABC-->
    <aop:config>
        <aop:advisor advice-ref="txAdvice" pointcut="execution(* service.*.*(..))"></aop:advisor>
    </aop:config>

</beans>
```

#### AOP配置基于注解
- 配置事务管理器，并将事务管理器交给spring
- 在目标类或者目标方法上添加注解即可@Transaction
- 事务详情配置：@Transactional()中
  - isolation=Isolation.DEFAULT
  - noRollbackFor
  - noRollbackForClassName
  - propogation=Propagation.REQUIRED
  - readOnly
  - rollbackFor
  - RollbackForClassName
  - timeout
  - value


修改applicationContext.xml
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/aop
        http://www.springframework.org/schema/aop/spring-aop.xsd
        http://www.springframework.org/schema/tx
        http://www.springframework.org/schema/tx/spring-tx.xsd">

    <!--1. dataSource-->
    <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
        <property name="driverClass" value="com.mysql.jdbc.Driver"></property>
        <property name="jdbcUrl" value="jdbc:mysql://localhost:3306/spring-class"></property>
        <property name="user" value="root"></property>
        <property name="password" value="123456"></property>
    </bean>

    <!--2.dao-->
    <bean id="accountDao" class="dao.AccountDaoImpl">
        <property name="dataSource" ref="dataSource"></property>
    </bean>

    <!--3.service-->
    <bean id="accountService" class="service.AccountServiceImpl">
        <property name="accountDao" ref="accountDao"></property>
    </bean>

    <!--4.事务管理-->
    <!--4.1 事务管理器-->
    <bean id="txManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="dataSource"></property>
    </bean>
    <!--4.2 将事务管理器交给spring
        transaction-manager:配置事务管理器
        proxy-target-class
            true:底层强制使用cglib代理
    -->
    <tx:annotation-driven transaction-manager="txManager" proxy-target-class="false"></tx:annotation-driven>
</beans>
```
修改service
```
package service;

import dao.AccountDao;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@Transactional
public class AccountServiceImpl implements AccountService {

    private AccountDao accountDao;
    
    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public AccountDao getAccountDao() {
        return accountDao;
    }

    public void transfer(final String outer, final String inner, final Integer money) {

        accountDao.in(inner, money);
        //断点
        //int i = 1 / 0;
        accountDao.out(outer, money);
    }

}
```
## 整合junit
- 导入jar包
  - 4+1
  - spring-test..jar
```
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-test</artifactId>
    <version>5.1.0.RELEASE</version>
</dependency>
```
- 让junit通知spring去加载配置文件
- 让spring容器自动进行注入

```
package jdbc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import service.AccountService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class TestAccount {
    
    @Autowired  //与junit整合，不需要在spring xml中配置扫描
    private AccountService accountService;

    @Test
    public void demo() {
        accountService.transfer("jack", "mary", 100);
    }
}
```

## 整合web
- 导入jar包
```
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-web</artifactId>
    <version>5.1.0.RELEASE</version>
</dependency>
```

1. tomcat 启动加载配置文件
  - servlet：init(ServletConfig)-- <load-on-startup>
  - filter:init(FilterConfig)--web.xml注册过滤器自动调用初始化
  - listener：ServletContextListener--servletContext对象监听
  - spring提供了一个监听器:ContextLoaderListener--在web.xml中配置<listener><listenter-class>...
  - 如果只配置监听器，默认加载xml位置：/WEB-INF/applicationContext.xml
2. 确定配置文件的位置，通过系统初始化参数
系统初始化参数就是ServletContext初始化参数，在web.xml中配置
```
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
         version="4.0">

    <!--配置spring监听器，用来加载xml配置文件-->
    <listener>
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>
    
    <!--确定配置文件位置-->
    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>classpath:applicationContext.xml</param-value>
    </context-param>
</web-app>
```
3. 从servletContext作用域获得spring容器     
```
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import service.AccountService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //从application作用域(ServletContext)获得spring容器
        //方式1,手动从作用域获得
        ApplicationContext applicationContext= (ApplicationContext) this.getServletContext().getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);

        //方式2，通过工具获取
        //ApplicationContext applicationContext=WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());

        //操作
        AccountService accountService= (AccountService) applicationContext.getBean("accountService");
        accountService.transfer("jack","mary",1000);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}
```

## 案例介绍

<br>
<br>
<br>



# spring boot
## 微服务
一个项目可以由多个小型服务构成，这些小服务就叫微服务

- spring boot:快速开发微服务模块
- spring cloud:连接各个微服务
- spring cloud data flow:个微服务通信的并行计算


## spring boot
### 好处
- 简化J2EE开发
- 整个spring技术栈的整合(spring mvc、spring)
- 整个J2EE技术的整合(mybatis、redis)
- 内置类Tomcat，不需要打包成war包，可以直接执行
- 将各个应用/第三方框架设置成一个个starter(场景)，以后要用就引入这个场景，就会将该场景需要的所有依赖注入

### 目录结构
- resources
  - static:放静态资源(js、css、图片、音频)
  - templates:放模板文件(freemarker、thymeleaf)
  - application.properties:放spring boot的配置文件
```
//对端口号进行配置
server.port=8888
```

### 入口程序@SpringBootApplication
```
//spring boot主配置类
@SpringBootApplication
//spring的配置文件默认会被spring boot自动配置好。如果要自己编写spring配置文件，需要@ImportResource去识别，否则是不被识别的
@ImportResource(locations= {"classpath:spring.xml"})
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}
```

该注解包含两个注解
1. @SpringBootConfiguration

包含@Configuration表示该类是一个配置类,加了该注解的类会自动纳入spring容器

2. @EnableAutoConfiguration
- 使spring boot自动配置，不需要写配置文件
- 包含AutoConfigurationPackage，可以找到@SpringBootConfiguration主配置类所在的包，就会将该包及所有子包全部纳入spring容器
- 包含Import(AutoConfigurationImportSelector.class)将第三方依赖(jar包、配置文件)加到spring容器。具体是spring boot在启动时，会根据META-INF/spring.factories(该文件在每个jar下面)找到相应的第三方依赖，并将这些依赖引入本项目
- 如何自动装配

以org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration为例

该类中包含以下内容
```
@Configuration
@EnableConfigurationProperties(HttpProperties.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(CharacterEncodingFilter.class)
@ConditionalOnProperty(prefix = "spring.http.encoding", value = "enabled", matchIfMissing = true)
public class HttpEncodingAutoConfiguration {
}
```

1)@Configuration:表示此类是一个配置类，将该类纳入spring ioc容器

2)@EnableConfigurationProperties(HttpProperties.class)：通过HttpProperties将默认编码设置成UTF8

3)修改默认编码：在application.properties中添加spring.http.charset=XXX(spring.http是HttpProperties的前缀，charset是他的一个属性)

4)什么时装配行HttpEncodingAutoConfiguration
```
//当满足下列条件的时候
//1.当是一个web应用
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
//如果项目里面存在这个class
@ConditionalOnClass(CharacterEncodingFilter.class)
//当属性满足要求是时此条件成立。此例是如果spring.http.encoding.enabled=XXX这个条件没有配，那么此条件成立
@ConditionalOnProperty(prefix = "spring.http.encoding", value = "enabled", matchIfMissing = true)
```
如果三个条件都成立，通过以下代码将这个配置放到IOC容器中
```
	@Bean
	@ConditionalOnMissingBean
	public CharacterEncodingFilter characterEncodingFilter() {
	    //过滤器对象
		CharacterEncodingFilter filter = new OrderedCharacterEncodingFilter();
		filter.setEncoding(this.properties.getCharset().name());
		//强制将request和response编码设置成UTF8
		filter.setForceRequestEncoding(this.properties.shouldForce(Type.REQUEST));
		filter.setForceResponseEncoding(this.properties.shouldForce(Type.RESPONSE));
		return filter;
	}
```
- 如何知道spring boot开启/禁止了哪些自动装配

在application.properties中加debug=true。启动项目就会有很多匹配(positive matches)、不匹配(negative matches)的信息

### application配置文件
举例：Student
```
@Component
//批量注入，配合application配置文件
@ConfigurationProperties(prefix="student")
//开启JSR303数据校验的注解，@ConfigurationPropertie支持，@Value不支持
@Validated
//spring boot会默认加载application.properties和application.yml
//但如果是其他的文件，要通过@PropertySource加载，并且只对properties有效，对yml无效
@PropertySource(value= {"classpath:conf.properties"})
public class Student {

    //通过@Value一个个赋值
    //支持SpEL @Value("${student.uname}")
	@Value("cppppp")
	private String name;
	
	@Value("44")
	private int age;
	
	private boolean sex;
	private Date birthday;
	
	//@Value不支持复杂类型
	//@Value("{province: 陕西,city: 西安,zone: 莲湖区}")不可以
	private Map<String, Object> location;
	private Pet pet;
	private String[]hobbies;
	private List<String>skills;
	
	@Email
	private String email;
}
```
```
public class Pet {
    //松散写法:nick-name
    //@ConfigurationProperties支持松散写法，@Value不支持
	private String nickName;
	private String strain;
}
```
#### application.properties配置文件(默认)

写法：key-value对,例如server.port=8888
```
student.name=pp
student.age=23
```

#### application.yml

yml不是一个标记文档(xml是一个标记文档，例如<server><port>8882</port></server>)。他的写法是
```
//key后的冒号必须有空格,通过垂直对其指定层次关系
//默认不加引号，但是""会将其中的转义符进行转义
server:
    port: 8882
    path:/a/b/c

student: 
  name: 张三
  age: 23
  sex: true
  birthday: 2018/02/12
//map
  location: {province: 陕西,city: 西安,zone: 莲湖区}
#  location：
#      province: 陕西
#      city: 西安
#      zone: 莲湖区

//集合
  hobbies: 
     - 足球
     - 篮球
# hobbies: [足球,篮球]

//数组
  skills: 
     - 编程
     - 逛街

//对象
  pet: 
    nickName: mm
    strain: 狗
#   pet: {nick-n ame:rr,strain:hh}
```
#### 占位符表达式

1. 随机数
```
//随机字符串
student.name=${random.value}
//随机整型数
student.age=${random.int}

/*
随机长整型数：${random.long}
10以内的整型数：${random.int(10)}
指定随机数范围：${random.int[1024,65536]}
*/
```
2. 引用变量值

application.properties
```
student.user.name=pp
```
application.yml
```
student
    //如果student.user.name不存在，那么就用默认值：无名
    name: ${student.user.name:无名}
```
#### 配置文件位置
1. 项目内部的配置文件
- properties和yml中的配置相互补充，如果有重复配置，那么properties优先
- spring boot默认读取application.properties和application.yml，这两个配置文件可以存在与以下四个位置
```
//按优先级从高到低
file:项目根目录/config
file:项目根目录
classpath:项目根目录/config
classpath:项目根目录
```
- 配置项目名称
```
server.servlet.context-path=/boot
```
在请求时的路径：localhost:8088/boot/helloWorld
2. 项目外部的配置文件(大量参数)

在spring boot主配置类的run configuratio--Argument--program Argument中配置
```
--spring.config.location=D:/application.properties
```
若同一个配置外部内部同时存在，外部覆盖内部

有时候项目已经打包了，但是又需要在运行的时候修改某些配置，那么就可以通过命令行在启动jar包的时候引用外部配置文件
```
java -jar HelloWorld-0.0.1-SNAPSHOT.jar --spring.config.location=D:/application.properties
```
3. 项目运行参数(只有一个参数)

在spring boot主配置类的run configuratio--Argument--program Argument中配置
```
--server.port=8883 --server.context-path=D:/
```
或者通过命令行
```
java -jar HelloWorld-0.0.1-SNAPSHOT.jar --server.port=8883
```

多个地方配置同一个参数时，命令参数(调用外部配置文件>运行参数)>内部
### 用注解方式写配置文件

spring boot不推荐自己写xml配置文件，推荐使用注解
```
//配置类
@Configuration
public class AppConfig {

	@Bean
	public StudentService studentService() {
		return new StudentService();
	}
}
```

### 多环境切换(profile)
#### 通过properties文件

spring boot默认会读取application.properties,如果有多个命名方式为：application-环境名.properties

假设有两个环境
- 开发环境：application-dev.properties
- 测试环境：application-test.properties

如果要选择某个具体的环境，那么在在application.properties中如果写上以下，就会加载dev这个配置文件中的配置
```
spring.profiles.active=dev
```
#### 通过yml
```
//主环境
server:
  port: 8883
spring:
  profiles:
    //指定本次使用的环境
    active:dev
---
server:
  port: 8884
spring:
  profiles: dev
---
server:
  port: 8884
spring:
  profiles: test
```
#### 动态切换环境
1. 通过编译器

在spring boot主配置类的run configuratio--Argument--program Argument中配置
```
--spring.profiles.active=环境名
```
2. 通过命令行

打包后生成HelloWorld-0.0.1-SNAPSHOT.jar包，再执行
```
java -jar HelloWorld-0.0.1-SNAPSHOT.jar --spring.profiles.active=环境名
```
3. 通过VM参数指定

在spring boot主配置类的run configuratio--Argument--VM Argument中配置
```
-Dspring.profiles.active=环境名
```
### spring boot对日志的支持
spring boot默认选用slf4j和logback两个日志框架，并且默认帮我们配置好了日志，我们直接使用即可
#### 日志级别
TRACE<DEBUG<INFO<WARN<ERROR<FATAL<OFF

默认是INFO级别，高于该级别的打印，低于该级别的不打印
```
@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {
	 
	 Logger log = LoggerFactory.getLogger(DemoApplicationTests.class);
	 
	@Test
	public void testLog() {
		log.trace("trace***");
		log.debug("debug****");
		log.info("info****");
		log.warn("warn***");
		log.error("error****");
	}
	/*
	info**** 
	warn***
    error****
	*/
}
```
手动配置级别，在application.properties中
```
//com.example.demo是主配置类所在的包
logging.level.com.example.demo=warn
/*现在只打印
    warn***
    error****
*/
```



#### 把日志输出外部
- 输出到文件
```
//会在相对于项目根目录下自动创建一个springboot.log文件
logging.file=springboot.log
```
- 输出到文件夹
```
//默认的文件名是spring.log
logging.path=XXX
```

#### 指定日志显示格式
- 输出到控制台
```
/*
%d:日期时间，并且格式为{yyyy-MM-dd}
%thread：当前线程名
%-5level：显示日志级别，-5表示从左显示5个字符宽度
%logger{50}：设置日志长度，超过50就把某些单词只显示首字母
%msg：日志消息
%n：回车
*/
logging.pattern.console=%d{yyyy-MM-dd} [%thread] %-5level %logger{50} - %msg%n
```
- 输出到文件
```
logging.pattern.file=%d{yyyy-MM-dd} [%thread] %-5level %logger{50} %msg%n
```





### spring boot开发web项目
#### 静态资源存放路径
- spring boot是一个jar，因此静态资源不是放到webapps中
- 静态资源的存放路径通过WebMvcAutoConfiguration类的addResourceHandlers()方法指定(在/webjars中)
- 自己写的静态资源
  - 将自己写的静态资源达成jar包
  - spring自动扫描的方式：spring boot将一些目录结构设置成存放静态资源的目录。在ResourceProperties中的
  ```
  //访问页面时，不需要写前缀
  	private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
			"classpath:/META-INF/resources/", "classpath:/resources/",
			"classpath:/static/", "classpath:/public/" };
  ```
#### 设置欢迎界面
在WebMvcAutoConfiguration的welcomePageHandlerMapping()方法中调用的getWelcomePage()设置
```
private Resource getIndexHtml(String location) {
    //任何一个静态资源目录下的index.html文件就是欢迎页
	return this.resourceLoader.getResource(location + "index.html");
}
```
#### 自定义logo
网站中网页标签的Logo是固定名字：favicon.io

在WebMvcAutoConfiguration中设置
```
@Bean
public SimpleUrlHandlerMapping faviconHandlerMapping() {
	SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
	
	mapping.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
	
	//任一静态资源目录下的favicon.ico都可以
	mapping.setUrlMap(Collections.singletonMap("**/favicon.ico",
	
	faviconRequestHandler()));
	return mapping;
}
```
#### 自定义静态资源位置
自定义静态资源位置之后，之前默认的位置都失效了
```
spring.resources.static-locations=classpath:/res/,classpath:/img/
```


#### webjars(Web Library in Jars)
比如之前我们要用到jquery，我们就把他的js文件引进来，现在spring boot把他们都变成jar包了


```
//访问：localhost:8080/webjars/jquery/3.3.1-1/jquery.js
<dependency>
	<groupId>org.webjars</groupId>
	<artifactId>jquery</artifactId>
	<version>3.3.1-1</version>
</dependency>
```

#### 动态资源

推荐使用模板引擎：thymeleaf

模板引擎将一个页面分成两部分
- 模板
```
<div>
    <h1>${user}</h1>
</div>
```
- 数据
```
model.setAttribute("user","pp");
```
模板引擎会将模板和数据结合，变成
```
<div>
    <h1>pp</h1>
</div>
```

##### 添加thymeleaf依赖
```
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter</artifactId>
</dependency>
<dependency>
	<groupId>org.thymeleaf</groupId>
	<artifactId>thymeleaf-spring5</artifactId>
</dependency>
<dependency>
	<groupId>org.thymeleaf.extras</groupId>
	<artifactId>thymeleaf-extras-java8time</artifactId>
</dependency>
```

##### 使用thymeleaf
- ThymeleafAutoConfiguration

通过ThymeleafAutoConfiguration，默认配置是
```
//使用thymeleaf只需要将文件放入目录：classpath:/templates/,并且文件的后缀是.html
public static final String DEFAULT_PREFIX = "classpath:/templates/";

public static final String DEFAULT_SUFFIX = ".html";
```
- 举例

/templates/result.html
```
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
    <!--th就是替换HTML原有的值先从${welcome}中取值，如果有就替换原有的值
    th的用法是:th:html属性名=值
    -->
	<p id="pid" class="pClass" th:id="${welcome}" th:class="${welcome}" th:text="${welcome}">welcome to our grocery store</p>
</body>
</html>
```
可以看到result.html的源码是
```
<body>
	<p id="welcome thymeleaf" class="welcome thymeleaf">welcome thymeleaf</p>
</body>
```

controller
```
package com.example.demo.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class BootController {

	@RequestMapping("/welcome")
	public String welcome(Map<String,Object>map) {
		//给thymeleaf准备数据
		map.put("welcome","welcome thymeleaf");
		return "result";
	}
}
```
测试
```
//结果是welcome thymeleaf
http://localhost:8080/welcome
```

##### thymeleaf部分用法
1. th:text VS th:utext

两个都是获取文本值，但是：

- th:text转义

将```<h1>hello</h1>```放大显示
- th:utext不转义

将```<h1>hello</h1>```原样文本显示

2. 除了$以外的其他符号
3. th:each

Product
```
public class Product {
	
	private String name;
	private double price;
	private int inStock;
}
```

/templates/result.html
```
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<table>
		<tr>
			<th>NAME</th>
			<th>PRICE</th>
			<th>IN STOCK</th>
		</tr>
		<tr th:each="prod:${prods}">
			<td th:text="${prod.name}">onions</td>
			<td th:text="${prod.price}">2.14</td>
			<td th:text="${prod.inStock}?#{true}:#{false}">yes</td>
		</tr>
	</table>
</body>
</html>
```
cnotroller
```
package com.example.demo.controller;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.entity.Product;

@Controller
public class BootController {
	
	@RequestMapping("/welcome")
	public String welcome(Map<String,Object>map) {
		
		List<Product>products=new ArrayList<Product>();
		products.add(new Product("a",100,10));
		products.add(new Product("b",200,20));
		products.add(new Product("c",300,30));
		
		map.put("prods", products);
		return "result";
	}
}
```
测试
```
http://localhost:8080/welcome
/*
NAME	PRICE	IN STOCK
a	100.0	10
b	200.0	20
c	300.0	30
*/
```

### 整合JSP
之前spring boot 默认自带一个内置的Tomcat，所以不需要打war包，直接通过jar包即可运行。spring boot默认是不支持jsp的，如果要整合jsp,就必须配置一个外置的Tomcat，就需要打war包。

1. 创建项目

在创建项目时Packaging要选择war

创建好后会发现pom.xml中有一个
```
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-tomcat</artifactId>
	<!--这是一个内置的Tomcat，provided表明之后打包不需要打包这个内置的Tomcat-->
	<scope>provided</scope>
</dependency>
```

2. 建立基本的web项目所需要的目录结构
- webapps/WEB-INF
- webapps/WEB-INF/web.xml(spring boot帮我们配置了)
- webapps/index.jsp

3. 创建tomcat实例并且部署项目
```
//外置Tomcat要输项目名
http://localhost:8080/springboot/index.jsp
```
4. 项目具体内容

controller
```
package com.example.demo.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {

	@RequestMapping("/welcome")
	public String welcome(Map<String, Object>map) {
		map.put("name", "pp");
		return "index";//前缀+index+后缀 
	}
}
```
index.jsp
```
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
 ${requestScope.name }
</body>
</html>
```
application.properties
```
spring.mvc.view.prefix=/
spring.mvc.view.suffix=.jsp
```

访问
```
http://localhost:8080/springboot/welcome
```



#### ServletInitializer
如果是一个war包的spring boot项目，在启动Tomcat时，会自动调用ServletInitializer中的configure方法。这个方法会调用spring boot的主配置类，从而启动spring boot。即在启动Tomcat服务器时，会先启动Tomcat，然后再去启动spring boot
```
package com.example.demo;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(SpringBootWebJspApplication.class);
	}
}
```





# spring mvc
## MVC在b/s下
1. 用户发出request请求到controller
2. 控制器Controller接受用户请求,请求模型进行处理
3. 模型Model(pojo、action、service、dao)处理结果并返回给controller
4. controller将处理结果交给视图View进行视图渲染，将模型数据填充到request域
5. controller给用户返回response响应

## spring mvc框架
1. 用户发出request请求(URL)到前端控制器DispatcherServlet
2. DispatcherServlet向处理器映射器HandlerMapping请求查找handler
3. HandlerMapping根据xml配置、注解返回一个执行链HandlerExecutionChain(包括Interceptor、Handler)
4. DispatcherServlet向处理器适配器HandlerAdapter请求执行处理器Handler(平常叫做controller)
5. HandlerAdapter执行controller
6. controller返回ModelAndView
7. HandlerAdapter向DispatcherServlet返回ModelAndView
8. DispatcherServlet向视图解析器View Resolver请求解析视图
9. View Resolver根据逻辑视图名给DispatcherServlet返回物理视图View
10. DispatcherServlet进行视图渲染，将模型数据填充到request域
11. DispatcherServlet向用户响应结果


## 组件
- 前端控制器DispatcherServlet

接受请求、响应结果(相当于转发器),他降低了其他组件之间的耦合度

- 处理器映射器HandlerMapping

根据请求的URL查找Handler

- 处理器适配器HandlerAdapter

按照特定的规则执行Handler。注意编写Handeler时要按照HandlerAdapter的要求去做，这样HandlerAdapter才能正确执行Handler

- 视图视图解析器View Resolver

进行视图解析，根据逻辑视图名解析成真正的视图

- 视图View

是一个接口，他的实现类支持不同的View类型(jsp、freemarker、PDF...)

## 入门程序(商品订单管理)
### 环境准备
- 数据库
  - user
  - orders
  - items
  - order_detail
- 配置前端控制器

web.xml
```
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
         version="4.0">

    <!--配置前端控制器-->
    <servlet>
        <servlet-name>springmvc</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <init-param>
            <!--通过contextConfigLocation配置springmvc加载的配置文件(处理器、适配器等)
                如果不配置，默认加载的是：/WEB-INF/servlet名称-servlet.xml，在这里是springmvc-servlet.xml
            -->
            <param-name>contextConfigLocation</param-name>
            <param-value>classpath:/config/springmvc.xml</param-value>
        </init-param>
    </servlet>

    <servlet-mapping>
        <servlet-name>springmvc</servlet-name>
        <!--
            1 *.action:访问以.action结尾的由DispatcherServlet解析
            2 /:所有访问的地址都由DispatcherServlet解析，对于静态文件的解析需要配置成不让DispatcherServlet解析
            3 /*:这样配置不对，使用这种配置最终要转发到一个jsp页面时，仍然会用DispatcherServlet解析jsp
                 ，但是不能根据jsp页面找到Handler
        -->
        <url-pattern>*.action</url-pattern>
    </servlet-mapping>
    
</web-app>
```
- 各种配置

springmvc.xml
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
                           http://www.springframework.org/schema/beans/spring-beans.xsd
                           http://www.springframework.org/schema/context
                           http://www.springframework.org/schema/context/spring-context.xsd
                           http://www.springframework.org/schema/aop
                           http://www.springframework.org/schema/aop/spring-aop.xsd
                           http://www.springframework.org/schema/mvc
                           http://www.springframework.org/schema/mvc/spring-mvc.xsd
                           http://www.springframework.org/schema/tx
                           http://www.springframework.org/schema/tx/spring-tx/xsd">

    <!--配置Handler
        name就是请求的url
    --> 
    <bean name="/queryItems.action" class="controller.ItemsController"/>

    <!--处理器映射器
        将bean的name作为url进行查找，需要在配置Handler时指定beanname(就是url)
    -->
    <bean class="org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping"/>


    <!--处理器适配器-->
    <!--
        所有的处理器适配器都实现了HandlerAdapter接口
        public class SimpleControllerHandlerAdapter implements HandlerAdapter {
	    @Override
	    public boolean supports(Object handler) {
		    return (handler instanceof Controller);
	    }
	    从supports看出SimpleControllerHandlerAdapter能执行Controller接口的Handler
    -->
    <bean class="org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter"></bean>

    <!--视图解析器
        需要配置解析jsp的视图解析器,默认使用jstl标签
    -->
    <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="prefix" value="/"/>
        <property name="suffix" value=".jsp"/>
    </bean>

</beans>
```
- 开发Handler

需要实现Controller接口，才能由SimpleControllerHandlerAdapter适配器执行
```
package controller;


import entity.Items;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import java.util.ArrayList;
import java.util.List;

public class ItemsController implements Controller {

    public ModelAndView handleRequest(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws Exception {
        List<Items> itemsList = new ArrayList<Items>();
        itemsList.add(new Items("联想", 6000, "thinkpad"));
        itemsList.add(new Items("苹果", 10000, "pro"));
        ModelAndView modelAndView = new ModelAndView();
        //相当于request.serAttribute(),在jsp页面中通过itemsList取数据
        modelAndView.addObject("itemsList", itemsList);
        //指定视图
        modelAndView.setViewName("itemList");
        return modelAndView;
    }
}
```
- 开发视图
items/itemList.jsp
```
<%--
  Created by IntelliJ IDEA.
  User: chenpeipei
  Date: 2018/12/3
  Time: 1:26 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<form action="${pageContext.request.contextPath }/controller/queryItems.action" method="post">
    查询条件：
    <table width="100%" border="1">
        <tr>
            <td><input type="submit" value="查看"></td>
        </tr>
    </table>
    商品列表：
    <table width="100%" border="1">
        <tr>
            <th>商品名称</th>
            <th>商品价格</th>
            <th>生产日期</th>
            <th>商品概述</th>
            <th>操作</th>
        </tr>
        <c:forEach items="${itemsList}" var="item">
            <tr>
                <td>${item.name}</td>
                <td>${item.price}</td>
                <td><fmt:formatDate value="${item.createTime}" pattern="yyyy-MM-dd HH:mm:ss"></fmt:formatDate></td>
                <td><a href="${pageContext.request.contextPath}/item/editItem.action?id=${item.id}">修改</a></td>
                <td>${item.name}</td>
            </tr>
        </c:forEach>
    </table>
</form>
</body>
</html>
```
- 测试
```
http://localhost:8080/queryItems.action
```
注意两种错误：

(1)HTTP Status 404 -

表示处理器映射器根据url找不到Handler,说明url错误

(2)HTTP Status 404 - /items/itemList.jsp

表示处理器映射器根据url找到了Handler，但是转发的jsp页面找不到


## 非注解的处理器映射器和适配器
### 处理器映射器
多个映射器可以并存。前端控制器判断url能让哪些映射器映射，就让正确的映射器处理
1. BeanNameUrlHandlerMapping
2. SimpleUrlHandlerMapping

是BeanNameUrlHandlerMapping的增强版本，他可以将url和处理器bean的id进行统一映射配置
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
                           http://www.springframework.org/schema/beans/spring-beans.xsd
                           http://www.springframework.org/schema/context
                           http://www.springframework.org/schema/context/spring-context.xsd
                           http://www.springframework.org/schema/aop
                           http://www.springframework.org/schema/aop/spring-aop.xsd
                           http://www.springframework.org/schema/mvc
                           http://www.springframework.org/schema/mvc/spring-mvc.xsd
                           http://www.springframework.org/schema/tx
                           http://www.springframework.org/schema/tx/spring-tx/xsd">

    <bean id="itemsController" name="/queryItems.action" class="controller.ItemsController"/>
    
    <!--简单url映射-->
    <bean class="org.springframework.web.servlet.handler.SimpleUrlHandlerMapping">
        <property name="mappings">
            <props>
                <!--对itemsController进行url映射，url是/queryItems.action-->
                <prop key="/queryItems.action">itemsController</prop>
                <prop key="/queryItems2.action">itemsController</prop>
            </props>
        </property>
    </bean>

</beans>
```

### 处理器适配器
1. SimpleControllerHandlerAdapter

要求编写的Handler实现Controller接口

2. HttpRequestHandlerAdapter

要求编写的Handler实现HttpRequestHandler接口
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
                           http://www.springframework.org/schema/beans/spring-beans.xsd
                           http://www.springframework.org/schema/context
                           http://www.springframework.org/schema/context/spring-context.xsd
                           http://www.springframework.org/schema/aop
                           http://www.springframework.org/schema/aop/spring-aop.xsd
                           http://www.springframework.org/schema/mvc
                           http://www.springframework.org/schema/mvc/spring-mvc.xsd
                           http://www.springframework.org/schema/tx
                           http://www.springframework.org/schema/tx/spring-tx/xsd">

    <!--配置Handler
        name就是请求的url
    -->

    <bean id="itemsController2" class="controller.ItemsController2"/>

    <!--简单url映射-->
    <bean class="org.springframework.web.servlet.handler.SimpleUrlHandlerMapping">
        <property name="mappings">
            <props>
                <prop key="/queryItems3.action">itemsController2</prop>
            </props>
        </property>
    </bean>

    <!--处理器适配器-->
    <bean class="org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter"/>

    <!--视图解析器
        需要配置解析jsp的视图解析器,默认使用jstl标签
    -->
    <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="prefix" value="/"/>
        <property name="suffix" value=".jsp"/>
    </bean>
</beans>
```
```
package controller;

import entity.Items;
import org.springframework.web.HttpRequestHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ItemsController2 implements HttpRequestHandler {

    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Items> itemsList = new ArrayList<Items>();
        itemsList.add(new Items("联想", 6000, "thinkpad"));
        itemsList.add(new Items("苹果", 10000, "pro"));
        request.setAttribute("itemsList", itemsList);
        request.getRequestDispatcher("items/itemList.jsp").forward(request, response);
        /*
        使用此种方法可以通过response设置响应的数据格式，比如响应json数据
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write("json串");
        */
    }
}
```





## DispatcherServlet.properties
位于
```
springframework/spring-webmvc/5.1.1.RELEASE/
spring-webmvc-5.1.1.RELEASE.jar/
org/springframework/web/servlet/
DispatcherServlet.properties
```
前端控制器会从这个文件中加载处理器映射器、适配器、视图解析器等组件，如果不在springmvc.xml中配置，它将使用默认的

## 注解的处理器映射器和适配器
### 处理器映射器
在spring3.1之前使用
```org.springframework.web.servlet.mvc.method.annotation.DefaultAnnotationHandlerMapping```
注解映射器

在spring3.1之后使用
```org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping```
注解映射器


### 适配器

在spring3.1之前使用
```org.springframework.web.servlet.mvc.method.annotation.AnnotationMethodHandlerAdapter```
注解适配器

在spring3.1之后使用
```org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter```
注解适配器

### 配置注解的映射器和适配器
```
<bean class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping"/>
<bean class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter"/>
<!--使用<mvc:annotation-driven/>可以代替上边注解适配器和映射器的配置
还默认加载了很多的参数的绑定方法，比如json转换解析器
-->
<mvc:annotation-driven/>
```
### 开发注解Handler
```
package controller;

import entity.Items;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

//@Controller表示他是一个控制器
@Controller
public class ItemsController3 {

    //@RequestMapping实现对queryItems和url进行映射，一个方法对应一个url
    //一般建议将url和方法写成一样
    @RequestMapping("/queryItems4")
    public ModelAndView queryItems() throws Exception {
        List<Items> itemsList = new ArrayList<Items>();
        itemsList.add(new Items("联想", 6000, "thinkpad"));
        itemsList.add(new Items("苹果", 10000, "pro"));
        ModelAndView modelAndView = new ModelAndView();
        //相当于request.serAttribute(),在jsp页面中通过itemsList取数据
        modelAndView.addObject("itemsList", itemsList);
        //指定视图
        modelAndView.setViewName("itemList");
        return modelAndView;
    }
}
```
### 在spring容器中加载handler
```
<!--对于注解的Handler可以单个配置-->
<bean class="controller.ItemsController3"/>
<!--实际开发中建议使用组件扫描-->
<context:component-scan base-package="controller"/>
```

### 测试
```
http://localhost:8080/queryItems4.action
```

## 分析spring mvc执行过程
- 前端控制器接受请求

前端控制器调用doDispatch
```
protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
    HttpServletRequest processedRequest = request;
	HandlerExecutionChain mappedHandler = null;
	boolean multipartRequestParsed = false;
}
```
- 前端控制器调用处理器映射器查找Handler
```
protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
    mappedHandler = getHandler(processedRequest);
}
```
```
@Nullable
protected HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
	if (this.handlerMappings != null) {
	    for (HandlerMapping mapping : this.handlerMappings) {
		    HandlerExecutionChain handler = mapping.getHandler(request);
		    if (handler != null) {
			    return handler;
		    }
	    }
    }
	return null;
}
```
- 调用处理器适配器执行handler,得到执行结果ModelAndView
```
protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
    mv = ha.handle(processedRequest, response, mappedHandler.getHandler());
}
```

- 视图渲染，将model数据填充到request域
```
protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
   processDispatchResult(processedRequest, response, mappedHandler, mv, dispatchException);
}
```
```
private void processDispatchResult(HttpServletRequest request, HttpServletResponse response,
			@Nullable HandlerExecutionChain mappedHandler, @Nullable ModelAndView mv,
			@Nullable Exception exception) throws Exception {
    if (mv != null && !mv.wasCleared()) {
        //得到view
		render(mv, request, response);
		if (errorView) {
			WebUtils.clearErrorRequestAttributes(request);
		}
	}
	//利用view的渲染方法，将模型数据填充到request域
	view.render(mv.getModelInternal(), request, response);
}
```
```
protected void render(ModelAndView mv, HttpServletRequest request, HttpServletResponse response) throws Exception {
	view = resolveViewName(viewName, mv.getModelInternal(), locale, request);
}
```
```
//将模型数据填充到request域
protected void exposeModelAsRequestAttributes(Map<String, Object> model,
			HttpServletRequest request) throws Exception {

	model.forEach((name, value) -> {
	if (value != null) {
		request.setAttribute(name, value);
	}
	else {
		request.removeAttribute(name);
		}
	});
}
```

## spring mvc和mybatis整合
### 需求
使用spring mvc和mybatis完成商品列表查询

### spring+springmvc+mybatis的系统架构
- 表现层:springmvc
- 业务层:service
- 持久层:mybatis

spring将各层进行整合，通过spring他可以管理
(mapper、service、handler都是Javabean)
- 持久层的mapper(相当于dao接口)
- 业务层的service:service中可以调用mapper接口
- 表现层的handler:handler可以调用service接口
- 事务控制

### 整合思路
- 整合dao层

mybatis和spring整合，通过spring管理mapper接口。使用mapper的扫描器自动扫描mapper接口在spring中进行注册
- 整合service

通过spring管理service接口。使用配置方式将service接口配置在spring配置文件中
- 整合spring mvc

由于springmvc是spring的模块，不需要整合


### 准备环境
#### jar包
- 数据库驱动包：mysql
- mybatis jar包
- mybatis和spring的整合包
- log4j
- 数据库连接池
- spring包
- jstl包
```
    <properties>  
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>  
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>  
  
    <!-- spring版本号 -->  
    <spring.version>5.0.6.RELEASE</spring.version>  
  
    <!-- mybatis版本号 -->  
    <mybatis.version>3.2.8</mybatis.version>  
  
    <!-- mysql驱动版本号 -->  
    <mysql-driver.version>5.1.29</mysql-driver.version>  
  
    <!-- log4j日志包版本号 -->  
    <slf4j.version>1.7.18</slf4j.version>  
    <log4j.version>1.2.17</log4j.version>  
  
  </properties> 

    <dependencies>  
    <!-- 添加jstl依赖 -->  
    <dependency>  
      <groupId>jstl</groupId>  
      <artifactId>jstl</artifactId>  
      <version>1.2</version>  
    </dependency>  
  
    <dependency>  
      <groupId>javax</groupId>  
      <artifactId>javaee-api</artifactId>  
      <version>7.0</version>  
    </dependency>  
  
    <!-- 添加junit4依赖 -->  
    <dependency>  
      <groupId>junit</groupId>  
      <artifactId>junit</artifactId>  
      <version>4.11</version>  
      <!-- 指定范围，在测试时才会加载 -->  
      <scope>test</scope>  
    </dependency>  
  
    <!-- 添加spring核心依赖 -->  
    <dependency>  
      <groupId>org.springframework</groupId>  
      <artifactId>spring-core</artifactId>  
      <version>${spring.version}</version>  
    </dependency>  
    <dependency>  
      <groupId>org.springframework</groupId>  
      <artifactId>spring-web</artifactId>  
      <version>${spring.version}</version>  
    </dependency>  
    <dependency>  
      <groupId>org.springframework</groupId>  
      <artifactId>spring-oxm</artifactId>  
      <version>${spring.version}</version>  
    </dependency>  
    <dependency>  
      <groupId>org.springframework</groupId>  
      <artifactId>spring-tx</artifactId>  
      <version>${spring.version}</version>  
    </dependency>  
    <dependency>  
      <groupId>org.springframework</groupId>  
      <artifactId>spring-jdbc</artifactId>  
      <version>${spring.version}</version>  
    </dependency>  
    <dependency>  
      <groupId>org.springframework</groupId>  
      <artifactId>spring-webmvc</artifactId>  
      <version>${spring.version}</version>  
    </dependency>  
    <dependency>  
      <groupId>org.springframework</groupId>  
      <artifactId>spring-context</artifactId>  
      <version>${spring.version}</version>  
    </dependency>  
    <dependency>  
      <groupId>org.springframework</groupId>  
      <artifactId>spring-context-support</artifactId>  
      <version>${spring.version}</version>  
    </dependency>  
    <dependency>  
      <groupId>org.springframework</groupId>  
      <artifactId>spring-aop</artifactId>  
      <version>${spring.version}</version>  
    </dependency>  
  
    <dependency>  
      <groupId>org.springframework</groupId>  
      <artifactId>spring-test</artifactId>  
      <version>${spring.version}</version>  
    </dependency>  
  
    <!-- 添加mybatis依赖 -->  
    <dependency>  
      <groupId>org.mybatis</groupId>  
      <artifactId>mybatis</artifactId>  
      <version>${mybatis.version}</version>  
    </dependency>  
  
    <!-- 添加mybatis/spring整合包依赖 -->  
    <dependency>  
      <groupId>org.mybatis</groupId>  
      <artifactId>mybatis-spring</artifactId>  
      <version>1.2.2</version>  
    </dependency>  
  
    <!-- 添加mysql驱动依赖 -->  
    <dependency>  
      <groupId>mysql</groupId>  
      <artifactId>mysql-connector-java</artifactId>  
      <version>${mysql-driver.version}</version>  
    </dependency>  
    <!-- 添加数据库连接池依赖 -->  
    <dependency>  
      <groupId>commons-dbcp</groupId>  
      <artifactId>commons-dbcp</artifactId>  
      <version>1.2.2</version>  
    </dependency>  
  
    <!-- 添加fastjson -->  
    <dependency>  
      <groupId>com.alibaba</groupId>  
      <artifactId>fastjson</artifactId>  
      <version>1.1.41</version>  
    </dependency>  
  
    <!-- 添加日志相关jar包 -->  
    <dependency>  
      <groupId>log4j</groupId>  
      <artifactId>log4j</artifactId>  
      <version>${log4j.version}</version>  
    </dependency>  
    <dependency>  
      <groupId>org.slf4j</groupId>  
      <artifactId>slf4j-api</artifactId>  
      <version>${slf4j.version}</version>  
    </dependency>  
    <dependency>  
      <groupId>org.slf4j</groupId>  
      <artifactId>slf4j-log4j12</artifactId>  
      <version>${slf4j.version}</version>  
    </dependency>  
  
    <!-- log end -->  
    <!-- 映入JSON -->  
    <dependency>  
      <groupId>org.codehaus.jackson</groupId>  
      <artifactId>jackson-mapper-asl</artifactId>  
      <version>1.9.13</version>  
    </dependency>  
    <!-- https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-core -->  
    <dependency>  
      <groupId>com.fasterxml.jackson.core</groupId>  
      <artifactId>jackson-core</artifactId>  
      <version>2.8.0</version>  
    </dependency>  
    <!-- https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind -->  
    <dependency>  
      <groupId>com.fasterxml.jackson.core</groupId>  
      <artifactId>jackson-databind</artifactId>  
      <version>2.8.0</version>  
    </dependency>  
  
    <dependency>  
      <groupId>commons-fileupload</groupId>  
      <artifactId>commons-fileupload</artifactId>  
      <version>1.3.1</version>  
    </dependency>  
  
    <dependency>  
      <groupId>commons-io</groupId>  
      <artifactId>commons-io</artifactId>  
      <version>2.4</version>  
    </dependency>  
  
    <dependency>  
      <groupId>commons-codec</groupId>  
      <artifactId>commons-codec</artifactId> 
      <version>1.9</version>  
    </dependency>  
    <dependency>
        <groupId>org.aspectj</groupId>
        <artifactId>aspectjweaver</artifactId>
        <version>1.8.4</version>
    </dependency>
  </dependencies>  
```
#### 部分配置文件
log4j.properties
```
log4j.rootLogger=DEBUG,stdout
log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.ConversionPattern=%5 [%t] - %m%n
```
db.properties
```
jdbc.driver=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/spring-mvc-class
jdbc.username=root
jdbc.password=root
```

### 整合dao
- mybatis/sqlMapConfig.xml
```
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">

<configuration>

    <!--全局setting配置，根据需要添加-->

    <!--配置别名-->
    <typeAliases>
        <!--批量扫描-->
        <package name="entity"/>
    </typeAliases>

    <!--由于使用springmvc和mybatis的整合包进行mapper扫描，这里不需要再配置
        必须遵循：mapper.xml和mapper.java文件同名且在一个目录
    -->
    <!--<mappers>-->

    <!--</mappers>-->

</configuration>
```
- spring/applicationContext-dao.xml

配置：数据源、SqlSessionFactory、mapper扫描器
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
                           http://www.springframework.org/schema/beans/spring-beans.xsd
                           http://www.springframework.org/schema/context
                           http://www.springframework.org/schema/context/spring-context.xsd
                           http://www.springframework.org/schema/aop
                           http://www.springframework.org/schema/aop/spring-aop.xsd
                           http://www.springframework.org/schema/mvc
                           http://www.springframework.org/schema/mvc/spring-mvc.xsd
                           http://www.springframework.org/schema/tx
                           http://www.springframework.org/schema/tx/spring-tx/xsd">

    <!--加载fb.properties文件中的内容-->
    <context:property-placeholder location="classpath:config/db.properties"/>

    <!--配置数据源dbcp-->
    <bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource">
        <property name="driverClassName" value="${jdbc.driver}"/>
        <property name="url" value="${jdbc.url}"/>
        <property name="username" value="${jdbc.username}"/>
        <property name="password" value="${jdbc.password}"/>
        <property name="maxActive" value="30"/>
        <property name="maxIdle" value="5"/>
    </bean>

    <!--sqlSessionFactory-->
    <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
        <property name="dataSource" ref="dataSource"/>
        <property name="configLocation" value="classpath:mybatis/sqlMapConfig.xml"/>
    </bean>

    <!--mapper扫描器-->
    <bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
        <!--扫描包路径，如果需要扫描多个包，中间使用半角逗号隔开-->
        <property name="basePackage" value="mappers"/>
        <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"/>
    </bean>
</beans>
```
- 逆向工程生成po类以及mapper

### 手动定义商品查询mapper
- ItemsMapperCustom.xml

sql语句
```
select * from items where name like '%笔记本'%
```
```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org/DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="mappers.ItemsMapperCustom">

    <sql id="query_items_where">
        <!--使用动态sql,通过if判断-->
        <!--商品查询条件通过ItemsVo包装对象中itemsCustom属性传递-->
        <if test="itemsCustom!=null">
            <if test="itemsCustom.name!=null and itemsCustom.name!=''">
                items.name like '%${itemsCustom.name}%'
            </if>
        </if>
    </sql>
    <!--商品列表查询-->
    <!--parameterType传入包装对象
        resultType使用扩展对象
    -->
    <select id="findItemsList" resultType="customs.ItemsCustom" parameterType="vo.ItemsVo">
        select * from items
        <where>
            <include refid="query_items_where"/>
        </where>
    </select>
</mapper>
```
- ItemsMapperCustom.java 
```
package mappers;

import customs.ItemsCustom;
import vo.ItemsVo;

import java.util.List;

public interface ItemsMapperCustom {

    public List<ItemsCustom> findItemsList(ItemsVo itemsVo);
}
```

### 整合service
让spring管理service接口

ItemsService接口
```
package service;

import customs.ItemsCustom;
import vo.ItemsVo;

import java.util.List;

public interface ItemsService {
    
    //商品查询列表
    public List<ItemsCustom>findItemsList(ItemsVo itemsVo);
}
```
ItemsServiceImpl实现类
```
package service.impl;

import customs.ItemsCustom;
import mappers.ItemsMapperCustom;
import org.springframework.beans.factory.annotation.Autowired;
import service.ItemsService;
import vo.ItemsVo;

import java.util.List;

public class ItemsServiceImpl implements ItemsService {

    @Autowired
    ItemsMapperCustom itemsMapperCustom;
    
    public List<ItemsCustom> findItemsList(ItemsVo itemsVo) {
        return itemsMapperCustom.findItemsList(itemsVo);
    }
}
```
在spring容器中配置serivice

applicationContext-service.xml
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
                           http://www.springframework.org/schema/beans/spring-beans.xsd
                           http://www.springframework.org/schema/context
                           http://www.springframework.org/schema/context/spring-context.xsd
                           http://www.springframework.org/schema/aop
                           http://www.springframework.org/schema/aop/spring-aop.xsd
                           http://www.springframework.org/schema/mvc
                           http://www.springframework.org/schema/mvc/spring-mvc.xsd
                           http://www.springframework.org/schema/tx
                           http://www.springframework.org/schema/tx/spring-tx/xsd">

    <!--定义商品管理的service-->
    <bean id="itemsService" class="service.impl.ItemsServiceImpl"/>
</beans>
```

### 事务控制

applicationContext-transaction.xml
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
                           http://www.springframework.org/schema/beans/spring-beans.xsd
                           http://www.springframework.org/schema/context
                           http://www.springframework.org/schema/context/spring-context.xsd
                           http://www.springframework.org/schema/aop
                           http://www.springframework.org/schema/aop/spring-aop.xsd
                           http://www.springframework.org/schema/mvc
                           http://www.springframework.org/schema/mvc/spring-mvc.xsd
                           http://www.springframework.org/schema/tx
                           http://www.springframework.org/schema/tx/spring-tx/xsd">

    <!--事务管理器
        对mybatis的操作数据库事务控制，spring使用jdbc的事务控制类-->
    <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="dataSource"/>
    </bean>

    <!--通知-->
    <tx:advice id="txAdvice" transaction-manager="transactionManager">
        <tx:attributes>
            <!--传播行为-->
            <tx:method name="save" propagation="REQUIRED"/>
            <tx:method name="delete" propagation="REQUIRED"/>
            <tx:method name="insert" propagation="REQUIRED"/>
            <tx:method name="update" propagation="REQUIRED"/>
            <tx:method name="find" propagation="SUPPORTS" read-only="true"/>
            <tx:method name="get" propagation="SUPPORTS" read-only="true"/>
            <tx:method name="select" propagation="SUPPORTS" read-only="true"/>
        </tx:attributes>
    </tx:advice>

    <!--aop-->
    <aop:config>
        <aop:advisor advice-ref="txAdvice" pointcut="execution(* service.impl.*.*(..))"/>
    </aop:config>
</beans>
```

### 整合springmvc
- 创建springmvc.xml,配置处理器映射器、适配器、视图解析器
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
                           http://www.springframework.org/schema/beans/spring-beans.xsd
                           http://www.springframework.org/schema/context
                           http://www.springframework.org/schema/context/spring-context.xsd
                           http://www.springframework.org/schema/aop
                           http://www.springframework.org/schema/aop/spring-aop.xsd
                           http://www.springframework.org/schema/mvc
                           http://www.springframework.org/schema/mvc/spring-mvc.xsd
                           http://www.springframework.org/schema/tx
                           http://www.springframework.org/schema/tx/spring-tx/xsd">


    <context:component-scan base-package="controller"/>
    <mvc:annotation-driven/>


    <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="prefix" value="/"/>
        <property name="suffix" value=".jsp"/>
    </bean>

</beans>
```
- web.xml,配置前端控制器
```
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
         version="4.0">

    <!--配置前端控制器-->
    <servlet>
        <servlet-name>springmvc</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <init-param>
            <!--通过contextConfigLocation配置springmvc加载的配置文件(处理器、适配器等)
                如果不配置，默认加载的是：/WEB-INF/servlet名称-servlet.xml，在这里是springmvc-servlet.xml
            -->
            <param-name>contextConfigLocation</param-name>
            <param-value>classpath:/spring/springmvc.xml</param-value>
        </init-param>
    </servlet>

    <servlet-mapping>
        <servlet-name>springmvc</servlet-name>
        <!--
            1 *.action:访问以.action结尾的由DispatcherServlet解析
            2 /:所有访问的地址都由DispatcherServlet解析，对于静态文件的解析需要配置成不让DispatcherServlet解析
            3 /*:这样配置不对，使用这种配置最终要转发到一个jsp页面时，仍然会用DispatcherServlet解析jsp
                 ，但是不能根据jsp页面找到Handler
        -->
        <url-pattern>/</url-pattern>
    </servlet-mapping>
    
</web-app>
```

### 编写controller
```
package controller;


import customs.ItemsCustom;
import entity.Items;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import service.ItemsService;


import java.util.ArrayList;
import java.util.List;

@Controller
public class ItemsController{

    @Autowired
    private ItemsService itemsService;

    @RequestMapping("/queryItems")
    public ModelAndView handleRequest(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws Exception {
        List<ItemsCustom> itemsList = itemsService.findItemsList(null);
        ModelAndView modelAndView = new ModelAndView();
        //相当于request.serAttribute(),在jsp页面中通过itemsList取数据
        modelAndView.addObject("itemsList", itemsList);
        //指定视图
        modelAndView.setViewName("/items/itemList");
        return modelAndView;
    }
}
```

### 编写jsp
```
<%--
  Created by IntelliJ IDEA.
  User: chenpeipei
  Date: 2018/12/3
  Time: 1:26 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<form action="${pageContext.request.contextPath }/controller/queryItems.action" method="post">
    查询条件：
    <table width="100%" border="1">
        <tr>
            <td><input type="submit" value="查看"></td>
        </tr>
    </table>
    商品列表：
    <table width="100%" border="1">
        <tr>
            <th>商品名称</th>
            <th>商品价格</th>
            <th>生产日期</th>
            <th>商品概述</th>
            <th>操作</th>
        </tr>
        <c:forEach items="${itemsList}" var="item">
            <tr>
                <td>${item.name}</td>
                <td>${item.price}</td>
                <td><fmt:formatDate value="${item.createTime}" pattern="yyyy-MM-dd HH:mm:ss"></fmt:formatDate></td>
                <td><a href="${pageContext.request.contextPath}/item/editItem.action?id=${item.id}">修改</a></td>
                <td>${item.name}</td>
            </tr>
        </c:forEach>
    </table>
</form>
</body>
</html>
```
### 加载spring容器
将mapper、service、controller都加载到spring容器中，就是把三个配置文件：
- applicationContext-dao.xml
- applicationContext-service.xml
- springmvc.xml

加载到spring容器中

在web.xml中添加spring容器监听器加载spring容器
```
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
         version="4.0">

    <!--配置前端控制器-->
    <servlet>
        <servlet-name>springmvc</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <init-param>
            <!--通过contextConfigLocation配置springmvc加载的配置文件(处理器、适配器等)
                如果不配置，默认加载的是：/WEB-INF/servlet名称-servlet.xml，在这里是springmvc-servlet.xml
            -->
            <param-name>contextConfigLocation</param-name>
            <param-value>classpath:/spring/springmvc.xml</param-value>
        </init-param>
    </servlet>

    <servlet-mapping>
        <servlet-name>springmvc</servlet-name>
        <!--
            1 *.action:访问以.action结尾的由DispatcherServlet解析
            2 /:所有访问的地址都由DispatcherServlet解析，对于静态文件的解析需要配置成不让DispatcherServlet解析
            3 /*:这样配置不对，使用这种配置最终要转发到一个jsp页面时，仍然会用DispatcherServlet解析jsp
                 ，但是不能根据jsp页面找到Handler
        -->
        <url-pattern>/</url-pattern>
    </servlet-mapping>

    <!--加载spring容器-->
    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>classpath:spring/applicationContext-*.xml</param-value>
    </context-param>
    <listener>
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>
</web-app>
```
### 商品修改功能开发
#### 开发mapper
通过逆向工程已经生成

#### 开发service
```
package service;

import customs.ItemsCustom;
import vo.ItemsVo;

import java.util.List;

public interface ItemsService {

    //根据id查询商品信息
    public ItemsCustom findItemsById(int id);

    //修改商品信息
    public void updateItems(Integer id, ItemsCustom itemsCustom);
}
```
```
package service.impl;

import customs.ItemsCustom;
import mappers.ItemsMapper;
import mappers.ItemsMapperCustom;
import model.Items;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import service.ItemsService;
import vo.ItemsVo;

import java.util.List;

public class ItemsServiceImpl implements ItemsService {

    @Autowired
    private ItemsMapper itemsMapper;

    public ItemsCustom findItemsById(int id) {
        //中间对商品信息进行业务处理
        //...
        //返回ItemsCustome
        Items items = itemsMapper.selectByPrimaryKey(id);
        ItemsCustom itemsCustom = new ItemsCustom();
        //将items的属性值拷贝到itemsCustom
        BeanUtils.copyProperties(items, itemsCustom);
        return itemsCustom;
    }

    public void updateItems(Integer id, ItemsCustom itemsCustom) {
        //添加业务校验，通常在service接口对关键参数进行校验
        //校验id是否为空，如果为空抛出异常

        itemsCustom.setId(id);
        //更新商品信息updateByPrimaryKeyWithBLOBs根据id更新items表中所有字段，包括大文本类型字段
        itemsMapper.updateByPrimaryKeyWithBLOBs(itemsCustom);
    }
}
```


#### 开发controller

```

```

### controller注解
#### @RequestMapping
- url映射

定义controller方法对应的url

映射成功后，springmvc框架生成一个handler对象，对象中只包括一个映射成功的method
- 窄化请求映射
```
@Controller
//为了对url进行分类管理，可以在这里定义根路径，最终访问url是根路径+子路径
//比如：/items/editItems
@RequestMapping("/items")
public class ItemsController{
}
```
- 限定http请求方法

出于安全考虑，对HTTP链接进行方法限制
```
//限定HTTP请求方法,可以同时为post和get
@RequestMapping(value = "/editItems",method = {RequestMethod.GET})
public  ModelAndView editItems(){
}
```
如果限制请求为post方法，再进行get请求，就会报错
```
HTTP Status 405 - Request method 'GET' not supported
```

#### controller方法的返回值
- 返回ModelAndView

需要方法结束时，定义返回ModelAndView，将model和view分别进行设置
- 返回string

如果controller返回string，表示返回逻辑视图名

1. 真正的视图(jsp路径)=前缀+逻辑视图名+后缀
```
    @RequestMapping(value = "/editItems", method = {RequestMethod.GET})
    public String editItems(Model model) {
        ItemsCustom itemsCustom = itemsService.findItemsById(1);

        //通过形参中的model将model数据传到页面
        //相当于modelAndView.addObject("itemsCustom",itemsCustom);
        model.addAttribute("itemsCustom", itemsCustom);
        return "/items/editItems";
    }
```
2. redirect重定向

redirect特点：浏览器地址栏中的URL会变化，修改提交的request数据无法传到重定向的地址。因为重定向后重新进行request

商品修改提交后，重定向到商品查询列表
```
    //商品信息修改提交
    @RequestMapping("/editItemsSubmit")
    public String editItemsSubmit() {
        //调用service更新商品信息,页面需要将商品信息传到此方法
        //.....

        //重定向,在一个controller里面，不用加/items
        return "redirect:queryItems";
    }
```
3. forward转发

redirect特点:url地址栏不变，request可以共享
```
    @RequestMapping("/editItemsSubmit")
    public String editItemsSubmit(HttpServletRequest request) {
        //测试forward后request是否可以共享
        System.out.println(request.getParameter("id"));
        //调用service更新商品信息,页面需要将商品信息传到此方法
        //.....

        //页面转发,在一个controller里面，不用加/items
        return "forward:queryItems";
    }
```

- 返回void

在controller方法形参上可以定义request和response，使用request和response指定响应结果
1. 使用request转向页面
```
request.getRequestDispatcher("页面路径").forward(request,response)
```
2. 通过response进行页面重定向
```
response.sendRedirect("url")
```
3. 通过response指定响应结果，例如响应json数据如下：
```
response.setCharacterEncoding("utf-8");
response.setContentType("application/json:charset=utf-8");
response.getWriter().write("json串")；
```

#### 参数绑定
- spring参数绑定过程

从客户端请求Key/value数据，处理器适配器调用springmvc提供的参数绑定组件将key/value数据绑定到controller方法的形参上

springmvc中，接收页面提交的数据是通过方法形参来接收，而不是在controller类中定义成员变量来接收

参数绑定组件在springmvc早期版本使用PropertyEditor(只能将字符串转成java对象),后期使用转换器converter(可以进行任意类型的转换)。springmvc提供了很多converter,不需要我们自己定义。在特殊情况下需要自定义，比如对日期数据的绑定
- 参数绑定默认支持的类型
  - HttpServletRequest:通过request对象获取请求信息
  - HttpServletResponse：通过response处理响应信息
  - HttpSession：通过session对象得到session中存放的信息
  - Model/ModelMap：ModelMap是Model接口的实现类，通过Model或ModelMap向页面传递数据。即将model数据填充到request域

直接在controller方法形参上定义上述类型的对象，就可以使用这些对象。在参数绑定过程中，如果遇到下边的类型则直接绑定
 
  
- 绑定简单类型
  
通过@RequestParam对简单类型的参数进行绑定。

如果不使用这个注解，要求request传入的参数名称和controller方法的形参名称一致方可绑定
```
@RequestMapping(value = "/editItems", method = {RequestMethod.GET})
//@RequestParam里边指定request传入的参数名称和controller方法形参进行绑定
//通过required指定参数是否必须要传入
//defaultValue可以设置默认值，如果id参数没有传入，则将默认值和形参进行绑定
public String editItems(Model model, @RequestParam(value = "id",required = true,defaultValue = "") Integer items_id) {
}
```

- 绑定pojo
```
    @RequestMapping("/editItemsSubmit")
    public String editItemsSubmit(HttpServletRequest request, Integer id, ItemsCustom itemsCustom) {
        itemsService.updateItems(id, itemsCustom);

        //页面转发,在一个controller里面，不用加/items
        return "forward:queryItems";
    }
```
上面传入的itemsCustom中的name是乱码，解决方案是在web.xml中添加post乱码过滤器
```
   <!--post乱码过滤器-->
    <filter>
        <filter-name>CharacterEncodingFilter</filter-name>
        <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
        <init-param>
            <param-name>encoding</param-name>
            <param-value>utf-8</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>CharacterEncodingFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
```

get请求中文参数出现乱码的解决方案(出现原因是Tomcat中间件的编码和工程文件的编码不一致造成的)
1. 修改Tomcat配置文件，添加编码与工程编码一致
```
<Connector URIEncoding="utf-8" connectionTimeout="20000" port="8080" protocol="HTTP/1.1" redirectPort="8443"/>
```
2. 在代码中进行转换
```
//ISO8859-1是Tomcat的默认编码，需要将Tomcat编码后的内容按utf-8编码
String username=new String(request.getParameter("username").getBytes("ISO8859-1"),"utf-8")
```

页面中的input的name和controller的pojo形参中的属性名称一致，将页面中的数据绑定到pojo
```
    @RequestMapping("/editItemsSubmit")
    public String editItemsSubmit(HttpServletRequest request, Integer id, ItemsCustom itemsCustom) {
        itemsService.updateItems(id, itemsCustom);

        //页面转发,在一个controller里面，不用加/items
        return "/items/success";
    }
```
```
<%--
  Created by IntelliJ IDEA.
  User: chenpeipei
  Date: 2018/12/3
  Time: 1:26 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<form id="itemForm" action="${pageContext.request.contextPath }/items/editItemsSubmit" method="post">
    <input type="hidden" name="id" value="${itemsCustom.id}">
    修改商品信息
    <table width="100%" border="1">
        <tr>
            <td>商品名称</td>
            <td><input type="text" name="name" value="${itemsCustom.name}"></td>
        </tr>
        <tr>
            <td>商品价格</td>
            <td><input type="text" name="price" value="${itemsCustom.price}"></td>
        </tr>
        <tr>
            <td>商品简介</td>
            <td><input type="text" name="detail" value="${itemsCustom.detail} "></td>
        </tr>
        <tr>
            <td colspan="2"><input type="submit" value="提交"></td>
        </tr>
    </table>

</form>
</body>
</html>
```

- 自定义参数绑定

对于日期类型的参数绑定需要进行自定义参数绑定：

（1）对于controller形参中的pojo对象，如果他的属性中有日期类型，就要自定义参数绑定。

（2）将请求日期数据串转成日期类型，要转换的日期类型和pojo中的日期属性类型要保持一致

（3）需要向处理器适配器中注入自定义的参数绑定组件
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
                           http://www.springframework.org/schema/beans/spring-beans.xsd
                           http://www.springframework.org/schema/context
                           http://www.springframework.org/schema/context/spring-context.xsd
                           http://www.springframework.org/schema/aop
                           http://www.springframework.org/schema/aop/spring-aop.xsd
                           http://www.springframework.org/schema/mvc
                           http://www.springframework.org/schema/mvc/spring-mvc.xsd
                           http://www.springframework.org/schema/tx
                           http://www.springframework.org/schema/tx/spring-tx/xsd">

    <mvc:annotation-driven conversion-service="conversionService"/>

    <!--自定义参数绑定-->
    <bean id="conversionService" class="org.springframework.format.support.FormattingConversionServiceFactoryBean">
        <property name="converters">
            <list>
                <!--日期类型转换-->
                <bean class="converter.CustomDateConverter"></bean>
            </list>
        </property>
    </bean>

</beans>
```
```
package converter;


import org.springframework.core.convert.converter.Converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

//<String, Date>:<原类型,目标类型>
public class CustomDateConverter implements Converter<String, Date> {

    public Date convert(String s) {
        //将日期串转换成日期类型(格式是yyyy-MM-dd HH:mm:ss)
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return simpleDateFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
```
## 高级参数绑定
### 包装类型pojo参数绑定
实现方法
1. 在形参中添加HttpServletRequest request参数，通过request接收查询条件参数
2. 在形参中让包装类型的pojo接收查询条件参数
```
    @RequestMapping("/queryItems")
    public ModelAndView queryItems(javax.servlet.http.HttpServletRequest request, ItemsVo itemsVo) throws Exception {
        List<ItemsCustom> itemsList = itemsService.findItemsList(itemsVo);
        ModelAndView modelAndView = new ModelAndView();
        //相当于request.serAttribute(),在jsp页面中通过itemsList取数据
        modelAndView.addObject("itemsList", itemsList);
        //指定视图
        modelAndView.setViewName("/items/itemList");
        return modelAndView;
    }
```
```
<%--
  Created by IntelliJ IDEA.
  User: chenpeipei
  Date: 2018/12/3
  Time: 1:26 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<form action="${pageContext.request.contextPath}/items/queryItems" method="post">
    查询条件：
    <table width="100%" border="1">
        <tr>
            <td>商品名称:<input name="itemsCustom.name"}></td>
            <td><input type="submit" value="查看"></td>
        </tr>
    </table>
</form>
</body>
</html>
```
### 集合类型绑定
- 数组绑定
```
<%--
  Created by IntelliJ IDEA.
  User: chenpeipei
  Date: 2018/12/3
  Time: 1:26 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title>Title</title>
    <script type="text/javascript">
        function deleteItems() {
            document.itemsForm.action = "${pageContext.request.contextPath}/items/deleteItems";
            document.itemsForm.submit();
        }
        function queryItems() {
            document.itemsForm.action = "${pageContext.request.contextPath}/items/queryItems";
            document.itemsForm.submit();
        }
    </script>
</head>
<body>
<form name="itemsForm" action="${pageContext.request.contextPath}/items/queryItems" method="post">
    查询条件：
    <table width="100%" border="1">
        <tr>
            <td>商品名称:<input name="itemsCustom.name" }></td>
            <td><input type="button" value="查看" onclick="queryItems()"></td>
            <td><input type="button" value="批量删除" onclick="deleteItems()"></td>
        </tr>
    </table>
    商品列表：
    <table width="100%" border="1">
        <tr>
            <th>选择</th>
            <th>商品名称</th>
            <th>商品价格</th>
            <th>生产日期</th>
            <th>商品概述</th>
            <th>操作</th>
        </tr>
        <c:forEach items="${itemsList}" var="item">
            <tr>
                <td><input type="checkbox" name="items_id" value="${item.id}"></td>
                <td>${item.name}</td>
                <td>${item.price}</td>
                <td><fmt:formatDate value="${item.createtime}" pattern="yyyy-MM-dd HH:mm:ss"></fmt:formatDate></td>
                <td>${item.detail}</td>
                <td><a href="${pageContext.request.contextPath}/items/editItems?id=${item.id}">修改</a></td>
            </tr>
        </c:forEach>
    </table>
</form>
</body>
</html>
```
```
package controller;


import customs.ItemsCustom;
import entity.Items;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import service.ItemsService;
import vo.ItemsVo;


import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/items")
public class ItemsController {

    @Autowired
    private ItemsService itemsService;

    @RequestMapping("/deleteItems")
    public String deleteItems(Integer[] items_id) {
        //调用service批量删除商品
        //....

        return "/items/success";
    }
}
```
- list绑定

使用List接受页面提交的批量数据，必须通过包装pojo接收，即在包装pojo中定义List属性
```
public class ItemsVo{

    //批量商品信息
    private List<ItemsCustom>itemsCustomList;
}
```
```
    //批量修改商品提交
    //通过itemsVo接收提交的批量商品信息，把这些信息存储到它里面的itemsCustomList属性中
    @RequestMapping("/editItemsAllSubmit")
    public String editItemsAllSubmit(ItemsVo itemsVo){
        return "/items/success";
    }
```
```
<%--
  Created by IntelliJ IDEA.
  User: chenpeipei
  Date: 2018/12/3
  Time: 1:26 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title>Title</title>
    <script type="text/javascript">
        function queryItems() {
            document.itemsForm.action = "${pageContext.request.contextPath}/items/queryItems";
            document.itemsForm.submit();
        }
        function editItemsSubmit() {
            document.itemsForm.action = "${pageContext.request.contextPath}/items/editItemsAllSubmit";
            document.itemsForm.submit();
        }
    </script>
</head>
<body>
<form name="itemsForm" action="${pageContext.request.contextPath}/items/queryItems" method="post">
    查询条件：
    <table width="100%" border="1">
        <tr>
            <td>商品名称:<input name="itemsCustom.name" }></td>
            <td><input type="button" value="查看" onclick="queryItems()"></td>
            <td><input type="button" value="批量修改提交" onclick="editItemsSubmit()"></td>
        </tr>
    </table>
    商品列表：
    <table width="100%" border="1">
        <tr>
            <th>选择</th>
            <th>商品名称</th>
            <th>商品价格</th>
            <th>生产日期</th>
            <th>商品概述</th>
            <th>操作</th>
        </tr>
        <c:forEach items="${itemsList}" var="item" varStatus="status">
            <tr>
                <td><input type="text" name="itemsCustomList[${status.index}].name" value="${item.name}"></td>
                <td><input type="text" name="itemsCustomList[${status.index}].price" value="${item.price}"></td>
                <td><input type="text" name="itemsCustomList[${status.index}].createtime" value="<fmt:formatDate value="${item.createtime}" pattern="yyyy-MM-dd HH:mm:ss"></fmt:formatDate>"></td>
                <td><input type="text" name="itemsCustomList[${status.index}].detail" value="${item.detail}"></td>
            </tr>
        </c:forEach>
    </table>
</form>
</body>
</html>
```
```
package controller;


import customs.ItemsCustom;
import entity.Items;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import service.ItemsService;
import vo.ItemsVo;


import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
//为了对url进行分类管理，可以在这里定义根路径，最终访问url是根路径+子路径
//比如：/items/editItems
@RequestMapping("/items")
public class ItemsController {

    @Autowired
    private ItemsService itemsService;
    
    //批量修改商品页面，将商品信息查询出来，在页面中可以编辑商品信息
    @RequestMapping("/editItemsQuery")
    public ModelAndView editItemsQuery(javax.servlet.http.HttpServletRequest request, ItemsVo itemsVo) throws Exception {
        List<ItemsCustom> itemsList = itemsService.findItemsList(itemsVo);
        ModelAndView modelAndView = new ModelAndView();
        //相当于request.serAttribute(),在jsp页面中通过itemsList取数据
        modelAndView.addObject("itemsList", itemsList);
        //指定视图
        modelAndView.setViewName("/items/editItemsList");
        return modelAndView;
    }

    //批量修改商品提交
    //通过itemsVo接收提交的批量商品信息，把这些信息存储到它里面的itemsCustomList属性中
    @RequestMapping("/editItemsAllSubmit")
    public String editItemsAllSubmit(ItemsVo itemsVo){
        return "/items/success";
    }
}
```
- map绑定

也通过在包装类型中定义map来接收
```
 <tr>
    <td><input type="text" name="itemInfo['name']" value="${item.name}"></td>
    <td><input type="text" name="itemInfo['price']" value="${item.price}"></td>
    <td><input type="text" name="itemInfo['createtime']" value="<fmt:formatDate value="${item.createtime}" pattern="yyyy-MM-dd HH:mm:ss"></fmt:formatDate>"></td>
    <td><input type="text" name="itemInfo['detail']" value="${item.detail}"></td>
</tr>
```
```
@RequestMapping("/editItemsAllSubmit")
public String editItemsAllSubmit(ItemsVo itemsVo){
    String[]name= (String[]) itemsVo.getItemInfo().get("name");
    return "/items/success";
}
```
## 服务端校验
### 校验理解
- 项目中，通常使用较多的是前端的校验，比如页面中的js校验
- 对于安全性要求高的建议在服务端进行校验
- 服务端校验包括
  - 控制层controller:校验页面请求的参数的合法性。在服务端controller校验不区分客户端类型
  - 业务层service:主要校验关键业务参数，仅限于service接口中使用的参数
  - 持久层dao:一般不校验

### springmvc校验
springmvc使用的是hibernate的校验框架(validation)

页面提交请求的参数，请求到controller方法中，使用validation进行校验，如果校验出去，将错误信息展示到页面

### 环境准备
```
<dependency>
    <groupId>javax.validation</groupId>
    <artifactId>validation-api</artifactId>
    <version>2.0.1.Final</version>
</dependency>
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-validator</artifactId>
    <version>6.0.13.Final</version>
</dependency>
<dependency>
    <groupId>org.jboss.logging</groupId>
    <artifactId>jboss-logging</artifactId>
    <version>3.3.2.Final</version>
</dependency>
```

### 配置校验器
springmvc中

```
    <!--校验器-->
    <bean id="validator" class="org.springframework.validation.beanvalidation.LocalValidatorFactoryBean">
        <!--校验器-->
        <property name="providerClass" value="org.hibernate.validator.HibernateValidator"/>
        <!--指定校验使用的资源文件，在该文件中配置校验错误信息。如果不指定则默认使用classpath下的ValidationMessages.properties-->
        <property name="validationMessageSource" ref="messageSource"/>
    </bean>

    <!--校验错误信息配置文件-->
    <bean id="messageSource" class="org.springframework.context.support.ReloadableResourceBundleMessageSource">
        <!--资源文件名-->
        <property name="basenames">
            <list>
                <value>classpath:/config/customValidationMessages</value>
            </list>
        </property>
        <!--资源文件编码格式-->
        <property name="fileEncodings" value="utf-8"/>
        <!--对资源文件内容缓存时间，单位秒-->
        <property name="cacheSeconds" value="120"/>
    </bean>
```

### 将校验器注入适配器中
```
<mvc:annotation-driven conversion-service="conversionService" validator="validator"/>
```

### 在pojo中添加校验规则
```
public class Items {
    private Integer id;

    //校验名称在1到30字符之间
    //message是提示校验出错的信息
    @Size(min=1,max=30,message = "{items.name.length.error}")
    private String name;

    private Double price;

    private String pic;

    //非空校验
    @NotNull(message = "{items.createtime.isNotNull}")
    private Date createtime;
```
config/customValidationMessages.properties
```
items.name.length.error=请输入1-30的字符
items.createtime.isNotNull=日期不能为空
````
### 在controller中使用校验
```
    @RequestMapping("/editItemsSubmit")
    //在需要校验的pojo前面添加@Validated，并在他后面添加BindingResult bindingResult接受校验出错信息
    //@Validated和BindingResult bindingResult是配对出现的，并且形参顺序固定(一前一后)
    public String editItemsSubmit(HttpServletRequest request, Integer id, @Validated ItemsCustom itemsCustom, BindingResult bindingResult) {
        //获取校验错误信息
        if (bindingResult.hasErrors()) {
            //输出错误信息
            List<ObjectError> objectErrors = bindingResult.getAllErrors();
            for (ObjectError objectError : objectErrors) {
                System.out.println(objectError.getDefaultMessage());
            }
        }
        
        itemsService.updateItems(id, itemsCustom);

        //页面转发,在一个controller里面，不用加/items
        return "/items/success";
    }
```
### 在页面显示校验错误信息
在controller中将错误信息传到页面即可
```
    @RequestMapping("/editItemsSubmit")
    //在需要校验的pojo前面添加@Validated，并在他后面添加BindingResult bindingResult接受校验出错信息
    //@Validated和BindingResult bindingResult是配对出现的，并且形参顺序固定(一前一后)
    public String editItemsSubmit(Model model,HttpServletRequest request, Integer id, @Validated ItemsCustom itemsCustom, BindingResult bindingResult) {
        //获取校验错误信息
        if (bindingResult.hasErrors()) {
            //输出错误信息
            List<ObjectError> objectErrors = bindingResult.getAllErrors();
            for (ObjectError objectError : objectErrors) {
                System.out.println(objectError.getDefaultMessage());
            }
            //将错误信息传到页面
            model.addAttribute("allErrors",objectErrors);
            return "/items/editItems";
        }

        itemsService.updateItems(id, itemsCustom);
        //页面转发,在一个controller里面，不用加/items
        return "/items/success";
    }
```
```
<!--显示错误信息-->
<c:if test="${allErrors!=null}">
    <c:forEach items="${allErrors}" var="error">
        ${error.defaultMessage}
    </c:forEach>
</c:if>
```
### 分组校验
在pojo中定义校验规则，而pojo是被多个controller共用，当不同的controller方法对同一个pojo进行校验，但是每个controller方法又需要不同的校验。

解决：

定义多个校验分组(其实是一个java接口)。每个controller使用不同的校验分组

```
package validation;

//校验分组
public interface ValidationGroup1 {

    //接口中不需要定义任何方法，仅是对不同的校验规则进行分组
    //此分组只校验商品名称长度
}
```
```
public class Items {
    //校验名称在1到30字符之间
    //message是提示校验出错的信息
    @Size(min = 1, max = 30, message = "{items.name.length.error}",groups = {ValidationGroup1.class})
    private String name;
}
```
```
//value = {ValidationGroup1.class}指定使用这个分组的校验
public String editItemsSubmit(Model model, HttpServletRequest request, Integer id, @Validated(value = {ValidationGroup1.class}) ItemsCustom itemsCustom, BindingResult bindingResult) {
}
```
### 数据回显
提交后，如果出现错误，将刚才提交的数据回显到刚才的提交页面。

- pojo数据回显方法
  - springmvc默认对pojo数据进行回显
  - pojo数据传入controller方法后，springmvc自动将pojo数据方法request域，key等于pojo类型(首字母小写)
  ```
    //@ModelAttribute("items")指定pojo回显到页面的数据在request中的key
    public String editItemsSubmit(Model model, HttpServletRequest request, Integer id, @ModelAttribute("items") @Validated(value = {ValidationGroup1.class}) ItemsCustom itemsCustom, BindingResult bindingResult) {
    }
  ```
  @ModelAttribute还可以将方法返回值传到页面
  ```
    //itemTypes表示最终将方法返回值放在request中的key
    @ModelAttribute("itemtypes")
    public Map<String,String>getItemTypes(){
        Map<String,String>itemTypes=new HashMap<String, String>();
        itemTypes.put("101","数码");
        itemTypes.put("102","母婴");
        return itemTypes;
    }
  ```
  ```
  商品类型：
    <select name="itemtype">
        <c:forEach items="${itemtypes}" var="itemtype">
            <option value="${itemtype.key}">${itemtype.value}</option>
        </c:forEach>
    </select>
  ```
- 简单类型数据回显：使用model
```
model.addAttribute("id",id);
```
  
## 异常处理
系统中异常包括两类，预期异常和运行时异常RuntimeException。前者通过捕获异常从而获取异常信息，后者主要通过规范代码开发、测试来减少运行时异常的发生

系统的dao、service、contr出现异常都通过throwa Exception向上抛出，最后由springmvc前端控制器交由异常处理器ExceptionResolver进行统一异常处理。一个系统只有一个异常处理器
### 自定义异常类
对不同的异常类型定义异常类，继承Exception
```
package exception;

//自定义异常类
//针对预期的异常，需要在程序中抛出此类的异常
public class CustomException extends Exception{

    //异常信息
    private String message;

    public CustomException(String message, String message1) {
        super(message);
        this.message = message1;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
```
### 全局异常处理器
系统遇到异常，在程序中手动抛出时，dao抛给service，service抛给controller，controller抛给前端控制器，前端控制器调用全局异常处理器

#### 全局异常处理器处理思路
- 解析出异常类型
- 如果该异常是自定义的异常，那么直接抛出异常信息，在错误页面展示
- 如果该异常不是自定义的异常，那么构造一个自定义的异常类型(信息为“位置错误”)
```
package exception;

import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//自定义的全局异常处理器
public class CustomExceptionResolver implements HandlerExceptionResolver {

    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler, Exception e) {
        //handler就是处理器适配器要执行的Handler对象(只有method一个方法)
        //e就是系统抛出的异常
        //解析出异常类型
        CustomException customException;
        if (e instanceof CustomException) {
            //如果该异常是自定义的异常，那么直接抛出异常信息，在错误页面展示
            customException = (CustomException) e;
        } else {
            //如果该异常不是自定义的异常，那么构造一个自定义的异常类型(信息为“位置错误”)
            customException = new CustomException("未知错误");
        }
        String message = customException.getMessage();
        ModelAndView modelAndView = new ModelAndView();
        //将错误信息传到页面
        modelAndView.addObject("message", message);
        modelAndView.setViewName("/items/error");
        return modelAndView;
    }
}
```
springmvc中
```
<!--全局异常处理器-->
<!--只要实现HandlerExceptionResolver接口，就是全局异常处理器-->
<bean class="exception.CustomExceptionResolver"/>
```
#### 异常测试
在controller、service、dao中任意一处需要手动抛出异常

如果是程序中手动抛出对的异常，在错误页面中会显示自定义的异常信息。如果不是手动抛出的异常则说明是一个运行时异常，在错误页面只显示“未知错误”

- controller中抛出异常
```
    @RequestMapping(value = "/editItems", method = {RequestMethod.GET})
    public String editItems(Model model, @RequestParam(value = "id", required = true, defaultValue = "") Integer items_id) throws Exception {
        ItemsCustom itemsCustom = itemsService.findItemsById(items_id);

        //判断商品是否为空，如果根据id没有查询到商品则抛出异常，提示用户商品信息不存在
        if (itemsCustom == null) {
            throw new CustomException("修改的商品信息不存在");
        }

        model.addAttribute("itemsCustom", itemsCustom);
        return "/items/editItems";
    }
```
也可以在service中抛出异常。

如果是和业务功能相关的异常，建议在service中抛出。与业务功能不相关的在controller中抛出。上面的这个例子就建议在service中抛出

## 上传图片
### springmvc对多部件类型的解析
在form中提交```enctype="multipart/form-data"```类型的数据时，需要springmvc对multipart类型的数据进行解析
```
<form id="itemForm" enctype="multipart/form-data" action="${pageContext.request.contextPath }/items/editItemsSubmit" method="post">
</form>
```
- jar 包
```
<dependency>
    <groupId>commons-fileupload</groupId>
    <artifactId>commons-fileupload</artifactId>
    <version>1.3.3</version>
</dependency>
<dependency>
    <groupId>commons-io</groupId>
    <artifactId>commons-io</artifactId>
    <version>2.6</version>
</dependency>
```

- 在springmvc.xml中配置multipart解析器
```
<!--文件上传解析器-->
<bean id="multipartResolver" class="org.springframework.web.multipart.commons.CommonsMultipartResolver">
    <!--设置上传文件的最大尺寸为5MB-->
    <property name="maxUploadSize">
        <value>5242880</value>
    </property>
</bean>
```
### 创建图片虚拟目录存储图片
两种方法

- IDEA中配置

tomcat中--Deployment---添加External Source
- tomcat配置文件中配置

tomcat目录--conf--server.xml
```
<Context docBase="XXX" path="/pic" reloadable="false"/>
```
就可以访问目录中的图片了
```
http://localhost:8080/img/1.JPG
```

注意在图片虚拟目录中，一定要将图片目录分级创建(为了提高I/O性能)，一般按日期(年、月、日)进行分级创建


### 上传图片代码
```
    @RequestMapping("/editItemsSubmit")
    //items_pic用来接收商品图片
    public String editItemsSubmit(Model model, HttpServletRequest request, Integer id,
                                  @ModelAttribute("items") @Validated(value = {ValidationGroup1.class}) ItemsCustom itemsCustom,
                                  BindingResult bindingResult,
                                  MultipartFile items_pic) throws IOException {
        //获取校验错误信息
        if (bindingResult.hasErrors()) {
            //输出错误信息
            List<ObjectError> objectErrors = bindingResult.getAllErrors();
            for (ObjectError objectError : objectErrors) {
                System.out.println(objectError.getDefaultMessage());
            }
            //将错误信息传到页面
            model.addAttribute("allErrors", objectErrors);
            return "/items/editItems";
        }

        //上传图片
        if (items_pic != null) {
            //上传的图片的原始名称s
            String originalFileName = items_pic.getOriginalFilename();
            if (originalFileName != null && originalFileName.length()>0) {
                //存储图片的物理路径
                String path = "/Users/chenpeipei/IDEAProjects/img/";

                //新的图片名称
                String newFileName = UUID.randomUUID() + originalFileName.substring(originalFileName.lastIndexOf("."));
                File newFile = new File(path + newFileName);
                //将内存中的数据写入磁盘
                items_pic.transferTo(newFile);
                //将新的图片名称写到itemsCustom中
                itemsCustom.setPic(newFileName);
            }
        }

        itemsService.updateItems(id, itemsCustom);
        //页面转发,在一个controller里面，不用加/items
        return "/items/success";
    }
```
```
       <tr>
            <td>
                <c:if test="${itemsCustom.pic!=null}">
                    <img src="/img/${itemsCustom.pic}" width="100" height="100"/>
                    <br/>
                </c:if>
                <input type="file" name="items_pic">
            </td>
        </tr>
```
## json数据交互
### 为什么要json数据交互
json数据格式在接口调用中、HTML页面中较常用，他的格式简单，解析方便。例如webservice接口，传输json数据

### springmvc进行json交互
客户端请求：
- 如果请求的是json串(contentType=application/json)，那么会经过controller的数据绑定，通过@RequestBody将json串转成java对象。通过@ResponseBody将java对象转成json串输出
- 如果请求的是key/value串(contentType=application/x-www-form-urlen,默认的)，不需要@RequestBody将json串转成java对象。还是需要通过@ResponseBody将java对象转成json串输出

最终输出的都是json数据，为了在前端页面方便对请求结果进行解析

### 环境准备
springmvc默认用MappingJacksonHttpMessageConverter对json数据进行转换

@RequestBody和@ResponseBody就是使用下面的包进行转换
```
<dependency>
    <groupId>org.codehaus.jackson</groupId>
    <artifactId>jackson-mapper-asl</artifactId>
    <version>1.9.13</version>
</dependency>
<dependency>
    <groupId>org.codehaus.jackson</groupId>
    <artifactId>jackson-core-asl</artifactId>
    <version>1.9.13</version>
</dependency>
```

### 配置json转换器
```
<!--注解适配器-->
<bean class="org.springframefork.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter">
    <property name="messageConverters">
        <list>
            <bean class="org.springframefork.http.converter.json.MappingJacksonHttpMessageConverter"></bean>
        </list>
    </property>
</bean>
```
但是使用```<mvc:annotation-driven/>```则不用定义上边的内容

### json交互测试
- 输入json串，输出json串
```
import customs.ItemsCustom;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

//json交互测试
@Controller
public class JsonTest {

    //请求json(商品信息),响应json(商品信息)
    //@RequestBody将请求的商品信息的json串转成itemsCustom对象
    //ResponseBody将itemsCustom转成json输出
    @RequestMapping("/requestJson")
    public @ResponseBody ItemsCustom requestJson(@RequestBody ItemsCustom itemsCustom){
        return itemsCustom;
    }
}
```
```
<%--
  Created by IntelliJ IDEA.
  User: chenpeipei
  Date: 2018/12/5
  Time: 11:11 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <script type="text/javascript" src="${pageContext.request.contextPath }/js/jquery-3.3.1.min.js"/>
    <script type="text/javascript">
        //请求json,输出json
        function requestJson() {
            $.ajax({
                type:'post',
                url:'${pageContext.request.contextPath }/requestJson',
                contentType:'application/json;charset=utf-8',
                //数据格式是json串
                data:'{"name":"手机","price":999}',
                success:function (data) {//返回json结果
                    alert(data);
                    //{"id":1,"name":"手机","price":999.0,"pic":null,"createtime":null,"detail":null}
                }
            })
        }
    </script>
</head>
<body>
<input type="button" onclick="requestJson()" value="请求json,输出json">
</body>
</html>
```

- 输入key/value,输出json串

```
package controller;

import customs.ItemsCustom;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

//json交互测试
@Controller
public class JsonTest {

    //请求key/value(商品信息),响应json(商品信息)
    @RequestMapping("/responseJson")
    public @ResponseBody ItemsCustom responseJson(ItemsCustom itemsCustom){
        return itemsCustom;
    }
}
```
```
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <script type="text/javascript" src="${pageContext.request.contextPath }/js/jquery-3.3.1.min.js"/>
    <script type="text/javascript">

        //请求key/value,输出json
        function responseJson() {
            $.ajax({
                type: 'post',
                url: '${pageContext.request.contextPath }/responseJson',
                //因为请求的是key/value，这里不需要请求contentType,因为默认就是key/value类型
                // contentType: 'application/json;charset=utf-8',
                data: 'name=手机&price=999',
                success: function (data) {//返回json结果
                    alert(data);
                    //name=手机&price=999
                }
            });
        }
    </script>
</head>
<body>
<input type="button" onclick="requestJson()" value="请求json,输出json">
<input type="button" onclick="responseJson()" value="请求key/value,输出json">
</body>
</html>
```
## springmvc对RESTful的支持
### RESTful(Representational State Transfer)
#### 定义
- 表现层的状态转换
- 是一种开发的理念，是对HTTP的很好的诠释。
- 表现层(Representational)指的是"资源的表现层"
  - 资源是网络上的一个实体(一个具体的存在)，他可以是一段文本、一张图片、一首歌曲。可以用一个URI指向她，每种资源对应一个特定的URI
  - 资源是一种信息实体，他可以有多种外在的表现形式。我们把资源具体呈现出来的形式叫他的"表现层"。比如文本可以用txt格式、HTML格式、JSON格式表现，图片可以用JPG格式、PNG格式
  - URI只代表资源的实体，不代表他的形式。严格的说，有些网址最后的".html"后缀名是不必要的，因为这个后缀名表示格式，属于表现层范畴。而URI应该只代表资源的位置。应该在HTTP请求的有信息中用Accept和Content-Type字段指定，这两个字段才是对表现层的描述
- 状态转化(State Transfer)
  - 访问一个网站，就代表了客户端和服务器的一个互动过程。在这个过程中，就涉及到数据和状态的变化
  - HTTP协议是一个无状态协议，所有状态都保存在无服务器端。如果客户端想要操作服务器，就必须通过某种手段让服务端发生"状态转化"，而这种转化是建立在表现层之上的，所以是"表现层状态转化"
  - 客户端用到的手段就是HTTP协议里面四个表示操作方式的动词:GET、POST、PUT、DELETE，他们分别对应四种基本操作：GET用来获取资源、POST用来新建/更新资源、PUT用来更新资源、DELETE用来删除资源

总结：
- 每一个URI代表一种资源
- 客户端和服务器之间传递这种资源的某种表现层
- 客户端通过四个HTTP动词，对服务器端资源进行操作，实现"表现层状态转化"

#### 要遵循RESTful的规则
- 对URL进行规范：写RESTful格式的URL
  - 非REST的url:```http://.../queryItems?id=001&type=T01```
  - REST的url:```http://.../items/001```

他的特点是url简介，将参数通过url传到服务端。不管是删除、添加、更新。使用的url是一致的。如果要进行删除，需要设置http的方法为delete。

后台controller方法就要判断http方法，如果是delete则执行删除，如果是post则执行添加

- 对http方法的规范
- 对http的contentType的规范

请求时指定contentType，要json数据就设置成json格式的type

### 举例
查询商品信息，返回json数据
```
package controller;


import customs.ItemsCustom;
import entity.Items;
import exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import service.ItemsService;
import validation.ValidationGroup1;
import vo.ItemsVo;


import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Controller
//为了对url进行分类管理，可以在这里定义根路径，最终访问url是根路径+子路径
//比如：/items/editItems
@RequestMapping("/items")
public class ItemsController {

    @Autowired
    private ItemsService itemsService;

    //查询商品信息，输出json
    @RequestMapping("/itemsView/{id}")
    ///itemsView/{id}里面的{id}表示将这个位置的参数传到@PathVariable指定的名称中
    public @ResponseBody ItemsCustom itemsView(@PathVariable("id") Integer id){
        ItemsCustom itemsCustom=itemsService.findItemsById(id);
        return itemsCustom;
    }
}
```
### REST风格对静态资源的解析
配置前端控制器的url-pattern中指定/,对静态资源的解析出现问题

就需要在springmvc.xml中配置对静态资源的解析
```
<!--对静态资源的解析,包括js、css、img-->
<mvc:resources location="/js/" mapping="/js/**"/>
```

## 拦截器
### 定义
定义一个拦截器，要实现HandlerInterceptor接口，这个接口中有三个方法
```
//在进入Handler方法之前执行
//用于身份认证和身份授权。如果身份认证不通过，需要此方法拦截不向下执行
public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
    //return false表示拦截，不向下执行
    //return true表示放行
    return false;
}

//进入Handler方法后，在返回modelAndView之前执行
//应用场景从modelAndView出发：将公用的模型数据在这里传到视图，也可以在这里同一指定视图。比如菜单的导航
public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler, ModelAndView modelAndView) throws Exception {

}

//执行Handler方法之后执行
//统一的异常处理、统一的日志处理
public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler, Exception e) throws Exception {

}
```
### 拦截器配置
1. springmvc拦截器是针对HandlerMapping进行拦截设置。如果在某个HandlerMapping中配置拦截器，那么经过该HandlerMapping映射成功的handler才使用该拦截器
```
<bean class="org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping">
    <property name="interceptors">
        <list>
            <ref bean="handlerInterceptor1"/>
            <ref bean="handlerInterceptor2"/>
        </list>
    </property>
</bean>
<bean id="handlerInterceptor1" class="interceptor.HandlerInterceptor1"/>
<bean id="handlerInterceptor2" class="interceptor.HandlerInterceptor2"/>
```
2. springmvc也可以配置类似全局的拦截器，springmvc将配置的类似全局的拦截器注入到每个HandlerMapping中

```
<!--拦截器-->
<mvc:interceptors>
    <!--多个拦截器,顺序执行-->
    <mvc:interceptor>
        <!--/**表示所有url包括子url路径,/*则只拦截最根的url-->
        <mvc:mapping path="/**"/>
        <bean class="interceptor.HandlerInterceptor1"/>
    </mvc:interceptor>
    <mvc:interceptor>
        <mvc:mapping path="/**"/>
        <bean class="interceptor.HandlerInterceptor2"/>
    </mvc:interceptor>
</mvc:interceptors>
```
### 拦截器测试
```
package interceptor;


import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HandlerInterceptor1 implements HandlerInterceptor {

    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        System.out.println("HandlerInterceptor1...preHandle");
        return false;
    }

    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        System.out.println("HandlerInterceptor1...postHandle");
    }

    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        System.out.println("HandlerInterceptor1...afterCompletion");
    }
}
```
```
package interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HandlerInterceptor2 implements HandlerInterceptor {

    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        System.out.println("HandlerInterceptor2...preHandle");
        return false;
    }

    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        System.out.println("HandlerInterceptor2...postHandle");
    }

    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        System.out.println("HandlerInterceptor2...afterCompletion");
    }
}
```
- 两个拦截器都放行,preHandle都return true
```
HandlerInterceptor1...preHandle
HandlerInterceptor2...preHandle
HandlerInterceptor2...postHandle
HandlerInterceptor1...postHandle
HandlerInterceptor2...afterCompletion
HandlerInterceptor1...afterCompletion
```
结论：

(1)preHandle按配置的顺序执行

(2)postHandle和afterCompletion按配置的逆向顺序执行


- 拦截器1放行，拦截器2不放行

页面就不显示了
```
HandlerInterceptor1...preHandle
HandlerInterceptor2...preHandle
HandlerInterceptor1...afterCompletion
```
结论:

(1)拦截器1放行，拦截器2的preHandle才会执行

(2)拦截器2的preHandle不放行，他的postHandle和afterCompletion就不执行

(3)拦截器2的preHandle不放行，拦截器1的postHandle不执行。说明只要有一个拦截器不放行，postHandle就不会执行

- 两个拦截器都不放行
```
HandlerInterceptor1...preHandle
```
结论:
拦截器1不放行，其余两个方法都不会执行

### 小结
- 如果要使用统一日志处理拦截器，那么需要拦截器preHandle放行，并且把它放在拦截器配置的第一个位置
- 如果要一个登陆认证拦截器，那么需要放在拦截器中的第一个位置，如果再有一个权限校验拦截器，那要把它放在登陆认证拦截器后面

### 拦截器应用(实现登陆认证)

- 用户请求url
- 拦截器进行拦截校验
  - 如果请求的url是公开地址(无需登陆即可访问的url)则放行
  - 如果用户session不存在则跳到登录界面
  - 如果用户session存在则放行继续操作

```
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<form action="${pageContext.request.contextPath}/login" method="post">
    用户账号:<input type="text" name="username"><br/>
    用户密码:<input type="text" name="password"><br/>
    <input type="submit" value="登陆">
</form>
</body>
</html>
```
```
package controller;

import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
public class LoginController {

    @RequestMapping("/login")
    public String login(HttpSession httpSession, String username, String password) {
        //调用service进行用户身份验证
        //...

        //在session中保存用户身份信息
        httpSession.setAttribute("username", username);

        //重定向到商品列表页面
        return "redirect:/items/queryItems";
    }

    @RequestMapping("/logout")
    public String logout(HttpSession httpSession) {
        //清除session
        httpSession.invalidate();
        //重定向到商品列表页面
        return "redirect:/items/queryItems";
    }
}
```
```
package interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginInterceptor implements HandlerInterceptor {

    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {

        //获取请求的url
        String url = httpServletRequest.getRequestURI();
        //判断url是否是公开地址(实际使用时将公开地址配置在配置文件中)
        //这里公开地址是登陆提交的地址
        if (url.indexOf("login") >= 0) {
            //如果进行登陆提交，放行
            return true;
        }

        //判断session
        HttpSession session = httpServletRequest.getSession();
        //从session中取出用户身份信息
        //从session中取出用户身份信息
        String username = (String) session.getAttribute("username");
        if (username != null) {
            //身份存在，放行
            return true;
        }

        //执行到这里表示用户身份需要认证,跳转到登陆界面
        httpServletRequest.getRequestDispatcher("login.jsp").forward(httpServletRequest, httpServletResponse);
        return false;
    }

    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
```
```
<mvc:interceptors>
    <!--多个拦截器,顺序执行-->
    <mvc:interceptor>
        <mvc:mapping path="/**"/>
        <bean class="interceptor.LoginInterceptor"/>
    </mvc:interceptor>
    <mvc:interceptor>
        <mvc:mapping path="/**"/>
        <bean class="interceptor.HandlerInterceptor1"/>
    </mvc:interceptor>
    <mvc:interceptor>
        <mvc:mapping path="/**"/>
        <bean class="interceptor.HandlerInterceptor2"/>
    </mvc:interceptor>
</mvc:interceptors>
```
