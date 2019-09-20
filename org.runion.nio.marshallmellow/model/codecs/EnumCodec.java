package model.codecs;

import model.Codec;
import model.Marshalled;

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;

public class EnumCodec implements Codec<Enum<?>> {
    @Override
    public void encode(Object sourceObject, Enum<?> sourceField, Marshalled annotation, ByteBuffer buffer) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        buffer.put((byte)sourceField.ordinal());
    }

    @Override
    public Enum<?> decode(Object targetObject, Enum<?> targetField, Marshalled annotation, ByteBuffer buffer) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        return targetField.getClass().getEnumConstants()[buffer.get()];
    }
}
