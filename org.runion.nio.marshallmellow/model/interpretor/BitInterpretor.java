package model.interpretor;

import model.Interpreted;
import model.Interpretor;

import java.util.BitSet;

public class BitInterpretor<T extends Number> implements Interpretor<T,  Boolean> {
    @Override
    public Boolean interpret(T source, Boolean target, Interpreted annotation) {
        return BitSet.valueOf(new long[] { source.longValue() }).get(annotation.bit());
    }
}
