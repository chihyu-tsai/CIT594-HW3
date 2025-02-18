import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class Huff implements ITreeMaker, IHuffEncoder, IHuffModel, IHuffHeader{

    // field
    private HuffTree huffTree;
    private HashMap<Integer, String> huffMap;

    @Override
    public HuffTree makeHuffTree(InputStream stream) throws IOException {

        PriorityQueue<HuffTree> huffTreePQ = new PriorityQueue<>();

        CharCounter cc = new CharCounter();
        int totalChar = cc.countAll(stream);
        Map<Integer, Integer> treeMap = cc.getTable();
        // populate all the nodes into a PriorityQueue
        for (int i: treeMap.keySet()) {
            HuffTree node = new HuffTree(i, treeMap.get(i));
            huffTreePQ.add(node);
        }
        while (huffTreePQ.size() > 1) {
            HuffTree first = huffTreePQ.poll();
            HuffTree second = huffTreePQ.poll();
            int newWeight = first.weight() + second.weight();
            HuffTree newNode = new HuffTree(first.root(), second.root(), newWeight);
            huffTreePQ.add(newNode);
        }
        huffTree = huffTreePQ.poll();
        return huffTree;
    }

    @Override
    public Map<Integer, String> makeTable() {
        HashMap<Integer, String> huffMap = new HashMap<>();
        HuffTree usedHuffTree = huffTree;
        String finalPath = "";
        huffmanRecursion(huffTree.root(), finalPath, huffMap);
        this.huffMap = huffMap;
        return huffMap;
    }

    @Override
    public String getCode(int i) {
        return huffMap.get(i);
    }

    @Override
    public Map<Integer, Integer> showCounts() {
        CharCounter cc = new CharCounter();
        for (int k: huffMap.keySet()) {

        }
        return Map.of();
    }


    // this method take care of the recursion for the Huffman Tree
    // can try a different approach
    public void huffmanRecursion(IHuffBaseNode rt, String path, Map<Integer, String> huffMap) {
        if (rt.isLeaf()) {
            HuffLeafNode leafNode = (HuffLeafNode) rt;
            huffMap.put(leafNode.element(), path);
        } else {
            // this is the case when it is internal node
            HuffInternalNode internalNode = (HuffInternalNode) rt;
            huffmanRecursion(internalNode.left(), path+ "0", huffMap);    // left recursion
            huffmanRecursion(internalNode.right(), path+ "1", huffMap);   // right recursion
        }
    }

    @Override
    public int headerSize() {
        return 0;
    }

    @Override
    public int writeHeader(BitOutputStream out) {
        return 0;
    }

    @Override
    public HuffTree readHeader(BitInputStream in) throws IOException {
        return null;
    }

    @Override
    public int write(String inFile, String outFile, boolean force) {
        return 0;
    }

    @Override
    public int uncompress(String inFile, String outFile) {
        return 0;
    }
}
