package test;

import annotations.AsBit;
import annotations.AsByte;
import annotations.AsObject;
import annotations.Precondition;

import java.util.Optional;

class OptionsHeader {
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
    private boolean option6Present;

    @Precondition(precondition = "option0Present")
    @AsObject
    private Option option0 = new Option();

    @Precondition(precondition = "option1Present")
    @AsObject
    private Option option1  = new Option();

    @Precondition(precondition = "option2Present")
    @AsObject
    private Option option2  = new Option();

    @Precondition(precondition = "option3Present")
    @AsObject
    private Option option3  = new Option();

    @Precondition(precondition = "option4Present")
    @AsObject
    private Option option4  = new Option();

    @Precondition(precondition = "option5Present")
    @AsObject
    private Option option5  = new Option();

    @Precondition(precondition = "option6Present")
    @AsObject
    private Option option6  = new Option();

    public Optional<byte[]> getOption0() {
        return option0.get();
    }

    public Optional<byte[]> getOption1() {
        return option1.get();
    }

    public Optional<byte[]> getOption2() {
        return option2.get();
    }

    public Optional<byte[]> getOption3() {
        return option3.get();
    }

    public Optional<byte[]> getOption4() {
        return option4.get();
    }

    public Optional<byte[]> getOption5() {
        return option5.get();
    }

    public Optional<byte[]> getOption6() {
        return option5.get();
    }
}
