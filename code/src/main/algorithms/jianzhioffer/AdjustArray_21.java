package algorithms.jianzhioffer;

/**
 * 调整数组中的顺序，使得奇数位于前半部分，偶数位于后半部分
 * {1,2,3,4,5} => {1,5,3,4,2}
 */
public class AdjustArray_21 {
    public static void main(String[] args) {
        int[] nums = new int[]{1, 2, 3, 4, 5};
        adjust(nums);

        for (int i = 0; i < nums.length; i++) {
            System.out.print(nums[i]);
        }
    }

    public static void adjust(int[] nums) {
        int start = 0;
        int end = nums.length - 1;
        while (start < end) {
            while (start < end && isOdd(nums[start])) {
                start++;
            }

            while (start < end && !isOdd(nums[end])) {
                end--;
            }

            if (start < end) {
                int temp = nums[start];
                nums[start] = nums[end];
                nums[end] = temp;
            }
        }
    }

    public static boolean isOdd(int num) {
        if ((num & 1) == 1) {
            return true;
        }

        return false;
    }
}
