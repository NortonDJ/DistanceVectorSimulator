import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Simulatior of Distance-vector routing algorithm
 * We represent Distance Vectors as HashMaps mapping SocketAddresses to costs. 
 * Each Distance Vector also contains the SocketAddress of the node it originated from. 
 * The DistanceVector class is meant to be concise and serializable as it is supposed to be sent over the network via UDP.
 * 
 * @Author Yizhong Chen, Darren Norton
 * @version May.6th.2017
 */
public class DistanceVector {
    private HashMap<SocketAddress, Integer> valuesMap;
    private SocketAddress source;

    /**
     * Constructor of Distance-Vector
     */
    public DistanceVector(SocketAddress source){
        this.source = source;
        this.valuesMap = new HashMap<>();
    }

    /**
     * Constructor of Distance-Vector
     */
    public DistanceVector(DistanceVector other){
        this.source = other.source;
        this.valuesMap = new HashMap<>(other.valuesMap);
    }

    /**
     * Get the source from the Distance-Vector
     * @Return source
     */
    public SocketAddress getSource(){
        return source;
    }

    /**
     * Store corresponding value into maps
     * @param address, value
     */
    public void addValue(SocketAddress address, Integer value){
        this.valuesMap.put(address, value);
    }

    /**
     * Get value from the maps
     * @param address
     * @Return value
     */
    public Integer getValue(SocketAddress address){
        return this.valuesMap.getOrDefault(address, 16);
    }

    /**
     * Apply Poison Reverse
     * @param destination, path map
     */
    public void applyPoison(SocketAddress destination, HashMap<SocketAddress, ArrayList<SocketAddress>> pathMap){
        if(pathMap.isEmpty()){
            return;
        }
        for(SocketAddress s : valuesMap.keySet()){
            ArrayList<SocketAddress> path = pathMap.getOrDefault(s, new ArrayList<>());
            if(path.isEmpty()){
                continue;
            }
            //if the path contains the destination, then poison it
            if(path.contains(destination)){
                valuesMap.put(s, 16);
            }
        }
    }

    /**
     * Get nodes from the map
     */
    public Set<SocketAddress> getNodes(){
        return this.valuesMap.keySet();
    }

    @Override
    public boolean equals(Object o){
        if(o == null)
            return false;
        if(o == this)
            return true;
        try{
            DistanceVector other = (DistanceVector) o;
            return this.source.equals(other.source) && this.valuesMap.equals(other.valuesMap);
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public String toString(){
        String result = "";
        result += "SOURCE: " + source;
        for(SocketAddress s : valuesMap.keySet()){
            result += "\n" + s + " | " + valuesMap.get(s);
        }
        return result;
    }

    public String distancesString(){
        String result ="distances: ";
        for(SocketAddress s : valuesMap.keySet()){
            result += "\n" + s + " " + valuesMap.get(s);
        }
        return result;
    }
}
