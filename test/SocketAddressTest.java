import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by nortondj on 4/27/17.
 */
public class SocketAddressTest {
    @Test
    public void equals() throws Exception {
        SocketAddress n1 = new SocketAddress("127.0.0.1", 9877);
        SocketAddress n2 = new SocketAddress("127.0.0.1", 9877);
        Assert.assertTrue(n1.equals(n2));
    }

}