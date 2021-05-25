package algorithms.jianzhioffer;

import com.sun.org.apache.regexp.internal.RE;

import java.util.ArrayList;

/**
 * 序列化和反序列化二叉树
 * <p>
 *        1
 *   2        3
 * 4        5   6
 */
public class DeserializeTree_37 {

    public static void main(String[] args) {
        BinaryNode node12 = new BinaryNode(6, null, null);
        BinaryNode node7 = new BinaryNode(5, null, null);
        BinaryNode node5 = new BinaryNode(4, null, null);
        BinaryNode node10 = new BinaryNode(3, node7, node12);
        BinaryNode node6 = new BinaryNode(2, node5, null);
        BinaryNode headA = new BinaryNode(1, node6, node10);

        ArrayList<Integer> data = new ArrayList<>();
        serialize(headA, data);
        for (int i = 0; i < data.size(); i++) {
            System.out.print(data.get(i) + " ");
        }

        System.out.println();

        BinaryNode newHead = deserialize(data);
        System.out.println(newHead);
    }

    public static void serialize(BinaryNode head, ArrayList<Integer> data) {
        if (head == null) {
            return;
        }

        data.add(head.getValue());
        if (head.getLeft() != null) {
            serialize(head.getLeft(), data);
        } else {
            data.add(-1);
        }

        if (head.getRight() != null) {
            serialize(head.getRight(), data);
        } else {
            data.add(-1);
        }
    }

    static Integer index = 0;

    public static BinaryNode deserialize(ArrayList<Integer> data) {
        if (index >= data.size()) {
            return null;
        }

        if (data.get(index) == -1) {
            index++;
            return null;
        }

        BinaryNode head = new BinaryNode(data.get(index), null, null);
        index++;

        head.setLeft(deserialize(data));
        head.setRight(deserialize(data));

        return head;
    }
}