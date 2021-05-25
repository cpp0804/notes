package algorithms.jianzhioffer;

/**
 * 将字符串中的空格替换成'%20'
 * we are happy => we%20are%20happy
 */
public class ReplaceSpace_5 {
    public static void main(String[] args) {
        replaceWithFunc("we are happy");
        replace("we are happy");
    }

    /**
     * 直接使用String的replaceAll方法
     *
     * @param str
     */
    public static void replaceWithFunc(String str) {
        String newStr = str.replaceAll(" ", "%20");
        System.out.println(newStr);
    }

    public static void replace(String str) {
        char[] strChar = str.toCharArray();
        int spaceCount = 0;
        for (int i = 0; i < strChar.length; i++) {
            if (strChar[i] == ' ') {
                spaceCount++;
            }
        }
        char[] newStrChar = new char[strChar.length + spaceCount * 2];
        int newStrCharPoint = 0;
        for (int i = 0; i < strChar.length; i++) {
            if (strChar[i] == ' ') {
                newStrChar[newStrCharPoint++] = '%';
                newStrChar[newStrCharPoint++] = '2';
                newStrChar[newStrCharPoint++] = '0';
            } else {
                newStrChar[newStrCharPoint++] = strChar[i];
            }
        }
        for (int i = 0; i < newStrChar.length; i++) {
            System.out.print(newStrChar[i]);
        }
    }

}
