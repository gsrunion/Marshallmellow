package test;

import model.Marshalled;
import model.codecs.*;
import org.junit.Test;
import processor.Marshallmellow;

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class DecoderTest {
    @Test
    public void testSignedAndUnsignedByte() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        byte[] data = new byte[]{(byte) 0xFF, (byte) 0xFF};

        class Struct {
            @Marshalled(codec = ByteCodec.class)
            public byte signed;
            @Marshalled(codec = UnsignedByteCodec.class)
            public short unsigned;
        }

        Struct struct = decode(new Struct(), data);
        assertEquals(-1, struct.signed);
        assertEquals(255, struct.unsigned);

        encode(struct, data);
    }

    @Test
    public void testSignedAndUnsignedShort() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        byte[] data = new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
        class Struct {
            @Marshalled(codec = ShortCodec.class)
            public short signed;
            @Marshalled(codec = UnsignedShortCodec.class)
            public int unsigned;
        }
        Struct struct = decode(new Struct(), data);
        assertEquals(-1, struct.signed);
        assertEquals(65535, struct.unsigned);
        encode(struct, data);
    }

    @Test
    public void testSignedAndUnsignedInt() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        byte[] data = new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};

        class Struct {
            @Marshalled(codec = IntegerCodec.class)
            public int signed;
            @Marshalled(codec = UnsignedIntCodec.class)
            public long unsigned;
        }
        Struct struct = decode(new Struct(), data);
        assertEquals(-1, struct.signed);
        assertEquals(4294967295L, struct.unsigned);
        encode(struct, data);
    }

    @Test
    public void testLong() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        byte[] data = new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};

        class Struct {
            @Marshalled(codec = LongCodec.class)
            public long negativeOne;
            @Marshalled(codec = LongCodec.class)
            public long zero = -1;
        }

        Struct struct = decode(new Struct(), data);
        assertEquals(-1, struct.negativeOne);
        assertEquals(0, struct.zero);
        encode(struct, data);
    }

    @Test
    public void testBoolean() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        byte[] data = new byte[] { (byte)0x01, (byte)0x00 };
        class Struct {
            @Marshalled(codec = BooleanCodec.class) public boolean ttrue = false;
            @Marshalled(codec = BooleanCodec.class) public boolean tfalse = true;
        }
        Struct struct = decode(new Struct(), data);
        assertEquals(true, struct.ttrue);
        assertEquals(false, struct.tfalse);
        encode(struct, data);
    }


    @Test
    public void testConditionalInclusion() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        byte[] data = new byte[] { (byte)0x01, (byte)0x01 };
        class Struct {
            @Marshalled(codec = ByteCodec.class) public byte flags = 0;
            @Marshalled(codec = ByteCodec.class, precondition = "(flags & 0x01) != 0") public byte first = -1;
            @Marshalled(codec = ByteCodec.class, precondition = "(flags & 0x01) == 0") public byte second = -1;
        }
        Struct struct = decode(new Struct(), data);
        assertEquals(1, struct.flags);
        assertEquals(1, struct.first);
        assertEquals(-1, struct.second);
        encode(struct, data);
    }

    @Test
    public void testObject() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        byte[] data = new byte[] { 0, 1 };

        class Inner {
            @Marshalled(codec = ByteCodec.class) public byte item;
        }

        class Outer {
            @Marshalled(codec = ObjectCodec.class) public Inner a = new Inner();
            @Marshalled(codec = ObjectCodec.class) public Inner b = new Inner();
        }

        Outer struct = decode(new Outer(), data);
        assertEquals(0, struct.a.item);
        assertEquals(1, struct.b.item);
        encode(struct, data);
    }

    @Test
    public void testArray() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        byte[] data = new byte[] { 0, 1, 2, 2, 3, 4};
        class Struct {
            @Marshalled(codec = ByteCodec.class, length = "3") public byte[] fixedLength = new byte[3];
            @Marshalled(codec = ByteCodec.class, length = "1") public byte len;
            @Marshalled(codec = ByteCodec.class, length = "len") public byte[] fieldPredicated = new byte[100];
        }

        Struct struct = decode(new Struct(), data);
        assertTrue(Arrays.equals(new byte[]{ 0, 1, 2}, struct.fixedLength));
        assertEquals(2, struct.len);
        assertTrue(Arrays.equals(new byte[]{ 3, 4}, struct.fieldPredicated));
        encode(struct, data);
    }

    @Test
    public void testArrayObject() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        byte[] data = new byte[] { 1, 2 };

        class Inner {
            @Marshalled(codec = ByteCodec.class) public byte item;
        }

        class Outer {
            @Marshalled(codec = ObjectCodec.class, length = "2")
            public Inner[] a = new Inner[] { new Inner(), new Inner() };
        }

        Outer struct = decode(new Outer(), data);
        assertEquals(1, struct.a[0].item);
        assertEquals(2, struct.a[1].item);
        encode(struct, data);
    }

    enum AnEnum {
        A,
        B,
        C,
    }
    @Test
    public void testEnum() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        byte[] data = new byte[] { 2 };

        class Struct {
            @Marshalled(codec = EnumCodec.class)
            public AnEnum anEnum = AnEnum.A;
        }

        Struct struct = decode(new Struct(), data);
        assertEquals(AnEnum.C, struct.anEnum);
        encode(struct, data);
    }

    private static <T> T decode(T me, byte[] is) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        assertEquals(0, Marshallmellow.decode(me, ByteBuffer.wrap(is)).remaining());
        return me;
    }

    private static <T> void encode(T me, byte[] is) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        byte[] data = Marshallmellow.encode(me, ByteBuffer.allocate(is.length)).array();
        assertTrue(Arrays.equals(is, data));
    }
}
