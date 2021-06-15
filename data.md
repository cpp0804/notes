26、找到搜索二叉树中两个错误的节点。

```java
public class test {
    public static void main(String[] args) {
        TreeNode root7 = new TreeNode(200, null, null);
        TreeNode root6 = new TreeNode(19, null, null );
        TreeNode root5 = new TreeNode(100, null, null);
        TreeNode root4 = new TreeNode(3, null, null);
        TreeNode root2 = new TreeNode(5, root7, root5);

        TreeNode root3 = new TreeNode(20, root6, root2);

        TreeNode root1 = new TreeNode(10, root7, root3);

        error(root1);
    }

    public static void error(TreeNode root) {
        if (root == null) {
            return;
        }

        error(root.getLeft());
        if (root.getLeft() != null && root.getLeft().getValue() > root.getValue()) {
            System.out.println(root.getLeft().getValue());
        }

        if (root.getRight() != null && root.getRight().getValue() < root.getValue()) {
            System.out.println(root.getRight().getValue());
        }
        error(root.getRight());
    }
}

```

27、两个单向链表，返回求和后的链表结构，例如2->3->1->5，和3->6，结果返回2->3->5->1
```java
public class test {
    public static void main(String[] args) {
        TreeNode rootA4 = new TreeNode(5, null, null);
        TreeNode rootA3 = new TreeNode(1, rootA4, null);
        TreeNode rootA2 = new TreeNode(3, rootA3, null);
        TreeNode rootA1 = new TreeNode(2, rootA2, null);

        TreeNode rootB2 = new TreeNode(6, null, null);
        TreeNode rootB1 = new TreeNode(3, rootB2, null);

        TreeNode sum = sum(rootA1, rootB1);
        while (sum != null) {
            System.out.println(sum.getValue());
            sum = sum.getLeft();
        }
    }

    public static TreeNode sum(TreeNode root1, TreeNode root2) {
        Stack<TreeNode> stack1 = new Stack<>();
        Stack<TreeNode> stack2 = new Stack<>();

        TreeNode temp1 = root1;
        TreeNode temp2 = root2;
        while (temp1 != null) {
            stack1.push(temp1);
            temp1 = temp1.getLeft();
        }

        while (temp2 != null) {
            stack2.push(temp2);
            temp2 = temp2.getLeft();
        }

        TreeNode prev = null;
        int extra = 0;
        while (!stack1.isEmpty() && !stack2.isEmpty()) {
            TreeNode n1 = stack1.pop();
            TreeNode n2 = stack2.pop();
            int sum = n1.getValue() + n2.getValue() + extra;
            extra = sum / 10;
            TreeNode node = new TreeNode(sum % 10, prev, null);
            prev = node;
        }

        while (!stack1.isEmpty()) {
            TreeNode n1 = stack1.pop();
            int sum = n1.getValue();
            if (extra != 0) {
                sum = n1.getValue() + extra;
                extra = sum / 10;
            }
            TreeNode node = new TreeNode(sum % 10, prev, null);
            prev = node;
        }

        while (!stack2.isEmpty()) {
            TreeNode n1 = stack2.pop();
            int sum = n1.getValue();
            if (extra != 0) {
                sum = n1.getValue() + extra;
                extra = sum / 10;
            }
            TreeNode node = new TreeNode(sum % 10, prev, null);
            prev = node;
        }

        return prev;
    }
}

```

12. 算法题，反转字符串
```java
public class test {
    public static void main(String[] args) {

        char[] chars = new char[]{'h', 'e', 'l', 'l', 'o'};
        reverse(chars);
        for (int i = 0; i < chars.length; i++) {
            System.out.print(chars[i] + " ");
        }
    }

    public static void reverse(char[] chars) {
        int start = 0;
        int end = chars.length - 1;
        while (start < end) {
            char temp = chars[start];
            chars[start] = chars[end];
            chars[end] = temp;

            start++;
            end--;
        }
    }
}

```

13. 算法题，字符串中大小写字母分成前后两部分，字母顺序不变
```java
public class test {
    public static void main(String[] args) {
        char[] orderChar = new char[] {'a', 'c', 'B', 'D', 'm', 'F', 'G', 'g', 'n'};
        split(orderChar);
    }

    public static void split(char[] chars) {
        LinkedList<Character> linkedList = new LinkedList<>();
        LinkedList<Character> linkedListUpper = new LinkedList<>();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] >= 'A' && chars[i] <= 'Z') {
                linkedListUpper.add(chars[i]);
            } else {
                linkedList.add(chars[i]);
            }
        }

        char[] newChar = new char[chars.length];
        int i = 0;
        while (!linkedList.isEmpty()) {
            newChar[i] = linkedList.removeFirst();
            i++;
        }

        while (!linkedListUpper.isEmpty()) {
            newChar[i] = linkedListUpper.removeFirst();
            i++;
        }

        for (int m = 0; m < newChar.length; m++) {
            System.out.print(newChar[m]);
        }
    }

    public static void split2(char[] chars) {
        int i = chars.length - 1;
        while (i >= 0) {
            if (i >= 0 && chars[i] >= 'a' && chars[i] <= 'z') {
                i--;
                continue;
            }

            for (int j = i + 1; j < chars.length; j++) {
                if (chars[j] >= 'A' && chars[j] <= 'Z') {
                    break;
                }
                char temp = chars[j - 1];
                chars[j - 1] = chars[j];
                chars[j] = temp;
            }
            i--;
        }

        for (int m = 0; m < chars.length; m++) {
            System.out.print(chars[m]);
        }
    }
}
```

十分钟手撕代码：找出给定字符串中所有长度大于等于3的回文。


幂等性怎么确保的？
事务
缓存
DB优化
创建订单的时候存到Redis的是什么？有没有解决过缓存穿透的问题？
RPC 
MySQL锁
设计模式