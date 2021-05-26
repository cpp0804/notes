package algorithms.treetraverse;

import algorithms.jianzhioffer.BinaryNode;

import java.util.Stack;

/**
 * 后序遍历
 *          1
 *     2         3
 * 4      5        6
 *      7
 */
public class postTraverse {
    public static void main(String[] args) {
        BinaryNode node11 = new BinaryNode(6, null, null);
        BinaryNode node9 = new BinaryNode(7, null, null);
        BinaryNode node7 = new BinaryNode(5, node9, null);
        BinaryNode node5 = new BinaryNode(4, null, null);
        BinaryNode node10 = new BinaryNode(3, null, node11);
        BinaryNode node6 = new BinaryNode(2, node5, node7);
        BinaryNode headA = new BinaryNode(1, node6, node10);

        post(headA);

        System.out.println();

        postNonRecurtion(headA);
    }

    public static void post(BinaryNode head) {
        if (head == null) {
            return;
        }

        post(head.getLeft());
        post(head.getRight());
        System.out.print(head.getValue() + " ");
    }

    public static void postNonRecurtion(BinaryNode head) {
        Stack<BinaryNode> stack = new Stack<>();
        stack.push(head);
        while (!stack.isEmpty()) {
            BinaryNode node = stack.pop();
            System.out.print(node.getValue() + " ");
            if (node.getRight() != null) {
                stack.push(node.getRight());
            }

            if (node.getLeft() != null) {
                stack.push(node.getLeft());
            }
        }
    }

}
