package algorithms.jianzhioffer;

/**
 * 删除排序链表中的重复节点
 */
public class DeleteRepeatNode_18_2 {
    public static void main(String[] args) {
        LinkNode c = new LinkNode(2, null);
        LinkNode b = new LinkNode(2, c);
        LinkNode a = new LinkNode(2, b);
        LinkNode head = new LinkNode(1, a);

        System.out.println(delete(head));
    }

    public static LinkNode delete(LinkNode head) {
        if (head.getNext() == null) {
            return null;
        }

        LinkNode node = head;
        LinkNode prev = null;
        while (node != null) {
            boolean needDelete = false;
            LinkNode next = node.getNext();
            if (next != null && node.getValue() == next.getValue()) {
                needDelete = true;
            }

            if (needDelete) {
                LinkNode current = node;
                while (current != null && current.getNext() != null && current.getValue() == current.getNext().getValue()) {
                    current = current.getNext();
                }
                if (prev == null) {
                    head = current.getNext();
                    node = head;
                } else {
                    prev.setNext(current.getNext());
                    node = prev.getNext();
                }
            } else {
                prev = node;
                node = node.getNext();
            }
        }
        return head;
    }
}
