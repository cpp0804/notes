package algorithms.jianzhioffer;

/**
 * 如果一个链表中有环，找出环的入口节点
 * <p>
 * 1 2 3 4 5 6中 3 4 5 6构成环，入口节点是3
 */
public class CircleKNode_23 {
    public static void main(String[] args) {
        LinkNode e = new LinkNode(6, null);
        LinkNode d = new LinkNode(5, e);
        LinkNode c = new LinkNode(4, d);
        LinkNode b = new LinkNode(3, c);
        e.setNext(b);
        LinkNode a = new LinkNode(2, b);
        LinkNode head = new LinkNode(1, a);

        System.out.println(getNode(head).getValue());

    }

    /**
     * 假如环中有m个节点，那么当第一个节点先走m步，第二个节点再开始走，他们相遇的地方就是入口节点
     * 判断是否有环：快指针走2步，满指针走1步，相遇就有环
     * 判断环中有几个节点：快慢指针相遇的节点继续走，边走边计数，直到再次走到这个节点
     *
     * @return
     */
    public static LinkNode getNode(LinkNode head) {
        LinkNode encounter = getEncounterNode(head);
        if (encounter == null) {
            return null;
        }

        int circleNum = getCircleNum(encounter);

        LinkNode first = head;
        for (int i = 0; i < circleNum; i++) {
            first = first.getNext();
        }

        LinkNode second = head;
        while (first != second) {
            first = first.getNext();
            second = second.getNext();
        }

        return second;

    }

    public static LinkNode getEncounterNode(LinkNode node) {
        LinkNode first = node;
        LinkNode second = node;
        while (first != null && first.getNext() != null) {
            first = first.getNext().getNext();
            second = second.getNext();

            if (first == second) {
                return first;
            }
        }

        return null;
    }

    public static int getCircleNum(LinkNode node) {
        LinkNode current = node;
        int count = 1;
        while (current.getNext() != node) {
            current = current.getNext();
            count++;
        }

        return count;
    }
}
