
import java.io.Serializable;
import java.util.ArrayList;

public class TCM implements Serializable {

    ArrayList<String> vocab;
    float[][] tcm;
    long time;

    public void setVocab(ArrayList<String> vocab) {
        this.vocab = vocab;
    }

    public void setTCM(float[][] tcm) {
        this.tcm = tcm;
    }

    public void setTime(long time) {
      this.time = time;
    }

}
