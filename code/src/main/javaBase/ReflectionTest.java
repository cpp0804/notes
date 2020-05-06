package javaBase;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 测试反射机制Class的使用
 */
public class ReflectionTest {

    public static void main(String[] args) {
        try {
            reflectMain();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void constructorTest() throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Class cs = Class.forName("javaBase.User");

        //第一种：使用newInstance()方法构造，他的本质也是使用了类的无参构造函数来创建对象
        User user = (User) cs.newInstance();


        //第二种：通过class获取类的构造函数，通过构造函数创建对象。好处是可以获取有参的构造函数
        Constructor constructor = cs.getConstructor(int.class, String.class);
        user = (User) constructor.newInstance(1, "pp");

        //可以通过class获取类所有的构造函数
        Constructor[] constructors = cs.getConstructors();
        for (Constructor cons : constructors) {
            //获取有参构造函数中参数的Class对象
            Class[] parameterTypes = cons.getParameterTypes();

            //通过参数的class对象获取参数类型
            for (Class c : parameterTypes) {
                System.out.println(c.getName());
            }
        }
    }

    public static void fieldTest() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, InstantiationException {
        Class cs = Class.forName("javaBase.User");
        User user = (User) cs.newInstance();

        //获取指定成员变量
        //getDeclaredField():获取私有成员变量
        //getField():获取公有成员变量
        Field field = cs.getDeclaredField("id");
        //获取私有变量后，打开其可见权限
        field.setAccessible(true);
        //给指定对象的该变量赋值
        field.setInt(user, 2);
        System.out.println(field.getInt(user));
        System.out.println("--------------");

        //获取全部私有成员变量
        Field[] fields = cs.getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            System.out.println(f.get(user));
        }
    }

    public static void methodTest() throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, NoSuchFieldException, InvocationTargetException {
        Class cs = Class.forName("javaBase.User");
        User user = (User) cs.newInstance();

        Field field = cs.getDeclaredField("id");
        field.setAccessible(true);
        field.setInt(user, 3);

        //获取无参public方法
        Method method = cs.getMethod("getId");
        //调用该方法
        System.out.println(method.invoke(user));
        System.out.println("--------------------");

        //获取有参public方法
        method = cs.getMethod("setId", int.class);
        method.invoke(user, 4);
        System.out.println(user.getId());
        System.out.println("--------------------");

        //获取有参private方法
        method = cs.getDeclaredMethod("setPrivateId", int.class);
        method.setAccessible(true);
        method.invoke(user, 5);
        System.out.println(user.getId());
        System.out.println("--------------------");

        //获取所有方法
        Method[] methods = cs.getMethods();
        for (Method m : methods) {
            System.out.println(m.getName());
            //获取方法的参数列表的Class对象
            Class[] parameterTypes = m.getParameterTypes();
            for (Class c : parameterTypes) {
                System.out.println(c.getName());
            }
            System.out.println("----------------");
        }
    }

    public static void insertStringToIntList() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<Integer> list = new ArrayList<Integer>();
        list.add(3);
        list.add(4);

        //在编译器，泛型生效，插入字符串对象会报错
        //list.add(“ddd”);

        Class cs = list.getClass();
        Method method = cs.getMethod("add", Object.class);
        method.invoke(list, "add");

        System.out.println(list);
    }

    public static void reflectMain() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class cs = Class.forName("javaBase.reflectMain");
        Method method = cs.getMethod("main", String[].class);
        method.invoke(null, (Object) new String[]{"a", "b", "c"});
    }
}
