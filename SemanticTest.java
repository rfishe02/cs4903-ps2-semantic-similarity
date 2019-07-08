
public class SemanticTest {

  /**
    The main method retreives the vocabulary. The vocabulary provides the size of the term-context matrix
    and the index locations of all terms to other methods.
    @param args Accepts the commands <filename>, <window>, <word1>, and optionally <word2> from the command line.
  */

  public static void main(String[] args) {

    long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

    Semantic s = new Semantic();

    try {

      ArrayList<String> vocab = Semantic.getVocab(in);
      float[][] tcm = Semantic.buildTermContextMatrix( in,vocab,vocab.size(),Integer.parseInt(args[1]) );

      int u = Semantic.wordSearch(data.vocab, args[2]);

      if(u < 0) {
        System.out.println(args[2] +" not found in vocab.");
      } else {

          if(args.length < 4) {
            System.out.println("query: top 10 context words -- " + args[2]);
            Semantic.getContext(data,10,u);
          } else {

            int v = Semantic.wordSearch(data.vocab,args[3]);

            if(v < 0) {
              System.out.println(args[3] +" not found in vocab.");
            } else {
              System.out.println("query: similarity -- " + args[2] +" & "+ args[3]);
              System.out.println(calculateSimilarity(data.tcm,u,v));
            }

          }
      }

    } catch(Exception ex) {
        ex.printStackTrace();
        System.exit(1);
    }

    long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    System.out.println("end memory usage -- "+(endMemory/1000/1000/1000)+" GB");

  }

}
