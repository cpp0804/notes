package SCJP;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternTest {
    public static void main(String[] args) {
        patternTest1();
    }

    /*
    source: abaaaba
    index:  0123456
    pattern:ab
     */
    public static void patternTest1() {
        Pattern p = Pattern.compile("ab");
        Matcher m = p.matcher("abaaaba");
        boolean b = false;
        while (b = m.find()) {
            System.out.print(m.start() + " ");
        }
    }
}
