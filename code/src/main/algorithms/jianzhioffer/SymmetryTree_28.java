package algorithms.jianzhioffer;

/**
 * 判断一颗二叉树是不是对称的
 *
 * 如果这个二叉树和他的镜像一样就是对称的
 */
public class SymmetryTree_28 {
    public static void main(String[] args) {
        BinaryNode node11 = new BinaryNode(5, null, null);
        BinaryNode node9 = new BinaryNode(7, null, null);
        BinaryNode node7 = new BinaryNode(7, null, null);
        BinaryNode node5 = new BinaryNode(5, null, null);
        BinaryNode node10 = new BinaryNode(6, node9, node11);
        BinaryNode node6 = new BinaryNode(6, node5, node7);
        BinaryNode headA = new BinaryNode(8, node6, node10);

        System.out.println(isSymmetry(headA, headA));
    }

    public static boolean isSymmetry(BinaryNode head1, BinaryNode head2) {
        if (head1 == null && head2 == null) {
            return true;
        }

        if (head1 == null || head2 == null) {
            return false;
        }

        if (head1.getValue() != head2.getValue()) {
            return false;
        }

        return isSymmetry(head1.getRight(), head2.getLeft()) && isSymmetry(head1.getLeft(), head2.getRight());
    }
}
