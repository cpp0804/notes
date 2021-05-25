package algorithms.jianzhioffer;

/**
 * 一个数组中除了两个数字，其他数字都出现了2次，找出这两个数字
 *
 * {2, 4, 3, 6, 3, 2, 5, 5}  =>  4, 6
 */
public class AppearOneNum_56_1 {
    public static void main(String[] args) {
        int[] nums = new int[]{2, 4, 3, 6, 3, 2, 5, 5};
        appearOnce(nums);
    }

    public static void appearOnce(int[] nums) {
        int xor = 0;
        for (int i = 0; i < nums.length; i++) {
            xor ^= nums[i];
        }

        int firstOne = findFirstOne(xor);
        int num1 = 0;
        int num2 = 0;
        for (int i = 0; i< nums.length; i++) {
            if (isOne(nums[i], firstOne)) {
                num1 ^= nums[i];
            } else {
                num2 ^= nums[i];
            }
        }
        System.out.println("num1= " + num1 + ", num2= " + num2);

    }

    public static int findFirstOne(int num) {
        int index = 0;
        while ((num & 1) == 0) {
            num = num >>> 1;
            index++;
        }
        return index;
    }

    public static boolean isOne(int num, int index) {
        num = num >>> index;
        return (num & 1) == 1;
    }
}
