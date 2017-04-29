import java.net.DatagramPacket;
import java.net.DatagramSocket;
/**
 * Created by nortondj on 4/29/17.
 */
public class UDPReceiver implements Runnable{

    private DatagramSocket welcomeSocket;

    public UDPReceiver(int port){
        try {
            this.welcomeSocket = new DatagramSocket(port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        if(args.length == 0) {

        } else {
            UDPReceiver r = new UDPReceiver(Integer.parseInt(args[0]));
            r.run();
        }
    }

    public void run(){
        try {
            while (true) {
                byte[] receive = new byte[1024];
                DatagramPacket p = new DatagramPacket(receive, receive.length);
                welcomeSocket.receive(p);
                String clientSentence = new String(p.getData());
                System.out.println("Receiver received: " + clientSentence);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
