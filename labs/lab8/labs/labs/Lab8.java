import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Lab8 {
    public static void main(String[] args) {
        double[][] data = readInput("data.txt");
        Matrix matrix = new Matrix(data, 4);
        getCustomerInput(matrix);
    }

     // reads user input from keyboard or file
    private static double[][] readInput(String filename) {
        List<double[]> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                double[] row = new double[values.length];
                for (int i = 0; i < values.length; i++) {
                    row[i] = Double.parseDouble(values[i]);
                }
                data.add(row);
            }
        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
        }
        return data.toArray(new double[0][]);
    }

    private static void getCustomerInput(Matrix matrix) {
        int[] row = new int[4];
        System.out.print("Enter value for attribute 0: ");
        row[0] = Integer.parseInt(System.console().readLine());
        System.out.print("Enter value for attribute 1: ");
        row[1] = Integer.parseInt(System.console().readLine());
        System.out.print("Enter value for attribute 2: ");
        row[2] = Integer.parseInt(System.console().readLine());
        System.out.print("Enter value for attribute 3: ");
        row[3] = Integer.parseInt(System.console().readLine());

        int category = matrix.findCategory(row);
        for (int i = 1; i <= 3; i++) {
            double prob = matrix.findProb(row, i);
            System.out.printf("For value %d: Probability is: %e%n", i, prob);
        }
        System.out.printf("Expected category: %d%n", category);
    }

    
}
