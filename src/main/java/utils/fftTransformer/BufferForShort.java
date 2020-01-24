package utils.fftTransformer;


/**
 * POJO for saving some short values data.  Useful for saving some arrays of values.
 * In out case we will save 1024 values of data in one such buffer.
 */
public class BufferForShort {

    private short[] values;

    public BufferForShort(short[] values) {
        this.values = values;
    }

    public short[] getValues() {
        return values;
    }
}
