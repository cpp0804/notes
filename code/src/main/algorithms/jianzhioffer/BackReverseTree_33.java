package algorithms.jianzhioffer;

/**
 * 判断一个数组是不是某个二叉搜索树的后序遍历结果
 * <p>
 *        8
 *   6        10
 * 5   7    9    11
 * <p>
 * {5, 7, 6, 9, 11, 10, 8}
 */
public class BackReverseTree_33 {
    public static void main(String[] args) {
        int[] nums = new int[]{5, 7, 6, 9, 11, 10, 8};

        BinaryNode node11 = new BinaryNode(11, null, null);
        BinaryNode node9 = new BinaryNode(9, null, null);
        BinaryNode node7 = new BinaryNode(7, null, null);
        BinaryNode node5 = new BinaryNode(5, null, null);
        BinaryNode node10 = new BinaryNode(10, node9, node11);
        BinaryNode node6 = new BinaryNode(6, node5, node7);
        BinaryNode headA = new BinaryNode(8, node6, node10);

        System.out.println(check(nums, headA, 0, nums.length - 1));
    }

    public static boolean check(int[] nums, BinaryNode tree, int start, int end) {
        if (start >= end || tree == null) {
            return true;
        }

        int root = nums[end];
        if (root != tree.getValue()) {
            return false;
        }

        int leftEnd = start;
        while (leftEnd <= end) {
            if (nums[leftEnd] > root) {
                break;
            }
            leftEnd++;
        }

        int rightStart = leftEnd;
        while (rightStart <= end) {
            if (nums[rightStart++] < root) {
                return false;
            }
        }

        boolean result = true;
        if (leftEnd - 1 >= start) {
            result = check(nums, tree.getLeft(), start, leftEnd - 1);
        }

        if (leftEnd < end) {
            result = result && check(nums, tree.getRight(), leftEnd, end - 1);
        }

        return result;
    }
}
