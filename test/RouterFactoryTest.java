import org.junit.Assert;
import org.junit.Test;


/**
 * Created by nortondj on 4/27/17.
 */
public class RouterFactoryTest {
    @Test
    public void makeRouterAssignsRouterCorrectAddress() throws Exception {
        Router r = RouterFactory.makeRouter("resources/neighbors1.txt", false);
        Assert.assertEquals("127.0.0.1", r.getIP());
        Assert.assertEquals(new Integer(9876), r.getPort());
    }

    @Test
    public void makeRouterAssignsCorrectNeighbors() throws Exception {
        Router r = RouterFactory.makeRouter("resources/neighbors1.txt", false);
        SocketAddress n1 = new SocketAddress("127.0.0.1", 9877);
        SocketAddress n2 = new SocketAddress("127.0.0.1", 9878);
        SocketAddress n3 = new SocketAddress("127.0.0.1", 9879);

        Assert.assertTrue(r.hasNeighbor(n1));
        Assert.assertTrue(r.hasNeighbor(n2));
        Assert.assertTrue(r.hasNeighbor(n3));
        Assert.assertEquals(1, r.getNeighborWeight(n1));
        Assert.assertEquals(1, r.getNeighborWeight(n2));
        Assert.assertEquals(1, r.getNeighborWeight(n3));
    }

}