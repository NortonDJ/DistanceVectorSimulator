import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by nortondj on 4/26/17.
 */
public class ThreadPractice {

    private ArrayList<String> typed;

    public ThreadPractice(){
        this.typed = new ArrayList<>();
    }
    public static void main(String[] args) {
        System.out.println("Processors available: " + Runtime.getRuntime().availableProcessors());
        ThreadPractice tp = new ThreadPractice();
        tp.simulate();
    }

    public void simulate(){
        // create a pool of max size 4, we expect 3 threads to be made, plus the current thread
        ScheduledThreadPoolExecutor threadPool = new ScheduledThreadPoolExecutor(4);

        threadPool.scheduleAtFixedRate(new UpdateThread(this), 0, 100, TimeUnit.SECONDS);
        threadPool.scheduleAtFixedRate(new ListenThread(), 0, 100, TimeUnit.SECONDS);
        threadPool.scheduleAtFixedRate(new CommandThread(this), 0, 100, TimeUnit.SECONDS);
    }

    public void showLines(){
        System.out.println("Show Lines invoked...");
        System.out.println(typed);
        System.out.println("CLEARING LINES");
        typed.clear();
    }

    public void addLine(String line){
        typed.add(line);
    }

}
