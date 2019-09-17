package model;

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;

/**
 * Class that represents a utility that interacts with a java.nio.ByteBuffer to read or write a value. *NOTE* implementors
 * of this class are expected to provide a default constructor.
 */
public interface Codec<T> {
    /**
     * Conditionally writes the provided value into the provided ByteBuffer taking into consideration, if necessary, the
     * properties of the sourceField, the object that contains that field (sourceObject), properties of the annotation,
     * and the provided ByteBuffer.
     *
     * @param sourceObject instance of containing class in which the sourceField is contained
     * @param sourceField particular value to be written to the ByteBuffer
     * @param annotation annotation which controls how sourceFiled is written
     * @param buffer ByteBuffer to insert sourceField into
     */
    public void encode(Object sourceObject, T sourceField, Marshalled annotation, ByteBuffer buffer) throws InvocationTargetException, IllegalAccessException, InstantiationException;

    /**
     * Conditionally reads the a value from the provided ByteBuffer taking into consideration, if necessary, the
     * properties of the targetField, the object that contains that field (targetObject), properties of the annotation,
     * and the provided ByteBuffer.
     *
     * @param targetObject instance of containing class in which the targetField is contained
     * @param targetField particular value to be read from the ByteBuffer
     * @param annotation annotation which controls how targetField is read
     * @param buffer ByteBuffer to read targetField from.
     * @return value to be written into targetField
     */
    public T decode(Object targetObject, T targetField, Marshalled annotation, ByteBuffer buffer) throws IllegalAccessException, InstantiationException, InvocationTargetException;
}
