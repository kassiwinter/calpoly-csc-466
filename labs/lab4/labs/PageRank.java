import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class PageRank {
    private HashSet<Integer> nodes;
    private HashMap<Integer, ArrayList<Integer>> adjacencyList;
    private HashMap<Integer, Integer> outgoingLinks;
    private HashMap<Integer, Double> pageRankOld;
    private HashMap<Integer, Double> pageRankNew;

    public PageRank() {
        nodes = new HashSet<>();            // used to store the entire graph
        adjacencyList = new HashMap<>();    // used to store each node and what nodes point to it 
        outgoingLinks = new HashMap<>();    // used to store each node and the total number of nodes that point to it
        pageRankOld = new HashMap<>();      // used to store the page ranks for the nodes (previous iteration of the formula)
        pageRankNew = new HashMap<>();      // used to store the the page rank for the nodes (current iteration of the formula)
    }

    // A function to initalize the structures
    public void readGraphFromFile(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int fromNode = Integer.parseInt(parts[0]);   // the node that points to the 'current' node
                int toNode = Integer.parseInt(parts[2]);    // the 'current' node
                nodes.add(fromNode);
                nodes.add(toNode);

                if (!adjacencyList.containsKey(toNode)) { // if the node is not already mentioned
                    adjacencyList.put(toNode, new ArrayList<>());
                }
                if (!adjacencyList.get(toNode).contains(fromNode)) { // if the pointing node has not been included in the list
                    adjacencyList.get(toNode).add(fromNode);         // add it to the arraylist of other nodes also pointing to the current node
                    outgoingLinks.put(fromNode, outgoingLinks.getOrDefault(fromNode, 0) + 1); // increase the total number of nodes that point to the 'current node'
                }
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // A function to initalize all of the node values so they all have an equal scale
    public void initializePageRanks() {
        double initialRank = 1.0 / nodes.size(); // ranks them all the same on a scale
        for (int node : nodes) {
            pageRankOld.put(node, initialRank);  // puts in ratings for old ranking to begin
        }
    }

    // A function that calculates the page rank of the nodes recursively
    public void calculatePageRank(double d, double threshold) {
        double dampingFactor = (1.0 - d) / nodes.size(); 

        while (true) {
            double grandtotal = 0.0;
            
            for (int node : nodes) {
                double total = 0.0;

                if (adjacencyList.containsKey(node)) { // the current node has other nodes pointing to it

                    for (int currentNode : adjacencyList.get(node)) { // go through the nodes that point to it
                        total += pageRankOld.get(currentNode) / (double)outgoingLinks.getOrDefault(currentNode, 0); // ratio divided by total number of nodes it points to
                    }

                } 
                /*else { // the current node has no other nodes pointing to it
                    total = 0;
                }*/

                grandtotal += dampingFactor + d * total;
                pageRankNew.put(node, dampingFactor + d * total); // update the nodes value
            }

            // put the normalized values into the new values
            for (int node : nodes) {
                pageRankNew.put(node, (pageRankNew.get(node) / grandtotal));
            } 

            // Does this iteration satisfy the given threshold?
            if (findDistance(pageRankOld, pageRankNew) < threshold) { // If so, leave the recursive algorithm
                break;
            } 
            pageRankOld = new HashMap<>(pageRankNew); // If not, update pageRankOld values for new iteration of the algorithm

        }
    }

    // A function that calculates the L1 norm of the distance between two nodes
    private double findDistance(HashMap<Integer, Double> pageRankOld, HashMap<Integer, Double> pageRankNew) {
        double distance = 0.0;
        for (int node : nodes) {
            distance += Math.abs(pageRankOld.get(node) - pageRankNew.get(node));
        }
        return distance;
    }

    // A function to print the found top nodes
    public void printTopNodes(int n) {
        ArrayList<Integer> topNodes = new ArrayList<>(pageRankOld.keySet());
        topNodes.sort((a, b) -> Double.compare(pageRankOld.get(b), pageRankOld.get(a)));
        System.out.print("[");
        for (int i = 0; i < n; i++) {
            System.out.print(topNodes.get(i));
            if (i < n - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }

    // Main Function
    public static void main(String[] args) {
        PageRank pageRank = new PageRank();
        pageRank.readGraphFromFile("graph.txt");
        pageRank.initializePageRanks();
        pageRank.calculatePageRank(0.9, 0.001);
        pageRank.printTopNodes(20);
    }
}