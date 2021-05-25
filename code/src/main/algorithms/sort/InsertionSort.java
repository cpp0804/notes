package algorithms.sort;

/**
 * 插入排序
 * <p>
 * 将未排序的第一个数字插入到已排序部分排序
 */
public class InsertionSort {
    public static void main(String[] args) {
        int[] nums = new int[]{4, 6, 2, 8, 1, 3, 9};
        insertion(nums);
        for (int i = 0; i < nums.length; i++) {
            System.out.print(nums[i] + " ");
        }
    }

    public static void insertion(int[] nums) {
        for (int i = 1; i < nums.length; i++) {
            int temp = nums[i];
            int j = i - 1;
            while (j >= 0 && temp < nums[j]) {
                nums[j + 1] = nums[j];
                j--;
            }
            nums[j + 1] = temp;
        }
    }
}
