package algorithms.treetraverse;

import algorithms.jianzhioffer.BinaryNode;

import java.util.Stack;

/**
 * 中序遍历
 *          1
 *     2         3
 * 4      5        6
 *     7
 */
public class InTraverse {
    public static void main(String[] args) {
        BinaryNode node11 = new BinaryNode(6, null, null);
        BinaryNode node9 = new BinaryNode(7, null, null);
        BinaryNode node7 = new BinaryNode(5, node9, null);
        BinaryNode node5 = new BinaryNode(4, null, null);
        BinaryNode node10 = new BinaryNode(3, null, node11);
        BinaryNode node6 = new BinaryNode(2, node5, node7);
        BinaryNode headA = new BinaryNode(1, node6, node10);

        in(headA);

        System.out.println();

        inNonRecurtion(headA);
    }

    public static void in(BinaryNode head) {
        if (head == null) {
            return;
        }

        in(head.getLeft());
        System.out.print(head.getValue() + " ");
        in(head.getRight());
    }

    public static void inNonRecurtion(BinaryNode head) {
        Stack<BinaryNode> stack = new Stack<>();
        BinaryNode node = head;
        while (node != null || !stack.isEmpty()) {
            if (node != null) {
                stack.push(node);
                node = node.getLeft();
            } else {
                node = stack.pop();
                System.out.print(node.getValue() + " ");
                node = node.getRight();
            }
        }
    }

}
