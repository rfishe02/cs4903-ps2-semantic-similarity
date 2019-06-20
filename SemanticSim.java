
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Iterator;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.PriorityQueue;

import java.util.HashMap;
import java.util.Map;

public class SemanticSim {

    static HashMap<String,Integer> vocab;
    static int window = 3;

    public static void main(String[] args) {
        
        float[][] tcm = buildTermContextMatrix(args[0]);
        
        int u = vocab.get("roses");
        
        getContext(tcm,5,u);
        

    }
    
    /** This method accepts a corpus with each term listed on a single line. 
        It packs these terms into segments, which are used to find contextual terms.
        
        This method takes t time to iterate over tokens.

        This method takes |V| space to store distinct tokens.
        It also takes |V| x |V| space to store the frequencies.
        
        Altogether, it takes |V| + |V| x |V| space.
    */
    
    public static float[][] buildTermContextMatrix(String document) {
        float[][] tcm = null;

        try {
            vocab = getVocab(document);
            
            BufferedReader br = new BufferedReader(new FileReader(document));
            String[] prev = null;
            String[] next = new String[window];
            tcm = new float[vocab.size()][vocab.size()];
            String read;
            int i = 0;
            
            int[] sum = new int[vocab.size() + 1];
    
            while((read=br.readLine())!=null) {
                next[i] = read;
                i++;
                
                if(i == window) {
                
                    if(prev != null) {
                        countTerms(tcm,window,prev,next,sum);
                    }

                    i = 0;
                    prev = next;
                    next = new String[window];
                }
            }
            
            countTerms(tcm,window,prev,next,sum);
 
            weightTerms(tcm,sum);
            
            //printContextMatrix(vocab,tcm);
            //printSums(vocab,tcm,sum);
 
            br.close();
        
        } catch(IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        
        return tcm;
    }
 
    /** 
        Pre-reads the file to obtain |V|. The vocab will be used to
        find the location of terms in the matrix.
    */
    
    public static HashMap<String,Integer> getVocab(String filename) {
        HashMap<String,Integer> vocab = null;
  
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            vocab = new HashMap<>();
            String[] spl;
            String read;
            int key = 0;
        
            while((read = br.readLine())!=null) {
                spl = read.split(" ");
            
                for(String s : spl) {
                    
                    if(!vocab.containsKey(s)) {
                        vocab.put(s,key);
                        key++;
                    }
                    
                }
            }
            
        } catch(IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        
        return vocab;
    }

    /**
    Use a faux-matrix to count the contextual terms of a segment.
    It pretends that the given segments are a S x S matrix.
    
    The two loops use their variables and offset values
    to find the contextual terms for each word in the segment. 
    It also finds contextual terms that overlap between to separate segments.
    
    The outer loop takes w time, where w represents the window size.
    The inner loop takes s time, where s represents the size of a segment.
    It takes log(|V|) time to find a word in the vocab.
    
    Altogether, it takes O( w s log(|V|) ).
    */
    
    public static void countTerms(float[][] tcm, int window, String[] prev, String[] next, int[] sum) {
        int b = 0;
        int w1, w2;
        
        for(int a = 0; a < window; a++) {
            
            for(int i = 0; i < prev.length-a; i++) {
                
                if(i < prev.length-(a+1)) {
                
                    w1 = vocab.get(prev[i]);
                    w2 = vocab.get(prev[i+(a+1)]);
                
                    tcm[ w1 ][ w2 ] ++ ;
                    tcm[ w2 ][ w1 ] ++ ;
                    
                    sum[ w1 + 1 ] ++; // Count sum, which is used to weight terms.
                    sum[ w2 + 1 ] ++;
                    sum[0] += 2;
                    
                    //System.out.println(i+": "+prev[i]+" "+(i+(a+1))+": "+prev[i+(a+1)]);
                }

                if(prev[i+b] != null && next[a] != null) {
                
                    w1 = vocab.get(prev[i+b]);
                    w2 = vocab.get(next[a]);
                
                    tcm[ w1 ][ w2 ] ++ ;
                    tcm[ w2 ][ w1 ] ++ ;
                    
                    sum[ w1 + 1 ] ++;
                    sum[ w2 + 1 ] ++;
                    sum[0] += 2;
                    
                    //System.out.println((i+b)+": "+prev[i+b]+" "+a+": "+next[a]);
                }
            }
            b+=1;
        }
    }
    
    /** 
    
    Uses PPMI to weight the terms of the term-context matrix. 
    
    */
    
    public static void weightTerms(float[][] tcm, int[] sum) {
        double val;
        
        for(int row = 0; row < tcm.length; row++) {
        
            for (int col = 0; col < tcm[0].length; col++) {
                val = ( (double)tcm[row][col] / sum[0] ) 
                / ( ( (double)sum[row+1] / sum[0] ) * ( Math.pow(sum[col+1],0.75) / Math.pow(sum[0],0.75) ) );
                
                if(val > 0.0) {
                    val = Math.log(val) / Math.log(2);
                }
                
                val = Math.max(val,0);
                
                tcm[row][col] = (float)val;
                
                System.out.printf("%2.2f ",tcm[row][col]);
            }
            System.out.println();
        }

    }
    
    /** Calculate cosine similarity, given two rows from the matrix. */
    
    public static float calculateSimilarity( float[][] tcm, int u, int v ) {
        double one = 0.0;
        double two = 0.0;
        double tot = 0.0;
        
        for(int col = 0; col < tcm[0].length; col++) {
            tot += ( tcm[u][col] * tcm[v][col] );
            one += Math.pow( tcm[u][col],2 );
            two += Math.pow( tcm[v][col],2 );
        }
        
        return (float)((tot) / (Math.sqrt(one) * Math.sqrt(two)));
    }
    
    /** Use a priority queue to find the top ten most similiar words. */

    public static String[] getContext(float[][] tcm, int k, int u) {
        PriorityQueue<ResultObj> pq = new PriorityQueue(new ContextComparator());
        Iterator row = vocab.entrySet().iterator();
        Map.Entry rPair;
        String[] res = new String[k];
        
        while(row.hasNext()) {
            rPair = (Map.Entry)row.next();
            pq.add(new ResultObj((String)rPair.getKey(),calculateSimilarity(tcm,u,(int)rPair.getValue())));
        }
        
        for(int j = 0; j < k; j++) {
            res[j] = pq.remove().word;
            System.out.println(res[j]);
        }
    
        return res;
    } 

    //--------------------------------------------------------
    // CUSTOM CLASSES
    
    static class VocabComparator implements Comparator<String> {
        public int compare(String s1, String s2) {
            return s1.compareTo(s2);
        }
    }
    
    static class ContextComparator implements Comparator<ResultObj> {
        public int compare(ResultObj s1, ResultObj s2) {
            if(s1.score > s2.score) {
                return -1;
            } else if(s1.score < s2.score) {
                return 1;
            } else {
                return 0;
            }
        }
    }
    
    static class ResultObj {
    
        String word;
        float score;
        
        public ResultObj(String word, float score) {
            this.word = word;
            this.score = score;
        }
    
    }
    
    //--------------------------------------------------------

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
                System.out.println( cPair.getKey() +" ("+(int)cPair.getValue()+") "+ sum[(int)cPair.getValue()+1]+ " " + rPair.getKey() +" ("+(int)rPair.getValue()+") "+ tcm[(int)cPair.getValue()][(int)rPair.getValue()] );

            }
            System.out.println();
        }
    }
    
}
