package algorithms.jianzhioffer;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * 用两个队列实现一个栈
 * <p>
 * 实现appendTail和deleteHead
 */
public class StackWithTwoQueue_9_2 {
    public static void main(String[] args) {

    }

    private static class StackQueue {
        private Queue<Integer> queue1 = new LinkedList<>();
        private Queue<Integer> queue2 = new LinkedList<>();

        public void appendTail(Integer num) {
            queue1.offer(num);
        }

        /**
         * 两个队列里面倒来倒去
         * @return
         */
        public Integer deleteHead() {
            queue2.clear();
            while (queue1.size() > 1) {
                queue2.offer(queue1.poll());
            }

            return queue1.poll();
        }
    }
}
