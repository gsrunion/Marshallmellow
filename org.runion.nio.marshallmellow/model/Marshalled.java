package model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to mark a field for inclusion in marshalling and to control aspects of how that value is marshalled.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Marshalled {
    /**
     * Class definition for an implementation of Codec that will be used for marshalling of the field.
     * Examples: ArrayCodec.class, ByteCodec.class...
     */
    Class<? extends Codec> codec();

    /**
     * Expression the produces a boolean value. Names of fields that exist before this in the parent object are allowed.
     * Examples: "true", "!false", "2 % 2 == 0", and "(flags & 0x1) != 0"
     */
    String precondition() default "true";

    /**
     * Expression the produces a integer value. Names of fields that exist before this in the parent object are allowed.
     * Examples: "1", "2", "numBits / 8", and "someArray.length"
     */
    String length() default "1";
}
