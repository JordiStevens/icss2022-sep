package nl.han.ica.datastructures;

public class HanLinkedList<T> implements IHANLinkedList<T> {

    private final ListNode<T> header = new ListNode<T>(null);

    @Override
    public String toString(){
        return header.getNext().toString();
    }

    public void addFirst(T value){
        ListNode<T> temp = new ListNode<T>(value);
        temp.setNext(header.getNext());
        header.setNext(temp);
    }

    @Override
    public void clear() {
        header.setNext(null);
    }

    public void removeFirst(){
        header.setNext(header.getNext().getNext());
    }

    @Override
    public T getFirst() {
        return header.getNext().getElement();
    }

    @Override
    public int getSize() {
        int size = 0;
        ListNode<T> current = header.getNext();
        while (current != null){
            size++;
            current = current.getNext();
        }
        return size;
    }

    public void insert(int index, T value){
        ListNode<T> temp = new ListNode<T>(value);
        temp.setElement(value);
        ListNode<T> current = header.getNext();
        if(index < 0){
            throw new IndexOutOfBoundsException();
        }
        if(index == 0){
            temp.setNext(header.getNext());
            header.setNext(temp);
        }else if(current != null){
            for(int i = 0; i < index && current.getNext() != null; i++){
                current = current.getNext();
            }
            temp.setNext(current.getNext());
            current.setNext(temp);
        }
    }

    public void delete(int index){
        ListNode<T> current = header.getNext();
        if(index < 0){
            throw new IndexOutOfBoundsException();
        }
        if(header.getNext() != null){
            for(int i = 0; i < index; i++){
                if(current.getNext() == null){
                    throw new IndexOutOfBoundsException();
                }
                current = current.getNext();
            }
            current.setNext(current.getNext().getNext());
        }
    }

    public T get(int index){
        ListNode<T> current;
        if(index < 0){
            return null;
        }
        if(header.getNext() != null){
            current = header.getNext();
            for(int i = 0; i < index; i++){
                if(current.getNext() == null){
                    return null;
                }
                current = current.getNext();
            }

            return current.getElement();
        }
        return null;
    }
}
