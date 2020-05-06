package javaBase;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * 通过Cglib生成代理对象
 */
public class CglibProxyFactory implements MethodInterceptor {

    //维护目标对象
    private Object target;

    public CglibProxyFactory(Object target) {
        this.target = target;
    }

    //创建代理对象
    public Object getProxyInstance() {
        /*
        1.工具类
        Enhancer既能够代理普通的class，也能够代理接口
        Enhancer创建一个代理类，它将作为目标类的子类并且拦截所有的方法调用（包括从Object中继承的toString和hashCode方法）
        */
        Enhancer enhancer = new Enhancer();
        //2. 将目标类作为代理类的父类
        enhancer.setSuperclass(target.getClass());
        //3.设置回调函数
        enhancer.setCallback(this);
        //4.创建代理对象
        return enhancer.create();
    }


    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println("开始事务");
        Object returnValue = method.invoke(target, objects);
        System.out.println("结束事务");
        return returnValue;
    }
}
