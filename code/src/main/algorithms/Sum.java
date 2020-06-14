package algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Sum {
    public static void main(String[] args) {
        int[] nums = new int[]{-1, 1, 2, 3, 4, 5, 6};
        List<Integer> list = new ArrayList<>();
        List<List<Integer>> lists = new ArrayList<>();
        LinkedList<LinkedList<Integer>> ret = getSum(nums, 2, 5, 0);
        for (List<Integer> l : ret) {
            for (Integer i : l) {
                System.out.print(i + " ");
            }
            System.out.println();
        }
    }

    /*
    写一个函数 void foo(arr, m, n)，arr是整数数组，m是个数，n是和，打印所有和为n的 二维数组，注意兼顾性能。
    例子：
    arr 为[-1，1，2，3，4，5，6]
    m=2, n=5时，打印 [  [1，4]，[2，3]，[-1，6] ]
    m=3, n=6时，打印[ [-1，1，6], [-1,2,5], [-1,3,4], [1,2,3] ]
    */
    public static LinkedList<LinkedList<Integer>> getSum(int[] nums, int n, int sum, int index) {
        LinkedList<LinkedList<Integer>> ret = new LinkedList<>();
        LinkedList<Integer> list = new LinkedList<>();
        getSumCore(nums, n, sum, 0, list, ret);
        return ret;
    }

    public static void getSumCore(int[] nums, int n, int sum, int index, LinkedList<Integer> list, List<LinkedList<Integer>> ret) {
        if (index >= nums.length) {
            return;
        }
        list.addLast(nums[index]);
        if (list.size() == n) {
            int listSum = 0;
            for (Integer i : list) {
                listSum += i;
            }
            if (listSum == sum) {
                ret.add(list);
                return;
            }
        } else if (list.size() < n) {
            getSumCore(nums, n, sum, index + 1, new LinkedList<>(list), ret);
        }
        list.removeLast();
        getSumCore(nums, n, sum, index + 1, new LinkedList<>(list), ret);
    }
}
