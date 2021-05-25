package algorithms.jianzhioffer;

/**
 * 将股票价格先后存储在数组中，求买买一次的最大利润
 */
public class Stock_63 {
    public static void main(String[] args) {
        int[] nums = new int[]{9, 11, 8, 5, 7, 12, 16, 14};
        maxProfit(nums);
    }

    public static void maxProfit(int[] nums) {
        int max = 0;
        int min = nums[0];
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] < min) {
                min = nums[i];
            } else if (nums[i] - min > max) {
                max = nums[i] - min;
            }
        }
        System.out.println(max);
    }
}
