import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by nortondj on 4/26/17.
 */
public class Router {
    private ForwardingTable table;
    private DistanceVectorCalculation mostRecentCalculation;

    public SocketAddress getAddress() {
        return address;
    }

    private SocketAddress address;
    private HashMap<SocketAddress, Integer> neighborsMap;
    private HashMap<SocketAddress, DistanceVector> vectorMap;
    private HashSet<SocketAddress> knownNodes;
    private boolean poison;
    private DatagramSocket socket;
    private RouterUDPSender sender;

    public static void main(String[] args) {
        Router r;
        if(args.length == 0){
            System.out.println("Please re-run the Router with the following format:");
            System.out.println("java Router [-reverse] <neighbors.txt>");
            return;
        } else if(args.length == 1){
            r = RouterFactory.makeRouter(args[0], false);
        } else {
            if(args[0].equals("-reverse")) {
                r = RouterFactory.makeRouter(args[1], true);
            } else {
                System.out.println("Please re-run the Router with the following format:");
                System.out.println("java Router [-reverse] <neighbors.txt>");
                return;
            }
        }
        r.start(30);
        r.close();
    }

    public void start(int timeBetweenUpdate){
        ScheduledThreadPoolExecutor threadPool = new ScheduledThreadPoolExecutor(4);
        threadPool.scheduleAtFixedRate(new DVUpdateThread(this), 0, timeBetweenUpdate, TimeUnit.SECONDS);
        threadPool.execute(new DVCommandThread(this));
        threadPool.execute(new RouterUDPReceiver(socket, this));
    }

    public void message(String message, SocketAddress destination){
        SocketAddress via = table.getNext(destination);
        if(via == null){
            System.out.println("Router could not find a way to send to " + destination);
            return;
        } else {
            sender.udpSend(message, via, destination);
        }
    }

    public void print(){
        System.out.println("Current distance vector: \n" + mostRecentCalculation.getResultVector());
    }

    public Router(SocketAddress address, HashMap<SocketAddress, Integer> neighborsMap, boolean poison) {
        this.address = address;
        this.table = new ForwardingTable();
        this.neighborsMap = neighborsMap;
        this.vectorMap = new HashMap<>();
        this.knownNodes = new HashSet<>();
        this.poison = poison;
        addNeighborsToDistVectMap();
        addNeighborsToKnownNodes();
        this.mostRecentCalculation = recalculateDistanceVector();
        updateForwardingTable(this.mostRecentCalculation);
        try {
            this.socket = new DatagramSocket(address.getPort());
        } catch(Exception e){
            System.out.println("Failed to open socket, exiting.");
            System.exit(1);
        }
        this.sender = new RouterUDPSender(socket);
    }

    private void addNeighborsToDistVectMap() {
        for (SocketAddress neighbor : neighborsMap.keySet()) {
            this.vectorMap.put(neighbor, null);
        }
    }

    private void addNeighborsToKnownNodes(){
        for(SocketAddress neighbor : neighborsMap.keySet()){
            this.knownNodes.add(neighbor);
        }
    }

    /**
     * Receives a distance vector update, saves it in the distance vector table,
     * Calculates the new distance vector, and if the new vector is different
     * from the old one, the router updates the forwarding table and broadcasts
     * the new distance vector to its neighbors
     *
     * @param vector the new distance vector
     * @return true if the vector caused a broadcast
     */
    public boolean receiveDistanceVector(DistanceVector vector) {
        System.out.println("New dv received from " + vector.getSource() +
                " with the following " + vector.distancesString());
        this.vectorMap.put(vector.getSource(), vector);
        for(SocketAddress s : vector.getNodes()){
            this.knownNodes.add(s);
        }
        return updateDistanceVector();
    }

    public boolean updateDistanceVector(){
        DistanceVectorCalculation oldCalculation = this.mostRecentCalculation;
        this.mostRecentCalculation = recalculateDistanceVector();
        // if a change has occurred
        if (!mostRecentCalculation.equals(oldCalculation)) {
            updateForwardingTable(mostRecentCalculation);
            broadCastDistanceVector(mostRecentCalculation);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Recalculates the distance vector and paths to other nodes based on the
     * current distance vector map. The new distance vector is calculated as
     * {@code
     * for all nodes the router knows about,
     *      dsrc->node = min over all i{dsrc->neighbor(i) + dneighbor(i)->node}
     * }
     * @return the wrapper class for the DistanceVector and the Mapping of paths
     * for each destination
     */
    public DistanceVectorCalculation recalculateDistanceVector() {
        DistanceVector newVec = new DistanceVector(this.address);
        HashMap<SocketAddress, ArrayList<SocketAddress>> pathMap = new HashMap<>();

        // ADD dsource->source = 0, through itself
        ArrayList<SocketAddress> sourcePath = new ArrayList<>();
        sourcePath.add(address);
        newVec.addValue(address, 0);
        pathMap.put(address, sourcePath);

        for (SocketAddress destination : knownNodes) {
            if(destination.equals(address)){
                continue;
            }
            Integer minimum = Integer.MAX_VALUE;
            ArrayList<SocketAddress> path = new ArrayList<>();

            // the path to the node must include a neighbor,
            // so iterate over the neighbors
            // and choose the minimum

            for (SocketAddress neighbor : neighborsMap.keySet()) {

                //calculate distance = dsrc->neighbor + dneighbor->node

                Integer distanceToNeighbor = neighborsMap.get(neighbor);

                DistanceVector neighborDistanceVector = vectorMap.getOrDefault(neighbor, null);
                Integer distanceNeighborToDest = findDistance(neighbor, destination, neighborDistanceVector);

                Integer calculation;
                if (distanceToNeighbor == Integer.MAX_VALUE || distanceNeighborToDest == Integer.MAX_VALUE) {
                    calculation = Integer.MAX_VALUE;
                } else {
                    calculation = distanceToNeighbor + distanceNeighborToDest;
                }

                if (calculation < minimum) {
                    path.clear();
                    path.add(this.address);
                    path.add(neighbor);
                    minimum = calculation;
                }
            }

            // Add the minimum distance from this node to the destination to DV,
            newVec.addValue(destination, minimum);
            // if the destination is reachable, add it to the path
            if(minimum != Integer.MAX_VALUE){
                // Add the knowledge of node->(destination if exist)->dest
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

    public void receiveMessage(String message, SocketAddress destination){
        if(destination.equals(address)){
            System.out.println("RECEIVED MESSAGE FINALLY: " + message);
        } else {
            message(message, destination);
        }
    }

    public Integer findDistance(SocketAddress node, SocketAddress destination, DistanceVector nodeDistanceVector){
        // if the neighbor has not sent a distance vector,
        //     we check if the neighbor and destination are the same
        //         if same, then dneighbor->dest = dneighbor->neighbor = 0;
        //         otherwise dneighbor->dest = inf, because router doesn't have path
        Integer distanceNodeToDest;
        if (nodeDistanceVector == null) {
            if (node.equals(destination)) {
                // destination and neighbor are the same
                distanceNodeToDest = 0;
            } else {
                // destination and neighbor are different and
                // no direct path from neighbor->dest
                distanceNodeToDest = Integer.MAX_VALUE;
            }
        } else {
            // we know dneighbor->dest
            if(node.equals(destination)){
                distanceNodeToDest = 0;
            } else {
                distanceNodeToDest = nodeDistanceVector.getValue(destination);
            }
        }
        return  distanceNodeToDest;
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
        table.update(calculation);
    }

    public void sendDistanceVector(SocketAddress neighbor, DistanceVector distanceVector) {
        sender.udpSend(distanceVector, neighbor);
    }

    public void sendWeightChange(int weight, SocketAddress destination){
        sender.udpSend(address, weight, destination);
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

    public void receiveWeight(SocketAddress neighbor, int weight){
        System.out.println("New weight to neighbor " + neighbor+ " of " + weight + "\n");
        this.neighborsMap.put(neighbor, weight);
        updateDistanceVector();
    }

    public void changeWeight(SocketAddress neighbor, int weight){
        this.neighborsMap.put(neighbor, weight);
        sendWeightChange(weight, neighbor);
        updateDistanceVector();
    }

    public int getNeighborWeight(SocketAddress address) {
        return neighborsMap.getOrDefault(address, -1);
    }

    public int getDistanceVectorWeight(SocketAddress address){
        return this.mostRecentCalculation.getResultVector().getValue(address);
    }

    public DistanceVectorCalculation getMostRecentCalculation() {
        return mostRecentCalculation;
    }

    public void close(){
        this.socket.close();
    }

}
