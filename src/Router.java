import java.net.DatagramSocket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Router is the most crucial component in this algorithm and is complex as such. 
 * There are quite a few key additions. First there is the HashMap mapping neighbors’ SocketAddresses to weights. 
 * This would represent the physical connections to a Router’s neighbors. 
 * There is a HashMap mapping SocketAddresses to the DistanceVectors the router has received from them. 
 * There is a list of known nodes, and finally a HashMap to help keep count of the recent communications 
 * from its neighbors to help drop neighbors after 3 time intervals.
 * 
 * @Author Yizhong Chen, Darren Norton
 * @version May.6th.2017
 */
public class Router {
    private ForwardingTable table;
    private DistanceVectorCalculation mostRecentCalculation;
    private SocketAddress address;
    private HashMap<SocketAddress, Integer> neighborsMap;
    private HashMap<SocketAddress, DistanceVector> vectorMap;
    private HashSet<SocketAddress> knownNodes;
    private HashMap<SocketAddress, Integer> timerCounts;
    private boolean poison;
    private DatagramSocket socket;
    private RouterUDPSender sender;
    private ReentrantLock lock;

    public static void main(String[] args) {
        Router r;
        if (args.length == 0) {
            System.out.println("Please re-run the Router with the following format:");
            System.out.println("java Router [-reverse] <neighbors.txt>");
            return;
        } else if (args.length == 1) {
            r = RouterFactory.makeRouter(args[0], false);
        } else {
            if (args[0].equals("-reverse")) {
                r = RouterFactory.makeRouter(args[1], true);
            } else {
                System.out.println("Please re-run the Router with the following format:");
                System.out.println("java Router [-reverse] <neighbors.txt>");
                return;
            }
        }
        r.start(30);
    }
    
    /**
     * Execute the threads
     * @param time between update
     */
    public void start(int timeBetweenUpdate) {
        ScheduledThreadPoolExecutor threadPool = new ScheduledThreadPoolExecutor(4);
        threadPool.scheduleAtFixedRate(new DVUpdateThread(this), 0, timeBetweenUpdate, TimeUnit.SECONDS);
        threadPool.execute(new DVCommandThread(this));
        threadPool.execute(new RouterUDPReceiver(socket, this));
    }

    /**
     * Get lock of this router
     * @return lock
     */
    public ReentrantLock getLock(){
        return this.lock;
    }

    /**
     * Send message to certain destination
     * @param message, destination
     * @return SocketAddress
     */
    public SocketAddress message(String message, SocketAddress destination) {
        SocketAddress via = table.getNext(destination);
        if (via == null) {
            System.out.println("Router could not find a way to send to " + destination);
            return null;
        } else {
            sender.udpSend(message.trim() + " | " + address.toString(), via, destination);
            return via;
        }
    }

    public void print() {
        System.out.println("Current distance vector: \n" + mostRecentCalculation.getResultVector());
        for (SocketAddress s : vectorMap.keySet()) {
            System.out.println("Distance vector from : " + s + "\n" + vectorMap.get(s));
        }
    }

    /**
     * Counting for the incrementing time
     */
    public void incrementTimerCounts() {
        boolean dropped = false;
        ArrayList<SocketAddress> removed = new ArrayList<>();
        for (SocketAddress s : timerCounts.keySet()) {
            int count = timerCounts.get(s);
            if (count++ == 3) {
                dropNeighbor(s);
                removed.add(s);
                dropped = true;
            } else {
                timerCounts.put(s, count);
            }
        }
        if(dropped){
            for(SocketAddress s : removed){
                timerCounts.remove(s);
            }
            updateDistanceVector();
        }
    }

    /**
     * Delete Neighbor router from the router table
     * @param neighbor address
     */
    public void dropNeighbor(SocketAddress neighbor) {
        System.out.println("Neighbor " + neighbor + " dropped\n");
        this.neighborsMap.remove(neighbor);
        this.vectorMap.remove(neighbor);
        this.knownNodes.remove(neighbor);
        this.table.remove(neighbor);
    }

    /**
     * Constructor of Router
     * @return address, address map, poison
     */
    public Router(SocketAddress address, HashMap<SocketAddress, Integer> neighborsMap, boolean poison) {
        this.lock = new ReentrantLock();
        this.address = address;
        this.table = new ForwardingTable();
        this.neighborsMap = neighborsMap;
        this.vectorMap = new HashMap<>();
        this.knownNodes = new HashSet<>();
        this.timerCounts = new HashMap<>();
        this.poison = poison;
        addNeighborsToDistVectMap();
        addNeighborsToKnownNodes();
        addNeighborsToTimerCounts();
        this.mostRecentCalculation = recalculateDistanceVector();
        updateForwardingTable(this.mostRecentCalculation);
        try {
            this.socket = new DatagramSocket(address.getPort());
        } catch (Exception e) {
            System.out.println("Failed to open socket, exiting.");
            System.exit(1);
        }
        this.sender = new RouterUDPSender(socket);
        broadCastWeights();
    }

    /**
     * Deliver weight to neighbors
     */
    public void broadCastWeights(){
        for(SocketAddress neighbor : neighborsMap.keySet()){
            sendWeightChange(neighborsMap.get(neighbor), neighbor);
        }
    }
    
    /**
     * Move time counting to neighbors 
     */
    private void addNeighborsToTimerCounts() {
        for (SocketAddress neighbor : neighborsMap.keySet()) {
            this.timerCounts.put(neighbor, 0);
        }
    }

    /**
     * Add neaby router to its map
     */
    private void addNeighborsToDistVectMap() {
        for (SocketAddress neighbor : neighborsMap.keySet()) {
            this.vectorMap.put(neighbor, null);
        }
    }

    /**
     * Add neaby neighbors to nodes
     */
    private void addNeighborsToKnownNodes() {
        for (SocketAddress neighbor : neighborsMap.keySet()) {
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
                " with the following " + vector.distancesString() + "\n");
        SocketAddress source = vector.getSource();
        this.vectorMap.put(source, vector);
        this.timerCounts.put(source, 0);
        for (SocketAddress s : vector.getNodes()) {
            this.knownNodes.add(s);
        }
        return updateDistanceVector();
    }

    public boolean updateDistanceVector() {
        DistanceVectorCalculation oldCalculation = this.mostRecentCalculation;
        this.mostRecentCalculation = recalculateDistanceVector();
        DistanceVector vector = mostRecentCalculation.getResultVector();
        HashMap<SocketAddress, ArrayList<SocketAddress>> pathMap = mostRecentCalculation.getPathMap();
        boolean change;
        // if a change has occurred
        if (!mostRecentCalculation.equals(oldCalculation)) {
            String s = "New dv calculated:";
            updateForwardingTable(mostRecentCalculation);
            for (SocketAddress node : pathMap.keySet()) {
                SocketAddress nextHop = table.getNext(node);
                String pathString = nextHop == null ? "~" : nextHop.toString();
                s += "\n" + node + " " + vector.getValue(node) + " " + pathString;
            }
            System.out.println(s + "\n");
            broadCastDistanceVector(mostRecentCalculation);
            change = true;
        } else {
            change = false;
        }
        return change;
    }

    /**
     * Recalculates the distance vector and paths to other nodes based on the
     * current distance vector map. The new distance vector is calculated as
     * {@code
     * for all nodes the router knows about,
     * dsrc->node = min over all i{dsrc->neighbor(i) + dneighbor(i)->node}
     * }
     *
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
            if (destination.equals(address)) {
                continue;
            }
            Integer minimum = 16;
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
                if (distanceToNeighbor >= 16 || distanceNeighborToDest == 16) {
                    calculation = 16;
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
            // if the destination is reachable, add it to the path
            if (minimum < 16) {
                newVec.addValue(destination, minimum);
                // Add the knowledge of node->(destination if exist)->dest
                path.add(destination);
                pathMap.put(destination, path);
            }
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
        for (SocketAddress neighbor : neighborsMap.keySet()) {
            DistanceVector toSend = prepareDistanceVectorToSend(neighbor, calculation);
            sendDistanceVector(neighbor, toSend);
        }
    }

    public void broadCastTimeout(){
        SimpleDateFormat date = new SimpleDateFormat("HH:mm:ss");
        Date current = new Date();
        String time = date.format(current);
        System.out.println("Update sent to all neighbors at time "+ time);
        DistanceVectorCalculation calculation = mostRecentCalculation;
        for (SocketAddress neighbor : neighborsMap.keySet()) {
            DistanceVector toSend = prepareDistanceVectorToSend(neighbor, calculation);
            System.out.println(neighbor + " " +calculation.getResultVector().getValue(neighbor));
            sendDistanceVector(neighbor, toSend);
        }
        System.out.println();
    }

    public void receiveMessage(String message, SocketAddress from, SocketAddress destination) {
        if (destination.equals(address)) {
            System.out.println("RECEIVED MESSAGE FINALLY: " + message + "\n");
        } else {
            forward(message, from, destination);
        }
    }

    public void forward(String message, SocketAddress from, SocketAddress destination) {
        SocketAddress via = table.getNext(destination);
        if (via == null) {
            System.out.println("Router could not find a way to send to " + destination);
        } else {
            String newMessage = message.trim() + " | " + address.toString();
            System.out.println("Message " + message +
                    " from " + from +
                    " to " + destination +
                    " forwarded to " + via);
            System.out.println("msg(" + newMessage + ")\n");
            sender.udpSend(newMessage, via, destination);
        }
    }

    public Integer findDistance(SocketAddress node, SocketAddress destination, DistanceVector nodeDistanceVector) {
        // if the node has not sent a distance vector,
        //     we check if the node and destination are the same
        //         if same, then dnode->dest = dnode->node = 0;
        //         otherwise dnode->dest = inf, because router doesn't have path
        Integer distanceNodeToDest;
        if (nodeDistanceVector == null) {
            if (node.equals(destination)) {
                // destination and node are the same
                distanceNodeToDest = 0;
            } else {
                // destination and node are different and
                // no direct path from neighbor->dest
                distanceNodeToDest = 16;
            }
        } else {
            // we know node->dest
            if (node.equals(destination)) {
                distanceNodeToDest = 0;
            } else {
                distanceNodeToDest = nodeDistanceVector.getValue(destination);
            }
        }
        return distanceNodeToDest;
    }

    /**
     * This is a subroutine for broadcasting distance vectors. This method will
     * create a copy of the distance vector from the calculation, and apply poison
     * if necessary.
     *
     * @param destination the destination to send the distance vector to
     * @param calculation the calculation object containing the DV and paths
     * @return the distance vector, with poison if necessary
     */
    public DistanceVector prepareDistanceVectorToSend(SocketAddress destination, DistanceVectorCalculation calculation) {
        DistanceVector individualizedVec = new DistanceVector(calculation.getResultVector());
        if (poison) {
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

    public void sendWeightChange(int weight, SocketAddress destination) {
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

    public void receiveWeight(SocketAddress neighbor, int weight) {
        System.out.println("New weight to neighbor " + neighbor + " of " + weight + "\n");
        this.neighborsMap.put(neighbor, weight);
        this.timerCounts.put(neighbor, 0);
        updateDistanceVector();
    }

    public void changeWeight(SocketAddress neighbor, int weight) {
        this.neighborsMap.put(neighbor, weight);
        this.timerCounts.put(neighbor, 0);
        sendWeightChange(weight, neighbor);
        updateDistanceVector();
    }

    public int getNeighborWeight(SocketAddress address) {
        return neighborsMap.getOrDefault(address, -1);
    }

    public int getDistanceVectorWeight(SocketAddress address) {
        return this.mostRecentCalculation.getResultVector().getValue(address);
    }

    public DistanceVectorCalculation getMostRecentCalculation() {
        return mostRecentCalculation;
    }

    public void close() {
        this.socket.close();
    }

}
