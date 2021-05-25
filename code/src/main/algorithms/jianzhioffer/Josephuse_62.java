package algorithms.jianzhioffer;

import java.util.ArrayList;
import java.util.List;

/**
 * 0,1,...,n-1这n个数字排成一个圆圈，从0开始，每次剔除第m个数字，求剩下的最后一个数字
 * <p>
 * 0,1,2,3,4,5组成圆圈，从0开始每次剔除第3个，则依次会剔除2,0,4,1，剩下的那个数字是3
 */
public class Josephuse_62 {
    public static void main(String[] args) {
        josephuse(5, 3);
    }

    public static void josephuse(int n, int m) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            list.add(i);
        }

        int current = (m - 1) % n;
        int size = n;
        while (size > 1) {
            list.remove(current);
            current = (current + m - 1) % list.size();
            size--;
        }
        System.out.println(list.get(0));
    }
}
