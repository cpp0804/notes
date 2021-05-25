package algorithms.sort;

import algorithms.jianzhioffer.LinkNode;

/**
 * 冒泡排序
 * <p>
 * 前后两两比较，每次都将最大的数冒泡到未排序部分的最后一个位子
 */
public class BubbleSort {
    public static void main(String[] args) {
        int[] nums = new int[]{4, 6, 2, 8, 1, 3, 9};
        bubble(nums);
        for (int i = 0; i < nums.length; i++) {
            System.out.print(nums[i] + " ");
        }

        LinkNode node9 = new LinkNode(9, null);
        LinkNode node3 = new LinkNode(3, node9);
        LinkNode node1 = new LinkNode(1, node3);
        LinkNode node8 = new LinkNode(8, node1);
        LinkNode node2 = new LinkNode(2, node8);
        LinkNode node6 = new LinkNode(6, node2);
        LinkNode head = new LinkNode(4, node6);

        bubble(head);
        System.out.println(head);

    }

    public static void bubble(int[] nums) {
        for (int i = nums.length - 1; i > 0; i--) {
            boolean flag = false;
            for (int j = 0; j < i; j++) {
                if (nums[j] > nums[j + 1]) {
                    int temp = nums[j + 1];
                    nums[j + 1] = nums[j];
                    nums[j] = temp;
                    flag = true;
                }
            }
            if (!flag) {
                break;
            }

        }
    }

    public static void bubble(LinkNode head) {
        if (head == null) {
            return;
        }

        LinkNode node = head;
        int length = 0;
        while (node != null) {
            length++;
            node = node.getNext();
        }

        node = head;
        LinkNode last = null;
        for (int i = 0; i < length; i++) {
            while (node.getNext() != last) {
                if (node.getValue() > node.getNext().getValue()) {
                    int temp = node.getValue();
                    node.setValue(node.getNext().getValue());
                    node.getNext().setValue(temp);
                }
                node = node.getNext();
            }
            if (node.getNext() == last) {
                last = node;
                node = head;
            }

        }

    }
}
