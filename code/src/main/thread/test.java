package thread;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class test {

    private static List<Integer> list = new ArrayList<>();


    public static void main(String[] args) {
        int a[] = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        getSubset(a, 9, 0);


    }

    public static void getSubset(int[] a, int m, int i) {
        List<List<Integer>> resList = new ArrayList<>();
        while (i < a.length) {
            list.add(a[i]);
            if (getsum(list) == m) {
                System.out.println(list);
            }
            i++;
            getSubset(a, m, i);
            list.remove(list.size() - 1);
        }
    }

    private static int getsum(List<Integer> list) {
        int sum = 0;
        Iterator<Integer> iterator = list.iterator();
        while (iterator.hasNext()) {
            sum += iterator.next();
        }
        return sum;
    }
}
