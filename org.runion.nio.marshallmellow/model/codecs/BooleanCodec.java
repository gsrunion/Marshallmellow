package model.codecs;

import model.Codec;
import model.Marshalled;
import java.nio.ByteBuffer;

public class BooleanCodec implements Codec {
    @Override
    public void encode(Object sourceObject, Object sourceField, Marshalled annotation, ByteBuffer buffer) {
        buffer.put((byte) ((boolean)sourceField ? 0x01 : 0x00));
    }

    @Override
    public Object decode(Object targetObject, Object targetField, Marshalled annotation, ByteBuffer buffer) {
        return buffer.get() != 0x00;
    }
}

