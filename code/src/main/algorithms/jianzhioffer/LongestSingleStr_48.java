package algorithms.jianzhioffer;

/**
 * 从一个字符串中找出一个最长的不包含重复字符的字符串，输出他的长度
 * <p>
 * 字符串中只包含'a'~'z'的字符
 * <p>
 * arabcacfr的最长不重复子串是acfr，长度是4
 */
public class LongestSingleStr_48 {
    public static void main(String[] args) {
        String str = "arabcacfr";
        maxStr(str);

    }

    public static void maxStr(String str) {
        int maxLength = 0;
        int currentLength = 0;
        //记录每个字符在字符串中出现最近一次的下标
        int[] position = new int[26];
        for (int i = 0; i < position.length; i++) {
            position[i] = -1;
        }

        for (int i = 0; i < str.length(); i++) {
            int posi = position[str.charAt(i) - 'a'];
            if (posi == -1 || i - posi > currentLength) {
                currentLength++;
            } else {
                if (currentLength > maxLength) {
                    maxLength = currentLength;
                }
                currentLength = i - posi;
            }
            position[str.charAt(i) - 'a'] = i;
        }

        if (currentLength > maxLength) {
            maxLength = currentLength;
        }
        System.out.println(maxLength);
    }
}
