package algorithms.jianzhioffer;

/**
 * 数组中重复的数字
 * 长度为n的数据里所有数字都在0~n-1，找出任意一个重复的数字
 * {2,3,4,0,2,5,3} 重复数字是2或者3
 */
public class RepeatNum_3_1 {
    public static void main(String[] args) {
        int[] nums = new int[]{2, 3, 4, 0, 2, 5, 3};
        findRepeat(nums);
    }

    /**
     * 长度为n，数字在[0, n-1]， 说明数字a位于下标A处
     * 如果a下标不是A，则和nums[A]处的数字作比较
     *
     * @param nums
     */
    public static void findRepeat(int[] nums) {
        for (int i = 0; i < nums.length; i++) {
            while (nums[i] != i) {
                if (nums[i] == nums[nums[i]]) {
                    System.out.println(nums[i]);
                    break;
                } else {
                    int temp = nums[i];
                    nums[i] = nums[temp];
                    nums[temp] = temp;
                }
            }
        }
    }

}
