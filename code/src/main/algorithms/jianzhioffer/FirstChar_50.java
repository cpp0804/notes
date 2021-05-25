package algorithms.jianzhioffer;

/**
 * 字符串中第一个只出现一次的字符
 * <p>
 * abaccdeff => b
 */
public class FirstChar_50 {
    public static void main(String[] args) {
        String str = "abaccdeff";
        firstUnrepeate(str);
    }

    /**
     * 扫描2次字符串
     * 第一次：记录每个字符出现的次数
     * 第二次：返回第一个出现次数为1的
     *
     * @param str
     */
    public static void firstUnrepeate(String str) {
        int[] count = new int[256];
        for (int i = 0; i < str.length(); i++) {
            count[str.charAt(i) - 'a']++;
        }

        for (int i = 0; i < str.length(); i++) {
            if (count[str.charAt(i) - 'a'] == 1) {
                System.out.println(str.charAt(i));
                break;
            }
        }
    }

    /**
     * 字符流中第一个只出现一次的数字
     * <p>
     * 用一个数组保存每个字符出现过的下标，如果重复出现就置特殊位。
     * <p>
     * 然后遍历数组，返回下标大于0并且最小的哪一个
     */
    static int[] position = new int[256];

    {
        for (int i = 0; i < 256; i++) {
            position[i] = -1;
        }
    }

    static int index = 0;

    public static void putInStream(char c) {
        if (position[c - 'a'] == -1) {
            position[c - 'a'] = index;
        } else {
            position[c - 'a'] = -2;
        }

        index++;
    }

    public static void getInStream() {
        int minIndex = 0;
        for (int i = 0; i < position.length; i++) {
            if (position[i] >= 0 && minIndex > position[i]) {
                minIndex = position[i];
            }
        }
        char str = (char) (minIndex + 'a');

        System.out.println(str);
    }
}
