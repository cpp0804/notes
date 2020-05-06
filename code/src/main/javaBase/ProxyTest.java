package javaBase;

/**
 * 测试代理模式
 */
public class ProxyTest {
    public static void main(String[] args) {
        CglibProxy();
    }

    //静态代理测试
    public static void staticProxy() {
        //目标类
        UserDAO target = new UserDAO();
        //代理类
        UserDAOProxy proxy = new UserDAOProxy(target);
        //执行代理类方法，间接调用目标类
        proxy.save();
    }

    //动态代理测试
    public static void dynamicProxy() {
        //目标对象
        UserDAO userDAO = new UserDAO();
        //代理对象
        IUserDAO proxy = (IUserDAO) new DynamicProxyFactory(userDAO).getProxyInstance();

        proxy.save();

        System.out.println(userDAO.getClass());
        System.out.println(proxy.getClass());
    }

    //Cglib测试
    public static void CglibProxy() {
        //目标对象
        UserDAO userDAO = new UserDAO();
        //代理对象
        UserDAO proxy = (UserDAO) new CglibProxyFactory(userDAO).getProxyInstance();
        proxy.save();

        System.out.println(userDAO.getClass());
        System.out.println(proxy.getClass());
    }
}
