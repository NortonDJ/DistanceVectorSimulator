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

}
