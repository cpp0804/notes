package algorithms.jianzhioffer;

import java.util.HashMap;
import java.util.Map;

/**
 * 有一个复杂链表，除了有next指针，还有指向任意节点的指针sibling
 */
public class CopyLink_35 {
    public static void main(String[] args) {
        LinkNode node5 = new LinkNode(5, null);
        LinkNode node4 = new LinkNode(4, node5);
        LinkNode node3 = new LinkNode(3, node4);
        LinkNode node2 = new LinkNode(2, node3);
        LinkNode node1 = new LinkNode(1, node2);

        node1.setSibling(node3);
        node2.setSibling(node5);
        node4.setSibling(node2);

        LinkNode newHead = copy(node1);
        System.out.println(newHead.getNext().getSibling().getValue());
        System.out.println(newHead.getSibling().getValue());
    }

    public static LinkNode copy(LinkNode head) {
        LinkNode copyHead = null;
        LinkNode current = head;
        Map<LinkNode, LinkNode> mapNode = new HashMap<>();
        boolean first = true;
        while (current != null) {
            LinkNode newNode = new LinkNode(current.getValue(), null);
            mapNode.put(current, newNode);
            if (first) {
                copyHead = newNode;
                first = false;
            }
            current = current.getNext();
        }

        for (Map.Entry<LinkNode, LinkNode> entry : mapNode.entrySet()) {
            LinkNode newNode = entry.getValue();
            LinkNode oldNode = entry.getKey();
            newNode.setNext(oldNode.getNext());
            newNode.setSibling(oldNode.getSibling());
        }
        return copyHead;
    }
}
