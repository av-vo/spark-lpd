package vo.av.localfeatures.sparktypes;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class PointFeaturesType1 implements Serializable, Writable, PointFeatures {

    private double lpd;
    private double[] normal;
    private final double[] normal_sph;

    public PointFeaturesType1(double lpd, double[] normal , double[] normal_sph){
        this.lpd = lpd;
        this.normal = normal;
        this.normal_sph = normal_sph;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeDouble(lpd);
        for(double ni : normal){
            dataOutput.writeDouble(ni);
        }
        for(double ni:normal_sph){
            dataOutput.writeDouble(ni);
        }
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        lpd = dataInput.readDouble();
        normal = new double[3];
        for(int i=0; i<3; i++){
            normal[i] = dataInput.readDouble();
        }
        for(int i=0; i<3; i++){
            normal_sph[i] = dataInput.readDouble();
        }
    }

    @Override
    public String toString(){

        double inclination = normal_sph[1]*180f/Math.PI;
        if (inclination > 90) inclination = 180 - inclination;

        return String.format("%.1f,%.3f,%.3f,%.3f,%.1f,%.1f",
                lpd,
                normal[0], normal[1], normal[2],
                normal_sph[0]*180f/Math.PI, inclination
                );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointFeaturesType1 that = (PointFeaturesType1) o;
        return Double.compare(that.lpd, lpd) == 0 &&
                Arrays.equals(normal, that.normal) &&
                Arrays.equals(normal_sph, that.normal_sph);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(lpd);
        result = 31 * result + Arrays.hashCode(normal);
        result = 31 * result + Arrays.hashCode(normal_sph);
        return result;
    }
}
