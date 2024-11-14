package DocumentClasses;
import java.util.*;
import java.io.*;


public class DocumentCollection implements Serializable{
    private HashMap<Integer, TextVector> documents; // stores the array of words for each doccument
    public DocumentCollection() {documents = new HashMap<>();} // variable creation
    private static final long serialVersionUID = 1L;

    public static  String noiseWordArray[] = {"a", "about", "above", "all", "along", // given noise words
        "also", "although", "am", "an", "and", "any", "are", "aren't", "as", "at",
        "be", "because", "been", "but", "by", "can", "cannot", "could", "couldn't",
        "did", "didn't", "do", "does", "doesn't", "e.g.", "either", "etc", "etc.",
        "even", "ever", "enough", "for", "from", "further", "get", "gets", "got", "had", "have",
        "hardly", "has", "hasn't", "having", "he", "hence", "her", "here",
        "hereby", "herein", "hereof", "hereon", "hereto", "herewith", "him",
        "his", "how", "however", "i", "i.e.", "if", "in", "into", "it", "it's", "its",
        "me", "more", "most", "mr", "my", "near", "nor", "now", "no", "not", "or", "on", "of", "onto",
        "other", "our", "out", "over", "really", "said", "same", "she",
        "should", "shouldn't", "since", "so", "some", "such",
        "than", "that", "the", "their", "them", "then", "there", "thereby",
        "therefore", "therefrom", "therein", "thereof", "thereon", "thereto",
        "therewith", "these", "they", "this", "those", "through", "thus", "to",
        "too", "under", "until", "unto", "upon", "us", "very", "was", "wasn't",
        "we", "were", "what", "when", "where", "whereby", "wherein", "whether",
        "which", "while", "who", "whom", "whose", "why", "with", "without",
        "would", "you", "your", "yours", "yes"};

  
    // returns the TextVector for the document with the ID that is given.
    public TextVector getDocumentById(int ID) {
      return documents.get(ID);
    }

    //returns the average length of a document not counting noise words. Use the method getTotalWordCount() on each document to calculate the number of non-noise words in each document. Add up the numbers and divide by the total number of documents.
    public double getAverageDocumentLength() {
      int total = 0;
        for (TextVector textVector : documents.values()) {
          total += textVector.getTotalWordCount();
        }
        return (double) total / documents.size();
    }

    // returns number of documents
    public int getSize(){
      return documents.size();
    }

    // returns a Collection<TextVector>
    public Collection<TextVector> getDocuments(){
      return documents.values();
    }

    // returns a mapping of document id to Text Vector, that is an object of type Set<Map.Entry<Integer, TextVector>>. Use the method entrySet on the HashMap to get the result.
    public Set<Map.Entry<Integer, TextVector>> getEntrySet() {
      return documents.entrySet();
    }

    // returns the number of documents that contain the input word.
    public int getDocumentFrequency(String word) {
      int frequency = 0;
        for (TextVector textVector : documents.values()) {
            if (textVector.contains(word)) {
                frequency++;
            }
        }
        return frequency;
    }

    // a constructor that reads the file that is specified as input and it uses the data in the file to populate the documents variable.
    public DocumentCollection(String given_file) {
      documents = new HashMap<>(); // create the new document collection
        try (BufferedReader reader = new BufferedReader(new FileReader(given_file))) { // creates a buffering input stream the size of the input file
            
          String line;
          TextVector doc = null; // start as null (empty)
          int id = 0; // start as 0 (none)
          boolean isContent = false;

          while ((line = reader.readLine()) != null) { // while we have not reached the end of the given file 
            if (line.startsWith(".I ")) { // #1 INDEX
                  if (doc != null) { // .. new section found, if there is an old vector we add it into doccuments before replacing it
                    //id = Integer.parseInt(line.substring(3).trim()); // Extract the document ID  
                    
                    documents.put(id++, doc);
                  }
                  doc = new TextVector(); // .. new document section = new vector created
                  isContent = false;
              }
              
            else if (line.startsWith(".W")) { // #2 INFORMATION 
              isContent = true;
            }
            else if (isContent){
              String[] words = line.split("[^a-zA-Z]+"); // .. for each line, extract only the words (ignoring #'s and symbols)
                for (String word : words) {
                    if (word.length() >= 2 && !isNoiseWord(word.toLowerCase())) { // .. make sure it's not a noise word 
                      doc.add(word.toLowerCase());
                    }
                }

            }

          }
    
          if (doc != null) { // case where the file is empty / contains nothing
              documents.put(id, doc);
          }
          
        } catch (IOException e) { // there was something wrong creating a buffer for the file
            e.printStackTrace();
        }

    }

    // is the input a noise word
    private boolean isNoiseWord(String word){
      return Arrays.asList(noiseWordArray).contains(word.toLowerCase());
    }
  }