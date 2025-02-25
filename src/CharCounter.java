import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class CharCounter implements ICharCounter, IHuffConstants {

    Map<Integer, Integer> resultMap;

    public CharCounter() {
        this.resultMap = new HashMap<>();
    }

    @Override
    public int getCount(int ch) {
        int answer = 0;
        try {
            if (resultMap.containsKey(ch)) {
                answer = resultMap.get(ch);
            } else {
                return 0;
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return answer;
    }

    @Override
    public int countAll(InputStream stream) throws IOException {
        int nextByte = stream.read();
        int charCount = 0;
        while (nextByte != -1) {
            add(nextByte);
            nextByte = stream.read();
            charCount += 1;
        }
        resultMap.put(PSEUDO_EOF, 1);
        return charCount;
    }

    @Override
    public void add(int i) {
        if (resultMap.containsKey(i)) {
            resultMap.put(i, resultMap.get(i) + 1);
        } else {
            set(i,1);
        }
    }

    @Override
    public void set(int i, int value) {
        resultMap.put(i, value);
    }

    @Override
    public void clear() {
        resultMap.clear();
    }

    @Override
    public Map<Integer, Integer> getTable() {
        return resultMap;
    }
}
