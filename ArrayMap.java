package maps;

import java.util.Iterator;
import java.util.Map;

/**
 * @see AbstractIterableMap
 * @see Map
 */
public class ArrayMap<K, V> extends AbstractIterableMap<K, V> {
    private static final int DEFAULT_INITIAL_CAPACITY = 1000;
    /*
    Warning:
    You may not rename this field or change its type.
    We will be inspecting it in our secret tests.
     */
    SimpleEntry<K, V>[] entries;
    private int numElements;


    /**
     * Constructs a new ArrayMap with default initial capacity.
     */
    public ArrayMap() {
        this(DEFAULT_INITIAL_CAPACITY);
        this.numElements = 0;
    }

    /**
     * Constructs a new ArrayMap with the given initial capacity (i.e., the initial
     * size of the internal array).
     *
     * @param initialCapacity the initial capacity of the ArrayMap. Must be > 0.
     */
    public ArrayMap(int initialCapacity) {
        this.entries = this.createArrayOfEntries(initialCapacity);
        this.numElements = 0;
    }

    /**
     * This method will return a new, empty array of the given size that can contain
     * {@code Entry<K, V>} objects.
     *
     * Note that each element in the array will initially be null.
     *
     * Note: You do not need to modify this method.
     */
    @SuppressWarnings("unchecked")
    private SimpleEntry<K, V>[] createArrayOfEntries(int arraySize) {
        /*
        It turns out that creating arrays of generic objects in Java is complicated due to something
        known as "type erasure."

        We've given you this helper method to help simplify this part of your assignment. Use this
        helper method as appropriate when implementing the rest of this class.

        You are not required to understand how this method works, what type erasure is, or how
        arrays and generics interact.
        */
        return (SimpleEntry<K, V>[]) (new SimpleEntry[arraySize]);
    }

    @Override
    public V get(Object key) {
        if (key != null) {
            for (int i = 0; i < entries.length; i++) {
                if (entries[i] != null && key.equals(entries[i].getKey())) {
                    return entries[i].getValue();
                }
            }
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        if (key != null) {
            for (int i = 0; i < entries.length; i++) {
                if (entries[i] == null) {
                    entries[i] = new SimpleEntry<>(key, value);
                    numElements++;
                    return null;
                } else if (key.equals(entries[i].getKey())) {
                    V val = entries[i].getValue();
                    entries[i] = new SimpleEntry<>(key, value);
                    return val;
                }
            }
        }
        return null;
    }

    @Override
    public V remove(Object key) {
        for (int i = 0; i < entries.length; i++) {
            if (entries[i] != null && key.equals(entries[i].getKey())) {
                V value = entries[i].getValue();
                if (i == entries.length - 1) {
                    entries[i] = null;
                } else {
                    entries[i] = entries[entries.length - 1];
                    entries[entries.length - 1] = null;
                }
                numElements--;
                return value;
            }
        }
        return null;
    }

    @Override
    public void clear() {
        for (int i = 0; i < entries.length; i++) {
            entries[i] = null;
        }
        numElements = 0;
    }

    @Override
    public boolean containsKey(Object key) {
        if (key != null) {
            for (int i = 0; i < entries.length; i++) {
                if (entries[i] != null && key.equals(entries[i].getKey())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int size() {
        return numElements;
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        return new ArrayMapIterator<>(this.entries);
    }

    private static class ArrayMapIterator<K, V> implements Iterator<Map.Entry<K, V>> {
        private final SimpleEntry<K, V>[] entries;
        private int index;

        public ArrayMapIterator(SimpleEntry<K, V>[] entries) {
            this.entries = entries;
            this.index = 0;
        }

        @Override
        public boolean hasNext() {
            while (index < entries.length && entries[index] == null) {
                index++;
            }
            return index < entries.length;
        }

        @Override
        public Map.Entry<K, V> next() {
            if (!hasNext()) {
                throw new java.util.NoSuchElementException();
            }
            return entries[index++];
        }
    }
}
