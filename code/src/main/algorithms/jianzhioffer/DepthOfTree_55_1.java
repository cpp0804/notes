package algorithms.jianzhioffer;

/**
 * 求二叉树的深度
 *
 *          1
 *     2         3
 * 4      5        6
 *      7
 */
public class DepthOfTree_55_1 {
    public static void main(String[] args) {
        BinaryNode node11 = new BinaryNode(6, null, null);
        BinaryNode node9 = new BinaryNode(7, null, null);
        BinaryNode node7 = new BinaryNode(5, node9, null);
        BinaryNode node5 = new BinaryNode(4, null, null);
        BinaryNode node10 = new BinaryNode(3, null, node11);
        BinaryNode node6 = new BinaryNode(2, node5, node7);
        BinaryNode headA = new BinaryNode(1, node6, node10);

        System.out.println(depth(headA));
    }

    public static int depth(BinaryNode head) {
        if (head == null) {
            return 0;
        }

        return Math.max(depth(head.getLeft()), depth(head.getRight())) + 1;
    }
}
