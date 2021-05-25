package algorithms.jianzhioffer;

/**
 * 给定一颗二叉树和其中一个节点，找出中序遍历序列的下一个节点
 * 树中节点有左右子树的指针，和指向父节点的指针
 * 中序{d,b,h,e,i,a,f,c,g}
 *          a
 *    b            c
 * d     e       f   g
 *     h   i
 */
public class NextBinaryNode_8 {

    public static void main(String[] args) {
        BinaryNode i = new BinaryNode(9, null, null);
        BinaryNode h = new BinaryNode(8, null, null);
        BinaryNode e = new BinaryNode(7, h, i);
        i.setParent(e);
        h.setParent(e);
        BinaryNode d = new BinaryNode(6, null, null);
        BinaryNode b = new BinaryNode(5, d, e);
        d.setParent(b);
        e.setParent(b);
        BinaryNode f = new BinaryNode(4, null, null);
        BinaryNode g = new BinaryNode(3, null, null);
        BinaryNode c = new BinaryNode(2, f, g);
        f.setParent(c);
        g.setParent(c);
        BinaryNode a = new BinaryNode(1, b, c);
        b.setParent(a);
        c.setParent(a);

        BinaryNode next = findNext(a, i);
        System.out.println(next.getValue());
    }

    /**
     * 1. 如果该节点有右子树：下一个节点就是右子树中的最左节点
     * 2. 如果该节点无右子树：
     *    2.1 如果该节点是父亲的左子节点：下一个节点就是他父亲
     *    2.2 如果该节点是父亲的右子节点：那就向上沿着父节点直到找到第一个是他父亲的左子的节点
     * @param head
     * @param node
     */
    public static BinaryNode findNext(BinaryNode head, BinaryNode node) {
        if (head == null || node == null) {
            return null;
        }

        if (node.getRight() != null) {
            BinaryNode right = node.getRight();
            while (right.getLeft() != null) {
                right = right.getLeft();
            }
            return right;
        } else {
            BinaryNode current = node;
            BinaryNode parent = node.getParent();
            while (current != null && parent != null && current == parent.getRight()) {
                current = parent;
                parent = parent.getParent();
            }
            return parent;
        }
    }
}
