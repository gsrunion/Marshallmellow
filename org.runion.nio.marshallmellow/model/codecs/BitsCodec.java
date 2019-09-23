package model.codecs;

import model.Bits;
import model.Codec;
import model.Marshalled;

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;

public class BitsCodec implements Codec<Bits> {
    @Override
    public void encode(Object sourceObject, Bits sourceField, Marshalled annotation, ByteBuffer buffer) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        switch(sourceField.getByteCount()) {
            case 1: buffer.put(sourceField.getData().byteValue()); break;
            case 2: buffer.putShort(sourceField.getData().shortValue()); break;
            case 4: buffer.putInt(sourceField.getData().intValue()); break;
            case 8: buffer.putLong(sourceField.getData().longValue()); break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public Bits decode(Object targetObject, Bits targetField, Marshalled annotation, ByteBuffer buffer) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        switch(targetField.getByteCount()) {
            case 1: return new Bits(1, buffer.get());
            case 2: return new Bits(2, buffer.getShort());
            case 4: return new Bits(4, buffer.getInt());
            case 8: return new Bits(8, buffer.getLong());
            default:
                throw new IllegalArgumentException();
        }
    }
}
