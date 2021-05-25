package algorithms.jianzhioffer;

/**
 * 输入二叉树A和B， 判断B是不是A的子结构
 * 下面B是A的子结构
 * A:
 *          8
 *    8           7
 * 9      2
 *      4   7
 *
 *
 * B:
 *   8
 * 9   2
 */
public class SubTree_26 {
    public static void main(String[] args) {
        BinaryNode node4 = new BinaryNode(4, null, null);
        BinaryNode node7 = new BinaryNode(7, null, null);
        BinaryNode node2 = new BinaryNode(2, node4, node7);
        BinaryNode node9 = new BinaryNode(9, null, null);
        BinaryNode node8 = new BinaryNode(8, node9, node2);
        BinaryNode nodeA7 = new BinaryNode(8, node9, node2);
        BinaryNode headA = new BinaryNode(7, node8, nodeA7);

        BinaryNode nodeB2 = new BinaryNode(2, null, null );
        BinaryNode nodeB9 = new BinaryNode(9, null, null);
        BinaryNode headB = new BinaryNode(8, nodeB9, nodeB2);

        System.out.println(isSub(headA, headB));
    }

    public static boolean isSub(BinaryNode head1, BinaryNode head2) {
        if (head2 == null) {
            return true;
        }

        if (head1 == null) {
            return false;
        }

        if (head1.getValue() == head2.getValue()) {
            return isSub(head1.getLeft(), head2.getLeft()) && isSub(head1.getRight(), head2.getRight());
        } else {
            return isSub(head1.getLeft(), head2) || isSub(head1.getRight(), head2);
        }
    }
}
