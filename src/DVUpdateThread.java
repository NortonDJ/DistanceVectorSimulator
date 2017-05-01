/**
 * Created by nortondj on 4/30/17.
 */
public class DVUpdateThread implements Runnable{

    private Router r;

    public DVUpdateThread(Router r){
        this.r = r;
    }

    public void run(){
        r.broadCastDistanceVector(r.getMostRecentCalculation());
        r.incrementTimerCounts();
    }

}
