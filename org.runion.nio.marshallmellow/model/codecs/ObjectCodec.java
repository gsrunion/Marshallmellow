package model.codecs;

import model.Codec;
import model.Marshalled;
import processor.Marshallmellow;

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;

public class ObjectCodec implements Codec<Object> {
    @Override
    public void encode(Object sourceObject, Object sourceField, Marshalled annotation, ByteBuffer buffer) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        Marshallmellow.encode(sourceField, buffer);
    }

    @Override
    public Object decode(Object targetObject, Object targetField, Marshalled annotation, ByteBuffer buffer) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Marshallmellow.decode(targetField, buffer);
        return targetField;
    }
}
