package test;

import annotations.*;
import codec.Encoder;
import org.junit.Test;
import sun.jvm.hotspot.utilities.CStringUtilities;

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class EncoderTest {
    @Test
    public void testSignedAndUnsignedByte() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        class Struct {
            @AsByte public byte signed;
            @AsUnsignedByte public short unsigned;
        }
        Struct struct = decode(new Struct(), (byte)0xFF, (byte)0xFF);
        assertEquals(-1, struct.signed);
        assertEquals(255, struct.unsigned);
    }

    @Test
    public void testSignedAndUnsignedShort() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        class Struct {
            @AsShort public short signed;
            @AsUnsignedShort public int unsigned;
        }
        Struct struct = decode(new Struct(), (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF);
        assertEquals(-1, struct.signed);
        assertEquals(65535, struct.unsigned);
    }

    @Test
    public void testSignedAndUnsignedInt() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        class Struct {
            @AsInt public int signed;
            @AsUnsignedInt public long unsigned;
        }
        Struct struct = decode(new Struct(), (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF);
        assertEquals(-1, struct.signed);
        assertEquals(4294967295L, struct.unsigned);
    }

    @Test
    public void testLong() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        class Struct {
            @AsLong public long negativeOne;
            @AsLong public long zero = -1;
        }
        Struct struct = decode(new Struct(), (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,
                                             (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00);
        assertEquals(-1, struct.negativeOne);
        assertEquals(0, struct.zero);
    }

    @Test
    public void testBoolean() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        class Struct {
            @AsBoolean public boolean ttrue = false;
            @AsBoolean public boolean tfalse = true;
        }
        Struct struct = decode(new Struct(), (byte)0x01, (byte)0x00);
        assertEquals(true, struct.ttrue);
        assertEquals(false, struct.tfalse);
    }

    enum AnEnum {
        X,
        Y,
        Z;
    }

    @Test
    public void testAsEnum() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        class Struct {
            @AsEnum AnEnum x = AnEnum.Z;
            @AsEnum AnEnum y = AnEnum.Z;
            @AsEnum AnEnum z = AnEnum.X;
        }
        Struct struct = decode(new Struct(), (byte)0x00, (byte)0x01, (byte)0x02);
        assertEquals(AnEnum.X, struct.x);
        assertEquals(AnEnum.Y, struct.y);
        assertEquals(AnEnum.Z, struct.z);
    }

    @Test
    public void testConditionalInclusion() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        class Struct {
            @AsBoolean
            public boolean truePredicate = false;
            @AsBoolean
            public boolean falsePredicate = true;

            @Precondition(precondition = "truePredicate")
            @AsByte
            public byte first;

            @Precondition(precondition = "falsePredicate")
            @AsByte
            public byte second;

            @Precondition(precondition = "shouldInclude")
            @AsByte
            public byte third;

            Struct() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
            }

            public boolean shouldInclude() {
                return truePredicate;
            }
        }
        Struct struct = decode(new Struct(), (byte)0x01, (byte)0x00, (byte)0x01, (byte)0x02);
        assertTrue(struct.truePredicate);
        assertFalse(struct.falsePredicate);
        assertEquals(1, struct.first);
        assertEquals(0, struct.second);
        assertEquals(2, struct.third);
    }

    @Test
    public void testAsBit() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        class Struct {
            @AsByte public byte bits;
            @AsBit(fieldName = "bits", bitIndex = 0) public boolean bit0;
            @AsBit(fieldName = "bits", bitIndex = 1) public boolean bit1;
            @AsBit(fieldName = "bits", bitIndex = 2) public boolean bit2;
            @AsBit(fieldName = "bits", bitIndex = 3) public boolean bit3;
            @AsBit(fieldName = "bits", bitIndex = 4) public boolean bit4;
            @AsBit(fieldName = "bits", bitIndex = 5) public boolean bit5;
            @AsBit(fieldName = "bits", bitIndex = 6) public boolean bit6;
            @AsBit(fieldName = "bits", bitIndex = 7) public boolean bit7;
        }
        Struct struct = decode(new Struct(), (byte)0xAA);
        assertFalse(struct.bit0);
        assertFalse(struct.bit2);
        assertFalse(struct.bit4);
        assertFalse(struct.bit6);
        assertTrue(struct.bit1);
        assertTrue(struct.bit3);
        assertTrue(struct.bit5);
        assertTrue(struct.bit7);
    }

    @Test
    public void testString() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        class Struct {
            @AsByte public byte len;
            @AsString(fixedLength = 4) public String fixedLengthString;
            @AsString public String nullTerminated;
            @AsString(lengthProvider = "len") public String fieldPredicatedString;
            @AsString(lengthProvider = "length") public String methodPredicatedString;

            public int length() {
                return len;
            }
        }
        Struct struct = decode(new Struct(), 2, 'A', 'B', 'C', 'D', 'A', 'B', 0, 'D', 'E', 'A', 'D');
        assertEquals(2, struct.len);
        assertEquals("ABCD", struct.fixedLengthString);
        assertEquals("AB", struct.nullTerminated);
        assertEquals("DE", struct.fieldPredicatedString);
        assertEquals("AD", struct.methodPredicatedString);
    }

    @Test
    public void testObject() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        class Inner {
            @AsByte public byte item;
        }

        class Outer {
            @AsObject public Inner a = new Inner();
            @AsObject public Inner b = new Inner();
        }

        Outer struct = decode(new Outer(), 0, 1);
        assertEquals(0, struct.a.item);
        assertEquals(1, struct.b.item);
    }

    @Test
    public void testArray() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        class Struct {
            @AsByte
            @AsArray(fixedLength = 3)
            public byte[] fixedLength = new byte[100];

            @AsByte public byte len;

            @AsByte
            @AsArray(lengthProvider = "len")
            public byte[] fieldPredicated = new byte[100];

            @AsByte
            @AsArray(lengthProvider = "length")
            public byte[] methodPredicated = new byte[100];

            public int length() {
                return len;
            }
        }

        Struct struct = decode(new Struct(), 0, 1, 2, 2, 3, 4, 5, 6);
        assertTrue(Arrays.equals(new byte[]{ 0, 1, 2}, struct.fixedLength));
        assertEquals(2, struct.len);
        assertTrue(Arrays.equals(new byte[]{ 3, 4}, struct.fieldPredicated));
        assertTrue(Arrays.equals(new byte[]{ 5, 6}, struct.methodPredicated));
    }

    @Test
    public void testArrayObject() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        class Inner {
            @AsByte public byte item;
        }

        class Outer {
            @AsObject
            @AsArray(fixedLength = 2)
            public Inner[] a = new Inner[] { new Inner(), new Inner() };
        }

        Outer struct = decode(new Outer(), 1, 2);
        assertEquals(1, struct.a[0].item);
        assertEquals(2, struct.a[1].item);
    }

    private static <T> T decode(T me, int...is) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        ByteBuffer buffer = ByteBuffer.allocate(is.length);
        Arrays.stream(is).forEach(b -> buffer.put((byte) b));
        buffer.flip();
        Encoder.decode(me, buffer);
        return me;
    }
}
