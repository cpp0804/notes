package algorithms.jianzhioffer;

/**
 * 输入一个递增数组的旋转，输出旋转数组中的最小数字
 * {3,4,5,1,2}是{1,2,3,4,5}的一个旋转，Min=1
 */
public class ReverseArray_11 {
    public static void main(String[] args) {
        int[] nums = new int[]{3, 4, 5, 1, 2};
        System.out.println(reverseMin(nums));

    }

    /**
     * 用二分查找法
     * mid>start:说明这个数字在mid-end之间，start指针移到mid
     * mid<end:说明这个数字在start-mid之间，end指针移到mid
     *
     * @param nums
     * @return
     */
    public static int reverseMin(int[] nums) {
        int start = 0;
        int end = nums.length - 1;
        int mid = start;
        while (start < end) {
            if (end - start == 1) {
                return nums[end];
            }

            mid = (start + end) >> 1;
            if (nums[start] == nums[end] && nums[start] == nums[mid] && nums[end] == nums[mid]) {
                return orderFind(nums, start, end);
            }

            if (nums[mid] >= nums[start]) {
                start = mid;
            } else if (nums[mid] <= nums[end]) {
                end = mid;
            }
        }
        return nums[mid];
    }

    private static int orderFind(int[] nums, int start, int end) {
        int min = nums[start];
        for (int i = start + 1; i <= end; i++) {
            if (nums[i] < min) {
                min = nums[i];
            }
        }

        return min;
    }
}
