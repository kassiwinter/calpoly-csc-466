import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;

public class Lab5 {
    private static ArrayList<ItemSet> transactions = new ArrayList<>(); // lists of all itemsets
    private static ArrayList<Integer> items = new ArrayList<>();        // lists of all items
    private static HashMap<Integer, ArrayList<ItemSet>> frequentItemSet = new HashMap<>(); // lists frequent imtemsets


    // Main Function
    public static void main(String[] args) {
        process("/Users/kassandrawinter/Desktop/csc-466-03/lab5/shopping_data.txt");
        findFrequentSingleItemSets();
        int k = 2;
        while (findFrequentItemSets(k)) {
            k++;
        }
        // Print or store frequent item sets

        System.out.println("{");
        for (int i = 1; i <= frequentItemSet.size(); i++) {
            if (frequentItemSet.containsKey(i)) {
                System.out.print(i + "=[");
                ArrayList<ItemSet> itemSets = frequentItemSet.get(i);
                for (int j = 0; j < itemSets.size(); j++) {
                    System.out.print(itemSets.get(j));
                    if (j < itemSets.size() - 1) {
                        System.out.print(", ");
                    }
                }
                System.out.println("],");
            }
        }
        System.out.println("}");
    }

    // Function to process the given data file
    public static void process(String fileName) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(", ");
                ArrayList<Integer> transactions_list = new ArrayList<>();
                Integer.parseInt(parts[0]);                 // ignore the transaction number (it's not needed)
                for (int i = 1; i < parts.length; i++) {    // go through all of the transaction items
                    int item = Integer.parseInt(parts[i]);
                    transactions_list.add(item);
                    if (!items.contains(item)) {            // if the item is not already included in the list of items
                        items.add(item);                    // then add it into the items list
                    }
                }
                transactions.add(new ItemSet(transactions_list)); // add all of the transactions as an itemset into the transactions list
            }
            Collections.sort(items);
            reader.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // A function that finds all 1-itemsets that meet the min support
    public static void findFrequentSingleItemSets() {
        for (Integer item : items) {
            ArrayList<Integer> itemList = new ArrayList<>();
            itemList.add(item);
            ItemSet newSet = new ItemSet(itemList);

            if(isFrequent(newSet)) {   // Is the given transaction item frequent across all transactions?

                if (!frequentItemSet.containsKey(1)) {      // If the 'Size 1 Item Set' has not been created, create it key
                    frequentItemSet.put(1, new ArrayList<ItemSet>());
                }
                frequentItemSet.get(1).add(newSet); 
            }
        }
    }

    
    // a function that finds all k-itemsets; returns false if no itemsets were found (precondition k>=2)
    public static boolean findFrequentItemSets(int k) {
        //if (k < 2) { return false; }  // the precondition was not met
        boolean foundFrequentItemSets = false;
        ArrayList<ItemSet> candidateItemSets = generateCandidateItemSets(k);
    
        for (ItemSet candidate : candidateItemSets) {
                if(isFrequent(candidate)) {
                    foundFrequentItemSets = true;
                    if (!frequentItemSet.containsKey(k)) {
                        frequentItemSet.put(k, new ArrayList<>());
                    }
                    frequentItemSet.get(k).add(candidate);
                }
        }
        return foundFrequentItemSets;
    }

    // a function that tells if the itemset is frequent, i.e., meets the minimum support of 1%
    public static boolean isFrequent(ItemSet itemSet) {
        int occurences = 0;
        for (ItemSet transaction : transactions) {        // goes through the transactions
            if (transaction.getItems().containsAll(itemSet.getItems())) {   // if it exists, increase its frequency
                occurences++;
            }
        }

        double support = (double) occurences / transactions.size(); // calculates support as a percentage
        double minSupport = 0.01;                                   // 1% support threshold

        return (support >= minSupport);
    }


    // a function to generate match ups 
    public static ArrayList<ItemSet> generateCandidateItemSets(int k) {
        ArrayList<ItemSet> candidates = new ArrayList<>();

        ArrayList<ItemSet> previous_frequent = frequentItemSet.get(k - 1); // get the previous frequent set

        for (int i = 0; i < previous_frequent.size(); i++) {              // iterates over frequent items k-1
            for (int j = i + 1; j < previous_frequent.size(); j++) {      // used to  to avoid rechecking pairs that have already been checked
                ItemSet itemSet1 = previous_frequent.get(i);
                ItemSet itemSet2 = previous_frequent.get(j);

                // Check if the first k-2 items are the same
                ArrayList<Integer> items1 = itemSet1.getItems();
                ArrayList<Integer> items2 = itemSet2.getItems();
                boolean canJoin = true;

                for (int l = 0; l < k - 2; l++) {                               // seeing if the first elements of the set match
                    if (!items1.get(l).equals(items2.get(l))) {
                        canJoin = false;
                        break;
                    }
                }

                // If the first k-2 items are the same, join the sets to create a candidate k-item set
                if (canJoin) {
                    ArrayList<Integer> combinedItems = new ArrayList<>(items1);
                    combinedItems.add(items2.get(k - 2)); // Add the last item from the second set
                    ItemSet candidate = new ItemSet(combinedItems);
                    //candidates.put(candidate, 0);
                    candidates.add(candidate);
                }
            }
        }

        return candidates;
    }


}
