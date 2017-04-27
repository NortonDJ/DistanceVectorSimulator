import java.net.Socket;

/**
 * Created by nortondj on 4/27/17.
 */
public class SocketAddress implements Comparable<SocketAddress> {
    private String ip;
    private Integer port;

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

    public String getIp() {
        return ip;
    }

    public Integer getPort() {
        return port;
    }

}
