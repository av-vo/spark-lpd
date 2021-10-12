package vo.av.localfeatures.geometry;

// todo use jts

/**
 * Immutable class
 */
public class PointType1 extends PointType0 {

    //private final double x;
    //private final double y;
    //private final double z;

    protected final boolean inBuffer;

    protected PointType1(double x, double y, double z){
        super(x, y, z);
        inBuffer = false;
    }

    protected PointType1(double x, double y, double z, boolean inBuffer){
        super(x, y, z);
        this.inBuffer = inBuffer;
    }

    public static PointType1 create(double x, double y, double z, boolean inBuffer){
        return new PointType1(x, y, z, inBuffer);
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

    public boolean isSynthetic(){
        return inBuffer;
    }

    @Override
    public double[] coords() {
        return new double[]{x, y, z};
    }

    @Override
    public String toString(){
        return String.format("%.3f,%.3f,%.3f,%d", x, y, z, inBuffer?1:0);
    }
}
