package JVM;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * 该类是为了测试语法糖
 */
public class SugarTest {

    public static void main(String[] args) {
        integerTest();
    }

    /**
     * 泛型擦除的重载测试
     */
    public void fanxing(List<String> list){

    }

//    public void fanxing(List<Integer> list){
//
//    }

    /**
     * 自动装箱、拆箱、遍历循环测试
     */
    public void pack(){
        List<Integer> list = Arrays.asList(1,2,3,4);
        int sum = 0;
        for (int i : list) {
            sum += i;
        }
        System.out.println(sum);
    }
    /**
     * 经编译之后的代码
     */
    public void packJavac(){
        List list = Arrays.asList(new Integer[] {
                Integer.valueOf(1),
                Integer.valueOf(2),
                Integer.valueOf(3),
                Integer.valueOf(4)
        });
        int sum = 0;
        for (Iterator localIterator = list.iterator(); localIterator.hasNext();) {
            int i = ((Integer)localIterator.next()).intValue();
            sum += i;
        }
        System.out.println(sum);
    }

    public static void integerTest() {
        Integer a = 1;
        Integer b = 2;
        Integer c = 3;
        Integer d = 3;
        Integer e = 321;
        Integer f = 321;
        Long g = 3L;
        System.out.println(c == d);
        System.out.println(e == f);
        System.out.println(c == (a + b));
        System.out.println(c.equals(a + b));
        System.out.println(g == (a + b));
        System.out.println(g.equals(a + b));
    }

    public void ifTest() {
        if (true) {
            System.out.println("t");
        } else {
            System.out.println("f");
        }
    }
}
