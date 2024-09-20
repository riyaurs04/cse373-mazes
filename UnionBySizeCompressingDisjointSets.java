package disjointsets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A quick-union-by-size data structure with path compression.
 * @see DisjointSets for more documentation.
 */
public class UnionBySizeCompressingDisjointSets<T> implements DisjointSets<T> {
    // Do NOT rename or delete this field. We will be inspecting it directly in our private tests.
    List<Integer> pointers;

    // maps items to their index in the pointers arraylist
    private final Map<T, Integer> itemIndex;

    /*
    However, feel free to add more fields and private helper methods. You will probably need to
    add one or two more fields in order to successfully implement this class.
    */

    public UnionBySizeCompressingDisjointSets() {
        this.pointers = new ArrayList<>();
        this.itemIndex = new HashMap<>();
    }

    @Override
    public void makeSet(T item) {
        // if item does not already exist, put it in the map and the pointers list
        if (!itemIndex.containsKey(item)) {
            itemIndex.put(item, pointers.size());
            // -1 --> item is a root and the size of the set is 1
            pointers.add(-1);
        }
    }

    @Override
    public int findSet(T item) {
        if (!itemIndex.containsKey(item)) {
            throw new IllegalArgumentException(item + " does not exist.");
        }
        // return index of representative
        return findSet(itemIndex.get(item));
    }

    // helper method --> finds the root of a set given an index
    private int findSet(int index) {
        if (pointers.get(index) < 0) {
            // root of the set
            return index;
        } else {
            // path compression --> directly links to the root
            pointers.set(index, findSet(pointers.get(index)));
            return pointers.get(index);
        }
    }

    @Override
    public boolean union(T item1, T item2) {
        int root1 = findSet(item1);
        int root2 = findSet(item2);

        if (root1 == root2) {
            // Already in the same set
            return false;
        }

        // Union by size
        if (pointers.get(root1) <= pointers.get(root2)) {
            // root1's tree is bigger
            pointers.set(root1, pointers.get(root1) + pointers.get(root2)); // Update size
            pointers.set(root2, root1); // root1 parent of root2
        } else {
            // root2's tree is bigger
            pointers.set(root2, pointers.get(root1) + pointers.get(root2)); // Update size
            pointers.set(root1, root2); // root2 parent of root1
        }
        return true;
    }
}
