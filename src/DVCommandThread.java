import java.util.Scanner;

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
                            r.print();
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
                            r.message(message, address);
                            break;
                        }
                        case ("CHANGE"): {
                            String destIp = lineScanner.next();
                            String destPort = lineScanner.next();
                            String weight = lineScanner.next();
                            SocketAddress address = new SocketAddress(destIp, Integer.parseInt(destPort));
                            r.changeWeight(address, Integer.parseInt(weight));
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
