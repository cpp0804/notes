package algorithms.jianzhioffer;

/**
 * 找出一个数组中最小的k个数
 * {4,5,1,6,2,7,3,8}中最小的4个数字是1,2,3,4
 */
public class MinK_40 {
    public static void main(String[] args) {
        int[] nums = new int[]{4,5,1,6,2,7,3,8};

        minK(nums, 4);
    }

    public static void minK(int[] nums, int k) {
        int start = 0;
        int end = nums.length - 1;
        int index = k - 1;
        int partition = partition(nums, start, end);
        while (index != partition) {
            if (index > partition) {
                start = partition + 1;
                partition = partition(nums, start, end);
            } else {
                end = partition - 1;
                partition = partition(nums, start, end);
            }
        }

        for (int i = 0; i < k; i++) {
            System.out.print(nums[i] + " ");
        }
    }

    public static int partition(int[] nums, int start, int end) {
        int temp = nums[start];
        while (start < end) {
            while (start < end && nums[end] > temp) {
                end--;
            }
            if (start < end) {
                nums[start++] = nums[end];
            }
            while (start < end && nums[start] <= temp) {
                start++;
            }
            if (start < end) {
                nums[end--] = nums[start];
            }
        }
        nums[start] = temp;
        return start;
    }
}
