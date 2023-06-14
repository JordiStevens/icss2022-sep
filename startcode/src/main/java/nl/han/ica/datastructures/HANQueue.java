package nl.han.ica.datastructures;

public class HANQueue<T> implements IHANQueue<T> {

    IHANLinkedList<T> list = new HanLinkedList<>();

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public boolean isEmpty() {
        return list.getSize() == 0;
    }

    @Override
    public void enqueue(T value) {
        list.insert(list.getSize(), value);
    }

    @Override
    public T dequeue() {
        T node = list.getFirst();
        list.removeFirst();
        return node;
    }

    @Override
    public T peek() {
        return list.getFirst();
    }

    @Override
    public int getSize() {
        return list.getSize();
    }
}
