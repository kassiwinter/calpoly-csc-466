package DocumentClasses;
import java.util.*;

public class DocumentVector extends TextVector {
    private HashMap<String, Double> normalizedVector = new HashMap<>();
    public int docId;
    private double maxFrequency;

    public DocumentVector(int docId) {
        this.docId = docId;
    }

    protected Set<Map.Entry<String, Double>> getNormalizedVectorEntrySet() {
        return normalizedVector.entrySet();
    }

    protected void normalize(DocumentCollection dc) {
         // Calculate TF-IDF for each word in the document
         for (Map.Entry<String, Double> entry : normalizedVector.entrySet()) {
            String word = entry.getKey();
            double tf = entry.getValue(); // Term frequency
            double idf = Math.log((double) dc.getSize() / dc.getDocumentFrequency(word)); // Inverse document frequency
            double tfidf = tf * idf;
            normalizedVector.put(word, tfidf);
            maxFrequency = Math.max(maxFrequency, tfidf); // Update maximum frequency
        }

        // Normalize the frequencies by dividing by the maximum frequency
        for (Map.Entry<String, Double> entry : normalizedVector.entrySet()) {
            entry.setValue(entry.getValue() / maxFrequency);
        }
    }

    protected double getNormalizedFrequency(String word) {
        return normalizedVector.getOrDefault(word, 0.0);
    }
}