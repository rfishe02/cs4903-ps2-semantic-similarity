
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class Calc implements Callable {

    double a;
    double b;
    double c;
    double d;
    
    public Calc( double a, double b, double c, double d ) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    public Object call() throws Exception  {

        double v = (a / d) / ( ( b / d ) 
        * ( Math.pow(c,0.75) / Math.pow(d,0.75) ) );
        
        if(v > 0.0001) {
            v = Math.log(v) / Math.log(2);
        }
                    
        v = Math.max(v,0);
        
        return v;
        
    }   

}
