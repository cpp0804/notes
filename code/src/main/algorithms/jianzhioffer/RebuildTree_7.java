package algorithms.jianzhioffer;


/**
 * 输入二叉树的前序和中序遍历（各自不包含重复数字），重建该二叉树
 * 前序{1,2,4,7,3,5,6,8}
 * 中序{4,7,2,1,5,3,8,6}
 * 结果
 *        1
 *   2        3
 * 4        5   6
 *   7        8
 */
public class RebuildTree_7 {
    public static void main(String[] args) {
        int[] pre = {1, 2, 4, 7, 3, 5, 6, 8};
        int[] in = {4, 7, 2, 1, 5, 3, 8, 6};

        BinaryNode root = rebuild(pre, in, 0, pre.length - 1, 0, in.length - 1);
        System.out.println(root);
    }

    /**
     * 前序的第一个数字是根节点，可以根据根节点将中序分成左右子树两个部分，然后再递归的进行
     *
     * @param pre
     * @param in
     */
    public static BinaryNode rebuild(int[] pre, int[] in, int preStart, int preEnd, int inStart, int inEnd) {
        if (preStart > preEnd && inStart > inEnd) {
            return null;
        }

        BinaryNode root = new BinaryNode(pre[preStart], null, null);

        int rootIn = 0;
        for (int i = inStart; i <= inEnd; i++) {
            if (in[i] == root.getValue()) {
                rootIn = i;
                break;
            }
        }

        int preLength = rootIn - inStart;
        int newPreEnd = preStart + preLength;
        if (preLength > 0) {
            root.setLeft(rebuild(pre, in, preStart + 1, newPreEnd, inStart, rootIn - 1));
        }

        if (preLength < preEnd - preStart + 1) {
            root.setRight(rebuild(pre, in, newPreEnd + 1, preEnd, rootIn + 1, inEnd));
        }

        return root;
    }
}
