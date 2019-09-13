package test;

import codec.Decoder;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class RealWorldTest {

    @Test
    public void testRealExample() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        OptionsHeader options = decode(new OptionsHeader(), 0x83, 5, 1, 2, 3, 4, 5, 1, 1);

        assertTrue(options.getOption0().isPresent());
        options.getOption0().ifPresent(o -> {
            assertTrue(Arrays.equals(new byte[] { 1, 2, 3, 4, 5}, o));
        });

        assertTrue(options.getOption1().isPresent());
        options.getOption1().ifPresent(o -> {
            assertTrue(Arrays.equals(new byte[] { 1 }, o));
        });

        assertFalse(options.getOption2().isPresent());
        assertFalse(options.getOption3().isPresent());
        assertFalse(options.getOption4().isPresent());
        assertFalse(options.getOption5().isPresent());
        assertFalse(options.getOption6().isPresent());
    }



    private static <T> T decode(T me, int...is) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        ByteBuffer buffer = ByteBuffer.allocate(is.length);
        Arrays.stream(is).forEach(b -> buffer.put((byte) b));
        buffer.flip();
        Decoder.decode(me, buffer);
        return me;
    }

}
