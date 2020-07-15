package SCJP;

public class WrapperTest {

    public static void main(String[] args) {
        Integer y = 567;
        Integer x = y;
        System.out.println(y == x);
        y++;
        System.out.println(x + " " + y);
        System.out.println(y == x);

        Integer i1 = 1000;
        Integer i2 = 1000;
        if (i1 != i2) System.out.println("different objects");
        if (i1.equals(i2)) System.out.println("meaningfully equal");

        Integer i3 = 10;
        Integer i4 = 10;
        if(i3 == i4) System.out.println("same object");
        if(i3.equals(i4)) System.out.println("meaningfully equal");

    }
}
