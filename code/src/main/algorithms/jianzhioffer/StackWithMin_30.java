package algorithms.jianzhioffer;

import java.util.Stack;

/**
 * 定义栈的数据结构，能实现的到栈的最小值的函数
 */
public class StackWithMin_30 {
    public static void main(String[] args) {

    }

    private static class StackWithMin {
        private Stack<Integer> data = new Stack<>();
        private Stack<Integer> min = new Stack<>();

        public void push(Integer num) {
            data.push(num);
            if (min.isEmpty() || min.peek() > num) {
                min.push(num);
            } else {
                min.push(min.peek());
            }
        }

        public Integer pop() {
            Integer num = data.pop();
            min.pop();
            return num;
        }

        public Integer min() {
            if (min.isEmpty()) {
                return null;
            }

            return min.peek();
        }
    }
}
