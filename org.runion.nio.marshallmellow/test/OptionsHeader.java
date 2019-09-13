package test;

import annotations.AsBit;
import annotations.AsByte;
import annotations.AsObject;
import annotations.Precondition;

import java.util.Optional;

class OptionsHeader {
    @AsObject private Options options = new Options();

    @Precondition(precondition = "extendedOptionsIncluded")
    @AsObject
    private ExtendedOptions extendedOptions = new ExtendedOptions();

    private boolean extendedOptionsIncluded() {
        return options.option6Present;
    }

    public Optional<byte[]> option0() {
        return options.option0.get();
    }

    public Optional<byte[]> option1() {
        return options.option1.get();
    }

    public Optional<byte[]> option2() {
        return options.option2.get();
    }

    public Optional<byte[]> option3() {
        return options.option3.get();
    }

    public Optional<byte[]> option4() {
        return options.option4.get();
    }

    public Optional<byte[]> option5() {
        return options.option5.get();
    }

    public Optional<byte[]> extendedOption0() {
        return extendedOptions.option0.get();
    }

    public Optional<byte[]> extendedOption1() {
        return extendedOptions.option1.get();
    }

    public Optional<byte[]> extendedOption2() {
        return extendedOptions.option2.get();
    }

    public Optional<byte[]> extendedOption3() {
        return extendedOptions.option3.get();
    }

    public Optional<byte[]> extendedOption4() {
        return extendedOptions.option4.get();
    }

    public Optional<byte[]> extendedOption5() {
        return extendedOptions.option5.get();
    }

    public Optional<byte[]> extendedOptiony() {
        return extendedOptions.option5.get();
    }
}
