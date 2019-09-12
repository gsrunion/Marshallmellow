package codec;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionUtils {
    public static Object readField(Object me, Field field) throws IllegalAccessException {
        return FieldUtils.readField(field, me, true);
    }

    public static Object readField(Object me, String fieldName) throws IllegalAccessException {
        return FieldUtils.readField(me, fieldName, true);
    }

    public static void writeField(Object me, Field field, Object value) throws IllegalAccessException {
        FieldUtils.writeField(field, me, value, true);
    }

    public static void writeField(Object me, String fieldName, Object value) throws IllegalAccessException {
        FieldUtils.writeField(me, fieldName, value, true);
    }

    public static Object callGetter(Object me, String methodName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return MethodUtils.invokeMethod(me, methodName);
    }

    public static Object callGettorOrReadField(Object me, String getterOrFieldName) throws InvocationTargetException, IllegalAccessException {
        try {
            return callGetter(me, getterOrFieldName);
        } catch (NoSuchMethodException e) {
            return readField(me, getterOrFieldName);
        }
    }
}
