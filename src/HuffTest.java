import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.io.*;
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
        HuffTree forEOF = new HuffTree(IHuffConstants.ALPH_SIZE, 1);
        HuffInternalNode internal3 = new HuffInternalNode(forE.root(), forEOF.root(), 2);
        HuffInternalNode internal2 = new HuffInternalNode(forE.root(), internal3, 4);
        HuffInternalNode internal1 = new HuffInternalNode(forA.root(),internal2, 7);
        HuffTree expected = new HuffTree(forL.root(), internal1, 11);
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
        expected.put((int) 't', "110");
        expected.put((int) 'e', "1111");
        expected.put(IHuffConstants.ALPH_SIZE, "1110");
        assertEquals(expected, actual);

    }

    @Test
    public void testGetCode() throws IOException {
        Huff huffer = new Huff();
        InputStream ins = new ByteArrayInputStream("llllaaatte".getBytes("UTF-8"));
        HuffTree initial = huffer.makeHuffTree(ins);
        Map<Integer, String> mappyMap = new HashMap<>();
        mappyMap = huffer.makeTable();
        String actual = huffer.getCode('t');
        assertEquals("110", actual);
    }


    @Test
    public void testShowCounts() throws IOException {
        Huff huffer = new Huff();
        InputStream ins = new ByteArrayInputStream("llllaaatte".getBytes("UTF-8"));
        HuffTree initial = huffer.makeHuffTree(ins);
        Map<Integer, Integer> actual = new HashMap<>();
        actual = huffer.showCounts();
        Map<Integer, Integer> expected = new HashMap<>();
        expected.put((int) 'l',4);
        expected.put((int) 'a',3);
        expected.put((int) 't',2);
        expected.put((int) 'e',1);
        expected.put(IHuffConstants.ALPH_SIZE,1);
        assertEquals(expected, actual);
    }



    @Test
    public void testWriteHeader() throws IOException {
        Huff huffer = new Huff();
        InputStream ins = new FileInputStream("testinputFile.txt");
        HuffTree huffyTree = huffer.makeHuffTree(ins);
        BitOutputStream fos = new BitOutputStream(new FileOutputStream("testing.txt"));
        int actual = huffer.writeHeader(fos);
        assertEquals(141, actual);
    }

    @Test
    public void testWrite() throws IOException {
        Huff huffer = new Huff();
        int actual = huffer.write("testinputFile.txt", "testoutputFile.txt", false);
        assertEquals(210,actual);
    }


    @Test
    public void testUncompress() throws IOException {
        Huff huffer = new Huff();
        int inputStream = huffer.write("testinputFile.txt", "testoutputFile.txt", true);
        int actual = huffer.uncompress("testoutputFile.txt", "testing1.txt");
        assertEquals(176, actual);
    }


}
