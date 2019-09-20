package processor;

import model.Marshalled;
import model.codecs.ArrayCodec;
import org.apache.commons.jexl3.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;

import static org.apache.commons.lang3.reflect.FieldUtils.readField;
import static org.apache.commons.lang3.reflect.FieldUtils.writeField;

public class Marshallmellow {
    private interface Operation {
        void operate(Field field, Object fieldValue, Marshalled annotation, int len) throws IllegalAccessException, InstantiationException, InvocationTargetException;
    }

    public static ByteBuffer decode(Object targetObject, ByteBuffer buffer) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        operate(targetObject, buffer, (field, fieldValue, marshalled, len) -> {
            if (len > 1) {
                writeField(field, targetObject, new ArrayCodec(len).decode(targetObject, fieldValue, marshalled, buffer), true);
            } else if (len == 1) {
                writeField(field, targetObject, marshalled.codec().newInstance().decode(targetObject, fieldValue, marshalled, buffer), true);
            }
        });
        return buffer;
    }

    public static ByteBuffer encode(Object sourceObject, ByteBuffer buffer) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        operate(sourceObject, buffer, (field, fieldValue, marshalled, len) -> {
            if (len > 1) {
                new ArrayCodec(len).encode(sourceObject, fieldValue, marshalled, buffer);
            } else if (len == 1) {
                marshalled.codec().newInstance().encode(sourceObject, fieldValue, marshalled, buffer);
            }
        });
        return buffer;
    }

    private static void operate(Object parent, ByteBuffer buffer, Operation operation) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        JexlEngine expressionEngine = new JexlBuilder().create();
        JexlContext expressionContext = new MapContext();

        for (Field field : parent.getClass().getDeclaredFields()) {
            Object fieldValue = readField(field, parent, true);
            Marshalled marshalled = field.getAnnotation(Marshalled.class);

            if ((marshalled != null) && (boolean) expressionEngine.createExpression(marshalled.precondition()).evaluate(expressionContext)) {
                int len = ((Number) expressionEngine.createExpression(marshalled.length()).evaluate(expressionContext)).intValue();
                operation.operate(field, fieldValue, marshalled, len);
            }

            expressionContext.set(field.getName(), readField(field, parent, true));
        }
    }
}
