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
public class ShortTuple3 implements Serializable, Writable {

    private int x, y, z;

    /**
     * Constructor
     * @param x
     * @param y
     * @param z
     */
    public ShortTuple3(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int x(){return x;}
    public int y(){return y;}
    public int z(){return z;}

    //@Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeShort(x);
        dataOutput.writeShort(y);
        dataOutput.writeShort(z);
    }

    //@Override
    public void readFields(DataInput dataInput) throws IOException {
        x = dataInput.readShort();
        y = dataInput.readShort();
        z = dataInput.readShort();
    }

    @Override
    public String toString(){
        return String.format("%d,%d,%d", x, y, z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShortTuple3 that = (ShortTuple3) o;
        return x == that.x &&
                y == that.y &&
                z == that.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
