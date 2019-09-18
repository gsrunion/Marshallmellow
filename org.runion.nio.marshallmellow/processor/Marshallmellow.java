package processor;

import model.Interpreted;
import model.Marshalled;
import model.codecs.ArrayCodec;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;

import static util.ReflectionUtils.*;

public class Marshallmellow {
    public static ByteBuffer decode(Object targetObject, ByteBuffer buffer) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        for (Field targetField : targetObject.getClass().getDeclaredFields()) {
            Marshalled marshalled = targetField.getAnnotation(Marshalled.class);
            if (marshalled != null && checkPrecondition(targetObject, marshalled.precondition())) {
                if (marshalled.isArray()) {
                    writeField(targetObject, targetField, new ArrayCodec().decode(targetObject, readField(targetObject, targetField), marshalled, buffer));
                } else {
                    writeField(targetObject, targetField, marshalled.codec().newInstance().decode(targetObject, readField(targetObject, targetField), marshalled, buffer));
                }
            }
        }
        return buffer;
    }

    public static ByteBuffer encode(Object sourceObject, ByteBuffer buffer) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        for (Field sourceField : sourceObject.getClass().getDeclaredFields()) {
            Marshalled marshalled = sourceField.getAnnotation(Marshalled.class);
            if ((marshalled != null) && checkPrecondition(sourceObject, marshalled.precondition())) {
                if (marshalled.isArray()) {
                    new ArrayCodec().encode(sourceObject, readField(sourceObject, sourceField), marshalled, buffer);
                } else {
                    marshalled.codec().newInstance().encode(sourceObject, readField(sourceObject, sourceField), marshalled, buffer);
                }
            }
        }
        return buffer;
    }

    public static void interpret(Object targetObject) throws IllegalAccessException, InstantiationException {
        for (Field targetField : targetObject.getClass().getDeclaredFields()) {
            Interpreted interpreted = targetField.getAnnotation(Interpreted.class);
            if(interpreted != null) {
                Object sourceValue = readField(targetObject, interpreted.feildName());
                Object targetValue = readField(targetObject, targetField);
                Object updated = interpreted.interpretor().newInstance().interpret(sourceValue, targetValue, interpreted);
                writeField(targetObject, targetField, updated);
            }
        }
    }
}
