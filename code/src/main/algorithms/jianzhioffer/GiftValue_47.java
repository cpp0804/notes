package algorithms.jianzhioffer;

/**
 * 在一个m*n的棋盘中，每一格都有礼物，每个礼物都有价值(大于0)
 * 从棋盘左上角开始拿礼物，可以向左或者向下移动，直到棋盘右下角
 * 求拿到礼物的最大价值
 * <p>
 * 1 10 3 8
 * 12 2 9 6    (1,12,5,7,7,16,5) max=53
 * 5 7 4 11
 * 3 7 16 5
 */
public class GiftValue_47 {
    public static void main(String[] args) {
        int[][] nums = new int[][]{
                {1, 10, 3, 8},
                {12, 2, 9, 6},
                {5, 7, 4, 11},
                {3, 7, 16, 5}
        };

        maxValue(nums);
    }

    public static void maxValue(int[][] nums) {
        int[][] value = new int[nums.length][nums[0].length];
        for (int i = 0; i < nums.length; i++) {
            for (int j = 0; j < nums[0].length; j++) {
                int left = 0;
                int up = 0;

                if (i > 0) {
                    up = value[i - 1][j];
                }

                if (j > 0) {
                    left = value[i][j - 1];
                }
                value[i][j] = Math.max(up, left) + nums[i][j];
            }
        }
        System.out.println(value[nums.length - 1][nums[0].length - 1]);
    }
}
