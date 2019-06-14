
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

public class SemanticSim {

    public static void main(String[] args) {

        

    }
    
    public static void buildTermContextMatrix(String document) {
        
        // Use HashSet to find duplicates?
        // For each token, word --> col index 
        //                 cword --> row index
        
        try {
        
            BufferedReader br = new BufferedReader(new FileReader(document));
            String[] spl;
            String read;
            
            while((read=br.readLine())!=null) {
                
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
