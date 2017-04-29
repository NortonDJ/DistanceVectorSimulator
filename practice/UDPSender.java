import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

/**
 * Created by nortondj on 4/29/17.
 */
public class UDPSender implements Runnable{
    private DatagramSocket socket;

    public UDPSender(int port) {
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        UDPSender sender;
        if (args.length == 0) {
            sender = new UDPSender(9876);
        } else {
            sender = new UDPSender(Integer.parseInt(args[0]));
        }
        sender.run();
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            boolean open = true;
            while (open) {
                String line = in.readLine();
                if(line.equals("quit")){
                    open = false;
                } else {
                    String lineSpaces = line.replace(" ", "");
                    udpSend(lineSpaces, "127.0.0.1", 9877);
                    udpSend(lineSpaces, "127.0.0.1", 9878);
                }
            }
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void udpSend(String message, String ip, int port) {
        try {
            byte[] bytes = message.getBytes();
            InetAddress IPAddress;
            IPAddress = InetAddress.getByName(ip);
            DatagramPacket sendPacket =
                    new DatagramPacket(bytes, bytes.length,
                            IPAddress, port);
            socket.send(sendPacket);
            System.out.println(this);
            System.out.println("    sent message: " + message);
            System.out.println("        to " + ip + ":" + port);
        } catch (UnknownHostException e) {
            System.out.println(e.getMessage());
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

}
