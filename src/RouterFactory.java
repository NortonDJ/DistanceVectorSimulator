import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Create Routers according to Input file
 * 
 * @Author Yizhong Chen, Darren Norton
 * @version May.6th.2017
 */
public class RouterFactory {
    /**
     * Create Router according to Input file
     * @param filename, boolean poison
     * @return new Router
     */
    public static Router makeRouter(String filename, boolean poison){
        Scanner in = null;
        try {
            in = new Scanner(new File(filename));
        } catch (FileNotFoundException e) {
            System.out.println("File " + filename + " not found...");
            System.out.println("Current Directory: " + System.getProperty("user.dir"));
            in.close();
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
            in.close();
            return new Router(routerAddress, neighborsMap, poison);
        } catch (Exception e){
            System.out.println("Error parsing input file " + filename);
            in.close();
            System.exit(1);
            return null;
        }
    }

}
