import java.net.Socket;
import java.util.HashMap;
import java.util.NoSuchElementException;

/**
 * Created by nortondj on 4/26/17.
 */
public class Router {
    private ForwardingTable table;
    private DistanceVector current;
    private SocketAddress address;
    private HashMap<SocketAddress, Integer> neighborsMap;

    public static void main(String[] args){

    }

    public Router(SocketAddress address){
        this.address = address;
        this.table = new ForwardingTable();
        this.current = new DistanceVector();
        this.neighborsMap = new HashMap<>();
    }

    public Router(SocketAddress address, HashMap<SocketAddress, Integer> neighborsMap){
        this.address = address;
        this.table = new ForwardingTable();
        this.current = new DistanceVector();
        this.neighborsMap = neighborsMap;
    }

    public String getIP(){
        return this.address.getIp();
    }

    public Integer getPort(){
        return this.address.getPort();
    }

    public boolean hasNeighbor(SocketAddress address){
        return neighborsMap.containsKey(address);
    }

    public int getWeight(SocketAddress address){
        return neighborsMap.getOrDefault(address, -1);
    }
}
