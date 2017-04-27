import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by nortondj on 4/26/17.
 */
public class CommandThread implements Runnable{
    private ThreadPractice tp;

    public CommandThread(ThreadPractice tp){
        this.tp = tp;
    }

    @Override
    public void run(){
        System.out.println("Command Thread started!");
        Scanner in = new Scanner(System.in);
        System.out.println("Type some phrases!");
        while(in.hasNextLine()){
            tp.addLine(in.nextLine());
        }
    }
}