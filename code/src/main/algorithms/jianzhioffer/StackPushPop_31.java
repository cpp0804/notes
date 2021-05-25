package algorithms.jianzhioffer;

import java.util.Stack;

/**
 * 输入栈的压入顺序，判断另一个数组是不是他的弹出顺序，所有压入栈的数字都不相等
 * <p>
 * 压栈：{1，2，3，4，5}
 * {4,5,3,2,1}是弹出序列，{4,3,5,1,2}不是弹出序列
 */
public class StackPushPop_31 {
    public static void main(String[] args) {
        int[] push = new int[]{1, 2, 3, 4, 5};
        int[] pop = new int[]{4, 5, 3, 2, 1};
        int[] pop2 = new int[]{4, 3, 5, 1, 2};

        System.out.println(check(push, pop));
        System.out.println(check(push, pop2));

    }

    public static boolean check(int[] push, int[] pop) {
        if (push.length < 0 || pop.length < 0) {
            return false;
        }

        Stack<Integer> stack = new Stack<>();

        int popIndex = 0;
        int pushIndex = 0;
        while (popIndex < pop.length) {
            while (stack.isEmpty() || (pop[popIndex] != stack.peek() && pushIndex < push.length)) {
                stack.push(push[pushIndex++]);
            }

            if (stack.peek() != pop[popIndex]) {
                return false;
            }

            stack.pop();
            popIndex++;
        }

        if (stack.isEmpty() && popIndex == pop.length) {
            return true;
        }

        return false;

    }
}
