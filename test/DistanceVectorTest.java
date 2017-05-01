import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Created by nortondj on 4/27/17.
 */
public class DistanceVectorTest {

    @Test
    public void applyPoisonNodeNotInPath(){
        SocketAddress source = new SocketAddress("127.0.0.1", 9876);
        SocketAddress n1 = new SocketAddress("127.0.0.1", 9877);
        SocketAddress n2 = new SocketAddress("127.0.0.1", 9878);
        SocketAddress n3 = new SocketAddress("127.0.0.1", 9879);
        DistanceVector d = new DistanceVector(source);
        d.addValue(source, 0);
        d.addValue(n1, 5);
        d.addValue(n2, 7);
        d.addValue(n3, 2);
        HashMap<SocketAddress, ArrayList<SocketAddress>> pathMap = new HashMap<>();

        ArrayList<SocketAddress> pathn1 = new ArrayList<>();
        pathn1.add(source);
        pathn1.add(n1);

        ArrayList<SocketAddress> pathn2 = new ArrayList<>();
        pathn2.add(source);
        pathn2.add(n2);

        ArrayList<SocketAddress> pathn3 = new ArrayList<>();
        pathn3.add(source);
        pathn3.add(n3);

        pathMap.put(n1, pathn1);
        pathMap.put(n2, pathn2);
        pathMap.put(n3, pathn3);

        d.applyPoison(n1, pathMap);
        Assert.assertEquals(new Integer(0), d.getValue(source));
        Assert.assertEquals(new Integer(7), d.getValue(n2));
        Assert.assertEquals(new Integer(2), d.getValue(n3));
    }

    @Test
    public void applyPoisonNodeInPath(){
        SocketAddress source = new SocketAddress("127.0.0.1", 9876);
        SocketAddress n1 = new SocketAddress("127.0.0.1", 9877);
        SocketAddress n2 = new SocketAddress("127.0.0.1", 9878);
        SocketAddress n3 = new SocketAddress("127.0.0.1", 9879);
        DistanceVector d = new DistanceVector(source);
        d.addValue(source, 0);
        d.addValue(n1, 5);
        d.addValue(n2, 7);
        d.addValue(n3, 2);
        HashMap<SocketAddress, ArrayList<SocketAddress>> pathMap = new HashMap<>();

        ArrayList<SocketAddress> pathn1 = new ArrayList<>();
        pathn1.add(source);
        pathn1.add(n2);
        pathn1.add(n1);

        ArrayList<SocketAddress> pathn2 = new ArrayList<>();
        pathn2.add(source);
        pathn2.add(n2);

        ArrayList<SocketAddress> pathn3 = new ArrayList<>();
        pathn3.add(source);
        pathn3.add(n3);

        pathMap.put(n1, pathn1);
        pathMap.put(n2, pathn2);
        pathMap.put(n3, pathn3);

        d.applyPoison(n2, pathMap);
        Assert.assertEquals(new Integer(16), d.getValue(n1));
        Assert.assertEquals(new Integer(16), d.getValue(n2));
    }

}