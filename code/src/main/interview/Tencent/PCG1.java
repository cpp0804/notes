package interview.Tencent;

import java.util.ArrayList;
import java.util.List;

/**
 * 在一个有n个元素的整型数组中，找出符合以下两个条件的所有元素，要求时间复杂度为O(n)：
 * a. 该元素比它前面所有数都大
 * b. 该元素比它后面所有数都小
 */
public class PCG1 {
    public static void main(String[] args) {
        int[] nums = new int[]{1, 3, 4, 9, 10, 11, 12, 13};
        List<Integer> list = findNum(nums);
        for (Integer i : list) {
            System.out.println(i);
        }
    }

    public static List<Integer> findNum(int[] nums) {
        int[] max = new int[nums.length];
        int[] min = new int[nums.length];
        int maxNum = Integer.MIN_VALUE;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] > maxNum) {
                maxNum = nums[i];
            }
            max[i] = maxNum;
        }
        int minNum = Integer.MAX_VALUE;
        for (int i = nums.length - 1; i >= 0; i--) {
            if (nums[i] < minNum) {
                minNum = nums[i];
            }
            min[i] = minNum;
        }
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] >= max[i] && nums[i] <= min[i]) {
                list.add(nums[i]);
            }
        }
        return list;
    }


}
