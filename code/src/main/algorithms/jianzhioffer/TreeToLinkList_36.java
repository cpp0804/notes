package algorithms.jianzhioffer;

/**
 * 将二叉搜索树转换成一个排序的双向链表
 *
 * 不能创建新节点 只能调整树中的节点指向
 *
 *
 *          10
 *    6           14         =>    {4, 6, 8, 10, 12, 14,16}
 *  4   8       12   16
 */
public class TreeToLinkList_36 {

    static BinaryNode prev = null;

    public static void main(String[] args) {

        BinaryNode node16 = new BinaryNode(16, null, null);
        BinaryNode node12 = new BinaryNode(12, null, null);
        BinaryNode node7 = new BinaryNode(8, null, null);
        BinaryNode node5 = new BinaryNode(4, null, null);
        BinaryNode node10 = new BinaryNode(14, node12, node16);
        BinaryNode node6 = new BinaryNode(6, node5, node7);
        BinaryNode headA = new BinaryNode(10, node6, node10);

        transfer(headA);
        BinaryNode copyPrev = prev;
        while (prev.getLeft() != null) {
            prev = prev.getLeft();
        }

        while (prev != null) {
            System.out.print(prev.getValue() + " ");
            prev = prev.getRight();
        }
        System.out.println();
        while (copyPrev != null) {
            System.out.print(copyPrev.getValue() + " ");
            copyPrev = copyPrev.getLeft();
        }
//        System.out.println(prev);
//        System.out.println(prev.getRight().getValue());
//        System.out.println(prev.getRight().getRight().getValue());
//        System.out.println(prev.getRight().getRight().getRight().getValue());
//        System.out.println(prev.getRight().getRight().getRight().getRight().getValue());


    }

    /**
     * 二叉搜索树：左 <= 根 <= 右
     * 中序遍历
     * @param head
     * @return
     */
    public static void transfer(BinaryNode head) {
        if (head == null) {
            return;
        }

        transfer(head.getLeft());

        BinaryNode node = head;
        node.setLeft(prev);
        if (prev != null) {
            prev.setRight(node);
        }
        prev = node;

        transfer(node.getRight());

    }
}
