/**
 * Created by nortondj on 4/29/17.
 */
public class UDPPractice {

    public static void main(String[] args){
        Thread sender = new Thread(new UDPSender(9876));
        Thread r1 = new Thread(new UDPReceiver(9877));
        Thread r2 = new Thread(new UDPReceiver(9878));
        sender.start();
        r1.start();
        r2.start();
    }

}
