
import java.io.Serializable;
import java.util.ArrayList;

public class TCM implements Serializable {

    ArrayList<String> vocab;
    float[][] tcm;
    
    public void setVocab(ArrayList<String> vocab) {
        this.vocab = vocab;
    }
    
    public void setTCM(float[][] tcm) {
        this.tcm = tcm;
    }
    
}
