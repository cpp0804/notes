package algorithms.jianzhioffer;

import java.util.Stack;

/**
 * 输入一个链表的头结点，从尾到头打印链表
 */
public class PrintLinkTailToHead_6 {
    public static void main(String[] args) {
        LinkNode c = new LinkNode(3, null);
        LinkNode b = new LinkNode(2, c);
        LinkNode a = new LinkNode(1, b);
        LinkNode head = new LinkNode(0, a);

        if (head == null) {
            System.out.println("NONE");
        }

        if (head.getNext() == null) {
            System.out.println(head);
        }

        print(head);
        System.out.println();
        printWithStack(head);

    }

    /**
     * 递归
     * @param head
     */
    public static void print(LinkNode head) {
        if (head == null) {
            return;
        }

        if (head.getNext() != null) {
            print(head.getNext());
        }

        System.out.println(head);
    }

    /**
     * 用栈
     */
    public static void printWithStack(LinkNode head) {
        Stack<LinkNode> stack = new Stack<>();
        LinkNode tempHead = head;
        while (tempHead != null) {
            stack.push(tempHead);
            tempHead = tempHead.getNext();
        }

        while (!stack.isEmpty()) {
            System.out.println(stack.pop());
        }
    }
}
