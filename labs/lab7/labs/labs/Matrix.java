import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

class Matrix {
    private int[][] data;

    public Matrix(int[][] data) {
        this.data = data;
    }

    // MATRIX SEARCH FUNCTIONS
    // function: calculates the frequency of a specific attribute-value pair within a subset of rows
    // purpose: used to compute probabilities or frequencies needed for entropy calculations, information gain, and other decision tree construction algorithms
    private int findFrequency(int attribute, int value, ArrayList<Integer> rows) {
        int count = 0;
        for (int row : rows) {
            if (data[row][attribute] == value) {
                count++;
            }
        }
        return count;
    }

    // funtion: get all of the unique values in the colum of the attribute (ex: poor, good, etc)
    // think -> LD1: 'All' and LD2: 'Some'
    private HashSet<Integer> findDifferentValues(int attribute, ArrayList<Integer> rows) {
        HashSet<Integer> values = new HashSet<>();
        for (int row : rows) {
            if (row >= 0 && row < data.length && attribute >= 0 && attribute < data[row].length) {
                values.add(data[row][attribute]);
            }
        }
        return values;
    }

    // function: finds how many rows exist for each unique value (ex: poor, good, etc) of the colum of the attribute 
    // think -> LD1 : {1, 2, 4} and LD2 : {3, 5}
    private ArrayList<Integer> findRows(int attribute, int value, ArrayList<Integer> rows) {
        ArrayList<Integer> newRows = new ArrayList<>();
        for (int row : rows) {
            if (data[row][attribute] == value) {
                newRows.add(row);
            }
        }
        return newRows;
    }

    // function: determines the most common class (target variable) among a subset of rows
    // purpose: making decisions at tree nodes, such as determining the class label for a leaf node.
    public int findMostCommonValue(ArrayList<Integer> rows) {
        int[] counts = new int[4];
        for (int row : rows) {
            counts[data[row][4] - 1]++;
        }
        int maxCount = 0;
        int maxValue = 0;
        for (int i = 0; i < 4; i++) {
            if (counts[i] > maxCount) {
                maxCount = counts[i];
                maxValue = i + 1;
            }
        }
        return maxValue;
    }

    // funtion: splits the dataset on the given class
    // purpose: operation for recursively splitting the dataset into subsets based on attribute values
    public HashMap<Integer, ArrayList<Integer>> split(int given_class, ArrayList<Integer> rows) {
        HashMap<Integer, ArrayList<Integer>> splitData = new HashMap<>();
        HashSet<Integer> values = findDifferentValues(given_class, rows);
        for (int value : values) {
            splitData.put(value, findRows(given_class, value, rows));
        }
        return splitData;
    }

    // CALCULATIONS
    // function: log based 2
    private double log2(double number) {
        return Math.log(number) / Math.log(2);
    }

    // function: finds the entropy of the given attribute / column of that round
    // to do -- A. go through each row, count how many times each cass occurs
    //          B. divide it by how many entries there currenty are
    // think -> Entropy L
    private double findEntropy(int attribute, ArrayList<Integer> rows) {
        double entropy = 0;
        HashSet<Integer> attribute_values = findDifferentValues(attribute, rows);       
        for (int value : attribute_values) {
            ArrayList<Integer> attribute_rows = findRows(attribute, value, rows);         
            entropy += (double) attribute_rows.size() / rows.size() * findEntropy(attribute_rows); // Find Entropy A: ratio * Entropy An
        }
        return entropy;
    }

     // function: finds the OVERALL entropy of that round OR for D1..Dn
     // to do -- A. go through each row, count how many times each cass occurs
     //          B. divide it by how many entries there currenty are
     // think -> Entropy D OR Entropy L1, Entropy L2, etc
     private double findEntropy(ArrayList<Integer> rows) {
        int[] class_occurences = new int[4];
        for (int row : rows) {                  // count the number of occurences of each attribute
            class_occurences[data[row][4]]++;   // ex: if class is 2, then it increases the number at [2] of the array
        }

        double entropy = 0;
        for (int given_class : class_occurences) {  // for each data column / attribute..
            if (given_class > 0) {              // if there exists one in the dataset..
                double p = (double) given_class / rows.size();    // a). total # class / total # entries
                entropy -= p * log2(p);                           // b). log that bitch
            }
        }
        return entropy;
    }

    // function that finds the information gain of breaking on a given class
    private double findGain(int attribute, ArrayList<Integer> rows) {
        return findEntropy(rows) - findEntropy(attribute, rows);
    }

    // function that finds the Information Gain Ratio of a given class
    public double computeIGR(int attribute, ArrayList<Integer> rows) {
        double gain = findGain(attribute, rows);
        HashSet<Integer> values = findDifferentValues(attribute, rows);
        double gain_ratio = 0;

        // think -> recomputing overall entropy (Entropy D), bottom half of ratio
        for (int value : values) {
            ArrayList<Integer> valueRows = findRows(attribute, value, rows);
            double p = (double) valueRows.size() / rows.size();
            gain_ratio -= p * log2(p);
        }
        return gain_ratio > 0 ? gain / gain_ratio : 0; // if the bottom haf is not 0, return the gain ratio
    }

}