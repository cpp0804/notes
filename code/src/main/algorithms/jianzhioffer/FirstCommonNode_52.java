package algorithms.jianzhioffer;

/**
 * 找出两个链表的第一个公共节点
 *
 * 1->2->3->
 *            6->7
 * 4->5->
 */
public class FirstCommonNode_52 {
    public static void main(String[] args) {
        LinkNode node7 = new LinkNode(7, null);
        LinkNode node6 = new LinkNode(6, node7);
        LinkNode node5 = new LinkNode(5, node6);
        LinkNode node4 = new LinkNode(4, node5);

        LinkNode node3 = new LinkNode(3, node6);
        LinkNode node2 = new LinkNode(2, node3);
        LinkNode node1 = new LinkNode(1, node2);

        LinkNode common = getFirstCommon(node1, node4);
        System.out.println(common.getValue());
    }

    public static LinkNode getFirstCommon(LinkNode head1, LinkNode head2) {
        int head1Long = count(head1);
        int head2Long = count(head2);
        int diff = Math.abs(head1Long - head2Long);

        LinkNode headLong = head1;
        LinkNode headShort = head2;

        if (head1Long < head2Long) {
            headLong = head2;
            headShort = head1;
        }

        LinkNode first = headLong;
        for (int i = 0; i < diff; i++) {
            first = first.getNext();
        }

        LinkNode second = headShort;
        while (first != null && second != null) {
            if (first == second) {
                return first;
            }

            first = first.getNext();
            second = second.getNext();
        }
        return null;
    }

    public static int count(LinkNode head) {
        LinkNode node = head;
        int count = 0;
        while (node != null) {
            count++;
            node = node.getNext();
        }
        return count;
    }
}
