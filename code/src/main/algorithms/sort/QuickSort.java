package algorithms.sort;

/**
 * 快速排序
 */
public class QuickSort {
    public static void main(String[] args) {
        int[] nums = new int[]{4, 6, 2, 8, 1, 3, 9};
        quick(nums, 0, nums.length - 1);
        for (int i = 0; i < nums.length; i++) {
            System.out.print(nums[i] + " ");
        }
    }

    public static void quick(int[] nums, int start, int end) {
        if (start >= end) {
            return;
        }

        int partition = partition(nums, start, end);
        quick(nums, start, partition - 1);
        quick(nums, partition + 1, end);
    }

    public static int partition(int[] nums, int start, int end) {
        int i = start;
        int j = end;
        int temp = nums[i];
        while (i < j) {
            while (i < j && nums[j] >= temp) {
                j--;
            }
            if (i < j) {
                nums[i] = nums[j];
                i++;
            }
            while (i < j && nums[i] < temp) {
                i++;
            }
            if (i < j) {
                nums[j] = nums[i];
                j--;
            }
        }
        nums[i] = temp;
        return i;
    }
}
