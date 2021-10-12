package vo.av.localfeatures.geometry;

public interface Point {
    double x();
    double y();
    double z();
    boolean isSynthetic();
    double[] coords();
    Point markAsBuffer();
}
