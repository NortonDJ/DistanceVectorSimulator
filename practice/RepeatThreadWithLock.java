import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by nortondj on 5/2/17.
 */
public class RepeatThreadWithLock implements Runnable {
    private DummyObject dummy;

    public RepeatThreadWithLock(DummyObject dummy){
        this.dummy = dummy;
    }

    public void run(){
        System.out.println("RTWL RUN INVOKED at time " + getCurrTime());
        ReentrantLock lock = dummy.getLock();
        lock.lock();
        System.out.println("RTWL LOCKED at time " + getCurrTime());
        try {
            dummy.print("RTWL");
        } finally {
            lock.unlock();
            System.out.println("RTWL UNLOCKED at time " + getCurrTime());
        }
    }

    public String getCurrTime(){
        SimpleDateFormat date = new SimpleDateFormat("HH:mm:ss");
        Date current = new Date();
        String time = date.format(current);
        return time;
    }
}
