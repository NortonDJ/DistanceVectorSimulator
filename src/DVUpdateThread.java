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
    /**
     * Constructor of DVUpdateThread
     */
    public DVUpdateThread(Router r){
        this.r = r;
    }

    public void run(){
        ReentrantLock lock = r.getLock();
        lock.lock();
        try {
            r.broadCastTimeout();
            r.incrementTimerCounts();
        } finally {
            lock.unlock();
        }
    }

}
