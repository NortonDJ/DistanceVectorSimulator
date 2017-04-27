import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by nortondj on 4/26/17.
 */
public class DistanceVector {
    private HashMap<SocketAddress, Integer> valuesMap;
    private SocketAddress source;

    public DistanceVector(SocketAddress source){
        this.source = source;
        this.valuesMap = new HashMap<>();
    }

    public SocketAddress getSource(){
        return source;
    }

    public void addValue(SocketAddress address, Integer value){
        this.valuesMap.put(address, value);
    }

    public Integer getValue(SocketAddress address){
        return this.valuesMap.getOrDefault(address, Integer.MAX_VALUE);
    }

    public void applyPoison(SocketAddress destination, ArrayList<SocketAddress> path){
        if(path.isEmpty()){
            return;
        }
        for(SocketAddress s : valuesMap.keySet()){
            //if the path contains the destination, then poison it
            if(path.contains(destination)){
                valuesMap.put(s, Integer.MAX_VALUE);
            }
        }
    }
}
