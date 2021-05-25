package algorithms.jianzhioffer;

/**
 * 输入一个整数s，打印出所有和为s的连续正数序列
 * <p>
 * s=15  =>    1+2+3+4+5=4+5+6+7=7+8
 */
public class SeriesS_57_2 {
    public static void main(String[] args) {
        seriesS(15);
    }

    public static void seriesS(int s) {
        int small = 1;
        int big = 2;
        int mid = s / 2;
        int current = small + big;
        while (small <= mid) {
            if (current == s) {
                print(small, big);
                current -= small;
                small++;
            } else if (current < s) {
                big++;
                current += big;
            } else if (current > s) {
                current -= small;
                small++;
            }
        }
    }

    public static void print(int start, int end) {
        for (int i = start; i <= end; i++) {
            System.out.print(i + ",");
        }
        System.out.println();
    }
}
