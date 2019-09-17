package model.codecs;

import model.Codec;
import model.Marshalled;

import java.nio.ByteBuffer;

public class UnsignedShortCodec implements Codec<Integer> {
    @Override
    public void encode(Object sourceObject, Integer sourceField, Marshalled annotation, ByteBuffer buffer) {
        buffer.putShort(sourceField.shortValue());
    }

    @Override
    public Integer decode(Object targetObject, Integer targetField, Marshalled annotation, ByteBuffer buffer) {
        return Short.toUnsignedInt(buffer.getShort());
    }
}
