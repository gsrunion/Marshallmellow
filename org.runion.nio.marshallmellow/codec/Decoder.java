package codec;

import annotations.*;

import static codec.ByteBufferUtils.*;
import static codec.BitUtils.*;
import static codec.ReflectionUtils.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

public class Decoder {
    interface FieldOperation {
        public Object operate(ByteBuffer b, Object src) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException;
    }

    static Map<Class<? extends Annotation>, FieldOperation> mappings = new HashMap<>();

    static {
        mappings.put(AsBoolean.class, (buff, in) -> ByteBufferUtils.getBoolean(buff));
        mappings.put(AsByte.class, (buff, in) -> buff.get());
        mappings.put(AsInt.class, (buff, in) -> buff.getInt());
        mappings.put(AsLong.class, (buff, in) -> buff.getLong());
        mappings.put(AsShort.class, (buff, in) -> buff.getShort());
        mappings.put(AsUnsignedByte.class, (buff, in) -> getUnsignedByte(buff));
        mappings.put(AsUnsignedInt.class, (buff, in) -> getUnsignedInt(buff));
        mappings.put(AsUnsignedShort.class, (buff, in) -> getUnsignedShort(buff));
        mappings.put(AsEnum.class, (buff, in) -> getEnum(buff, in));
        mappings.put(AsObject.class, (buff, in) -> decode(in, buff));
    }

    public static Object decode(Object me, ByteBuffer buffer)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException {
        for (Field f : me.getClass().getDeclaredFields()) {
            decodeField(me, f, buffer);
        }
        return me;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void decodeField(Object me, Field f, ByteBuffer buffer) throws IllegalAccessException, InvocationTargetException {
        if (shouldProcessField(me, f) && processArrayAnnotations(me, f, buffer) && processTransformationalAnnotations(me, f) && processParsingAnnotations(me, f, buffer)) {
            processStringAnnotations(me, f, buffer);
        }
    }

    /**
     * Determine if field should be process considering what annotations are present.
     *
     * @return true if annotations are present and the potentially present precondition annotation says to continue
     */
    private static boolean shouldProcessField(Object me, Field f) throws InvocationTargetException, IllegalAccessException {
        if (f.getAnnotations().length == 0) {
            return false;
        }

        Precondition precondition = f.getAnnotation(Precondition.class);
        if (precondition != null && !(boolean) callGettorOrReadField(me, precondition.precondition())) {
            return false;
        }

        return true;
    }

    /**
     * Process annotations that do not consume data from the buffer but instead populate fields based on some other
     * field that does consume data from teh the buffer.
     *
     * @return false if a transformational annotation was present, false otherwise
     */
    private static boolean processTransformationalAnnotations(Object me, Field f) throws IllegalAccessException, InvocationTargetException {
        AsBit asBit = f.getAnnotation(AsBit.class);
        if (asBit != null) {
            writeField(me, f, getBit((byte) readField(me, asBit.fieldName()), asBit.bitIndex()));
            return false;
        }

        return true;
    }

    /**
     * Process annotations, if present, that describe how to consume data from the buffer
     */
    private static boolean processParsingAnnotations(Object me, Field f, ByteBuffer buffer) throws IllegalAccessException, InvocationTargetException {
        Optional<FieldOperation> encoder = mappings.keySet().stream().filter(a -> f.getAnnotation(a) != null).map(mappings::get).findFirst();
        if (encoder.isPresent()) {
            writeField(me, f, encoder.get().operate(buffer, readField(me, f)));
            return false;
        }
        return true;
    }

    private static boolean processStringAnnotations(Object me, Field f, ByteBuffer buffer) throws InvocationTargetException, IllegalAccessException {
        AsString asString = f.getAnnotation(AsString.class);
        if (asString != null) {
            int fixedLength = asString.fixedLength();
            if (fixedLength != -1) {
                writeField(me, f, getString(buffer, asString.charSet(), fixedLength).replace("\0", ""));
                return false;
            } else if (!asString.lengthProvider().isEmpty()) {
                writeField(me, f, getString(buffer, asString.charSet(), ((Number) callGettorOrReadField(me, asString.lengthProvider())).intValue()).replace("\0", ""));
                return false;
            } else {
                writeField(me, f, getString(buffer, asString.charSet()).replace("\0", ""));
                return false;
            }
        }
        return true;
    }

    private static boolean processArrayAnnotations(Object me, Field f, ByteBuffer buffer) throws IllegalAccessException, InvocationTargetException {
        AsArray asArray = f.getAnnotation(AsArray.class);
        if (asArray != null) {
            Optional<FieldOperation> encoder = mappings.keySet().stream().filter(a -> f.getAnnotation(a) != null).map(mappings::get).findFirst();

            Object target = readField(me, f);

            int length = asArray.fixedLength();
            if (length == -1 && !asArray.lengthProvider().isEmpty()) {
                length = ((Number) callGettorOrReadField(me, asArray.lengthProvider())).intValue();
            } else if (length == -1 && asArray.lengthProvider().isEmpty()) {
                length = Array.getLength(target);
            }

            //target = Array.newInstance(target.getClass().getComponentType(), length);
            for (int x = 0; x < length; x++) {
                Array.set(target, x, encoder.get().operate(buffer, Array.get(target, x)));
            }

            Class<?> arrayType = target.getClass().getComponentType();
            Object copy = Array.newInstance(arrayType, length);
            System.arraycopy(target, 0, copy, 0, length);
            writeField(me, f, copy);

            return false;
        }
        return true;
    }
}

