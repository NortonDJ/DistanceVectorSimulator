/**
 * Created by nortondj on 4/26/17.
 */
public class UpdateThread implements Runnable{
    ThreadPractice tp;

    public UpdateThread(ThreadPractice tp){
        this.tp = tp;
    }
    @Override
    public void run() {
        System.out.println("Update Thread started!");
        for(int i = 1; i <= 100; i++){
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e){
                return;
            }
            System.out.println("It's been 30 seconds, COUNT = " + i);
            tp.showLines();
        }
    }
}