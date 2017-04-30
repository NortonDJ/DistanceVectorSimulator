import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

/**
 * Created by nortondj on 4/30/17.
 */
public class DVUDPSender {
    private DatagramSocket socket;

    public DVUDPSender(int port) {
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            System.exit(1);
        }
    }

    public void udpSend(String message, SocketAddress address) {
        try {
            byte[] bytes = message.getBytes();
            String ip = address.getIp();
            int port = address.getPort();
            InetAddress IPAddress;
            IPAddress = InetAddress.getByName(ip);
            DatagramPacket sendPacket =
                    new DatagramPacket(bytes, bytes.length,
                            IPAddress, port);
            message += " " + address;
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
