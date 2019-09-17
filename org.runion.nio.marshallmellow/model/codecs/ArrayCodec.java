package model.codecs;

import model.Codec;
import model.Marshalled;
import util.ReflectionUtils;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;

public class ArrayCodec implements Codec<Object> {
    @Override
    public void encode(Object sourceObject, Object sourceField, Marshalled annotation, ByteBuffer buffer) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        Codec marshaller = annotation.codec().newInstance();
        int length = ((Number) ReflectionUtils.callGettorOrReadFieldOrDefault(sourceObject, annotation.length(), Array.getLength(sourceField))).intValue();
        for (int x = 0; x < length; x++) {
            marshaller.encode(sourceObject, Array.get(sourceField, x), annotation, buffer);
        }
    }

    @Override
    public Object decode(Object targetObject, Object targetField, Marshalled annotation, ByteBuffer buffer) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Codec marshaller = annotation.codec().newInstance();
        int length = ((Number) ReflectionUtils.callGettorOrReadFieldOrDefault(targetObject, annotation.length(), Array.getLength(targetField))).intValue();

        for (int x = 0; x < length; x++) {
            Array.set(targetField, x, marshaller.decode(targetObject, Array.get(targetField, x), annotation, buffer));
        }

        Class<?> arrayType = targetField.getClass().getComponentType();
        Object copy = Array.newInstance(arrayType, length);
        System.arraycopy(targetField, 0, copy, 0, length);

        return copy;
    }
}
