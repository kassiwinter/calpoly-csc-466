import java.util.ArrayList;

public class ItemSet {
    private ArrayList<Integer> items;

    public ItemSet(ArrayList<Integer> items) {
        this.items = items;
    }

    public ArrayList<Integer> getItems() {
        return items;
    }

    public boolean contains(Integer item) {
        return items.contains(item);
    }

    public int size() {
        return items.size();
    }

    public Integer getItemAtIndex(int index) {
        if (index < 0 || index >= items.size()) {
            return null; // Return null for out-of-bounds index
        }
        return items.get(index);
    }

    

    @Override
    public String toString() {
        return items.toString();
    }

    
    
}