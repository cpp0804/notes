package algorithms.jianzhioffer;

/**
 * O(1)时间内删除链表节点
 */
public class DeleteNode_18_1 {
    public static void main(String[] args) {
        LinkNode c = new LinkNode(3, null);
        LinkNode b = new LinkNode(2, c);
        LinkNode a = new LinkNode(1, b);
        LinkNode head = new LinkNode(0, a);

        System.out.println(delete(head, c));
    }

    /**
     * 1. 如果是头结点：删除头结点
     * 2. 如果不是头结点：
     *    2.1 如果是尾结点：顺序遍历
     *    2.2 如果不是尾结点：将node的next的value赋值到node节点，删除next节点
     * @param head
     * @param node
     * @return
     */
    public static LinkNode delete(LinkNode head, LinkNode node) {
        if (head == null || node == null) {
            return null;
        }

        if (node == head) {
            head = head.getNext();
            return head;
        }

        if (node.getNext() != null) {
            node.setValue(node.getNext().getValue());
            node.setNext(node.getNext().getNext());
        } else {
            LinkNode prev = null;
            LinkNode temp = head;
            while (node != temp) {
                prev = temp;
                temp = temp.getNext();
            }
            prev.setNext(null);
        }
        return head;
    }
}
