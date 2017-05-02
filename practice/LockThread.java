import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by nortondj on 5/2/17.
 */
public class LockThread implements Runnable{
    private DummyObject dummy;

    public LockThread(DummyObject dummy){
        this.dummy = dummy;
    }

    public void run(){
        System.out.println("LT RUN INVOKED at time " + getCurrTime());
        ReentrantLock lock = dummy.getLock();
        lock.lock();
        System.out.println("LT LOCKED at time " + getCurrTime());
        try {
            Thread.sleep(5000);
            dummy.print("LT");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
            System.out.println("LT UNLOCKED at time " + getCurrTime());
        }
    }

    public String getCurrTime(){
        SimpleDateFormat date = new SimpleDateFormat("HH:mm:ss");
        Date current = new Date();
        String time = date.format(current);
        return time;
    }
}
