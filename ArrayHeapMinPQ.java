package priorityqueues;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @see ExtrinsicMinPQ
 */
public class ArrayHeapMinPQ<T> implements ExtrinsicMinPQ<T> {
    // IMPORTANT: Do not rename these fields or change their visibility.
    // We access these during grading to test your code.
    static final int START_INDEX = 0;
    List<PriorityNode<T>> items;
    private Map<T, Integer> itemIndexes;

    public ArrayHeapMinPQ() {
        items = new ArrayList<>();
        itemIndexes = new HashMap<>();
    }

    // Here's a method stub that may be useful. Feel free to change or remove it, if you wish.
    // You'll probably want to add more helper methods like this one to make your code easier to read.

    /**
     * A helper method for swapping the items at two indices of the array heap.
     */
    private void swap(int a, int b) {
        PriorityNode<T> temp = items.get(a);
        items.set(a, items.get(b));
        items.set(b, temp);
        itemIndexes.put(items.get(a).getItem(), a);
        itemIndexes.put(items.get(b).getItem(), b);
    }

    // Adds an item with the given priority value.
    @Override
    public void add(T item, double priority) {
        // cannot be two copies of the same item --> if item already exists, throw exception
        if (contains(item)) {
            throw new IllegalArgumentException("This item already exists in priority queue");
        }
        // else add new item to the queue
        PriorityNode<T> newNode = new PriorityNode<>(item, priority);
        items.add(newNode);
        int index = items.size() - 1;
        itemIndexes.put(item, index);
        while (index > START_INDEX && items.get((index - 1) / 2).getPriority() > items.get(index).getPriority()) {
            swap(index, (index - 1) / 2);
            index = (index - 1) / 2;
        }
    }

    // Returns true if the PQ contains the given item; false otherwise.
    @Override
    public boolean contains(T item) {
        return itemIndexes.containsKey(item);
    }

    // Returns the item with least-valued priority.
    @Override
    public T peekMin() {
        if (items.isEmpty()) {
            throw new NoSuchElementException("Priority queue is empty.");
        }
        return items.get(START_INDEX).getItem();
    }

    // Removes and returns the item with least-valued priority.
    @Override
    public T removeMin() {
        if (items.isEmpty()) {
            throw new NoSuchElementException("Priority queue is empty.");
        }
        T minItem = items.get(START_INDEX).getItem();
        swap(START_INDEX, items.size() - 1);
        items.remove(items.size() - 1);
        itemIndexes.remove(minItem);
        if (!items.isEmpty()) {
            checkHeap(START_INDEX);
        }
        return minItem;
    }

    // restores the heap after a removal or a priority change
    private void checkHeap(int index) {
        while (2 * index + 1 < items.size()) {
            int j = 2 * index + 1; // Left child
            if (j < items.size() - 1 && items.get(j).getPriority() > items.get(j + 1).getPriority()) {
                j++; // Right child is smaller
            }
            if (items.get(index).getPriority() <= items.get(j).getPriority()) {
                break;
            }
            swap(index, j);
            index = j;
        }
    }

    // Changes the priority of the given item.
    @Override
    public void changePriority(T item, double priority) {
        Integer index = itemIndexes.get(item);
        if (index == null) {
            throw new NoSuchElementException("Item does not exist in the priority queue.");
        }
        PriorityNode<T> node = items.get(index);
        double oldPriority = node.getPriority();
        node.setPriority(priority);
        if (priority < oldPriority) {
            while (index > START_INDEX && items.get((index - 1) / 2).getPriority() > items.get(index).getPriority()) {
                swap(index, (index - 1) / 2);
                index = (index - 1) / 2;
            }
        } else {
            checkHeap(index);
        }
    }

    // Returns the number of items in the PQ.
    @Override
    public int size() {
        return items.size();
    }

    // Returns true if the PQ is empty, false otherwise.
    public boolean isEmpty() {
        return items.isEmpty();
    }
}
