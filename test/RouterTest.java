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

}