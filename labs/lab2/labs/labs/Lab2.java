package labs;
import java.io.*;
import java.util.*;

import DocumentClasses.CosineDistance;
import DocumentClasses.DocumentCollection;
import DocumentClasses.TextVector;

public class Lab2 {
    public static DocumentCollection documents;
    public static DocumentCollection queries;

    public static void main(String[] args) {
        // Load data from binary file                                                   // serialize it
        /*try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(new File("/Users/kassandrawinter/Desktop/csc-466-03/labs-2/documents_serialized.txt")))) { // add a full path to vectors
            documents = (DocumentCollection) is.readObject();
            is.close();
        } 
        catch (Exception e) {
          System.out.println(e);
        }*/

        // Initialize queries and normalize
        documents = new DocumentCollection("/Users/kassandrawinter/Desktop/csc-466-03/labs-2/documents_serialized.txt");
        queries = new DocumentCollection("/Users/kassandrawinter/Desktop/csc-466-03/labs-2/queries.txt");
        
        documents.normalize(documents);
        queries.normalize(documents);


        // Find top 20 relevant documents for each query
        HashMap<Integer, ArrayList<Integer>> topDocs = new HashMap<>();
        // Find top 20 documents for each query
            for (Map.Entry<Integer, TextVector> entry : queries.getEntrySet()) {
                int queryId = entry.getKey();
                TextVector query = entry.getValue();
                ArrayList<Integer> top20Docs = query.findClosestDocuments(documents, new CosineDistance());
                System.out.println("Documents for query " + queryId + ": " + top20Docs);
            }

        // Store results in a file
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(new File("top_docs.bin")))) {
            os.writeObject(topDocs);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}