package DocumentClasses;

import java.util.Map;

public class CosineDistance implements DocumentDistance {
    public double findDistance(TextVector query, TextVector document, DocumentCollection documents) {
        double dotProduct = 0;
        for (Map.Entry<String, Double> entry : query.getNormalizedVectorEntrySet()) {
            String word = entry.getKey();
            double queryFreq = entry.getValue();
            double docFreq = document.getNormalizedFrequency(word);
            dotProduct += queryFreq * docFreq;
        }
        return 1 - (dotProduct / (query.getL2Norm() * document.getL2Norm()));
    }
}