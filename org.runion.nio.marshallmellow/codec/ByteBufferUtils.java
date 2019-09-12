package codec;

import org.apache.commons.lang3.EnumUtils;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.spi.CharsetProvider;
import java.util.Arrays;

public class ByteBufferUtils {
    public static <T extends Enum<T>> T getEnum(ByteBuffer buffer, Object value) {
        return (T) value.getClass().getEnumConstants()[getUnsignedByte(buffer)];
    }

    public static boolean getBoolean(ByteBuffer buffer) {
        return buffer.get() == 0 ? false : true;
    }

    public static short getUnsignedByte(ByteBuffer buffer){
        return (short)Byte.toUnsignedInt(buffer.get());
    }

    public static int getUnsignedShort(ByteBuffer buffer){
        return Short.toUnsignedInt(buffer.getShort());
    }

    public static long getUnsignedInt(ByteBuffer buffer) {
        return Integer.toUnsignedLong(buffer.getInt());
    }

    public static String getString(ByteBuffer buffer, String charset) {
        int x = 0;
        ByteBuffer slice = buffer.slice();
        while(slice.remaining() > 0) {
            x++;
            if(slice.get() == 0) {
                break;
            }
        }

        byte[] data = new byte[x];
        buffer.get(data);
        return new String(data, Charset.forName(charset));
    }

    public static String getString(ByteBuffer buffer, String charset, int length) {
        byte[] data = new byte[length];
        buffer.get(data);
        return new String(data, Charset.forName(charset));
    }
}
