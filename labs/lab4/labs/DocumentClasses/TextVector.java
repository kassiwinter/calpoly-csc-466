package DocumentClasses;
import java.io.Serializable;
import java.util.*;

public abstract class TextVector implements Serializable{

    HashMap<String, Integer> rawVector; // stores the frequency for each non-noise word
    public TextVector() {rawVector = new HashMap<>();} // variable creation
    protected abstract Set<Map.Entry<String, Double>> getNormalizedVectorEntrySet();
    protected abstract void normalize(DocumentCollection dc);
    protected abstract double getNormalizedFrequency(String word);

    // -- LAB 1 FUNCTIONS--
    // returns an object of type Set<Map.Entry<String, Integer>>. This is a mapping from each word to its frequency. Use the method entrySet on the HashMap to get the result.
    public Set<Map.Entry<String, Integer>> getRawVectorEntrySet() {
        return rawVector.entrySet();
    }
    
    // adds a word to the rawVector
    public void add(String word) {
        word = word.toLowerCase(); // convert word to lowercase
        if (rawVector.containsKey(word)) { // if the word is not new, frequency is incremented by one.
            rawVector.put(word, getRawFrequency(word) + 1);
        }
        else {
            //System.out.println("Unique word : " + word);
            rawVector.put(word, 1); // if the word is new, add it into the vector with frequency 1
        }
    } 

    // returns true if the word is in the rawVector and false otherwise.
    public boolean contains(String word) {
        return rawVector.containsKey(word.toLowerCase());
    }

    // returns the frequency of the word.
    public int getRawFrequency(String word) {
        return rawVector.getOrDefault(word.toLowerCase(), 0);
    }

    // returns the total number of non-noise words that are stored for the document (e.g., if frequency =2, then count the word twice).
    public int getTotalWordCount() {
        int total = 0;
        for (int frequency : rawVector.values()) {
            total += frequency;
        }
        return total;
    }

    // returns the number of distinct words that are stored.
    public int getDistinctWordCount() {
        return rawVector.size();
    }

    // returns the highest word frequency.
    public int getHighestRawFrequency() {
        int highest = 0;
        for (Map.Entry<String, Integer> entry : rawVector.entrySet()) {
            if (entry.getValue() > highest) {
                highest = entry.getValue();
            }
        }
        return highest; 
    }

    // returns the word with the highest frequency.
    public String getMostFrequentWord() {
        int highest = 0;
        String found_word = "";
        for (Map.Entry<String, Integer> entry : rawVector.entrySet()) {
            if (entry.getValue() > highest) {
                highest = entry.getValue();
                found_word = entry.getKey();
            }
        }
        return found_word;
    }

    // -- LAB 2 FUNCTIONS--
    public double getL2Norm() {
        double sum = 0;
        for (Map.Entry<String, Double> entry : getNormalizedVectorEntrySet()) {
            sum += Math.pow(entry.getValue(), 2);
        }
        return Math.sqrt(sum);
    }

    public ArrayList<Integer> findClosestDocuments(DocumentCollection documents, DocumentDistance distanceAlg) {
        ArrayList<Integer> closestDocs = new ArrayList<>();
        PriorityQueue<Map.Entry<Integer, Double>> pq = new PriorityQueue<>(Comparator.comparingDouble(Map.Entry::getValue));

        for (Map.Entry<Integer, TextVector> entry : documents.getEntrySet()) {
            TextVector doc = entry.getValue();
            if (!doc.getNormalizedVectorEntrySet().isEmpty()) {
                double distance = distanceAlg.findDistance(this, doc, documents);
                Map.Entry<Integer, Double> entryToAdd = new AbstractMap.SimpleEntry<>(entry.getKey(), distance);
                pq.offer(entryToAdd);
            }
        }

        for (int i = 0; i < 20 && !pq.isEmpty(); i++) {
            Map.Entry<Integer, Double> entry = pq.poll();
            closestDocs.add(entry.getKey());
        }

        return closestDocs;
    }
    
}
