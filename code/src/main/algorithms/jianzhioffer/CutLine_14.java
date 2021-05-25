package algorithms.jianzhioffer;


/**
 * 一根长度为n的绳子，剪成m端，每段长度是k[0], k[1],...,k[n]
 * 求max(k[0]Xk[1]X...Xk[n])
 * n=8时，剪成 2 3 3得到max=18
 */

public class CutLine_14 {
    public static void main(String[] args) {
        System.out.println(cutMax(8));
    }

    public static int cutMax(int line) {
        if (line < 2) {
            return 0;
        }

        if (line == 2) {
            return 1;
        }

        if (line == 3) {
            return 2;
        }

        int[] result = new int[line + 1];
        result[0] = 0;
        result[1] = 1;
        result[2] = 2;
        result[3] = 3;

        for (int i = 4; i <= line; i++) {
            int max = 0;
            for (int j = 1; j <= i / 2; j++) {
                int temp = result[j] * result[i - j];
                if (max < temp) {
                    max = temp;
                }
            }
            result[i] = max;
        }
        return result[line];
    }

}