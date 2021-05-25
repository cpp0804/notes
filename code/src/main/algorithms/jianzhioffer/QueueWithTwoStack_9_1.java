package algorithms.jianzhioffer;

import java.util.Stack;

/**
 * 用两个栈实现一个队列
 * <p>
 * 实现appendTail和deleteHead
 */
public class QueueWithTwoStack_9_1 {
    public static void main(String[] args) {
        QueueStack queueStack = new QueueStack();
        queueStack.appendTail(1);
        queueStack.appendTail(2);
        queueStack.appendTail(3);
        queueStack.appendTail(4);

        System.out.println(queueStack.deleteHead());
    }

    private static class QueueStack {
        private Stack<Integer> stack1 = new Stack<>();
        private Stack<Integer> stack2 = new Stack<>();

        public void appendTail(Integer num) {
            stack1.push(num);
        }

        public Integer deleteHead() {
            if (stack2.isEmpty()) {
                while (!stack1.isEmpty()) {
                    stack2.push(stack1.pop());
                }
            }


            if (!stack2.isEmpty()) {
                return stack2.pop();
            }

            return null;
        }
    }
}
