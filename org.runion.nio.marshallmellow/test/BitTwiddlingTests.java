package test;

import org.junit.Test;

import java.util.Random;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class BitTwiddlingTests {
    private boolean testBit(long value, int index) {
        return (value & (0x01L << index)) != 0;
    }

    private long testBits(long value, int start, int count) {
        // highest bit we want is in position start + count we want to shift that to the msb in the long
        long leftJustified = value << (long)(64 - count - start);

        // now shift the lowest bit of the desired range to lsb of the long >>> means all higher level bits are zero
        long rightjustified = leftJustified >>> (long)(64 - count);

        return rightjustified;
    }

    private long setBit(long value, int index) {
        return value | (0x01L << index);
    }

    private long setBits(long output, int start, long input) {
        return output | (input << start);
    }

    @Test
    public void getSingleBit() {
        long value = 0x80000000000000AAL;
        assertFalse(testBit(value, 0));
        assertTrue(testBit(value, 1));
        assertFalse(testBit(value, 2));
        assertTrue(testBit(value, 3));
        assertFalse(testBit(value, 4));
        assertTrue(testBit(value, 5));
        assertFalse(testBit(value, 6));
        assertTrue(testBit(value, 7));
        assertTrue(testBit(value, 63));
    }

    @Test
    public void setSingleBit() {
        long value = 0;
        value = setBit(value, 1);
        value = setBit(value, 3);
        value = setBit(value, 5);
        value = setBit(value, 7);
        value = setBit(value, 63);
        assertEquals(0x80000000000000AAL, value);
    }

    @Test
    public void getMultipleBits() {
        // ..... 1010 1010
        long value = 0x80000000000000AAL;
        assertEquals(2L, testBits(value, 0, 2));
        assertEquals(10, testBits(value, 0, 4));
        assertEquals(0x2A, testBits(value, 2, 6));
        assertEquals(128, testBits(value, 56 , 8));
    }

    @Test
    public void setMultipleBits() {
        long value = 0;
        assertEquals(0x8000000000000000L, setBits(value, 63, 1));
        assertEquals(0x00000000000000A0L, setBits(value, 4, 0xA));
        assertEquals(0x0000000000000080L, setBits(value, 6, 0x2));
    }
}
