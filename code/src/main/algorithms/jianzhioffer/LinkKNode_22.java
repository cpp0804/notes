package algorithms.jianzhioffer;

/**
 * 输入一个链表，输出这个链表中的倒数第k个节点
 * 1,2,3,4,5,6 的倒数第三个是4
 */
public class LinkKNode_22 {

    public static void main(String[] args) {
        LinkNode e = new LinkNode(6, null);
        LinkNode d = new LinkNode(5, e);
        LinkNode c = new LinkNode(4, d);
        LinkNode b = new LinkNode(3, c);
        LinkNode a = new LinkNode(2, b);
        LinkNode head = new LinkNode(1, a);

        System.out.println(getK(head, 4));
    }

    /**
     * 倒数第k个，就是正数第n-k+1个
     * 相当于n-(k-1)
     * 第一个节点先走k-1步，第二个再开始走，当地一个节点走到尾结点时，第二个节点的位子就是倒数第k个
     * @param head
     * @return
     */
    public static LinkNode getK(LinkNode head, int k) {
        if (head == null) {
            return null;
        }

        LinkNode first = head;
        for (int i = 1; i < k; i++) {
            first = first.getNext();
        }

        LinkNode second = head;
        while (second != null && first != null && first.getNext() != null) {
            first = first.getNext();
            second = second.getNext();
        }

        return second;

    }
}
