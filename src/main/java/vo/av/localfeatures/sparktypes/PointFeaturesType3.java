package vo.av.localfeatures.sparktypes;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

public class PointFeaturesType3 implements Serializable, Writable, PointFeatures {

    private double timestamp;

    public double timestamp(){ return timestamp;}

    public PointFeaturesType3(){}

    public PointFeaturesType3(double timestamp){
        this.timestamp = timestamp;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeDouble(timestamp);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        timestamp = dataInput.readDouble();
    }

    @Override
    public String toString(){
        return String.format("%.6f",timestamp);
    }

}
