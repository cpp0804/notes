package algorithms.jianzhioffer;

/**
 * 一个二维数组，每一行从左到右递增，每一列从上到下递增
 * 输入一个整数，判断数组中是否包含这个数字
 * 1 2 8 9
 * 2 4 9 12
 * 4 7 10 13
 * 6 8 11 15
 * <p>
 * 数字7包含，数字5不包含
 */
public class TwoArrayFind_4 {
    public static void main(String[] args) {
        int[][] nums = new int[][]{
                {1, 2, 8, 9},
                {2, 4, 9, 12},
                {4, 7, 10, 13},
                {6, 8, 11, 15}
        };
        int num = 7;
        findNum(nums, num);
    }

    /**
     * 从右上角开始，如果大于num则剔除这一列，如果小于num则剔除这一行
     * @param nums
     * @param num
     */
    public static void findNum(int[][] nums, int num) {
        int row = 0;
        int col = nums[0].length - 1;
        while (row < nums.length && col >= 0) {
            if (nums[row][col] == num) {
                System.out.println("row= " + row + ", col= " + col);
                break;
            } else if (nums[row][col] > num) {
                col--;
            } else if (nums[row][col] < num) {
                row++;
            }
        }
    }
}
