package maps;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @see AbstractIterableMap
 * @see Map
 */
public class ChainedHashMap<K, V> extends AbstractIterableMap<K, V> {
    private static final double DEFAULT_RESIZING_LOAD_FACTOR_THRESHOLD = 10;
    private static final int DEFAULT_INITIAL_CHAIN_COUNT = 10;
    private static final int DEFAULT_INITIAL_CHAIN_CAPACITY = 10;
    private int numElements;
    private static double threshold;
    private static int chainCapacity;

    /*
    Warning:
    You may not rename this field or change its type.
    We will be inspecting it in our secret tests.
     */
    AbstractIterableMap<K, V>[] chains;

    /**
     * Constructs a new ChainedHashMap with default resizing load factor threshold,
     * default initial chain count, and default initial chain capacity.
     */
    public ChainedHashMap() {
        this(DEFAULT_RESIZING_LOAD_FACTOR_THRESHOLD, DEFAULT_INITIAL_CHAIN_COUNT, DEFAULT_INITIAL_CHAIN_CAPACITY);
        this.numElements = 0;
        this.threshold = DEFAULT_RESIZING_LOAD_FACTOR_THRESHOLD;
        this.chainCapacity = DEFAULT_INITIAL_CHAIN_CAPACITY;
    }

    /**
     * Constructs a new ChainedHashMap with the given parameters.
     *
     * @param resizingLoadFactorThreshold the load factor threshold for resizing. When the load factor
     *                                    exceeds this value, the hash table resizes. Must be > 0.
     * @param initialChainCount the initial number of chains for your hash table. Must be > 0.
     * @param chainInitialCapacity the initial capacity of each ArrayMap chain created by the map.
     *                             Must be > 0.
     */
    public ChainedHashMap(double resizingLoadFactorThreshold, int initialChainCount, int chainInitialCapacity) {
        if (resizingLoadFactorThreshold <= 0 || initialChainCount <= 0 || chainInitialCapacity <= 0) {
            throw new IllegalArgumentException("Invalid parameters");
        }
        this.threshold = resizingLoadFactorThreshold;
        this.chainCapacity = chainInitialCapacity;
        this.chains = createArrayOfChains(initialChainCount);
        this.numElements = 0;
        for (int i = 0; i < initialChainCount; i++) {
            this.chains[i] = createChain(chainInitialCapacity);
        }
    }

    /**
     * This method will return a new, empty array of the given size that can contain
     * {@code AbstractIterableMap<K, V>} objects.
     *
     * Note that each element in the array will initially be null.
     *
     * Note: You do not need to modify this method.
     * @see ArrayMap createArrayOfEntries method for more background on why we need this method
     */
    @SuppressWarnings("unchecked")
    private AbstractIterableMap<K, V>[] createArrayOfChains(int arraySize) {
        return (AbstractIterableMap<K, V>[]) new AbstractIterableMap[arraySize];
    }

    /**
     * Returns a new chain.
     *
     * This method will be overridden by the grader so that your ChainedHashMap implementation
     * is graded using our solution ArrayMaps.
     *
     * Note: You do not need to modify this method.
     */
    protected AbstractIterableMap<K, V> createChain(int initialSize) {
        return new ArrayMap<>(initialSize);
    }

    // Gets the chain at the specified index and returns the value associated with the key
    @Override
    public V get(Object key) {
        int index = getIndex(key);
        if (chains[index] != null && chains[index].containsKey(key)) {
            return chains[index].get(key);
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        if ((double) numElements / chains.length >= threshold) {
            resize2();
        }
        int index = getIndex(key);
        if (chains[index] == null) {
            chains[index] = createChain(chainCapacity);
        }
        V oldValue = chains[index].put(key, value);
        if (oldValue == null) {
            numElements++;
        }
        return oldValue;
    }

    private void resize() {
        int newCapacity = chains.length * 2;
        AbstractIterableMap<K, V>[] newChains = this.createArrayOfChains(newCapacity);

        for (int i = 0; i < newCapacity; i++) {
            newChains[i] = createChain(chainCapacity);
        }

        for (AbstractIterableMap<K, V> chain : chains) {
            if (chain != null) {
                for (K key : chain.keySet()) {
                    V value = chain.get(key);
                    int newChainIndex = Math.abs((key == null ? 0 : key.hashCode())) % newCapacity;
                    newChains[newChainIndex].put(key, value);
                }
            }
        }
        chains = newChains;
    }

    private void resize2() {
        int newCapacity = chains.length * 2;
        AbstractIterableMap<K, V>[] newChains = (AbstractIterableMap<K, V>[]) new AbstractIterableMap[newCapacity];

        ChainedHashMapIterator<K, V> iterator = new ChainedHashMapIterator<>(chains);
        while (iterator.hasNext()) {
            Map.Entry<K, V> entry = iterator.next();
            K key = entry.getKey();
            V value = entry.getValue();

            int newIndex = Math.abs((key == null ? 0 : key.hashCode())) % newCapacity;
            if (newChains[newIndex] == null) {
                newChains[newIndex] = createChain(chainCapacity);
            }
            newChains[newIndex].put(key, value);
        }
        this.chains = newChains;
    }

    @Override
    public V remove(Object key) {
        int index = getIndex(key);
        if (chains[index] != null) {
            V removedValue = chains[index].remove(key);
            if (removedValue != null) {
                numElements--;
            }
            return removedValue;
        }
        return null;
    }

    @Override
    public void clear() {
        numElements = 0;
        chains = createArrayOfChains(chains.length);
    }

    @Override
    public boolean containsKey(Object key) {
        int index = getIndex(key);
        return chains[index] != null && chains[index].containsKey(key);
    }

    @Override
    public int size() {
        return numElements;
    }

    private int getIndex(Object key) {
        if (key == null) {
            return 0;
        }
        int hashCode = key.hashCode();
        return Math.abs(hashCode) % chains.length;
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        // Note: you won't need to change this method (unless you add more constructor parameters)
        return new ChainedHashMapIterator<>(this.chains);
    }

    /*
    See the assignment webpage for tips and restrictions on implementing this iterator.
     */
    private static class ChainedHashMapIterator<K, V> implements Iterator<Map.Entry<K, V>> {
        private AbstractIterableMap<K, V>[] chains;
        private int currentChainIndex;
        private Iterator<Map.Entry<K, V>> currentChainIterator;

        public ChainedHashMapIterator(AbstractIterableMap<K, V>[] chains) {
            this.chains = chains;
            this.currentChainIndex = 0;
            this.currentChainIterator = getNextChainIterator();
        }

        private Iterator<Map.Entry<K, V>> getNextChainIterator() {
            while (currentChainIndex < chains.length && chains[currentChainIndex] == null) {
                currentChainIndex++;
            }
            if (currentChainIndex < chains.length) {
                return chains[currentChainIndex].iterator();
            }
            return null;
        }

        // iterate through the chains array, inspecting it in order to find an available ArrayMap,
        // while ignoring the Null values. Once it finds an available ArrayMap, it returns true.
        // False otherwise (When you see a Null value).
        @Override
        public boolean hasNext() {
            if (currentChainIterator == null) {
                return false;
            }
            while (currentChainIterator != null && !currentChainIterator.hasNext()) {
                currentChainIndex++;
                currentChainIterator = getNextChainIterator();
            }
            return currentChainIterator != null && currentChainIterator.hasNext();
        }

        @Override
        public Map.Entry<K, V> next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements");
            }
            return currentChainIterator.next();
        }
    }
}
