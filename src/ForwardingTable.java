import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Fowarding tables represents the Router’s forwarding table. 
 * The ForwardingTable class is what you might expect the DistanceVectorCalculation to hold, 
 * that is, a mapping between SocketAddress to SocketAddress as the next hop. 
 * Having this data structure allows the Router to easily handle messaging traffic, 
 * where if the traffic is not destined for itself, it can look for the next hop.
 * 
 * @Author Yizhong Chen, Darren Norton
 * @version May.6th.2017
 */
public class ForwardingTable {
    // a -> b, where a is the desired address, b is the node to send to
    private HashMap<SocketAddress, SocketAddress> table;

    /**
     * Constructor of fowarding table
     */
    public ForwardingTable(){
        this.table = new HashMap<>();
    }

    /**
     * get next socket address
     * @param destination
     */
    public SocketAddress getNext(SocketAddress destination){
        return table.getOrDefault(destination, null);
    }

    public void update(DistanceVectorCalculation calculation){
        HashMap<SocketAddress, ArrayList<SocketAddress>> pathMap = calculation.getPathMap();
        DistanceVector vector = calculation.getResultVector();
        for(SocketAddress address : pathMap.keySet()){
            if(vector.getValue(address) == Integer.MAX_VALUE || vector.getValue(address) == 0){
                continue;
            }
            ArrayList<SocketAddress> path = pathMap.get(address);
            if(path.isEmpty()){
                System.out.println("Forwarding table found a path that is empty:\n" +
                        "Address : " + address);
                //System.exit(1);
                continue;
            }
            //knowing that path.get(0) is the source, next hop at index 1
            SocketAddress nextHop;
            if(path.size() == 1){
                nextHop = path.get(0);
            } else {
                nextHop = path.get(1);
            }
            table.put(address, nextHop);
        }
    }

    /**
     * Delete a path in the table
     * @param node
     */
    public void remove(SocketAddress node){
        this.table.remove(node);
    }
}
