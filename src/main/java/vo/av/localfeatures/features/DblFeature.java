package vo.av.localfeatures.features;

public abstract class DblFeature extends ScalarFeature{
    double data;

    public String toString(int precision){
        return String.format("%." + precision +"f", data);
    }
}
