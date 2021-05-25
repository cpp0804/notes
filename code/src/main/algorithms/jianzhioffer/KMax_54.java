package algorithms.jianzhioffer;

/**
 * 找出一颗二叉搜索树中第k大的节点
 * <p>
 *       5
 *   3          7        =>第3大：4
 * 2  4       6   8
 */
public class KMax_54 {
    public static void main(String[] args) {
        BinaryNode node11 = new BinaryNode(8, null, null);
        BinaryNode node9 = new BinaryNode(6, null, null);
        BinaryNode node7 = new BinaryNode(4, null, null);
        BinaryNode node5 = new BinaryNode(2, null, null);
        BinaryNode node10 = new BinaryNode(7, node9, node11);
        BinaryNode node6 = new BinaryNode(3, node5, node7);
        BinaryNode headA = new BinaryNode(5, node6, node10);

        System.out.println(kMax(headA, 3).getValue());
    }

    static int current = 0;

    public static BinaryNode kMax(BinaryNode head, int k) {
        BinaryNode target = null;

        if (head.getLeft() != null) {
            target = kMax(head.getLeft(), k);
        }

        if (target == null) {
            current++;
            if (current == k) {
                target = head;
            }
        }


        if (target == null && head.getRight() != null) {
            target = kMax(head.getRight(), k);
        }

        return target;
    }
}
