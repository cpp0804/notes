package algorithms.jianzhioffer;

/**
 * 判断一个字符串是不是数字
 * "+100" "5e2" "-123" "3.1416" "-1E-16" ".123" "123.45e+6"都是数字
 */
public class IfNumber_20 {

    public static void main(String[] args) {
//        System.out.println(isNumber("+100"));
//        System.out.println(isNumber("123.45e+6"));
        System.out.println(isNumber("12e+5.4"));

    }

    static int start = 0;

    public static boolean isNumber(String num) {
        boolean numeric = check(num);

        if (start < num.length() && num.charAt(start) == '.') {
            start++;
            numeric = numeric || checkUnsigned(num);
        }

        if (start < num.length() && (num.charAt(start) == 'e' || num.charAt(start) == 'E')) {
            start++;
            numeric = numeric && checkUnsigned(num);
        }

        return numeric;
    }

    public static boolean checkUnsigned(String num) {
        int count = start;
        while (start < num.length() && num.charAt(start) >= '0' && num.charAt(start) <= '9') {
            start++;
        }

        return start > count;
    }

    public static boolean check(String num) {
        if (num.charAt(start) == '+' || num.charAt(start) == '-') {
            start++;
        }

        return checkUnsigned(num);

    }
}
