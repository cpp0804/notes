package algorithms.jianzhioffer;

/**
 * 输入一颗二叉树，输出他的镜像
 *
 *         8                             8
 *   6           10        =>   10              6
 * 5   7       9     11       11    9        7     5
 */
public class MirrorTree_27 {
    public static void main(String[] args) {
        BinaryNode node11 = new BinaryNode(11, null, null);
        BinaryNode node9 = new BinaryNode(9, null, null);
        BinaryNode node7 = new BinaryNode(7, null, null);
        BinaryNode node5 = new BinaryNode(5, null, null);
        BinaryNode node10 = new BinaryNode(10, node9, node11);
        BinaryNode node6 = new BinaryNode(6, node5, node7);
        BinaryNode headA = new BinaryNode(8, node6, node10);

        mirror(headA);
        System.out.println(headA);
    }

    public static void mirror(BinaryNode head) {
        if (head == null || (head.getLeft() == null && head.getRight() == null)) {
            return;
        }

        BinaryNode temp = head.getLeft();
        head.setLeft(head.getRight());
        head.setRight(temp);

        mirror(head.getLeft());
        mirror(head.getRight());
    }
}
