package model.codecs;

import model.Codec;
import model.Marshalled;

import java.nio.ByteBuffer;

public class UnsignedByteCodec implements Codec<Short> {
    @Override
    public void encode(Object sourceObject, Short sourceField, Marshalled annotation, ByteBuffer buffer) {
        buffer.put(sourceField.byteValue());
    }

    @Override
    public Short decode(Object targetObject, Short targetField, Marshalled annotation, ByteBuffer buffer) {
        return Integer.valueOf(Byte.toUnsignedInt(buffer.get())).shortValue();
    }
}
