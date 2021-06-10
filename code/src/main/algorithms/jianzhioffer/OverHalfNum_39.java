package algorithms.jianzhioffer;

/**
 * 数组中有一个数字出现的次数超过一般，求这个数字
 * <p>
 * {1,2,3,2,2,2,5,4,2} 2出现了5次，超过一半
 */
public class OverHalfNum_39 {
    public static void main(String[] args) {
        int[] nums = new int[]{1, 2, 2, 2, 2, 2, 3, 3, 2};
        overHalf(nums);
//        overHalf2(nums);
    }

    /**
     * 排序后一定处于中间位子
     *
     * @param nums
     * @return
     */
    public static void overHalf(int[] nums) {
        int start = 0;
        int end = nums.length - 1;
        int mid = (start + end) >> 1;
        int partition = partition(nums, start, end);
        while (mid != partition) {
            if (mid > partition) {
                start = partition + 1;
                partition = partition(nums, start, end);
            } else {
                end = partition - 1;
                partition = partition(nums, start, end);
            }
        }
        System.out.println(nums[mid]);
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

    public static void overHalf2(int[] nums) {
        int count = 1;
        int num = nums[0];
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] != num) {
                count--;
            } else {
                count++;
            }

            if (count == 0) {
                num = nums[i];
                count = 1;
            }
        }
        System.out.println(num);
    }

}
