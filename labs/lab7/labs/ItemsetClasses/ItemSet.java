package ItemsetClasses;
import java.util.*;

public class ItemSet {

    private final TreeSet<Integer> items;

    /**
     * Default constructor.
     */
    public ItemSet() {
        this.items = new TreeSet<>();
    }

    /**
     * Constructor to generate an ItemSet from a list of items.
     * @param items a {@code List<Integer>} of items to initialize the ItemSet with.
     */
    public ItemSet(List<Integer> items) {
        this.items = new TreeSet<>();
        this.items.addAll(items);
    }

    /**
     * Parses an ItemSet from a string.
     * @param str a string representation of an ItemSet.
     *            Expected format is <br><code>[I<sub>1</sub>, I<sub>2</sub>, ..., I<sub>n</sub>]</code>
     * @return a new ItemSet based on the string representation
     */
    public static ItemSet parseItemSet(String str) {
        String[] split = str.split("[\\[\\], ]");
        List<Integer> items = Arrays.stream(split)
                .filter(s -> !s.isEmpty())
                .mapToInt(Integer::parseInt)
                .boxed().toList();
        return new ItemSet(items);
    }

    /**
     * @return A new ItemSet which is a deep copy of the calling ItemSet.
     */
    public ItemSet copy() {
        ItemSet copy = new ItemSet();
        copy.addAll(this);
        return copy;
    }

    /**
     * Takes another ItemSet and returns a new ItemSet which is the union of this ItemSet and the other.
     * Does not modify either of the two.
     */
    public ItemSet union(ItemSet other) {
        ItemSet u = this.copy();
        u.addAll(other);
        return u;
    }

    /**
     * Returns a copy of this ItemSet without any items contained in {@code other}.
     * Useful if modifying the calling ItemSet is undesirable.
     * @return A new ItemSet without items in {@code other}
     */
    public ItemSet withoutAny(ItemSet other) {
        ItemSet copy = this.copy();
        copy.removeAll(other);
        return copy;
    }

    /**
     * Returns the items in the ItemSet as a List.
     * <p>
     *     Equivalent to {@code new ArrayList<Integer>(this.getItemSet())}
     * </p>
     *
     * @return an ordered {@code ArrayList<Integer>} of the items in the ItemSet.
     */
    public ArrayList<Integer> getItems() {
        return new ArrayList<>(items);
    }

    /**
     * Returns the TreeSet used to store items in the ItemSet.
     * @return a TreeSet of items in this ItemSet
     */
    public TreeSet<Integer> getItemSet() {
        return items;
    }

    /**
     * Checks if this ItemSet contains the passed item.
     * @param item an item to look for
     * @return True if the ItemSet contains this item; False otherwise
     */
    public boolean contains(Integer item) {
        return items.contains(item);
    }

    /**
     * Checks if the provided ItemSet is a subset of this ItemSet.
     * @param other the ItemSet to check
     * @return True if this ItemSet contains all items in {@code other}; False otherwise
     */
    public boolean contains(ItemSet other) {
        return items.containsAll(other.items);
    }

    public int size() {
        return items.size();
    }

    /**
     * Adds an item to this ItemSet if it doesn't already contain it.
     * @param item The item to add.
     */
    public void addItem(Integer item) {
        items.add(item);
    }

    /**
     * Adds all items in {@code other} to this ItemSet.
     */
    public void addAll(ItemSet other) {
        items.addAll(other.items);
    }

    /**
     * Removes a specific item from this ItemSet.
     */
    public void removeItem(Integer item) {
        items.remove(item);
    }

    /**
     * Removes all items in {@code other} from this ItemSet.
     */
    public void removeAll(ItemSet other) {
        items.removeAll(other.items);
    }

    /**
     * @return A list of all proper nonempty subsets of this ItemSet.
     */
    public ArrayList<ItemSet> subsets() {
        ArrayList<ItemSet> subsets = new ArrayList<>();
        for (int s = 0; s < items.size(); s++) {
            subsets.addAll(this.subsetsOfSize(s));
        }
        return subsets;
    }

    public Integer getItemAtIndex(int index) {
        int n = items.size();  
        int arr[] = new int[n]; 

        int i = 0;  
        
        // using for-each loop to traverse through  
        // the set and adding each element to array 
        for (int ele : items)  
            arr[i++] = ele; 

        return arr[index];
    }

    /**
     * Generates all possible subsets of the specified size.
     * @param size The desired size of subsets.
     * @return A list of all subsets of the specified size.
     */
    public ArrayList<ItemSet> subsetsOfSize(int size) {
        if (size == 0) {
            return new ArrayList<>();
        }
        ArrayList<ItemSet> subsets = new ArrayList<>();
        if (size == 1) {
            for (Integer item : items) {
                ItemSet singleton = new ItemSet(Collections.singletonList(item));
                subsets.add(singleton);
            }
            return subsets;
        }
        ItemSet copy = this.copy();
        for (Integer item : items) {
            copy.removeItem(item);
            for (ItemSet subset : copy.subsetsOfSize(size - 1)) {
                ItemSet newSubset = subset.copy();
                newSubset.addItem(item);
                subsets.add(newSubset);
            }
        }
        return subsets;
    }

    @Override
    public String toString() {
        return Arrays.toString(items.toArray());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemSet itemSet)) return false;
        return Objects.equals(items, itemSet.items);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getItems());
    }

}
