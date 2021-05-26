package algorithms.sort;

/**
 * 堆排序
 */
public class HeapSort {
    public static void main(String[] args) {
        int[] nums = new int[]{4, 6, 2, 8, 1, 3, 9};
        heap(nums);
        for (int i = 0; i < nums.length; i++) {
            System.out.print(nums[i] + " ");
        }
    }

    public static void heap(int[] nums) {
        for (int i = nums.length / 2 - 1; i >= 0; i--) {
            adjust(nums, i, nums.length - 1);
        }

        for (int i = nums.length - 1; i > 0; i--) {
            int temp = nums[0];
            nums[0] = nums[i];
            nums[i] = temp;
            adjust(nums, 0, i - 1);
        }
    }

    public static void adjust(int[] nums, int start, int end) {
        int i = start;
        int j = 2 * start + 1;
        int temp = nums[i];
        while (j <= end) {
            if (j + 1 <= end && nums[j] < nums[j + 1]) {
                j++;
            }
            if (temp < nums[j]) {
                nums[i] = nums[j];
                i = j;
                j = 2 * j + 1;
            } else {
                break;
            }
        }
        nums[i] = temp;
    }
}
