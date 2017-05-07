import java.util.Scanner;

/**
 * Create Distance-Vector Routing algorithm
 */
public class DistanceVectorFactory {

    public static void main(String[] args){
        System.out.println(DistanceVectorFactory.makeDistanceVector(
                "SOURCE: 127.0.0.1:9876\n" +
                "127.0.0.1:9878 | 1\n" +
                "127.0.0.1:9877 | 1\n" +
                "127.0.0.1:9876 | 0\n" +
                "127.0.0.1:9879 | 1"));
    }

    /**
     * Create Distance-Vector Routing algorithm accordint to input file
     */
    public static DistanceVector makeDistanceVector(String reconcile) {
        try {
            /*
             *  example DV string :
             *  SOURCE: 127.0.0.1:9876
             *  127.0.0.1:9878 | 1
             *  127.0.0.1:9877 | 1
             *  127.0.0.1:9876 | 0
             *  127.0.0.1:9879 | 1
             */

            //GETTING SOURCE
            Scanner lines = new Scanner(reconcile);
            Scanner sourceLine = new Scanner(lines.nextLine());
            sourceLine.next(); // skip "SOURCE: "
            String source = sourceLine.next();
            sourceLine.close();
            String[] srcWords = source.split(":");

            SocketAddress sourceAddress = new SocketAddress(srcWords[0], Integer.parseInt(srcWords[1]));

            //Create vector to return
            DistanceVector vector = new DistanceVector(sourceAddress);

            //GETTING NODE COSTS
            while (lines.hasNext()) {
                String line = lines.nextLine();
                if(line.isEmpty()){
                    continue;
                } else {
                    String[] words = line.split("[: ]");
                    // splittings words like this gives: [127.0.0.1, 9878, |, 1]
                    String ip = words[0];
                    int port = Integer.parseInt(words[1]);
                    //skip the |
                    int weight = Integer.parseInt(words[3].trim());

                    //Create the neighbor's address
                    SocketAddress neighbor = new SocketAddress(ip, port);

                    //add the entry to the vector's table
                    vector.addValue(neighbor, weight);
                }
            }
            return vector;
        } catch (Exception e) {
            System.out.println("Error reconciling from string: " + reconcile);
            e.printStackTrace();
            return null;
        }
    }

}
