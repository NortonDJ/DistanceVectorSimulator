import java.util.HashMap;

/**
 * Created by nortondj on 4/26/17.
 */
public class ForwardingTable {
    // a -> b, where a is the desired address, b is the node to send to
    private HashMap<SocketAddress, SocketAddress> table;

    public ForwardingTable(){
        this.table = new HashMap<>();
    }
}
