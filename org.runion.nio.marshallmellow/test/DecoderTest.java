package test;

import model.Bits;
import model.Marshalled;
import model.codecs.*;
import org.junit.Test;
import processor.Marshallmellow;

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.BitSet;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class DecoderTest {
    private static final ByteOrder[] orders = {
            ByteOrder.BIG_ENDIAN,
            ByteOrder.LITTLE_ENDIAN
    };

    private static <T> T decode(T me, ByteBuffer buffer) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        buffer.flip();
        assertEquals(0, Marshallmellow.decode(me, buffer).remaining());
        return me;
    }

    private static <T> void encode(T me, ByteBuffer buffer) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        buffer.flip();
        ByteBuffer result = Marshallmellow.encode(me, ByteBuffer.allocate(buffer.remaining()).order(buffer.order()));
        assertTrue(Arrays.equals(buffer.array(), result.array()));
    }

    @Test
    public void testSignedAndUnsignedByte() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        class Struct {
            @Marshalled(codec = ByteCodec.class)
            public byte signed;
            @Marshalled(codec = UnsignedByteCodec.class)
            public short unsigned;
            @Marshalled(codec = ByteCodec.class)
            public byte signed2;
            @Marshalled(codec = UnsignedByteCodec.class)
            public short unsigned2;

        }

        for (ByteOrder order : orders) {
            ByteBuffer buffer = ByteBuffer.allocate(4).order(order).put((byte) 0xFF).put((byte) 0xFF).put((byte) 88).put((byte) 88);
            Struct struct = decode(new Struct(), buffer);
            assertEquals(-1, struct.signed);
            assertEquals(255, struct.unsigned);
            assertEquals(88, struct.signed2);
            assertEquals(88, struct.unsigned2);
            encode(struct, buffer);
        }
    }

    @Test
    public void testSignedAndUnsignedShort() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        class Struct {
            @Marshalled(codec = ShortCodec.class)
            public short signed;
            @Marshalled(codec = UnsignedShortCodec.class)
            public int unsigned;
            @Marshalled(codec = ShortCodec.class)
            public short signed2;
            @Marshalled(codec = UnsignedShortCodec.class)
            public int unsigned2;
        }

        for (ByteOrder order : orders) {
            ByteBuffer buffer = ByteBuffer.allocate(8).order(order).putShort((short) 0xFFFF).putShort((short) 0xFFFF).putShort((short) 88).putShort((short) 88);
            Struct struct = decode(new Struct(), buffer);
            assertEquals(-1, struct.signed);
            assertEquals(65535, struct.unsigned);
            assertEquals(88, struct.signed2);
            assertEquals(88, struct.unsigned2);
            encode(struct, buffer);
        }

    }

    @Test
    public void testSignedAndUnsignedInt() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        class Struct {
            @Marshalled(codec = IntegerCodec.class)
            public int signed;
            @Marshalled(codec = UnsignedIntCodec.class)
            public long unsigned;
            @Marshalled(codec = IntegerCodec.class)
            public int signed2;
            @Marshalled(codec = UnsignedIntCodec.class)
            public long unsigned2;
        }

        for (ByteOrder order : orders) {
            ByteBuffer buffer = ByteBuffer.allocate(16).order(order).putInt(0xFFFFFFFF).putInt(0xFFFFFFFF).putInt(88).putInt(88);
            Struct struct = decode(new Struct(), buffer);
            assertEquals(-1, struct.signed);
            assertEquals(4294967295L, struct.unsigned);
            assertEquals(88, struct.signed2);
            assertEquals(88, struct.unsigned2);
            encode(struct, buffer);
        }
    }

    @Test
    public void testLong() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        class Struct {
            @Marshalled(codec = LongCodec.class)
            public long negativeOne;
            @Marshalled(codec = LongCodec.class)
            public long zero = -1;
        }

        for (ByteOrder order : orders) {
            ByteBuffer buffer = ByteBuffer.allocate(16).order(order).putLong(0xFFFFFFFFFFFFFFFFL).putLong(0);
            Struct struct = decode(new Struct(), buffer);
            assertEquals(-1, struct.negativeOne);
            assertEquals(0, struct.zero);
            encode(struct, buffer);
        }

    }

    @Test
    public void testBoolean() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        class Struct {
            @Marshalled(codec = BooleanCodec.class)
            public boolean ttrue = false;
            @Marshalled(codec = BooleanCodec.class)
            public boolean tfalse = true;
        }

        for (ByteOrder order : orders) {
            ByteBuffer buffer = ByteBuffer.allocate(2).order(order).put((byte) 1).put((byte) 0);
            Struct struct = decode(new Struct(), buffer);
            assertEquals(true, struct.ttrue);
            assertEquals(false, struct.tfalse);
            encode(struct, buffer);
        }
    }

    @Test
    public void testConditionalInclusion() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        class Struct {
            @Marshalled(codec = ByteCodec.class)
            public byte flags = 0;
            @Marshalled(codec = ByteCodec.class, precondition = "(flags & 0x01) != 0")
            public byte first = -1;
            @Marshalled(codec = ByteCodec.class, precondition = "(flags & 0x01) == 0")
            public byte second = -1;
        }

        for (ByteOrder order : orders) {
            ByteBuffer buffer = ByteBuffer.allocate(2).order(order).put((byte) 1).put((byte) 1);
            Struct struct = decode(new Struct(), buffer);
            assertEquals(1, struct.flags);
            assertEquals(1, struct.first);
            assertEquals(-1, struct.second);
            encode(struct, buffer);
        }
    }

    @Test
    public void testObject() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        class Inner {
            @Marshalled(codec = ByteCodec.class)
            public byte item;
        }

        class Outer {
            @Marshalled(codec = ObjectCodec.class)
            public Inner a = new Inner();
            @Marshalled(codec = ObjectCodec.class)
            public Inner b = new Inner();
        }

        for (ByteOrder order : orders) {
            ByteBuffer buffer = ByteBuffer.allocate(2).order(order).put((byte) 0).put((byte) 1);
            Outer struct = decode(new Outer(), buffer);
            assertEquals(0, struct.a.item);
            assertEquals(1, struct.b.item);
            encode(struct, buffer);
        }
    }

    @Test
    public void testArray() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        class Struct {
            @Marshalled(codec = ByteCodec.class, length = "3")
            public byte[] fixedLength = new byte[3];
            @Marshalled(codec = ByteCodec.class, length = "1")
            public byte len;
            @Marshalled(codec = ByteCodec.class, length = "len")
            public byte[] fieldPredicated = new byte[100];
        }

        for (ByteOrder order : orders) {
            ByteBuffer buffer = ByteBuffer.allocate(6).order(order).put((byte) 0).put((byte) 1).put((byte) 2).put((byte) 2).put((byte) 3).put((byte) 4);
            Struct struct = decode(new Struct(), buffer);
            assertTrue(Arrays.equals(new byte[]{0, 1, 2}, struct.fixedLength));
            assertEquals(2, struct.len);
            assertTrue(Arrays.equals(new byte[]{3, 4}, struct.fieldPredicated));
            encode(struct, buffer);
        }
    }

    @Test
    public void testArrayObject() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        class Inner {
            @Marshalled(codec = ByteCodec.class)
            public byte item;
        }

        class Outer {
            @Marshalled(codec = ObjectCodec.class, length = "2")
            public Inner[] a = new Inner[]{new Inner(), new Inner()};
        }

        for (ByteOrder order : orders) {
            ByteBuffer buffer = ByteBuffer.allocate(2).order(order).put((byte) 1).put((byte) 2);
            Outer struct = decode(new Outer(), buffer);
            assertEquals(1, struct.a[0].item);
            assertEquals(2, struct.a[1].item);
            encode(struct, buffer);
        }
    }

    enum AnEnum {
        A,
        B,
        C,
    }

    @Test
    public void testEnum() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        class Struct {
            @Marshalled(codec = EnumCodec.class)
            public AnEnum anEnum = AnEnum.A;
        }

        for (ByteOrder order : orders) {
            ByteBuffer buffer = ByteBuffer.allocate(1).order(order).put((byte) 2);
            Struct struct = decode(new Struct(), buffer);
            assertEquals(AnEnum.C, struct.anEnum);
            encode(struct, buffer);
        }
    }

    @Test
    public void testGetBit() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        class Struct {
            @Marshalled(codec = BitsCodec.class)
            public Bits bits = new Bits(8);
        }

        for (ByteOrder order : orders) {
            ByteBuffer buffer = ByteBuffer.allocate(8).order(order).putLong(0xDEADBEEF00000000L);
            Struct struct = decode(new Struct(), buffer);
            int bit = 0;

            assertFalse(struct.bits.getBit(bit++));
            assertFalse(struct.bits.getBit(bit++));
            assertFalse(struct.bits.getBit(bit++));
            assertFalse(struct.bits.getBit(bit++));
            assertFalse(struct.bits.getBit(bit++));
            assertFalse(struct.bits.getBit(bit++));
            assertFalse(struct.bits.getBit(bit++));
            assertFalse(struct.bits.getBit(bit++));

            assertFalse(struct.bits.getBit(bit++));
            assertFalse(struct.bits.getBit(bit++));
            assertFalse(struct.bits.getBit(bit++));
            assertFalse(struct.bits.getBit(bit++));
            assertFalse(struct.bits.getBit(bit++));
            assertFalse(struct.bits.getBit(bit++));
            assertFalse(struct.bits.getBit(bit++));
            assertFalse(struct.bits.getBit(bit++));

            assertFalse(struct.bits.getBit(bit++));
            assertFalse(struct.bits.getBit(bit++));
            assertFalse(struct.bits.getBit(bit++));
            assertFalse(struct.bits.getBit(bit++));
            assertFalse(struct.bits.getBit(bit++));
            assertFalse(struct.bits.getBit(bit++));
            assertFalse(struct.bits.getBit(bit++));
            assertFalse(struct.bits.getBit(bit++));

            assertFalse(struct.bits.getBit(bit++));
            assertFalse(struct.bits.getBit(bit++));
            assertFalse(struct.bits.getBit(bit++));
            assertFalse(struct.bits.getBit(bit++));
            assertFalse(struct.bits.getBit(bit++));
            assertFalse(struct.bits.getBit(bit++));
            assertFalse(struct.bits.getBit(bit++));
            assertFalse(struct.bits.getBit(bit++));


            // 0xF
            assertTrue(struct.bits.getBit(bit++));
            assertTrue(struct.bits.getBit(bit++));
            assertTrue(struct.bits.getBit(bit++));
            assertTrue(struct.bits.getBit(bit++));

            // 0xE
            assertFalse(struct.bits.getBit(bit++));
            assertTrue(struct.bits.getBit(bit++));
            assertTrue(struct.bits.getBit(bit++));
            assertTrue(struct.bits.getBit(bit++));

            // 0xE
            assertFalse(struct.bits.getBit(bit++));
            assertTrue(struct.bits.getBit(bit++));
            assertTrue(struct.bits.getBit(bit++));
            assertTrue(struct.bits.getBit(bit++));

            // 0xB
            assertTrue(struct.bits.getBit(bit++));
            assertTrue(struct.bits.getBit(bit++));
            assertFalse(struct.bits.getBit(bit++));
            assertTrue(struct.bits.getBit(bit++));

            // 0xD
            assertTrue(struct.bits.getBit(bit++));
            assertFalse(struct.bits.getBit(bit++));
            assertTrue(struct.bits.getBit(bit++));
            assertTrue(struct.bits.getBit(bit++));

            // 0xA
            assertFalse(struct.bits.getBit(bit++));
            assertTrue(struct.bits.getBit(bit++));
            assertFalse(struct.bits.getBit(bit++));
            assertTrue(struct.bits.getBit(bit++));

            // 0xE
            assertFalse(struct.bits.getBit(bit++));
            assertTrue(struct.bits.getBit(bit++));
            assertTrue(struct.bits.getBit(bit++));
            assertTrue(struct.bits.getBit(bit++));

            // 0xD
            assertTrue(struct.bits.getBit(bit++));
            assertFalse(struct.bits.getBit(bit++));
            assertTrue(struct.bits.getBit(bit++));
            assertTrue(struct.bits.getBit(bit++));

            encode(struct, buffer);
        }
    }

    @Test
    public void testSetBit() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        class Struct {
            @Marshalled(codec = BitsCodec.class)
            public Bits bits = new Bits(8);
        }

        for (ByteOrder order : orders) {
            ByteBuffer buffer = ByteBuffer.allocate(8).order(order).putLong(4294967298L);
            Struct struct = new Struct();
            struct.bits.setBit(1);  // 2^1 = 2
            struct.bits.setBit(32); // 2^32 = 4,294,967,296
            encode(struct, buffer);
        }
    }

    @Test
    public void testGetBits() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        class Struct {
            @Marshalled(codec = BitsCodec.class)
            public Bits bits = new Bits(8);
        }

        for (ByteOrder order : orders) {
            ByteBuffer buffer = ByteBuffer.allocate(8).order(order).putLong(0x80000000000000AAL);
            Struct struct = decode(new Struct(), buffer);
            assertEquals(2L, struct.bits.getBits(0, 2));
            assertEquals(10, struct.bits.getBits(0, 4));
            assertEquals(0x2A, struct.bits.getBits(2, 6));
            assertEquals(128, struct.bits.getBits(56, 8));
            encode(struct, buffer);
        }
    }

    @Test
    public void testSetBits() {
        class Struct {
            @Marshalled(codec = BitsCodec.class)
            public Bits bits = new Bits(8);
        }

        for (ByteOrder order : orders) {
            ByteBuffer buffer = ByteBuffer.allocate(8).order(order);
            Struct struct = new Struct();
            struct.bits.setBits(63, 1);
            assertEquals(0x8000000000000000L, (long)struct.bits.getData());
            struct.bits.setBits(4, 10);
            assertEquals(0x80000000000000A0L, (long)struct.bits.getData());
            struct.bits.setBits(2, 2);
            assertEquals(0x80000000000000A8L, (long)struct.bits.getData());
            struct.bits.setBits(0, 2);
            assertEquals(0x80000000000000AAL, (long)struct.bits.getData());
        }
    }
}

