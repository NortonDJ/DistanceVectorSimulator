import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by nortondj on 5/2/17.
 */
public class RepeatThread implements Runnable {
    private DummyObject dummy;

    public RepeatThread(DummyObject dummy){
        this.dummy = dummy;
    }

    public void run(){
        System.out.println("RT RUN INVOKED at time " + getCurrTime());
        dummy.print("RT");
    }

    public String getCurrTime(){
        SimpleDateFormat date = new SimpleDateFormat("HH:mm:ss");
        Date current = new Date();
        String time = date.format(current);
        return time;
    }
}
