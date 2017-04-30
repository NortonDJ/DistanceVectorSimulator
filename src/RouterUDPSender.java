import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;

/**
 * Created by nortondj on 4/30/17.
 */
public class RouterUDPSender {
    private DatagramSocket socket;

    public RouterUDPSender(DatagramSocket socket) {
        this.socket = socket;
    }

    public void udpSend(String message, SocketAddress via, SocketAddress destination) {
        //message format is [2, destAddress.toString().getBytes(), 15, message.getBytes()]
        String destinationString = destination.toString();
        int size = 1 + destinationString.length() + 1 + message.length();
        byte[] bytes = new byte[size];

        //add identifier
        byte identifier = 2; // start of text
        bytes[0] = identifier;

        int index = 1; //index in bytes array for next position

        //add encoding for destination
        byte[] destinationBytes = destinationString.getBytes();
        for (int i = 0; i < destinationBytes.length; i++, index++) {
            bytes[index] = destinationBytes[i];
        }

        //add separator
        bytes[index++] = 15;

        //add encoding for message
        byte[] messageBytes = message.getBytes();
        for (int i = 0; i < messageBytes.length; i++, index++) {
            bytes[index] = messageBytes[i];
        }

        //send the message to the next node
        sendBytes(bytes, via);
    }

    public void sendBytes(byte[] bytes, SocketAddress destination) {
        try {
            String ip = destination.getIp();
            int port = destination.getPort();
            InetAddress IPAddress;
            IPAddress = InetAddress.getByName(ip);
            DatagramPacket sendPacket =
                    new DatagramPacket(bytes, bytes.length,
                            IPAddress, port);
            socket.send(sendPacket);
            System.out.println(this);
            System.out.println("    sent message: " + new String(bytes));
            System.out.println("        to " + destination);
        } catch (UnknownHostException e) {
            System.out.println(e.getMessage());
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    public void udpSend(DistanceVector vector, SocketAddress destination) {
        String message = vector.toString();
        byte[] bytes = new byte[message.length() + 1];
        byte identifier = 0; // null ascii
        bytes[0] = identifier;
        byte[] messageBytes = message.getBytes();
        for (int i = 0; i < messageBytes.length; i++) {
            bytes[i + 1] = messageBytes[i];
        }
        sendBytes(bytes, destination);
    }

    public void udpSend(SocketAddress neighbor, int weight, SocketAddress destination) {
        String message = neighbor.toString() + ", " + weight;
        byte[] bytes = new byte[message.length() + 1];
        byte identifier = 1; // start of header ascii
        bytes[0] = identifier;
        byte[] messageBytes = message.getBytes();
        for (int i = 0; i < messageBytes.length; i++) {
            bytes[i + 1] = messageBytes[i];
        }
        sendBytes(bytes, destination);
    }

}
