package algorithms.jianzhioffer;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class PrintTree_32 {
    public static void main(String[] args) {
        BinaryNode node11 = new BinaryNode(11, null, null);
        BinaryNode node9 = new BinaryNode(9, null, null);
        BinaryNode node7 = new BinaryNode(7, null, null);
        BinaryNode node5 = new BinaryNode(5, null, null);
        BinaryNode node10 = new BinaryNode(10, node9, node11);
        BinaryNode node6 = new BinaryNode(6, node5, node7);
        BinaryNode headA = new BinaryNode(8, node6, node10);

        printWithoutLine(headA);

        System.out.println();
        System.out.println();

        printWithLine(headA);

        System.out.println();
        System.out.println();

        BinaryNode nodeB15 = new BinaryNode(15, null, null);
        BinaryNode nodeB14 = new BinaryNode(14, null, null);
        BinaryNode nodeB13 = new BinaryNode(13, null, null);
        BinaryNode nodeB12 = new BinaryNode(12, null, null);
        BinaryNode nodeB11 = new BinaryNode(11, null, null);
        BinaryNode nodeB10 = new BinaryNode(10, null, null);
        BinaryNode nodeB9 = new BinaryNode(9, null, null);
        BinaryNode nodeB8 = new BinaryNode(8, null, null);
        BinaryNode nodeB7 = new BinaryNode(7, nodeB14, nodeB15);
        BinaryNode nodeB6 = new BinaryNode(6, nodeB12, nodeB13);
        BinaryNode nodeB5 = new BinaryNode(5, nodeB10, nodeB11);
        BinaryNode nodeB4 = new BinaryNode(4, nodeB8, nodeB9);
        BinaryNode nodeB3 = new BinaryNode(3, nodeB6, nodeB7);
        BinaryNode nodeB2 = new BinaryNode(2, nodeB4, nodeB5);
        BinaryNode headB = new BinaryNode(1, nodeB2, nodeB3);

        printWithZ(headB);
    }

    public static void printWithoutLine(BinaryNode head) {
        if (head == null) {
            return;
        }

        Queue<BinaryNode> queue = new LinkedList<>();
        queue.add(head);
        BinaryNode node = null;
        while (!queue.isEmpty()) {
            node = queue.poll();
            System.out.print(node.getValue() + " ");
            if (node.getLeft() != null) {
                queue.add(node.getLeft());
            }
            if (node.getRight() != null) {
                queue.add(node.getRight());
            }
        }
    }

    public static void printWithLine(BinaryNode head) {
        if (head == null) {
            return;
        }

        Queue<BinaryNode> queue = new LinkedList<>();
        queue.add(head);
        BinaryNode node = null;
        int count = 1;
        while (!queue.isEmpty()) {
            node = queue.poll();
            System.out.print(node.getValue() + " ");
            count--;
            if (node.getLeft() != null) {
                queue.add(node.getLeft());
            }
            if (node.getRight() != null) {
                queue.add(node.getRight());
            }

            if (count == 0) {
                System.out.println();
                count = queue.size();
            }
        }
    }

    public static void printWithZ(BinaryNode head) {
        if (head == null) {
            return;
        }

        //奇数行
        Stack<BinaryNode> stack1 = new Stack<>();

        //偶数行
        Stack<BinaryNode> stack2 = new Stack<>();

        int current = 0;
        int next = 1;
        Stack[] stacks = new Stack[2];
        stacks[current] = stack1;
        stacks[next] = stack2;

        stacks[current].push(head);
        while (!stacks[current].isEmpty()) {
            BinaryNode node = (BinaryNode) stacks[current].pop();
            System.out.print(node.getValue() + " ");

            if (current == 0) {
                if (node.getLeft() != null) {
                    stacks[next].push(node.getLeft());
                }
                if (node.getRight() != null) {
                    stacks[next].push(node.getRight());
                }
            } else {
                if (node.getRight() != null) {
                    stacks[next].push(node.getRight());
                }
                if (node.getLeft() != null) {
                    stacks[next].push(node.getLeft());
                }
            }

            if (stacks[current].isEmpty()) {
                current = 1 - current;
                next = 1 - next;
                System.out.println();
            }
        }
    }

}
