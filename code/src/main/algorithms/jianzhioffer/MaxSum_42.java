package algorithms.jianzhioffer;

/**
 * 输入一个数组，求所有子数组的和的最大值
 * <p>
 * {1,-2,3,10,-4,7,2,-5}的最大子数组和是18 {3,10,-4,7,2}
 */
public class MaxSum_42 {
    public static void main(String[] args) {
        int[] nums = new int[]{1, -2, 3, 10, -4, 7, 2, -5};
        maxSum(nums);
    }

    public static void maxSum(int[] nums) {
        int max = Integer.MIN_VALUE;
        int currentSum = 0;
        for (int i = 0; i < nums.length; i++) {
            if (currentSum <= 0) {
                currentSum = nums[i];
            } else {
                currentSum += nums[i];
            }

            if (max < currentSum) {
                max = currentSum;
            }
        }
        System.out.println(max);
    }
}
