
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Queue;
import java.util.Iterator;
import java.util.Map;

public class SemanticSim {

    static HashMap<String,Integer> wordIndex;
    static boolean debug = false;

    public static void main(String[] args) {
        
        float[][] tcm = buildTermContextMatrix(args[0]);
        

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
            ex.printStackTrace();
            System.exit(1);
        }
        
        return vocab;
    }
    
    /** This method accepts a corpus with each term listed on a single line. */
    
    public static float[][] buildTermContextMatrix(String document) {
        float[][] tcm = null;
        int window = 3;
       
        try {
            wordIndex = getVocab(document);
            
            BufferedReader br = new BufferedReader(new FileReader(document));
            tcm = new float[wordIndex.size()][wordIndex.size()];
            String[] prev = null;
            String[] next = new String[window];
            String read;
            int i = 0;
            
            int[] sum = new int[wordIndex.size() + 1];
    
            while((read=br.readLine())!=null) {
                next[i] = read;
                i++;
                
                if(i == window) {
                
                    if(prev != null) {
                        countTerms(wordIndex,tcm,window,prev,next,sum);
                    }

                    i = 0;
                    prev = next;
                    next = new String[window];
                    
                }
            }
            
            countTerms(wordIndex,tcm,window,prev,next,sum);
            
            
            weightTerms(tcm,sum);
 
            if(debug) {
                printContextMatrix(wordIndex,tcm);
                //printSums(wordIndex,tcm);
            }
            
            br.close();
        
        } catch(IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        
        return tcm;
    }
    
    /** 
    
    Use a faux-matrix to count the contextual terms.
    
    */
    
    public static void countTerms(HashMap<String,Integer> wordIndex, float[][] tcm, int window, String[] prev, String[] next, int[] sum) {
        int b = 0;
        int w1;
        int w2;
    
        for(int a = 0; a < window; a++) {
            
            for(int i = 0; i < prev.length-a; i++) {
                 
                if(i < prev.length-(a+1)) {
                
                    w1 = wordIndex.get(prev[i]);
                    w2 = wordIndex.get(prev[i+(a+1)]);
                
                    tcm[ w1 ][ w2 ] ++ ;
                    tcm[ w2 ][ w1 ] ++ ;
                    
                    sum[ w1 + 1 ] ++;
                    sum[ w2 + 1 ] ++;
                    sum[0] += 2;
                    
                    if(debug) {
                        System.out.println(i+": "+prev[i]+" "+(i+(a+1))+": "+prev[i+(a+1)]);
                    }
                }

                if(prev[i+b] != null && next[a] != null) {
                
                    w1 = wordIndex.get(prev[i+b]);
                    w2 = wordIndex.get(next[a]);
                
                    tcm[ w1 ][ w2 ] ++ ;
                    tcm[ w2 ][ w1 ] ++ ;
                    
                    sum[ w1 + 1 ] ++;
                    sum[ w2 + 1 ] ++;
                    sum[0] += 2;
                    
                    if(debug) {
                        System.out.println((i+b)+": "+prev[i+b]+" "+a+": "+next[a]);
                    }
                }
            }
            if(debug) {
                System.out.println();
            }
            
            b+=1;
        }
    }
    
    public static void weightTerms(float[][] tcm, int[] sum) {
        double val;

        for(int i = 0; i < tcm.length; i++) {
        
            for (int j = 0; j < tcm[0].length; j++) {
                val = ( (double)tcm[i][j]/sum[0] ) 
                / ( ( (double)sum[i+1]/sum[0] ) * ( Math.pow(sum[j+1],0.75)/Math.pow(sum[0],0.75) ) );
                
                if(val > 0.0) {
                    val = Math.log(val) / Math.log(2);
                }
                
                val = Math.max(val,0);
                tcm[i][j] = (float)val;
                
                if(debug) {
                    System.out.printf("%2.2f ",tcm[i][j]);
                }
            }
            if(debug) {
                System.out.println();
            }
        }
    }
    
    public static void calculateSimilarity() {
        
    
    }
    
    public static void getContext() {
        
    
    }
    
    /** Print the matrix to the console. */
    
    public static void printContextMatrix(HashMap<String,Integer> wordIndex, float[][] matrix) {
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
                System.out.printf("%8.2f ",matrix[(int)rPair.getValue()][(int)cPair.getValue()]);
                
                colNum++;
            }
            System.out.println();
        }
    }
    
    /** Print the added sums to the console. */
    
    public static void printSums(HashMap<String,Integer> wordIndex, float[][] tcm, int[] sum) {
        Iterator row = wordIndex.entrySet().iterator();
        Iterator col = wordIndex.entrySet().iterator();
        Map.Entry rPair;
        Map.Entry cPair;
        
        while(row.hasNext()) {
            rPair = (Map.Entry)row.next();
            
            col = wordIndex.entrySet().iterator();
            while(col.hasNext()) {
            
                cPair = (Map.Entry)col.next();
                System.out.println( cPair.getKey() +" ("+(int)cPair.getValue()+") "+ sum[(int)cPair.getValue()]+ " " + rPair.getKey() +" ("+(int)rPair.getValue()+") "+ tcm[(int)cPair.getValue()][(int)rPair.getValue()] );

            }
            System.out.println();
        }
    }

}
