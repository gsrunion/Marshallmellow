package model.codecs;

import model.Codec;
import model.Marshalled;

import java.nio.ByteBuffer;

public class LongCodec implements Codec<Long> {
    @Override
    public void encode(Object sourceObject, Long sourceField, Marshalled annotation, ByteBuffer buffer) {
        buffer.putLong(sourceField);
    }

    @Override
    public Long decode(Object targetObject, Long targetField, Marshalled annotation, ByteBuffer buffer) {
        return buffer.getLong();
    }
}
