package algorithms.jianzhioffer;

/**
 * 判断一棵树是不是平衡二叉树，任意节点的左右子树深度差值小于等于1
 *          1
 *     2         3
 * 4      5        6
 *      7
 */
public class BalanceTree_55_2 {
    public static void main(String[] args) {
        BinaryNode node11 = new BinaryNode(6, null, null);
        BinaryNode node9 = new BinaryNode(7, null, null);
        BinaryNode node7 = new BinaryNode(5, node9, null);
        BinaryNode node5 = new BinaryNode(4, null, null);
        BinaryNode node10 = new BinaryNode(3, null, node11);
        BinaryNode node6 = new BinaryNode(2, node5, node7);
        BinaryNode headA = new BinaryNode(1, node6, node10);

        System.out.println(balance(headA));
    }

    public static boolean balance(BinaryNode head) {
        if (head == null) {
            return true;
        }
        int left = DepthOfTree_55_1.depth(head.getLeft());
        int right = DepthOfTree_55_1.depth(head.getRight());
        if (Math.abs(left - right) > 1) {
            return false;
        }

        return balance(head.getLeft()) && balance(head.getRight());
    }
}
