package test;

import annotations.AsBit;
import annotations.AsByte;
import annotations.AsObject;
import annotations.Precondition;

import java.util.Optional;

public class Options {
    @AsByte
    private Byte flags;

    @AsBit(fieldName = "flags", bitIndex = 0)
    private boolean option0Present;

    @AsBit(fieldName = "flags", bitIndex = 1)
    private boolean option1Present;

    @AsBit(fieldName = "flags", bitIndex = 2)
    private boolean option2Present;

    @AsBit(fieldName = "flags", bitIndex = 3)
    private boolean option3Present;

    @AsBit(fieldName = "flags", bitIndex = 4)
    private boolean option4Present;

    @AsBit(fieldName = "flags", bitIndex = 5)
    private boolean option5Present;

    @AsBit(fieldName = "flags", bitIndex = 6)
    public boolean option6Present;

    @Precondition(precondition = "option0Present")
    @AsObject
    public Option option0 = new Option();

    @Precondition(precondition = "option1Present")
    @AsObject
    public Option option1  = new Option();

    @Precondition(precondition = "option2Present")
    @AsObject
    public Option option2  = new Option();

    @Precondition(precondition = "option3Present")
    @AsObject
    public Option option3  = new Option();

    @Precondition(precondition = "option4Present")
    @AsObject
    public Option option4  = new Option();

    @Precondition(precondition = "option5Present")
    @AsObject
    public Option option5  = new Option();

    @Precondition(precondition = "option6Present")
    @AsObject
    private byte option6Prefix;
}
