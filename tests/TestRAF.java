
import java.io.*;


public class TestRAF {

  public static void main (String[] args) {

    int[][] block = new int[2][5];

    try {

      //createRAF();

      RandomAccessFile test = new RandomAccessFile("text.raf","rw");



      for(int i = 0; i < (test.length()/4)/(block.length * block[0].length); i++) {

        System.out.println(i);

        for(int a = 0; a < block.length; a++) {
          for(int b = 0; b < block[0].length; b++) {
            block[a][b] = test.readInt();
          }
        }

        for(int x = 0; x < block.length; x++) {
          for(int y = 0; y < block[0].length; y++) {
            System.out.print(block[x][y]+" ");
          }
          System.out.println();
        }

      }

      /*
      int row = 100;
      int col = 100;
      int size = 5;
      int bytes = 4;

      test.seek( (bytes * (row * size)) + (bytes * col) );

      System.out.println(test.readInt());
      */

      test.close();

    } catch(Exception ex) {
      ex.printStackTrace();
      System.exit(1);
    }

  }

  public static void createRAF() throws IOException {
    RandomAccessFile test = new RandomAccessFile("text.raf","rw");

    for(int i = 0; i < 1000; i++) {

      test.writeInt(i);

    }

    test.close();
  }

}
