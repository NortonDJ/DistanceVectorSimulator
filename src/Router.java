import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by nortondj on 4/26/17.
 */
public class Router {
    private ForwardingTable table;
    private DistanceVectorCalculation mostRecentCalculation;
    private SocketAddress address;
    private HashMap<SocketAddress, Integer> neighborsMap;
    private HashMap<SocketAddress, DistanceVector> vectorMap;

    public static void main(String[] args){

    }

    public Router(SocketAddress address){
        this.address = address;
        this.table = new ForwardingTable();
        this.neighborsMap = new HashMap<>();
        this.vectorMap = new HashMap<>();
    }

    public Router(SocketAddress address, HashMap<SocketAddress, Integer> neighborsMap) {
        this.address = address;
        this.table = new ForwardingTable();
        this.neighborsMap = neighborsMap;
    }

    public void receiveDistanceVector(DistanceVector vector, boolean poison){
        this.vectorMap.put(vector.getSource(), vector);
        DistanceVectorCalculation oldCalculation = this.mostRecentCalculation;
        this.mostRecentCalculation = recalculateDistanceVector();
        // if a change has occurred
        if(!mostRecentCalculation.equals(oldCalculation)){
            updateForwardingTable(mostRecentCalculation);
            broadCastDistanceVector(mostRecentCalculation, poison);
        }
    }

    public DistanceVectorCalculation recalculateDistanceVector(){
        DistanceVector newVec = new DistanceVector(this.address);
        HashMap<SocketAddress, ArrayList<SocketAddress>> pathMap = new HashMap<>();
        for(SocketAddress destination : vectorMap.keySet()){

            Integer minimum = Integer.MAX_VALUE;
            ArrayList<SocketAddress> path = new ArrayList<>();

            // the path to the node must include a neighbor,
            // so iterate over the neighbors
            // and choose the minimum

            for(SocketAddress neighbor : neighborsMap.keySet()){

                //calculate distance = dsrc->neighbor + dneighbor->node

                Integer distanceToNeighbor = neighborsMap.get(neighbor);
                Integer distanceNeighborToNode;

                DistanceVector neighborDistanceVector = vectorMap.getOrDefault(neighbor, null);

                // if the neighbor has not sent a distance vector,
                //     we check if the neighbor and destination are the same
                //         if same, then dneighbor->dest = dneighbor->neighbor = 0;
                //         otherwise dneighbor->dest = inf, because router doesn't have path

                if(neighborDistanceVector == null) {
                    if(neighbor.equals(destination)){
                        // destination and neighbor are the same
                        distanceNeighborToNode = 0;
                    } else {
                        // destination and neighbor are different and
                        // no direct path from neighbor->dest
                        distanceNeighborToNode = Integer.MAX_VALUE;
                    }
                } else {
                    // we know dneighbor->dest
                    distanceNeighborToNode = neighborDistanceVector.getValue(destination);
                }


                Integer calculation = distanceToNeighbor + distanceNeighborToNode;

                if(calculation < minimum){
                    path.clear();
                    path.add(this.address);
                    path.add(neighbor);
                    minimum = calculation;
                }
            }

            // Add the minimum distance from this node to the destination to DV,
            // Add the knowledge of node->(neighbor if exist)->dest
            newVec.addValue(destination, minimum);
            pathMap.put(destination, path);
        }
        return new DistanceVectorCalculation(newVec, pathMap);
    }

    public void broadCastDistanceVector(DistanceVectorCalculation calculation, boolean poison){
        HashMap<SocketAddress, ArrayList<SocketAddress>> nextHops = calculation.getPathMap();
        for(SocketAddress neighbor : neighborsMap.keySet()){
            DistanceVector individualizedVec = calculation.getResultVector();
            HashMap<SocketAddress,ArrayList<SocketAddress>> pathMap = calculation.getPathMap();
            ArrayList<SocketAddress> path = pathMap.getOrDefault(neighbor, new ArrayList<>());

            if(poison){
                individualizedVec.applyPoison(neighbor, path);
            }
            sendDistanceVector(neighbor, individualizedVec);
        }
    }

    public void updateForwardingTable(DistanceVectorCalculation calculation){
        System.out.println("UPDATE FORWARDING TABLE NOT SUPPORTED YET");
    }

    public void sendDistanceVector(SocketAddress neighbor, DistanceVector distanceVector){
        System.out.println("SENDING NOT SUPPORTED YET");
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
