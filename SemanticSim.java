
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Queue;
import java.util.Iterator;
import java.util.Map;

public class SemanticSim {

    static HashMap<String,Integer> wordIndex;
    static int count;

    public static void main(String[] args) {
        
        int[][] tcm = buildTermContextMatrix(args[0]);
        weightTerms(wordIndex,tcm);

    }
    
    /** Pre-read the file to get |V|, and map terms to columns. */
    
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
    
    public static int[][] buildTermContextMatrix(String document) {
        
        int[][] termContentMatrix = null;
        int window = 3;
        
        count = 0;
        
        try {
        
            wordIndex = getVocab(document);
            BufferedReader br = new BufferedReader(new FileReader(document));
            termContentMatrix = new int[wordIndex.size()][wordIndex.size()];
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
            
            countTerms(wordIndex,termContentMatrix,prev,next,window);
            
            br.close();
            
            //printContextMatrix(wordIndex,termContentMatrix);
            //System.out.println(count);
        
        } catch(IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        
        return termContentMatrix;
        
    }
    
    /** 
    
    Use a faux-matrix to count the contextual terms.
    
    */
    
    public static void countTerms(HashMap<String,Integer> wordIndex, int[][] termContentMatrix, String[] prev, String[] next, int window) {
    
        int b = 0;
    
        for(int a = 0; a < window; a++) {
            
            for(int i = 0; i < prev.length-a; i++) {
                 
                if(i < prev.length-(a+1)) {
                    termContentMatrix[wordIndex.get(prev[i])][wordIndex.get(prev[i+(a+1)])] +=1 ;
                    termContentMatrix[wordIndex.get(prev[i+(a+1)])][wordIndex.get(prev[i])] +=1 ;
                    
                    count += 2;
                    
                    //System.out.println(prev[i]+" "+prev[i+(a+1)]);
                    //System.out.println(i+" "+(i+(a+1)));
                }

                if(prev[i+b] != null && next[a] != null) {
                    termContentMatrix[wordIndex.get(prev[i+b])][wordIndex.get(next[a])] +=1 ;
                    termContentMatrix[wordIndex.get(next[a])][wordIndex.get(prev[i+b])] +=1 ;
                    
                    count += 2;
                    
                    //System.out.println(prev[i+b]+" "+next[a]);
                    //System.out.println((i+b)+" "+a);
                }
                
            }
            //System.out.println();
            
            b+=1;
           
        }

    }
    
    /** Use PPMI to weight the frequencies of the term content matrix. */
    
    public static void weightTerms(HashMap<String,Integer> wordIndex, int[][] termContentMatrix) {
        
        // Pr( w_1, w_2 ) / Pr( w_1 ) * Pr_a( w_2 )
        // Pr_a( w_2 ) = | w_2 |^a / SUM ( | c |^a)
        
        double val;
        
        for(int i = 0; i < termContentMatrix.length; i++) {
        
            for (int j = 0; j <= i; j++) {
                System.out.print(termContentMatrix[i][j]+" ");
            }
            System.out.println();
            
        }
        
        /*
        Iterator row = wordIndex.entrySet().iterator();
        Iterator col = wordIndex.entrySet().iterator();
        Map.Entry rPair;
        Map.Entry cPair;
        
        while(row.hasNext()) {
            rPair = (Map.Entry)row.next();
            
            col = wordIndex.entrySet().iterator();
            while(col.hasNext()) {
            
                cPair = (Map.Entry)col.next();
                System.out.println( cPair.getKey() + " " + rPair.getKey() );

            }
            System.out.println();
            
        }*/
        
    }
    
    public static void calculateSimilarity() {
        
    
    }
    
    public static void getContext() {
        
    
    }
    
    /** Print the matrix to the console. */
    
    public static void printContextMatrix(HashMap<String,Integer> wordIndex, int[][] matrix) {
    
        Iterator row = wordIndex.entrySet().iterator();
        Iterator col = wordIndex.entrySet().iterator();
        Map.Entry rPair;
        Map.Entry cPair;
        int colNum;
        
        System.out.printf("%10s ","");
         
        while(col.hasNext()) {
            cPair = (Map.Entry)col.next();
            System.out.printf("%8s ",cPair.getKey());
        }
        System.out.println();

        while(row.hasNext()) {
            rPair = (Map.Entry)row.next();
            
            col = wordIndex.entrySet().iterator();
            colNum = 0;
            while(col.hasNext()) {
            
                if(colNum == 0) {
                    System.out.printf("%10s ",rPair.getKey());
                }
            
                cPair = (Map.Entry)col.next();
                System.out.printf("%8d ",matrix[(int)rPair.getValue()][(int)cPair.getValue()]);
                
                colNum++;
            }
            System.out.println();
            
        }
    
    }

}
