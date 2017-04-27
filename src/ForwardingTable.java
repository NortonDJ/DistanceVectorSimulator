import java.util.HashMap;

/**
 * Created by nortondj on 4/26/17.
 */
public class ForwardingTable {
    private HashMap<SocketAddress, DistanceVector> vectorMap;

    public ForwardingTable(){
        this.vectorMap = new HashMap<>();
    }
}
