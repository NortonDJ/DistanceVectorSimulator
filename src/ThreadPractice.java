import java.util.ArrayList;

/**
 * Created by nortondj on 4/26/17.
 */
public class ThreadPractice {

    private ArrayList<String> typed;

    public ThreadPractice(){
        this.typed = new ArrayList<>();
    }
    public static void main(String[] args) {
        ThreadPractice tp = new ThreadPractice();
        tp.start();
    }

    public void start(){
        Thread updateThread = new Thread(new UpdateThread(this));
        Thread listenThread = new Thread(new ListenThread());
        Thread commandThread = new Thread(new CommandThread(this));

        updateThread.start();
        listenThread.start();
        commandThread.start();
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
