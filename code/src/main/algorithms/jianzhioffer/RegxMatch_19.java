package algorithms.jianzhioffer;

/**
 * 匹配包含'.'和'*'的正则表达式
 * '.'表示任意一个字符
 * '*'表示它前面的字符可以出现0~n次
 * 例如字符串"aaa"匹配模式串"a.a"和"ab*ac*a",不匹配"ab*a"
 */
public class RegxMatch_19 {
    public static void main(String[] args) {
        System.out.println(match("aaa", "a.a", 0, 0));
        System.out.println(match("aaa", "ab*ac*a", 0, 0));
        System.out.println(match("aaa", "ab*a", 0, 0));

    }

    public static boolean match(String str, String pattern, int strStart, int patternStart) {
        if (strStart == str.length() || patternStart == pattern.length()) {
            return true;
        }

        if (pattern.charAt(strStart + 1) != '*') {
            if (pattern.charAt(patternStart) == '.' || pattern.charAt(patternStart) == str.charAt(strStart)) {
                return match(str, pattern, strStart + 1, patternStart + 1);
            }
        } else {
            if (pattern.charAt(patternStart) == '.' || pattern.charAt(patternStart) == str.charAt(strStart)) {
                return match(str, pattern, strStart, patternStart + 1) ||
                        match(str, pattern, strStart + 1, patternStart + 2) ||
                        match(str, pattern, strStart + 1, patternStart);
            } else {
                match(str, pattern, strStart, patternStart + 2);
            }
        }
        return true;
    }
}
