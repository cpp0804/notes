package algorithms.jianzhioffer;

/**
 * 斐波那契数列
 * f(n):
 * 0              n=0
 * 1              n=1
 * f(n-1)+f(n-2)  n>1
 */
public class Fabonacci_10 {
    public static void main(String[] args) {
        System.out.println(fabonacci(5));
    }

    public static int fabonacci(int n) {
        if (n <= 0) {
            return 0;
        }

        if (n == 1) {
            return 1;
        }

        int n1 = 0;
        int n2 = 1;
        int result = 0;
        for (int i = 2; i <= n; i++) {
            result = n1 + n2;
            n1 = n2;
            n2 = result;
        }

        return result;
    }
}
