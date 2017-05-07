import java.util.concurrent.locks.ReentrantLock;

/**
 * As per the project requirements, the Router must update its neighbors every n seconds. 
 * We schedule these updates by using a ScheduledThreadPoolExecutor. 
 * This thread handles updating behavior by invoking broadcasting methods in the Router.
 * 
 * @Author Yizhong Chen, Darren Norton
 * @version May.6th.2017
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
