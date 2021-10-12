package vo.av.localfeatures.geometry;

import com.github.davidmoten.rtreemulti.Entry;
import com.github.davidmoten.rtreemulti.geometry.Rectangle;
import vo.av.localfeatures.features.FeatureCompInput;

import java.util.List;

public class Neighbourhood implements FeatureCompInput {
    List<Entry<Point, Rectangle>> neighbours;
    double maxDistance;

    private Neighbourhood(List<Entry<Point, Rectangle>> neighbours, double maxDistance){
        this.neighbours = neighbours;
        this.maxDistance = maxDistance;
    }

    public static Neighbourhood create(
            List<Entry<Point, Rectangle>> neighbours,
            double maxDistance){
        return new Neighbourhood(neighbours, maxDistance);
    }

    public List<Entry<Point, Rectangle>> neighbours(){
        return neighbours;
    }

    public double maxDistance(){
        return maxDistance;
    }

    public int size(){
        return neighbours.size();
    }
}
