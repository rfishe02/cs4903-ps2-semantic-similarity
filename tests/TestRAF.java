
import java.io.*;


public class TestRAF {

  public static void main (String[] args) {

    try {

      //createRAF();

      RandomAccessFile test = new RandomAccessFile("text.raf","rw");

      int row = 100;
      int col = 100;
      int size = 5;
      int bytes = 4;

      test.seek( (bytes * (row * size)) + (bytes * col) );

      System.out.println(test.readInt());

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
  }

}
