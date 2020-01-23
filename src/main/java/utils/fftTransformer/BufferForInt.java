package utils.fftTransformer;


/**
 * POJO for saving some integer data.  Useful for saving some arrays of values.
 * In out case we will save 1024 values of data in one such buffer.
 */
public class BufferForInt {

    private int[] values;

    public BufferForInt(int[] values) {
        this.values = values;
    }

    public int[] getValues() {
        return values;
    }
}
