import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Created by nortondj on 4/27/17.
 */
public class RouterTest {
    @Test
    public void recalculateDistanceVectorInitOthers() throws Exception {
        Router r = RouterFactory.makeRouter("resources/test1.txt", false);
        DistanceVectorCalculation result = r.recalculateDistanceVector();
        SocketAddress n1 = new SocketAddress("127.0.0.1", 9877);
        Integer expectedWeight1 = 1;
        SocketAddress n2 = new SocketAddress("127.0.0.1", 9878);
        Integer expectedWeight2 = 2;
        SocketAddress n3 = new SocketAddress("127.0.0.1", 9879);
        Integer expectedWeight3 = 3;
        DistanceVector resultVec = result.getResultVector();
        HashMap<SocketAddress, ArrayList<SocketAddress>> pathMap = result.getPathMap();
        Assert.assertEquals(expectedWeight1, resultVec.getValue(n1));
        Assert.assertEquals(expectedWeight2, resultVec.getValue(n2));
        Assert.assertEquals(expectedWeight3, resultVec.getValue(n3));
    }

    @Test
    public void recalculateDistanceVectorInitSelf() throws Exception {
        Router r = RouterFactory.makeRouter("resources/test1.txt", false);
        DistanceVectorCalculation result = r.recalculateDistanceVector();
        SocketAddress source = new SocketAddress("127.0.0.1", 9876);
        DistanceVector resultVec = result.getResultVector();
        HashMap<SocketAddress, ArrayList<SocketAddress>> pathMap = result.getPathMap();
        Assert.assertEquals(new Integer(0), resultVec.getValue(source));
    }

    @Test
    public void findDistance(){
        Router r = RouterFactory.makeRouter("resources/test2.txt", false);
        r.recalculateDistanceVector();
        SocketAddress source = new SocketAddress("127.0.0.1", 9876);
        SocketAddress n1 = new SocketAddress("127.0.0.1", 9877);
        SocketAddress n2 = new SocketAddress("127.0.0.1", 9878);
        SocketAddress n3 = new SocketAddress("127.0.0.1", 9879);

        //from test2.txt, source -> n1,n2,n3 = (2,7,20)
        //create a vector from n1, that has n1 -> n3 = 5

        DistanceVector n1vector = new DistanceVector(n1);
        n1vector.addValue(n3,5);

        //from n1 -> n1, we expect 0 by definition
        Assert.assertEquals(new Integer(0), r.findDistance(n1,n1,n1vector));
        //from n1 -> n2, we expect inf, due to unconnected
        Assert.assertEquals(new Integer(Integer.MAX_VALUE), r.findDistance(n1,n2,n1vector));
        //from n1 -> n3, we expect 5, by value
        Assert.assertEquals(new Integer(5), r.findDistance(n1,n3,n1vector));
    }

    @Test
    public void receiveDistanceVector(){
        Router r = RouterFactory.makeRouter("resources/test2.txt", false);
        r.recalculateDistanceVector();
        SocketAddress source = new SocketAddress("127.0.0.1", 9876);
        SocketAddress n1 = new SocketAddress("127.0.0.1", 9877);
        SocketAddress n2 = new SocketAddress("127.0.0.1", 9878);
        SocketAddress n3 = new SocketAddress("127.0.0.1", 9879);

        //from test2.txt, source -> n1,n2,n3 = (2,7,20)
        //create a vector from n1, that has n1 -> n3 = 6
        //will cause source -> n1,n2,n3 = (2, 7, 8)

        Integer expectedWeight1 = new Integer(2);
        Integer expectedWeight2 = new Integer(7);
        Integer expectedWeight3 = new Integer(8);

        DistanceVector n1vector = new DistanceVector(n1);
        n1vector.addValue(n3,6);
        r.receiveDistanceVector(n1vector);
        DistanceVectorCalculation result = r.getMostRecentCalculation();
        DistanceVector resultVec = result.getResultVector();
        HashMap<SocketAddress, ArrayList<SocketAddress>> pathMap = result.getPathMap();

        Assert.assertEquals(expectedWeight1, resultVec.getValue(n1));
        Assert.assertEquals(expectedWeight2, resultVec.getValue(n2));
        Assert.assertEquals(expectedWeight3, resultVec.getValue(n3));
    }

}