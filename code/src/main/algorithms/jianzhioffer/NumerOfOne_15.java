package algorithms.jianzhioffer;

/**
 * 输入一个整数，输出他的二进制表示中1的个数
 * <p>
 * 9:1001 1的个数是2
 */
public class NumerOfOne_15 {
    public static void main(String[] args) {
        System.out.println(numberOfOne(9));
        System.out.println(numberOfOne2(9));
    }

    /**
     * (n-1) & n将二进制中最后一位1变成0
     *
     * @param num
     * @return
     */
    public static int numberOfOne(int num) {
        int count = 0;
        while (num > 0) {
            count++;
            num = (num - 1) & num;
        }
        return count;
    }

    /**
     * 将flag为左移，不断与运算
     * 循环次数的整数二进制的位数(flag的位数)
     *
     * @param num
     * @return
     */
    public static int numberOfOne2(int num) {
        int flag = 1;
        int count = 0;
        while (flag > 0) {
            if ((flag & num) > 0) {
                count++;
            }
            flag = flag << 1;
        }

        return count;
    }
}
