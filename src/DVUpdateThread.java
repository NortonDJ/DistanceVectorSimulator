import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by nortondj on 4/30/17.
 */
public class DVUpdateThread implements Runnable{

    private Router r;

    public DVUpdateThread(Router r){
        this.r = r;
    }

    public void run(){
        long timeStart = System.currentTimeMillis();
        ReentrantLock lock = r.getLock();
        lock.lock();
        try {
            r.broadCastTimeout();
            r.incrementTimerCounts();
        } finally {
            long timeStop = System.currentTimeMillis();
            System.out.println("Time spent DVU with between lock and unlock: " + (timeStop-timeStart));
            lock.unlock();
        }
    }

}
