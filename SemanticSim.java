
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Queue;

public class SemanticSim {

    public static void main(String[] args) {
    
        //buildTermContextMatrix(args[0]);
        
        String[] test = {"this","is","a","test"};
        String[] prev = {"its","just","a","test"};
        //String read = "This is a test its just a test";
        
        int window = 2;
        
        //StringTokenizer st = new StringTokenizer(read," ");
        //Queue<String> q = new Queue<>(window);
        // int len = st.countTokens();
        
        int b = test.length-window;

        for(int a = 1; a <= window; a++) {
            
            for(int i = 0; i < test.length; i++) {
                
                if(i < test.length-a) {
                    System.out.println(test[i]+" "+test[i+a]);
                }
                
                if(i >= b && a <= window) {
                    System.out.println(test[i]+" "+prev[a-1]);
                }
            
            }  
            b++;
    
        }


    }
    
    public static void buildTermContextMatrix(String document) {
        
        // Use HashSet to find duplicates?
        // For each token, word --> col index 
        //                 cword --> row index
        
        // Arrange in alphabetical order, then use binary search.
        
        // Get a cound of distinct words, decide matrix size.
        
        // Represent words differently.
        
        // Hashmap --> fill maxtrix
        
        // Need Pr(Word), Pr(w1, w2), total
        
        //HashMap<String,Integer> wordIndex = new HashMap<>();
        int window = 3;
        
        try {
        
            BufferedReader br = new BufferedReader(new FileReader(document));
            String[] spl;
            String read;
            int col = 0;
            
            while((read=br.readLine())!=null) {
                spl = read.split(" ");
                
                
                
            }
            
            br.close();
            
        
        } catch(IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        
    }
    
    public static void calculateSimilarity() {
        
    
    }
    
    public static void getContext() {
        
    
    }

}
