package codec;

import java.util.BitSet;

public class BitUtils {
    public static  boolean getBit(byte source, int index) {
        return BitSet.valueOf(new long[] { source }).get(index);
    }

    public static long getBits(byte source, int startIndex, int toIndex) {
        long[] result = BitSet.valueOf(new long[] { source }).get(startIndex, toIndex).toLongArray();
        return result.length > 0 ? result[0] : 0;
    }
}
