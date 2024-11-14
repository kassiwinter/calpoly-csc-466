import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;

public class Lab7 {

    public static void main(String[] args) throws IOException {
        int[][] data = process("/Users/kassandrawinter/Desktop/csc-466-03/lab7/data.txt");     // Ex: int[][] data = {
        ArrayList<Integer> attributes = new ArrayList<>(); // creates a list of attributes [0, 1,2,3]      //     {51, 35, 14, 2, 2},
        for (int i = 0; i < 4; i++) {                     //  representing each column                     //     {49, 30, 14, 2, 2}, etc
            attributes.add(i);
        }
        ArrayList<Integer> rows = new ArrayList<>(); // creates an array for total rows (one for each line; for indexing attributes)
        for (int i = 0; i < data.length; i++) {      // Ex: ArrayList<Integer> rows = {0, 1, .. , 149}
            rows.add(i);
        }

        // print out the tree based on the data
        printDecisionTree(data, attributes, rows, 0, 100);  // note: levelinitially set to 0 to determine how many tabs to print
    }                                                                     //       IGR initially set to 100 to create initial terminating condition


    // function to parse through given file and return a two-dimensional array of the input file
    public static int[][] process(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        ArrayList<int[]> data = new ArrayList<>();
        String line;

        while ((line = reader.readLine()) != null) { // go through each line in the file
            String[] strings = line.split(",");      // split the data by number
            int[] numbers = new int[5];                    // array to store translated number values
            for (int i = 0; i <= 4; i++) {
                double double_translation = Double.parseDouble(strings[i]);
                numbers[i] = (int)double_translation; // translate & store as int value
            }
            data.add(numbers);
        }
        reader.close();
        return data.toArray(new int[0][]);
    }

    // recursive function used to create & print decision tree based on the data
    public static void printDecisionTree(int[][] data, ArrayList<Integer> attributes, ArrayList<Integer> rows, int level, double currentIGR) {
        Matrix matrix = new Matrix(data);
        if (rows.isEmpty()) {   // case where the data is empty or no rows left to go through
            return;
        }

        if (attributes.isEmpty() || currentIGR < 0.01) {    // case where there are no more attributes to compare
            int value = matrix.findMostCommonValue(rows);
            System.out.println(String.join("", Collections.nCopies(level, "  ")) + "value = " + value);
            return;
        }

        int bestSplit = -1;                    // used to keep track of the best attribute to split on
        double highestIGR = Double.MIN_VALUE; // used to keep track of the highest information gain ratio for that attribute

        for (int attribute : attributes) {   // goes through each attribute
            double igr = matrix.computeIGR(attribute, rows); // computes the igr value
            if (igr > highestIGR) { // if it is higher than the current, replace the current
                bestSplit = attribute;
                highestIGR = igr;
            }
        }

        if (highestIGR < 0.01) {    // case where the IGR is too low
            int value = matrix.findMostCommonValue(rows);
            System.out.println(String.join("", Collections.nCopies(level, "  ")) + "value = " + value);
            return;
        }

        attributes.remove((Integer) bestSplit); // remove the best attribute for the next recursive call
        
        //System.out.println(String.join("", Collections.nCopies(level, "  ")) + "When attribute " + (bestSplit + 1) + " has value");
        HashMap<Integer, ArrayList<Integer>> splitData = matrix.split(bestSplit, rows); //  splits the dataset based on the values of the bestClass
        for (int value : splitData.keySet()) { // loops over the keys (unique values) found from splitting
            System.out.println(String.join("", Collections.nCopies(level, "  ")) + "When attribute " + (bestSplit + 1) + " has value " + value);
            //System.out.println(String.join("", Collections.nCopies(level + 1, "  ")) + value);
            printDecisionTree(data, new ArrayList<>(attributes), splitData.get(value), level + 2, highestIGR);
        }
        
    } 
}