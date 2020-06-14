package algorithms;

import javax.xml.stream.events.EndDocument;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * 十进制和十六进制的转换
 */
public class Bit {

    private static final char[] array = {'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', 'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'z', 'x', 'c', 'v', 'b', 'n', 'm', '8', '5', '2', '7', '3', '6', '4', '0', '9', '1', 'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P', 'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L', 'Z', 'X', 'C', 'V', 'B', 'N', 'M', '+', '-'};

    public static void main(String[] args) {
        System.out.println(encode(1234.12));
        System.out.println(decode(encode(1234.12)));

    }

    /**
     * 十进制转十六进制
     *
     * @param num
     */
    public static String encode(double num) {
        String[] numString = String.valueOf(num).split("\\.");

        //整数部分转换
        //整数部分不断对64取余直到商为0，余数倒排
        StringBuilder i1 = new StringBuilder(0);
        Stack<Character> stack = new Stack<Character>();
        long i1num = 0;
        if (numString.length < 1) {
            i1num = (long) num;
        } else {
            i1num = Long.parseLong(numString[0]);
        }
        long i1numTemp = i1num;
        while (i1num > 0) {
            //取余
            stack.push(array[(int) (i1num % 64)]);
            i1num = i1num / 64;
        }

        while (!stack.isEmpty()) {
            i1.append(stack.pop());
        }

        String result = i1.toString();

        //小数部分转换
        //小数部分不断乘64直到小数部分为0，取出乘积的整数部分正排
        if (numString.length > 1) {
            StringBuilder i2 = new StringBuilder(0);
            LinkedList queue = new LinkedList();
            double decimal = num - i1numTemp;
            for (int i = 0; i < numString[1].length(); i++) {
                decimal = decimal * 64;

                long temp = getInteger(decimal);
                //整数部分放在队列中
                queue.addLast(array[(int) temp]);

                //小数部分继续乘
                decimal = decimal - temp;
            }

            while (!queue.isEmpty()) {
                i2.append(queue.removeFirst());
            }
            result = result + "." + i2.toString();
        }

        return result;
    }


    /**
     * 十六进制转十进制
     *
     * @param num
     * @return
     */
    public static double decode(String num) {
        String[] numString = String.valueOf(num).split("\\.");

        String num1 = num;
        if (numString.length > 0) {
            num1 = numString[0];
        }

        long r1 = 0;
        int multiple = 1;
        //整数部分从后往前乘64的i-1次方
        for (int i = num1.length() - 1; i >= 0; i--) {
            r1 += decodeCore(num1.charAt(i)) * multiple;
            multiple *= 64;
        }

        double result = r1;
        //小数部分，从前往后乘64的-i次方
        if (numString.length > 0) {
            String num2 = numString[1];
            double n2 = 0;
            double m = 1.0 / 64;
            for (int i = 0; i < num2.length(); i++) {
                n2 += decodeCore(num2.charAt(i)) * m;
                m *= 1.0 / 64;
            }
            result += n2;
        }
        return result;

    }

    //取出整数部分
    public static long getInteger(double decimal) {
        String[] numString = String.valueOf(decimal).split("\\.");
        return Long.parseLong(numString[0]);
    }

    public static int decodeCore(char c) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == c) {
                return i;
            }
        }
        return -1;
    }

}
