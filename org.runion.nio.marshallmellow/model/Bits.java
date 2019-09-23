package model;

public class Bits {
    private final int byteCount;
    protected Long data = 0L;

    public Bits(int byteCount) {
        switch(byteCount) {
            case 1:
            case 2:
            case 4:
            case 8:
                break;
            default:
                throw new IllegalArgumentException("Only options are 1, 2, 4, 8");
        }
        this.byteCount = byteCount;
    }

    public Bits(int byteCount, long data) {
        this(byteCount);
        this.data = data;
    }

    public int getByteCount() {
        return byteCount;
    }

    public Long getData() {
        return data;
    }

    public boolean getBit(int index) {
        return (data & (0x01L << index)) != 0;
    }

    public long getBits(int start, int count) {
        // highest bit we want is in position start + count we want to shift that to the msb in the long
        long leftJustified = data << (long)(64 - count - start);

        // now shift the lowest bit of the desired range to lsb of the long >>> means all higher level bits are zero
        long rightjustified = leftJustified >>> (long)(64 - count);

        return rightjustified;
    }

    public Bits setBit(int index) {
        data |= (0x01L << index);
        return this;
    }

    public Bits setBits(int start, long input) {
        data |= (input << start);
        return this;
    }
}
