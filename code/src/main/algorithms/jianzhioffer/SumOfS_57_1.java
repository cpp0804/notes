package algorithms.jianzhioffer;

/**
 * 输入一个递增排序数组和一个数字s，在数组中任意找2个数，使得他们的和正好是s
 * <p>
 * {1,2,3,4,5,6} s=7  =>  2,5 或3,4
 */
public class SumOfS_57_1 {
    public static void main(String[] args) {
        int nums[] = new int[]{1, 2, 3, 4, 5, 6};
        sumOfS(nums, 7);
    }

    public static void sumOfS(int[] nums, int s) {
        int start = 0;
        int end = nums.length - 1;
        while (start < end) {
            if (nums[start] + nums[end] == s) {
                System.out.println("s1= " + nums[start] + ", s2=" + nums[end]);
                break;
            } else {
                if (nums[start] + nums[end] > s) {
                    end--;
                } else {
                    start++;
                }
            }
        }
    }
}
