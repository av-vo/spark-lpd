package vo.av.localfeatures.sparktypes;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

/**
 * (32*2) = 64 bits
 */
public class IntTuple2 implements Serializable, Writable {

    private int x, y;

    /**
     * Constructor
     * @param x
     * @param y
     */
    public IntTuple2(int x, int y){
        this.x = x;
        this.y = y;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(x);
        dataOutput.writeInt(y);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        x = dataInput.readInt();
        y = dataInput.readInt();
    }

    @Override
    public String toString(){
        return String.format("%d,%d", x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntTuple2 gridIndex = (IntTuple2) o;
        return x == gridIndex.x &&
                y == gridIndex.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
