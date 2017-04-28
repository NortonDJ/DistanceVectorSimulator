import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by nortondj on 4/27/17.
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
