package algorithms.jianzhioffer;

/**
 * 实现函数 double power(double base, int exponent)
 */
public class Power_16 {

    public static void main(String[] args) {
        System.out.println(power(2, -2));
    }

    public static double power(double base, int exponent) {
        if (base == 0) {
            return 0;
        }

        if (exponent == 0) {
            return 1;
        }

        if (base <= 0 && exponent <= 0) {
            return 0;
        }

        int absExponent = Math.abs(exponent);
        double result = 1;
        for (int i = 1; i <= absExponent; i++) {
            result *= base;
        }

        if (exponent < 0) {
            result = 1.0 / result;
        }

        return result;
    }
}
