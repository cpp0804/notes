package algorithms.jianzhioffer;

/**
 * 合并两个递增排序的链表，合并后仍然是递增排序的
 * {1,3,5,7} {2,4,6,8} => {1,2,3,4,5,6,7,8}
 */
public class MergeLink_25 {
    public static void main(String[] args) {
        LinkNode c = new LinkNode(7, null);
        LinkNode b = new LinkNode(5, c);
        LinkNode a = new LinkNode(3, b);
        LinkNode head1 = new LinkNode(1, a);

        LinkNode c2 = new LinkNode(8, null);
        LinkNode b2 = new LinkNode(6, c2);
        LinkNode a2 = new LinkNode(4, b2);
        LinkNode head2 = new LinkNode(2, a2);

        System.out.println(merge(head1, head2));
    }

    public static LinkNode merge(LinkNode head1, LinkNode head2) {
        if (head1 == null) {
            return head2;
        }

        if (head2 == null) {
            return head1;
        }

        LinkNode newHead = null;
        if (head1.getValue() > head2.getValue()) {
            newHead = head2;
            newHead.setNext(merge(head1, head2.getNext()));
        } else {
            newHead = head1;
            newHead.setNext(merge(head1.getNext(), head2));
        }

        return newHead;
    }
}
