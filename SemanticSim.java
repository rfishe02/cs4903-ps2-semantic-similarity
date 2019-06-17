
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Queue;
import java.util.Iterator;
import java.util.Map;

//-------------------------------

import java.util.Comparator;
import java.util.TreeSet;
import java.util.HashMap;
import java.util.ArrayList;

public class SemanticSim {

    static ArrayList<String> vocab;
    static boolean debug = false;

    public static void main(String[] args) {
        
        float[][] tcm = buildTermContextMatrix(args[0]);

    }
    
    /** This method accepts a corpus with each term listed on a single line. */
    
    public static float[][] buildTermContextMatrix(String document) {
        float[][] tcm = null;
        int window = 3;
       
        try {
            vocab = getVocab(document);
            
            BufferedReader br = new BufferedReader(new FileReader(document));
            tcm = new float[vocab.size()][vocab.size()];
            String[] prev = null;
            String[] next = new String[window];
            String read;
            int i = 0;
            
            int[] sum = new int[vocab.size() + 1];
    
            while((read=br.readLine())!=null) {
                next[i] = read;
                i++;
                
                if(i == window) {
                
                    if(prev != null) {
                        countTerms(vocab,tcm,window,prev,next,sum);
                    }

                    i = 0;
                    prev = next;
                    next = new String[window];
                }
            }
            
            countTerms(vocab,tcm,window,prev,next,sum);
            
            weightTerms(tcm,sum);
 
            if(debug) {
                //printContextMatrix(vocab,tcm);
                //printSums(vocab,tcm);
            }
            
            br.close();
        
        } catch(IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        
        return tcm;
    }
    
    static class VocabComparator implements Comparator<String> {
        public int compare(String s1, String s2) {
            return s1.compareTo(s2);
        }
    }
    
    /** Pre-read the file to get |V|. */
    
    public static ArrayList<String> getVocab(String filename) {
        TreeSet<String> set;
        ArrayList<String> vocab = null;
        BufferedReader br;
        
        try {
            br = new BufferedReader(new FileReader(filename));
            set = new TreeSet<>(new VocabComparator());
            vocab = new ArrayList<>(set.size());
            String[] spl;
            String read;
            int col = 0;
        
            while((read = br.readLine())!=null) {
                spl = read.split(" ");
            
                for(String s : spl) {
                    set.add(s);
                }
            }
            
            Iterator<String> it = set.iterator();
            while(it.hasNext()) {
                vocab.add(it.next());
            }
            
        } catch(IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        
        return vocab;
    }

    /** 
    
    Use a faux-matrix to count the contextual terms.
    
    */
    
    public static void countTerms(ArrayList<String> vocab, float[][] tcm, int window, String[] prev, String[] next, int[] sum) {
        int b = 0;
        int w1;
        int w2;
    
        for(int a = 0; a < window; a++) {
            
            for(int i = 0; i < prev.length-a; i++) {
                 
                if(i < prev.length-(a+1)) {
                
                    w1 = wordSearch(vocab,prev[i]);
                    w2 = wordSearch(vocab,prev[i+(a+1)]);
                
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
                
                    w1 = wordSearch(vocab,prev[i+b]);
                    w2 = wordSearch(vocab,next[a]);
                
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
    
    /** Use a binary search to get the index of a word. This is an attempt to
    avoid storing unnecessary integers to refer to a column. */
    
    public static int wordSearch(ArrayList<String> vocab, String target) {
        
        int ind = -1;
        int l = 0;
        int m;
        int r = vocab.size()-1;
        
        while(l <= r) {
        
            m = (l+r)/2;
            
            if(target.compareTo(vocab.get(m)) > 0) {
                l = m+1;
            } else if(target.compareTo(vocab.get(m)) < 0) {
                r = m-1;
            } else {
                return m;
            }
        
        }
        
        return ind;
    }    

    /** Uses PPMI to weight the terms of the term-context matrix. */
    
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
    /*
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
                System.out.println( cPair.getKey() +" ("+(int)cPair.getValue()+") "+ sum[(int)cPair.getValue()]+ " " + rPair.getKey() +" ("+(int)rPair.getValue()+") "+ tcm[(int)cPair.getValue()][(int)rPair.getValue()] );

            }
            System.out.println();
        }
    }
    */
}
