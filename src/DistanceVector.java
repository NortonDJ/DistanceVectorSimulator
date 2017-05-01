import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

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

    public DistanceVector(DistanceVector other){
        this.source = other.source;
        this.valuesMap = new HashMap<>(other.valuesMap);
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
                valuesMap.put(s, Integer.MAX_VALUE);
            }
        }
    }

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
