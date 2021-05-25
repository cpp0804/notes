package algorithms.jianzhioffer;

/**
 * 一个数组中，除了一个数字只出现一次外，其他数字都出现了3次，找出这个数字
 * <p>
 * {1,2,3,1,2,3,4,5,5,5} => 4
 */
public class AppearOneNum_56_2 {
    public static void main(String[] args) {
        int[] nums = new int[]{1, 2, 1, 1, 2, 2, 4, 5, 5, 5};
        System.out.println(appearOnce(nums));
    }

    public static int appearOnce(int[] nums) {
        int[] andSum = new int[32];
        for (int i = 0; i < nums.length; i++) {
            int flag = 1;
            for (int j = 31; j >= 0; j--) {
                int and = nums[i] & flag;
                if (and != 0) {
                    andSum[j] += 1;
                }
                flag <<= 1;
            }
        }

        int result = 0;
        for (int i = 0; i < 32; i++) {
            result = result << 1;
            result += andSum[i] % 3;
        }

        return result;
    }
}
