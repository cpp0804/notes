package algorithms.jianzhioffer;

/**
 * 统计一个数字在排序数组中出现的次数
 * <p>
 * {1,2,3,3,3,3,4,5} 数字3出现了4次
 */
public class NumCount_53_1 {
    public static void main(String[] args) {
        int[] nums = new int[]{1, 2, 3, 3, 3, 3, 4, 5};
        System.out.println(numCount(nums, 3));
    }

    public static int numCount(int[] nums, int num) {
        int start = getFirst(nums, num);
        int end = getLast(nums, num);
        if (start == -1 || end == -1) {
            return -1;
        }

        return end - start + 1;
    }

    public static int getFirst(int[] nums, int num) {
        int start = 0;
        int end = nums.length - 1;
        int mid = (start + end) >> 1;
        while (start <= end) {
            if (nums[mid] == num) {
                if (mid == 0 || nums[mid - 1] != num) {
                    return mid;
                } else {
                    end = mid - 1;
                }
            } else if (nums[mid] > num) {
                end = mid - 1;
            } else {
                start = mid + 1;
            }
            mid = (start + end) >> 1;
        }
        return -1;
    }

    public static int getLast(int[] nums, int num) {
        int start = 0;
        int end = nums.length - 1;
        int mid = (start + end) >> 1;
        while (start <= end) {
            if (nums[mid] == num) {
                if (mid == nums.length - 1 || nums[mid + 1] != num) {
                    return mid;
                } else {
                    start = mid + 1;
                }
            } else if (nums[mid] > num) {
                end = mid - 1;
            } else {
                start = mid + 1;
            }
            mid = (start + end) >> 1;
        }
        return -1;
    }
}
