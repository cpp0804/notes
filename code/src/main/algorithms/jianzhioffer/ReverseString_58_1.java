package algorithms.jianzhioffer;

/**
 * 翻转字符串
 * I am a student.  =>  student. a am I
 */
public class ReverseString_58_1 {
    public static void main(String[] args) {
        reverse("I am a student.");
    }

    public static void reverse(String str) {
        char[] strChar = str.toCharArray();
        innerReverse(strChar);
        String newStr = String.valueOf(strChar);

        String[] strArray = newStr.split(" ");
        String result = "";
        for (int i = 0; i < strArray.length; i++) {
            char[] chars = strArray[i].toCharArray();
            innerReverse(chars);
            result = result.concat(" ").concat(String.valueOf(chars));
        }
        System.out.println(result);
    }

    public static void innerReverse(char[] str) {
        int start = 0;
        int end = str.length - 1;
        while (start < end) {
            char temp = str[start];
            str[start] = str[end];
            str[end] = temp;
            start++;
            end--;
        }
    }
}
