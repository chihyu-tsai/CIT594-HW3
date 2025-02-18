import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class CharCounter implements ICharCounter, IHuffConstants{

    Map<Integer, Integer> resultMap;

    public CharCounter() {
        this.resultMap = new HashMap<>();
    }

    @Override
    public int getCount(int ch) {
        int answer = 0;
        try {
            answer = resultMap.get(ch);
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
            charCount += 1;
            if (!resultMap.containsKey(nextByte)) {
                set(nextByte, 1);
            } else {
                add(nextByte);
            }
            nextByte = stream.read();
        }
        return charCount;
    }

    @Override
    public void add(int i) {
        resultMap.put(i, resultMap.get(i) + 1);;
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
