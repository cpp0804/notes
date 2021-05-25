package algorithms.jianzhioffer;

public class LinkNode {

    private int value;

    private LinkNode next;
    private LinkNode sibling;
    private LinkNode prev;

    public LinkNode(int value, LinkNode next) {
        this.value = value;
        this.next = next;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public LinkNode getNext() {
        return next;
    }

    public void setNext(LinkNode next) {
        this.next = next;
    }

    public LinkNode getSibling() {
        return sibling;
    }

    public void setSibling(LinkNode sibling) {
        this.sibling = sibling;
    }

    public LinkNode getPrev() {
        return prev;
    }

    public void setPrev(LinkNode prev) {
        this.prev = prev;
    }


    @Override
    public String toString() {
        return "LinkNode{" +
                "value=" + value +
                ", next=" + next +
                ", sibling=" + sibling +
                ", prev=" + prev +
                '}';
    }
}
