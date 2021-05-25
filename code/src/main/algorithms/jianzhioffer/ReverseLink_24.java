package algorithms.jianzhioffer;

/**
 * 翻转一个链表，输出翻转后的头结点
 *
 */
public class ReverseLink_24 {

    public static void main(String[] args) {
        LinkNode e = new LinkNode(6, null);
        LinkNode d = new LinkNode(5, e);
        LinkNode c = new LinkNode(4, d);
        LinkNode b = new LinkNode(3, c);
        LinkNode a = new LinkNode(2, b);
        LinkNode head = new LinkNode(1, a);

        System.out.println(reverse(head));
    }

    public static LinkNode reverse(LinkNode head) {
        if (head == null || head.getNext() == null) {
            return head;
        }

        LinkNode prev = null;
        LinkNode current = head;
        LinkNode next = null;
        while (current != null) {
            next = current.getNext();
            current.setNext(prev);
            prev = current;
            current = next;
        }

        return prev;
    }
}
