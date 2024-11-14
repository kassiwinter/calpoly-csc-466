import java.util.ArrayList;

class Matrix {
    private double[][] data;
    private int categoryIndex;

    public Matrix(double[][] data, int categoryIndex) {
        this.data = data;
        this.categoryIndex = categoryIndex;
    }

    // function to count all rows (used for indexing)
    public ArrayList<Integer> findAllRows() {
        ArrayList<Integer> rows = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            rows.add(i);
        }
        return rows;
    }

    // function to get the index of a category
    public int getCategoryAttribute() {
        return categoryIndex;
    }

    // for a row and a category, calculates probability that the row belongs to the category
    public double findProb(int[] row, int category) {
        double probability = 1.0;
        int n = data.length;
        for (int i = 0; i < row.length; i++) {
            if (i != categoryIndex) {
                int count = 0;
                for (double[] tuple : data) {
                    if (tuple[i] == row[i] && tuple[categoryIndex] == category) {
                        count++;
                    }
                }
                double smoothingFactor = 1.0 / n;
                probability *= (count + smoothingFactor) / (getCategoryCount(category) + smoothingFactor * data[0].length);
            }
        }
        return probability;
    }

     // counts the total number of categories
    private int getCategoryCount(int category) {
        int count = 0;
        for (double[] tuple : data) {
            if (tuple[categoryIndex] == category) {
                count++;
            }
        }
        return count;
    }

    // finds the most probable category of a given row
    public int findCategory(int[] row) {
        double maxProb = 0.0;
        int category = 0;
        for (int i = 1; i <= 3; i++) {
            double prob = findProb(row, i);
            if (prob > maxProb) {
                maxProb = prob;
                category = i;
            }
        }
        return category;
    }
}