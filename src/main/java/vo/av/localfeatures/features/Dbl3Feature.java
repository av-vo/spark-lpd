package vo.av.localfeatures.features;

public abstract class Dbl3Feature extends VectorFeature{
    double[] data;

    public String toString(int precision){
        return String.format("%." + precision +"f"
                        + Constants.DELIM + "%." + precision +"f"
                        + Constants.DELIM + "%." + precision +"f"
                , data[0], data[1], data[2]);
    }
}
