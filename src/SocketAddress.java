import java.net.Socket;

/**
 * Simulation of Socket address.
 * A SocketAddress is simple, in that it contains an IP address and a Port, 
 * which designate the location and port of another router running our program. 
 * The known values for SocketAddresses are provided in text files which are passed in upon program startup.
 * 
 * @Author Yizhong Chen, Darren Norton
 * @version May.6th.2017
 */
public class SocketAddress implements Comparable<SocketAddress> {
    private String ip;
    private Integer port;

    /**
     * Constructor of Socket Address
     */
    public SocketAddress(String ip, Integer port){
        this.ip = ip;
        this.port = port;
    }

    @Override
    public int compareTo(SocketAddress other){
        int ipComp = this.ip.compareTo(other.ip);
        if(ipComp != 0){
            return ipComp;
        } else {
            int portComp = this.port.compareTo(other.port);
            return portComp;
        }
    }

    @Override
    public boolean equals(Object other){
        if(other == null)
            return false;
        if(other == this)
            return true;
        try{
            SocketAddress otherAddress = (SocketAddress)other;
            return this.compareTo(otherAddress) == 0;
        } catch (Exception e){
            return false;
        }
    }

    @Override
    public int hashCode(){
        return ip.hashCode() + port;
    }

    @Override
    public String toString(){
        return ip + ":" + port;
    }

    public String getIp() {
        return ip;
    }

    public Integer getPort() {
        return port;
    }

}
