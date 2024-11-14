package labs;
import DocumentClasses.DocumentCollection;
import DocumentClasses.TextVector;

import java.io.*;
import java.util.*;


public class Lab1 {
    public static void main(String[] args){
        DocumentCollection new_collection = new DocumentCollection("/Users/kassandrawinter/Desktop/csc-466-03/labs/documents.txt"); // create an instance of the DocumentCollection class

        // ending statistic variables
        String most_frequent_word = "";
        int highest_frequency = 0;

        for (TextVector textVector : new_collection.getDocuments()) { // go through the TextVector objects in the DocumentCollection
            int curr_frequency = textVector.getHighestRawFrequency(); 
            if (curr_frequency > highest_frequency) { // find the highest frequency of all the objects
                highest_frequency = curr_frequency;
                most_frequent_word = textVector.getMostFrequentWord();
            }
        }

        int distinct_words = 0;
        int total_words = 0;
        Set<Map.Entry<Integer, TextVector>> entrySet = new_collection.getEntrySet();
        for (Map.Entry<Integer, TextVector> entry : entrySet) {   // calculate the distinct word count and total word count
            TextVector textVector = entry.getValue();
            distinct_words += textVector.getDistinctWordCount();
            total_words += textVector.getTotalWordCount();
        }

        // print the statistics found
        System.out.println("Word = " + most_frequent_word);
        System.out.println("Frequency = " + highest_frequency);
        System.out.println("Distinct Number of Words = " + distinct_words);
        System.out.println("Total word count = " + total_words);

        // serialize!! woo hoo!
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(new File("documents_serialized.txt")))) {
            os.writeObject(new_collection);
        } catch (Exception e) {
            System.out.println(e);
        }
	}
    
}
