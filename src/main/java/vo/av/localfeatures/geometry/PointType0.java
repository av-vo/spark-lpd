package vo.av.localfeatures.geometry;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

// todo use jts

/**
 * Immutable class
 */
public class PointType0 implements Point, Serializable, Writable {

    protected double x;
    protected double y;
    protected double z;

    public PointType0(){}

    protected PointType0(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static PointType0 create(double x, double y, double z){
        return new PointType0(x, y, z);
    }

    @Override
    public PointType1 markAsBuffer(){
        return new PointType1(x, y, z, true);
    }

    @Override
    public double x() {
        return x;
    }

    @Override
    public double y() {
        return y;
    }

    @Override
    public double z() {
        return z;
    }

    @Override
    public boolean isSynthetic() {
        return false;
    }

    @Override
    public double[] coords() {
        return new double[]{x, y, z};
    }

    @Override
    public String toString(){
        return String.format("%.3f,%.3f,%.3f", x, y, z);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeDouble(x);
        out.writeDouble(y);
        out.writeDouble(z);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        x = in.readDouble();
        y = in.readDouble();
        z = in.readDouble();
    }
}
