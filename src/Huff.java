import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class Huff implements ITreeMaker, IHuffEncoder, IHuffModel, IHuffHeader {

    // field
    private HuffTree huffTree;
    private HashMap<Integer, String> huffMap;
    private HashMap<Integer, Integer> freqMap;
    private int sizeCount;

    @Override
    public HuffTree makeHuffTree(InputStream stream) throws IOException {

        PriorityQueue<HuffTree> huffTreePQ = new PriorityQueue<>();
        CharCounter cc = new CharCounter();
        int totalChar = cc.countAll(stream);
        freqMap = (HashMap<Integer, Integer>) cc.getTable();
        // populate all the nodes into a PriorityQueue
        for (int i : freqMap.keySet()) {
            HuffTree node = new HuffTree(i, freqMap.get(i));
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
        return freqMap;
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
            huffmanRecursion(internalNode.left(), path + "0", huffMap);    // left recursion
            huffmanRecursion(internalNode.right(), path + "1", huffMap);   // right recursion
        }
    }

    @Override
    public int headerSize() {
        // to account for the magic number
        return huffTree.size() + BITS_PER_INT;
    }

    @Override
    public int writeHeader(BitOutputStream out) {
        try {
            out.write(BITS_PER_INT, MAGIC_NUMBER);
            // do the preorder traversal here - write out the whole tree
            IHuffBaseNode rootNode = huffTree.root();
            treeShape(rootNode, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return headerSize();
    }

    // this method do the pre-order traversal to get the shape of the tree
    public void treeShape(IHuffBaseNode node, BitOutputStream bos) throws IOException {
        if (node.isLeaf()) {
            bos.write(1, 1);
            HuffLeafNode leafNode = (HuffLeafNode) node;
            bos.write(9, leafNode.element());
            //sizeCount += 10;
        } else {
            bos.write(1, 0);
            //sizeCount += 1;
            HuffInternalNode internalNode = (HuffInternalNode) node;
            treeShape(internalNode.left(), bos);
            treeShape(internalNode.right(), bos);
        }
    }

    @Override
    public HuffTree readHeader(BitInputStream in) throws IOException {
        int magic = in.read(BITS_PER_INT);
        if (magic != MAGIC_NUMBER) {
            throw new IOException("magic number not right");
        }
        HuffTree resultTree = new HuffTree(0, 0);
        resultTree.setRoot(readTreeShape(in));
        return resultTree;
    }


    public IHuffBaseNode readTreeShape(BitInputStream bis) throws IOException {
        int reading = bis.read(1);
        if (reading == 1) {
            int character = bis.read(9);
            HuffLeafNode leafNode = new HuffLeafNode(character, 0);
            return leafNode;
        } else {
            HuffInternalNode internalNode =
                    new HuffInternalNode(readTreeShape(bis), readTreeShape(bis), 0);
            return internalNode;
        }
    }


    @Override
    public int write(String inFile, String outFile, boolean force) {
        int estCompress = 0;
        try {
            // this part calculate the size of the original file
            int originalFileSize;
            CharCounter cc = new CharCounter();
            FileInputStream originalFis = new FileInputStream(inFile);
            originalFileSize = cc.countAll(originalFis) * BITS_PER_WORD;

            // this part serve as an estimation of what compressed file is going to look like
//            BitOutputStream testOut = new BitOutputStream(new FileOutputStream(outFile));
            ByteArrayOutputStream tempBuffer = new ByteArrayOutputStream();
            BitOutputStream testOut = new BitOutputStream(tempBuffer);
            BitInputStream testIn = new BitInputStream(new FileInputStream(inFile));
            this.makeHuffTree(testIn);
            this.makeTable();
            testIn.close();
            StringBuilder sb = new StringBuilder();
            testIn = new BitInputStream(new FileInputStream(inFile));
            int estWriteHeader = this.writeHeader(testOut);
            int testy;
            while ((testy = testIn.read(BITS_PER_WORD)) != -1) {
                sb.append(huffMap.get(testy));
            }
            sb.append(huffMap.get(PSEUDO_EOF));
            estCompress = estWriteHeader + sb.toString().length();

            // this part take actions on whether to compress files or not
            if (!force && (estCompress >= originalFileSize)) {
                return estCompress;
            } else {
                BitOutputStream bos = new BitOutputStream(new FileOutputStream(outFile));
                BitInputStream bis = new BitInputStream(new FileInputStream(inFile));
                this.writeHeader(bos);
                int inbits;
                while ((inbits = bis.read(BITS_PER_WORD)) != -1) {
                    // convert the string path into int and write to the output
                    for (int i = 0; i < huffMap.get(inbits).length(); i++) {
                        bos.write(1, (int) (huffMap.get(inbits).charAt(i) - '0'));
                    }
                }
                for (int i = 0; i < huffMap.get(PSEUDO_EOF).length(); i++) {
                    bos.write(1, (int) (huffMap.get(PSEUDO_EOF).charAt(i) - '0'));
                }
            }
            originalFis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return estCompress;
    }

    @Override
    public int uncompress(String inFile, String outFile) {
        int finalCount = 0;
        try {
            BitInputStream bis = new BitInputStream(new FileInputStream(inFile));
            BitOutputStream bos = new BitOutputStream(new FileOutputStream(outFile));
            readHeader(bis);
            IHuffBaseNode rootNode = this.huffTree.root();
            IHuffBaseNode curr = rootNode;
            while (true) {
                int bits = bis.read(1);
                if (bits == -1) {
                    break;
                }
                if ((bits & 1) == 0) {
                    curr = ((HuffInternalNode) curr).left();
                } else {
                    curr = ((HuffInternalNode) curr).right();
                }
                if (curr.isLeaf()) {
                    if (((HuffLeafNode) curr).element() == PSEUDO_EOF) {
                        break;
                    } else {
                        bos.write(BITS_PER_WORD, ((HuffLeafNode) curr).element());
                        curr = rootNode;
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            CharCounter cc = new CharCounter();
            FileInputStream fis = new FileInputStream(outFile);
            finalCount = cc.countAll(fis) * BITS_PER_WORD;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return finalCount;
    }
}


