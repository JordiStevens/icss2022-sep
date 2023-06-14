package nl.han.ica.datastructures;

public class HANStack<T> implements IHANStack<T> {

    private final IHANLinkedList<T> hanLinkedList = new HanLinkedList<>();

    @Override
    public void push(T value) {
        hanLinkedList.addFirst(value);
    }

    @Override
    public T pop() {
        T temp = hanLinkedList.get(0);
        hanLinkedList.removeFirst();
        return temp;
    }

    @Override
    public T peek() {
        return hanLinkedList.get(0);
    }
}
