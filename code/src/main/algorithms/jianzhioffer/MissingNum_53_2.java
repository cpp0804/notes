package algorithms.jianzhioffer;

/**
 * 有一个长度为n-1的递增排序数组，每个数字范围是0~n-1,数组中所有数字都是唯一的
 * 0~n-1这n个数字中有且仅有一个数字不在数组中，找出这个数字
 */
public class MissingNum_53_2 {
    public static void main(String[] args) {
        int[] nums = new int[]{0, 1, 3, 4, 5};
        System.out.println(findMissingNum(nums));
    }

    public static int findMissingNum(int[] nums) {
        int start = 0;
        int end = nums.length - 1;
        int mid = (start + end) >> 1;
        while (start <= end) {
            if (nums[mid] == mid) {
                start = mid + 1;
            } else {
                if (mid == 0 || nums[mid - 1] == mid - 1) {
                    return mid;
                } else {
                    end = mid - 1;
                }
            }
            mid = (start + end) >> 1;
        }
        if (start == nums.length) {
            return nums.length;
        }

        return -1;
    }
}
