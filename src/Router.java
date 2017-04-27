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
    private boolean poison;

    public static void main(String[] args) {

    }

    public Router(SocketAddress address) {
        this.address = address;
        this.table = new ForwardingTable();
        this.neighborsMap = new HashMap<>();
        this.vectorMap = new HashMap<>();
        addNeighborsToDistVectMap();
    }

    public Router(SocketAddress address, HashMap<SocketAddress, Integer> neighborsMap, boolean poison) {
        this.address = address;
        this.table = new ForwardingTable();
        this.neighborsMap = neighborsMap;
        this.vectorMap = new HashMap<>();
        this.poison = poison;
        addNeighborsToDistVectMap();
    }

    private void addNeighborsToDistVectMap() {
        for (SocketAddress neighbor : neighborsMap.keySet()) {
            this.vectorMap.put(neighbor, null);
        }
    }

    /**
     * Receives a distance vector update, saves it in the distance vector table,
     * Calculates the new distance vector, and if the new vector is different
     * from the old one, the router updates the forwarding table and broadcasts
     * the new distance vector to its neighbors
     *
     * @param vector the new distance vector
     */
    public void receiveDistanceVector(DistanceVector vector) {
        this.vectorMap.put(vector.getSource(), vector);

        DistanceVectorCalculation oldCalculation = this.mostRecentCalculation;
        this.mostRecentCalculation = recalculateDistanceVector();
        // if a change has occurred
        if (!mostRecentCalculation.equals(oldCalculation)) {
            updateForwardingTable(mostRecentCalculation);
            broadCastDistanceVector(mostRecentCalculation);
        }
    }

    /**
     * Recalculates the distance vector and paths to other nodes based on the
     * current distance vector map. The new distance vector is calculated as
     *
     * @return the wrapper class for the DistanceVector and the Mapping of paths
     * for each destinations
     * @<code>for all nodes the router knows about,
     * dsrc->node = min over all i{dsrc->neighbor(i) + dneighbor(i)->node}
     * @</code>
     */
    public DistanceVectorCalculation recalculateDistanceVector() {
        DistanceVector newVec = new DistanceVector(this.address);
        HashMap<SocketAddress, ArrayList<SocketAddress>> pathMap = new HashMap<>();

        // ADD dsource->source = 0, through itself
        ArrayList<SocketAddress> sourcePath = new ArrayList<>();
        sourcePath.add(address);
        newVec.addValue(address, 0);
        pathMap.put(address, sourcePath);

        // calculate rest of vectors
        for (SocketAddress destination : vectorMap.keySet()) {

            Integer minimum = Integer.MAX_VALUE;
            ArrayList<SocketAddress> path = new ArrayList<>();

            // the path to the node must include a neighbor,
            // so iterate over the neighbors
            // and choose the minimum

            for (SocketAddress neighbor : neighborsMap.keySet()) {

                //calculate distance = dsrc->neighbor + dneighbor->node

                Integer distanceToNeighbor = neighborsMap.get(neighbor);
                Integer distanceNeighborToNode;

                DistanceVector neighborDistanceVector = vectorMap.getOrDefault(neighbor, null);

                // if the neighbor has not sent a distance vector,
                //     we check if the neighbor and destination are the same
                //         if same, then dneighbor->dest = dneighbor->neighbor = 0;
                //         otherwise dneighbor->dest = inf, because router doesn't have path

                if (neighborDistanceVector == null) {
                    if (neighbor.equals(destination)) {
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

                Integer calculation;
                if (distanceToNeighbor == Integer.MAX_VALUE || distanceNeighborToNode == Integer.MAX_VALUE) {
                    calculation = Integer.MAX_VALUE;
                } else {
                    calculation = distanceToNeighbor + distanceNeighborToNode;
                }

                if (calculation < minimum) {
                    path.clear();
                    path.add(this.address);
                    path.add(neighbor);
                    minimum = calculation;
                }
            }

            // Add the minimum distance from this node to the destination to DV,
            // Add the knowledge of node->(neighbor if exist)->dest
            newVec.addValue(destination, minimum);
            // if the destination is reachable, add it to the path
            if(minimum != Integer.MAX_VALUE){
                path.add(destination);
            }
            pathMap.put(destination, path);
        }
        return new DistanceVectorCalculation(newVec, pathMap);
    }

    /**
     * Broadcasts the given DistanceVectorCalculation to all neighbors.
     * If the router is set to poison, then it will do so here by copying the
     * distance vector from the calculation and applying the poison on an individual
     * basis per neighbor.
     *
     * @param calculation
     */
    public void broadCastDistanceVector(DistanceVectorCalculation calculation) {
        HashMap<SocketAddress, ArrayList<SocketAddress>> nextHops = calculation.getPathMap();
        for (SocketAddress neighbor : neighborsMap.keySet()) {
            DistanceVector toSend = prepareDistanceVectorToSend(neighbor, calculation);
            sendDistanceVector(neighbor, toSend);
        }
    }

    /**
     * This is a subroutine for broadcasting distance vectors. This method will
     * create a copy of the distance vector from the calculation, and apply poison
     * if necessary.
     * @param destination the destination to send the distance vector to
     * @param calculation the calculation object containing the DV and paths
     * @return the distance vector, with poison if necessary
     */
    public DistanceVector prepareDistanceVectorToSend(SocketAddress destination, DistanceVectorCalculation calculation){
        DistanceVector individualizedVec = new DistanceVector(calculation.getResultVector());
        if(poison){
            HashMap<SocketAddress, ArrayList<SocketAddress>> pathMap = calculation.getPathMap();
            individualizedVec.applyPoison(destination, pathMap);
        }
        return individualizedVec;
    }

    public void updateForwardingTable(DistanceVectorCalculation calculation) {
        System.out.println("UPDATE FORWARDING TABLE NOT SUPPORTED YET");
    }

    public void sendDistanceVector(SocketAddress neighbor, DistanceVector distanceVector) {
        System.out.println("SENDING NOT SUPPORTED YET");
    }

    public String getIP() {
        return this.address.getIp();
    }

    public Integer getPort() {
        return this.address.getPort();
    }

    public boolean hasNeighbor(SocketAddress address) {
        return neighborsMap.containsKey(address);
    }

    public int getWeight(SocketAddress address) {
        return neighborsMap.getOrDefault(address, -1);
    }

}
