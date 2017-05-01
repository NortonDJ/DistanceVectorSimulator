import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created by nortondj on 4/30/17.
 */
public class RouterUDPReceiver implements Runnable {

    private DatagramSocket socket;
    private Router r;

    public RouterUDPReceiver(DatagramSocket socket, Router r) {
        this.socket = socket;
        this.r = r;
    }

    public static void main(String[] args) {
        if (args.length == 0) {

        } else {
            UDPReceiver r = new UDPReceiver(Integer.parseInt(args[0]));
            r.run();
        }
    }

    public void run() {
        try {
            while (true) {
                byte[] receive = new byte[1024];
                DatagramPacket p = new DatagramPacket(receive, receive.length);
                socket.receive(p);
                decode(p.getData());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void decode(byte[] bytes) {
        try {
            byte first = bytes[0];
            switch (first) {
                case (0): {
                    byte[] decoded = new byte[bytes.length - 1];
                    for (int i = 0; i < bytes.length - 1; i++) {
                        decoded[i] = bytes[i + 1];
                    }
                    String message = new String(decoded);
                    // received a distance vector
                    r.receiveDistanceVector(DistanceVectorFactory.makeDistanceVector(message));
                    break;
                }
                case (1): {
                    byte[] decoded = new byte[bytes.length - 1];
                    for (int i = 0; i < bytes.length - 1; i++) {
                        decoded[i] = bytes[i + 1];
                    }
                    String message = new String(decoded);
                    String[] words = message.split("[:,]");
                    String ip = words[0];
                    int port = Integer.parseInt(words[1]);
                    int weight = Integer.parseInt(words[2].trim());
                    SocketAddress neighbor = new SocketAddress(ip, port);
                    r.receiveWeight(neighbor, weight);
                    break;
                }
                case (2): {
                    //extract the destination from the message
                    int delimIndex = 0;
                    //   search for the 15 character
                    for (int i = 1; i < bytes.length; i++) {
                        if (bytes[i] == 15) {
                            delimIndex = i;
                            break;
                        }
                    }
                    //form a byte[] of the informatoin encoding the destination
                    byte[] destBytes = new byte[delimIndex - 1];
                    for (int i = 0; i < delimIndex - 1; i++) {
                        destBytes[i] = bytes[i + 1];
                    }
                    //convert the destBytes[] to a string representing the dest address
                    String destString = new String(destBytes);
                    String[] destWords = destString.split(":");
                    String destIp = destWords[0];
                    int destPort = Integer.parseInt(destWords[1]);
                    SocketAddress destAddress = new SocketAddress(destIp, destPort);

                    //deal with message
                    byte[] messageBytes = new byte[bytes.length - 2 - destBytes.length];
                    for (int i = 0; i < messageBytes.length; i++) {
                        messageBytes[i] = bytes[delimIndex + 1 + i];
                    }
                    String message = new String(messageBytes);
                    r.receiveMessage(message, destAddress);
                    break;
                }
                default: {
                    System.out.println("Error has occurred in receiving message");
                    System.exit(1);
                }
            }
        } catch (Exception e) {
            System.out.println("Exception has occurred in receiving message");
            e.printStackTrace();
        }
    }

}
