package vo.av.localfeatures.sparktypes;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class PointFeaturesType2 implements Serializable, Writable, PointFeatures {

    private double lpd;
    private double slope;

    public double lpd(){ return lpd;}
    public double slope(){ return slope;}

    public PointFeaturesType2(){}

    public PointFeaturesType2(double lpd, double slope){
        this.lpd = lpd;
        this.slope = slope;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeDouble(lpd);
        dataOutput.writeDouble(slope);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        lpd = dataInput.readDouble();
        slope = dataInput.readDouble();
    }

    @Override
    public String toString(){
        // normalise and convert to degrees
        slope = slope*180f/Math.PI;
        if (slope > 90) slope = 180 - slope;

        return String.format("%.1f,%.1f",
                lpd,slope
                );
    }

}
