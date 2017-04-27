import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by nortondj on 4/27/17.
 */
public class RouterFactory {

    public static Router makeRouter(String filename){
        Scanner in = null;
        try {
            in = new Scanner(new File(filename));
        } catch (FileNotFoundException e) {
            System.out.println("File " + filename + " not found...");
            System.out.println("Current Directory: " + System.getProperty("user.dir"));
            System.exit(1);
        }
        try {
            String firstLine = in.nextLine();
            String[] words = firstLine.split(" ");
            String routerIp = words[0];
            Integer routerPort = Integer.parseInt(words[1]);
            SocketAddress routerAddress = new SocketAddress(routerIp, routerPort);
            HashMap<SocketAddress, Integer> neighborsMap = new HashMap<>();
            while(in.hasNextLine()){
                String neighbor = in.nextLine();
                words = neighbor.split(" ");
                String neighborIp = words[0];
                Integer neighborPort = Integer.parseInt(words[1]);
                Integer weight = Integer.parseInt(words[2]);
                SocketAddress neighborAddress = new SocketAddress(neighborIp, neighborPort);
                neighborsMap.put(neighborAddress, weight);
            }
            return new Router(routerAddress, neighborsMap);
        } catch (Exception e){
            System.out.println("Error parsing input file " + filename);
            System.exit(1);
            return null;
        }
    }

}
