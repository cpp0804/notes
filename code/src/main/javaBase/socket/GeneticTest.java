package javaBase.socket;

import java.util.ArrayList;
import java.util.Random;

/**
 * 泛型测试
 */
public class GeneticTest {

    public static void main(String[] args) {
//        printMsg("111", 222, "aaaa", "2323.4", 55.55);

        String[] strings = {"a", "b", "c"};
        UnaryFunction<String> sameString = identityFunction();
        for (String s : strings) {
            System.out.println(sameString.apply(s));
        }

        Number[] numbers = {1.2, 0, 3L};
        UnaryFunction<Number> sameNumber = identityFunction();
        for (Number n : numbers) {
            System.out.println(sameNumber.apply(n));
        }
    }

    interface UnaryFunction<T> {
        T apply(T arg);
    }

    private static UnaryFunction<Object> IDENTITY_FUNCTION = new UnaryFunction<Object>() {
        @Override
        public Object apply(Object arg) {
            return arg;
        }
    };

    public static <T> UnaryFunction<T> identityFunction() {
        return (UnaryFunction<T>) IDENTITY_FUNCTION;
    }


    public static void instanceOfTest() {
        ArrayList<Integer> integerBox = new ArrayList<>();

        //Compiler Error:
        //Cannot perform instanceof check against
        //parameterized type Box<Integer>.
        //Use the form Box<?> instead since further
        //generic type information will be erased at runtime
        if (integerBox instanceof ArrayList<?>) {

        }
    }

    public static <T> void printMsg(T... args) {
        for (T t : args) {
            System.out.println("泛型测试,t is " + t);
        }
    }


    public interface Generator<T> {
        public T next();

        public void showkey(T genericObj);
    }

    //实现接口的类如果不指定具体的泛型实参，那类本身也必须声明泛型
    class FruitGenerator<T> implements Generator<T> {
        @Override
        public T next() {
            return null;
        }

        public void showkey(T genericObj) {

        }

    }

    public class FruitGenerator2 implements Generator<String> {

        private String[] fruits = new String[]{"Apple", "Banana", "Pear"};

        @Override
        public String next() {
            Random rand = new Random();
            return fruits[rand.nextInt(3)];
        }

        public void showkey(String genericObj) {

        }
    }


}
