package DocumentClasses;
import java.util.*;

public class QueryVector extends TextVector {
    private HashMap<String, Double> normalizedVector = new HashMap<>();


    protected Set<Map.Entry<String, Double>> getNormalizedVectorEntrySet() {
        return normalizedVector.entrySet();
    }

    protected void normalize(DocumentCollection dc) {
        // Calculate TF-IDF for each word in the query
        for (Map.Entry<String, Double> entry : normalizedVector.entrySet()) {
            String word = entry.getKey();
            double tf = entry.getValue(); // Term frequency
            double idf = Math.log((double) dc.getSize() / dc.getDocumentFrequency(word)); // Inverse document frequency
            double tfidf = tf * idf;
            normalizedVector.put(word, tfidf);
        }

        // Normalize the frequencies by dividing by the maximum frequency
        double maxFrequency = 0;
        for (double freq : normalizedVector.values()) {
            maxFrequency = Math.max(maxFrequency, freq);
        }
        for (Map.Entry<String, Double> entry : normalizedVector.entrySet()) {
            entry.setValue(entry.getValue() / maxFrequency);
        }
    }

    protected double getNormalizedFrequency(String word) {
        return normalizedVector.getOrDefault(word, 0.0);
    }
}