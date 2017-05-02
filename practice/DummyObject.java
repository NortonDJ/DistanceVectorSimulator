import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by nortondj on 5/2/17.
 */
public class DummyObject {
    private int count;
    private ReentrantLock lock;

    public static void main(String[] args){
        if(args.length == 0){
            DummyObject.demo1();
        } else {
            DummyObject.demo2();
        }
    }

    public static void demo1(){
        DummyObject d = new DummyObject();
        ScheduledThreadPoolExecutor threadPool = new ScheduledThreadPoolExecutor(4);
        threadPool.scheduleAtFixedRate(new LockThread(d), 0, 10, TimeUnit.SECONDS);
        threadPool.scheduleAtFixedRate(new RepeatThread(d), 1, 500, TimeUnit.MILLISECONDS);
    }

    public static void demo2(){
        DummyObject d = new DummyObject();
        ScheduledThreadPoolExecutor threadPool = new ScheduledThreadPoolExecutor(4);
        threadPool.scheduleAtFixedRate(new LockThread(d), 0, 10, TimeUnit.SECONDS);
        threadPool.scheduleAtFixedRate(new RepeatThreadWithLock(d), 1, 1500, TimeUnit.MILLISECONDS);
    }

    public DummyObject(){
        this.count = 0;
        this.lock = new ReentrantLock();
    }

    public void print(String caller){
        System.out.println(caller + " My count: " + count++);
    }

    public ReentrantLock getLock(){
        return this.lock;
    }
}
