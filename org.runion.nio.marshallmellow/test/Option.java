package test;

import annotations.AsArray;
import annotations.AsByte;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

class Option {
    @AsByte
    private byte len = 0;

    @AsByte
    @AsArray(lengthProvider = "len")
    private byte[] data = new byte[128];

    public Optional<byte[]> get() {
        if(len == 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(data);
    }
}
