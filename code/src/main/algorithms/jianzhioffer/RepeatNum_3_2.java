package algorithms.jianzhioffer;


/**
 * 不修改数组，找出重复的数字
 * 长度为n+1的数据里所有数字都在1~n，找出任意一个重复的数字
 * {2,3,5,4,3,2,6,7} 重复数字是2或者3
 */
public class RepeatNum_3_2 {
    public static void main(String[] args) {
        int[] nums = new int[]{2, 3, 5, 4, 3, 2, 6, 7};
        findRepeat(nums);
    }

    /**
     * 假设1~n的中位数是m，如果nums中1~m的数字超过一半，那这个重复数字就属于1~m中，否则属于m+1~n
     *
     * @param nums
     */
    public static void findRepeat(int[] nums) {
        int start = 1;
        int end = nums.length - 1;
        while (start <= end) {
            int middle = (start + end) >> 1;
            int count = countNum(nums, start, middle);
            if (start == end) {
                if (count > 1) {
                    System.out.println(start);
                }
                break;
            }
            if (count > middle - start + 1) {
                end = middle;
            } else {
                start = middle + 1;
            }
        }
    }

    public static int countNum(int[] nums, int start, int end) {
        int count = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] >= start && nums[i] <= end) {
                count++;
            }
        }
        return count;
    }

}
