import static org.junit.Assert.assertEquals;
import org.junit.Test;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class HuffTest {

    @Test
    public void testMakeHuffTree() throws IOException {
        Huff huff = new Huff();
        InputStream ins = new ByteArrayInputStream("llllaaatte".getBytes("UTF-8"));
        HuffTree actual = huff.makeHuffTree(ins);
        HuffTree forL = new HuffTree('l', 4);
        HuffTree forA = new HuffTree('a',3);
        HuffTree forT = new HuffTree('t',2);
        HuffTree forE = new HuffTree('e',1);
        HuffInternalNode internal2 = new HuffInternalNode(forE.root(), forT.root(), 3);
        HuffInternalNode internal1 = new HuffInternalNode(forA.root(),internal2, 6);
        HuffTree expected = new HuffTree(forL.root(), internal1, 10);
        assertEquals(expected.weight(), actual.weight());
    }



    @Test
    public void testMakeTable() throws IOException {
        Huff huffer = new Huff();
        InputStream ins = new ByteArrayInputStream("llllaaatte".getBytes("UTF-8"));
        Map<Integer, String> actual = new HashMap<>();
        Map<Integer, String> expected = new HashMap<>();
        HuffTree initial = huffer.makeHuffTree(ins);
        actual = huffer.makeTable();
        expected.put((int) 'l', "0");
        expected.put((int) 'a', "10");
        expected.put((int) 't', "111");
        expected.put((int) 'e', "110");
        assertEquals(expected, actual);

    }


}
