
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Queue;
import java.util.Iterator;
import java.util.Map;

public class SemanticSim {

    public static void main(String[] args) {
    
        buildTermContextMatrix(args[0]);

    }
    
    public static HashMap<String,Integer> getVocab(String filename) {
        HashMap<String, Integer> vocab = new HashMap<>();
        
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String[] spl;
            String read;
            int col = 0;
        
            while((read = br.readLine())!=null) {
                spl = read.split(" ");
            
                for(String s : spl) {
                    if(!vocab.containsKey(s)) {
                        vocab.put(s,col);
                        col++;
                    } 
                }
            
            }
        } catch(IOException ex) {
        
        }
        
        return vocab;
    }
    
    /** This method accepts a corpus with each term listed on a single line. */
    
    public static void buildTermContextMatrix(String document) {
        
        int window = 3;
        
        try {
        
            HashMap<String,Integer> wordIndex = getVocab(document);
            BufferedReader br = new BufferedReader(new FileReader(document));
            int[][] termContentMatrix = new int[wordIndex.size()][wordIndex.size()];
            String read;
  
            String[] prev = null;
            String[] next = new String[window];
            int i = 0;
  
            while((read=br.readLine())!=null) {
     
                next[i] = read;
                i++;
                
                if(i == window) {
                
                    if(prev != null) {
                        countTerms(wordIndex,termContentMatrix,prev,next,window);
                    }

                    i = 0;
                    prev = next;
                    next = new String[window];
                    
                }
                
            }
            
            countTerms(wordIndex,termContentMatrix,prev,null,window);
            
            br.close();
            
            printContextMatrix(wordIndex,termContentMatrix);
        
        } catch(IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        
    }
    
    /** 
    
    
    
    */
    
    public static void countTerms(HashMap<String,Integer> wordIndex, int[][] termContentMatrix, String[] prev, String[] next, int window) {
    
        int b = 0;
    
        for(int a = 0; a < window; a++) {
            
            for(int i = 0; i < prev.length-a; i++) {
                 
                if(i < prev.length-(a+1)) {
                    termContentMatrix[wordIndex.get(prev[i])][wordIndex.get(prev[i+(a+1)])] +=1 ;
                    termContentMatrix[wordIndex.get(prev[i+(a+1)])][wordIndex.get(prev[i])] +=1 ;
                    System.out.println(prev[i]+" "+prev[i+(a+1)]);
                }

                if(next != null) {
                    termContentMatrix[wordIndex.get(prev[i+b])][wordIndex.get(next[a])] +=1 ;
                    termContentMatrix[wordIndex.get(next[a])][wordIndex.get(prev[i+b])] +=1 ;
                    
                    System.out.println(prev[i+b]+" "+next[a]);
                    //System.out.println((i+b)+" "+a);
                }
                
            }
            System.out.println();
            b+=1;
           
        }
    
        // int i = 0; i < prev.length; i++
        // i < prev.length-a
        //termContentMatrix[wordIndex.get(prev[i])][wordIndex.get(prev[i+a])]
    }
    
    public static void calculateSimilarity() {
        
    
    }
    
    public static void getContext() {
        
    
    }
    
    public static void printContextMatrix(HashMap<String,Integer> mp, int[][] matrix) {
    
        int colNum;
        System.out.printf("%10s ","");
    
        Iterator col = mp.entrySet().iterator();
        while(col.hasNext()) {
            Map.Entry cPair = (Map.Entry)col.next();
            System.out.printf("%8s ",cPair.getKey());
        }
        System.out.println();

        Iterator row = mp.entrySet().iterator();
        while(row.hasNext()) {
            Map.Entry rPair = (Map.Entry)row.next();
            
            col = mp.entrySet().iterator();
            colNum = 0;
            while(col.hasNext()) {
            
                if(colNum == 0) {
                    System.out.printf("%10s ",rPair.getKey());
                }
            
                Map.Entry cPair = (Map.Entry)col.next();
                System.out.printf("%8d ",matrix[(int)rPair.getValue()][(int)cPair.getValue()]);
                
                colNum++;
            }
            System.out.println();
            
        }
    
    }

}
