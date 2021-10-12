package vo.av.localfeatures.features;

public abstract class IntFeature extends ScalarFeature{
    int data;

    public String toString(int precision){
        return String.format("%d", data);
    }
}
