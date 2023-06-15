package nl.han.ica.datastructures;

public class ListNode<T>  {
    private T value;
    private ListNode<T> next;

    public ListNode(T value){
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T element) {
        this.value = element;
    }

    public  ListNode<T> getNext() {
        return next;
    }

    public void setNext(ListNode<T> next) {
        this.next = next;
    }

    @Override
    public String toString(){
        if(next == null){
            return value.toString();
        } else {
            return value + " " + next.toString();
        }
    }
}
