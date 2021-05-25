package algorithms.jianzhioffer;

import java.util.Iterator;
import java.util.Stack;

/**
 * 输入一棵二叉树和一个整数，打印出二叉树中节点值和为该整数的所有路径
 * 一条路径指的是从根节点开始一直到叶子节点经过的路线
 *
 * 整数：22
 *
 *      10
 *  5        12
 * 4 7
 *
 * 路径：
 * {10, 5, 7}  {10, 12}
 */
public class RootOfSum_34 {
    public static void main(String[] args) {
        BinaryNode node7 = new BinaryNode(7, null, null);
        BinaryNode node5 = new BinaryNode(4, null, null);
        BinaryNode node10 = new BinaryNode(12, null, null);
        BinaryNode node6 = new BinaryNode(5, node5, node7);
        BinaryNode headA = new BinaryNode(10, node6, node10);

        sumRoot(headA, 22, new Stack<>());
    }

    private static int currentSum = 0;
    public static void sumRoot(BinaryNode head, int sum, Stack<BinaryNode> stack) {
        if (head == null) {
            return;
        }

        stack.push(head);
        currentSum += head.getValue();

        if (currentSum == sum && head.getLeft() == null && head.getRight() == null) {
            Iterator<BinaryNode> iterator = stack.iterator();
            while (iterator.hasNext()) {
                System.out.print(iterator.next().getValue() + " ");
            }
        }

        if (head.getLeft() != null) {
            sumRoot(head.getLeft(), sum, stack);
        }

        if (head.getRight() != null) {
            sumRoot(head.getRight(), sum, stack);
        }

        Integer pop = stack.pop().getValue();
        currentSum -= pop;
        System.out.println();
    }


}
