package model;

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;

public interface Interpretor<SourceType, TargetType> {
    public TargetType interpret(SourceType source,  TargetType target,  Interpreted annotation);
}
