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
     * Class definition for an implementation of Codec that will be used for marshalling of the field
     */
    Class<? extends Codec> codec();

    /**
     * Marks the field as an array such that elements of the array are marshalled accordingly.
     */
    boolean isArray() default false;

    /**
     * Name of a boolean variable or boolean returning method on the class that contains the annotation field that is
     * used for selective inclusion of this field in marshalling.
     */
    String precondition() default "";

    /**
     * Name of an integer (byte, short, int, long) variable or integer returning method on the class that contains the
     * annotated field, used to indicate how many items to read or write from the ByteBuffer. This is applicable when
     * marshalling Strings, where number of items indicates number of bytes, or arrays (isArray = true) where the number
     * of items is the number of elements in the array.
     */
    String length() default "";
}
