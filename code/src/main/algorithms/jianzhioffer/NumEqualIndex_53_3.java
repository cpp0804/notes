package algorithms.jianzhioffer;

/**
 * 在一个单调递增排序的数组中，每个数都是唯一的，找出数组中任意一个数字和下标相等的数
 * <p>
 * {-3,-1,1,3,5} =>数字3
 */
public class NumEqualIndex_53_3 {
    public static void main(String[] args) {
        int[] nums = new int[]{-3, -1, 1, 3, 5};
        System.out.println(numEqualsIndex(nums));
    }

    /**
     * 如果某个数比下标大，那他后面的数字一定比下标大
     *
     * @param nums
     * @return
     */
    public static int numEqualsIndex(int[] nums) {
        int start = 0;
        int end = nums.length - 1;
        int mid = (start + end) >> 1;
        while (start <= end) {
            if (nums[mid] == mid) {
                return nums[mid];
            } else {
                if (nums[mid] > mid) {
                    end = mid - 1;
                } else {
                    start = mid + 1;
                }
            }
            mid = (start + end) >> 1;
        }
        return -99999;
    }
}
