package model.interpretor;

import model.Interpreted;
import model.Interpretor;

public class EnumInterpretor<EnumType extends Enum<EnumType>, T extends Number> implements Interpretor<T, EnumType> {
    @Override
    public EnumType interpret(T source, EnumType target, Interpreted annotation) {
        return (EnumType) target.getClass().getEnumConstants()[source.intValue()];
    }
}
