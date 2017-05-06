import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by nortondj on 4/30/17.
 */
public class DVCommandThread implements Runnable {

    private Router r;

    public DVCommandThread(Router r) {
        this.r = r;
    }

    public void run() {
        Scanner in = new Scanner(System.in);
        System.out.println("Hello, type !help for supported commands.\n");
        while (in.hasNextLine()) {
            try {

                String line = in.nextLine();
                Scanner lineScanner = new Scanner(line);
                if (lineScanner.hasNext()) {
                    String firstWord = lineScanner.next();
                    switch (firstWord) {
                        case ("!help"): {
                            System.out.println(
                                    "Supported commands: \n" +
                                            "PRINT - print the current node's distance vector, and the distance vectors received from the neighbors.\n" +
                                            "MSG <dst-ip> <dst-port> <msg> - send message msg to a destination with the specified address.\n" +
                                            "CHANGE <dst-ip> <dst-port> <new-weight> - change the weight between the current node and the specified node to new-weight and update the specified node about the change.\n");
                            break;
                        }
                        case ("PRINT"): {
                            long timeStart = System.currentTimeMillis();
                            ReentrantLock lock = r.getLock();
                            lock.lock();
                            try {
                                r.print();
                            } finally {
                                long timeStop = System.currentTimeMillis();
                                System.out.println("Time spent DVC with between lock and unlock: " + (timeStop-timeStart));
                                lock.unlock();
                            }
                            break;
                        }
                        case ("MSG"): {
                            String destIp = lineScanner.next();
                            String destPort = lineScanner.next();
                            String message = "";
                            if(lineScanner.hasNext()){
                                message += lineScanner.next();
                            }
                            while(lineScanner.hasNext()){
                                message += " " + lineScanner.next();
                            }
                            SocketAddress address = new SocketAddress(destIp, Integer.parseInt(destPort));
                            long timeStart = System.currentTimeMillis();
                            ReentrantLock lock = r.getLock();
                            lock.lock();
                            try {
                                r.message(message, address);
                            } finally {
                                long timeStop = System.currentTimeMillis();
                                System.out.println("Time spent DVC with between lock and unlock: " + (timeStop-timeStart));
                                lock.unlock();
                            }
                            break;
                        }
                        case ("CHANGE"): {
                            String destIp = lineScanner.next();
                            String destPort = lineScanner.next();
                            String weight = lineScanner.next();
                            SocketAddress address = new SocketAddress(destIp, Integer.parseInt(destPort));
                            long timeStart = System.currentTimeMillis();
                            ReentrantLock lock = r.getLock();
                            lock.lock();
                            try {
                                r.changeWeight(address, Integer.parseInt(weight));
                            } finally {
                                long timeStop = System.currentTimeMillis();
                                System.out.println("Time spent DVC with between lock and unlock: " + (timeStop-timeStart));
                                lock.unlock();
                            }
                            break;
                        }
                    }
                }

            } catch (Exception e) {
                System.out.println("Error, please try again");
            }
        }
    }

}
