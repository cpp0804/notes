package algorithms.sort;

public class MergeSort {
    public static void main(String[] args) {
        int[] nums = new int[]{4, 6, 2, 8, 1, 3, 9};
        merge(nums, 0, nums.length - 1);
        for (int i = 0; i < nums.length; i++) {
            System.out.print(nums[i] + " ");
        }
    }

    public static void merge(int[] nums, int start, int end) {
        if (start >= end) {
            return;
        }
        int mid = (start + end) >> 1;
        merge(nums, start, mid);
        merge(nums, mid + 1, end);
        innerMerge(nums, start, end, mid);
    }

    public static void innerMerge(int[] nums, int start, int end, int mid) {
        int index = start;
        int index1 = start;
        int index2 = mid + 1;
        int temp[] = new int[end + 1];

        while (index1 <= mid && index2 <= end) {
            temp[index++] = nums[index1] < nums[index2] ? nums[index1++] : nums[index2++];
        }

        while (index1 <= mid) {
            temp[index++] = nums[index1++];
        }

        while (index2 <= end) {
            temp[index++] = nums[index2++];
        }

        for (int i = start; i < temp.length; i++) {
            nums[i] = temp[i];
        }
    }
}
