package SCJP;

public class SwitchTest {
    public static void main(String[] args) {
        int a = 1;
        int b = 2;
        int x = 1;
        switch (x) {
            case 1:
                System.out.println("a");
            case 2:
                System.out.println("b");
        }


        Color c = Color.green;
        switch (c) {
            case red:
                System.out.print("red ");
            case green:
                System.out.print("green ");
            case blue:
                System.out.print("blue ");
            default:
                System.out.println("done");
        }

        int m = 1;
        switch (m) {
            case 1:
                System.out.println("x is one");
            case 2:
                System.out.println("x is two");
            case 3:
                System.out.println("x is three");
        }
        System.out.println("out of the switch");

    }

    enum Color {red, green, blue}
}
