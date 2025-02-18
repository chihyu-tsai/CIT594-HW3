import static org.junit.Assert.assertEquals;
import org.junit.Test;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


public class CharCounterTest{


    @Test
    public void testGetCount() throws IOException {
        ICharCounter cc = new CharCounter();
        InputStream trial = new ByteArrayInputStream("superdupery".getBytes("UTF-8"));
        int initial = cc.countAll(trial);
        int actual = cc.getCount('p');
        assertEquals(2, actual);
    }

    @Test
    // combine two test
    public void testCountAll() throws IOException {
        ICharCounter cc = new CharCounter();
        InputStream ins = new ByteArrayInputStream("teststring".getBytes("UTF-8"));
        int actual = cc.countAll(ins);
        assertEquals(10, actual);
    }


    @Test
    public void testGetTable() throws IOException {
        ICharCounter cc = new CharCounter();
        InputStream ins = new ByteArrayInputStream("berkeleypenn".getBytes("UTF-8"));
        int firstStep = cc.countAll(ins);
        Map<Integer, Integer> actual = new HashMap<>();;
        actual = cc.getTable();
        Map<Integer, Integer> expected = new HashMap<>();
        expected.put((int)'e', 4);
        expected.put((int)'n', 2);
        expected.put((int)'b', 1);
        expected.put((int)'r', 1);
        expected.put((int)'k', 1);
        expected.put((int)'l', 1);
        expected.put((int)'y', 1);
        expected.put((int)'p', 1);
        assertEquals(expected,actual);


    }

}
