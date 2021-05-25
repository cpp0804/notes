package algorithms.sort;

import algorithms.jianzhioffer.LinkNode;

/**
 * 选择排序
 *
 * 在未排序部分中找出最小的数，插入到已排序部分的末尾
 */
public class SelectionSort {
    public static void main(String[] args) {
        int[] nums = new int[]{4, 6, 2, 8, 1, 3, 9};
        selection(nums);
        for (int i = 0; i < nums.length; i++) {
            System.out.print(nums[i] + " ");
        }

        System.out.println();

        LinkNode node9 = new LinkNode(9, null);
        LinkNode node3 = new LinkNode(3, node9);
        LinkNode node1 = new LinkNode(1, node3);
        LinkNode node8 = new LinkNode(8, node1);
        LinkNode node2 = new LinkNode(2, node8);
        LinkNode node6 = new LinkNode(6, node2);
        LinkNode head = new LinkNode(4, node6);

//        bubble(head);
        System.out.println(head);
    }

    public static void selection(int[] nums) {
        for (int i = 0; i < nums.length; i++) {
            int min = i;
            for (int j = i + 1; j < nums.length; j++) {
                if (nums[j] < nums[min]) {
                    min = j;
                }
            }
            int temp = nums[min];
            nums[min] = nums[i];
            nums[i] = temp;
        }
    }
}
