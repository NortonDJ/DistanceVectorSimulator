import java.util.ArrayList;
import java.util.HashMap;

/**
 * DistanceVectorCalculation contains both the DistanceVector, and a HashMap mapping SocketAddresses to ArrayList of 
 * paths. The HashMap can be imagined as the path from the current node to each destination node. 
 * We could have just kept the next hop in the path, reducing the ArrayList to a SocketAddress, 
 * but having an ArrayList allows for a deeper print-out if necessary for debugging.
 * 
 * @Author Yizhong Chen, Darren Norton
 * @version May.6th.2017
 */
public class DistanceVectorCalculation {

    private DistanceVector resultVector;
    private HashMap<SocketAddress, ArrayList<SocketAddress>> pathMap;

    public DistanceVectorCalculation(DistanceVector vec, HashMap<SocketAddress, ArrayList<SocketAddress>> pathMap){
        this.resultVector = vec;
        this.pathMap = pathMap;
    }

    public DistanceVector getResultVector() {
        return resultVector;
    }

    public HashMap<SocketAddress, ArrayList<SocketAddress>> getPathMap() {
        return pathMap;
    }

    @Override
    public boolean equals(Object other){
        if(other == null)
            return false;
        if(other == this)
            return true;
        try{
            DistanceVectorCalculation otherDVC = (DistanceVectorCalculation) other;
            return this.resultVector.equals(otherDVC.resultVector);
        } catch (Exception e){
            return false;
        }
    }

}
