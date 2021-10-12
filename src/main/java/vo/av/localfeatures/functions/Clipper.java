package vo.av.localfeatures.functions;

import org.apache.spark.api.java.function.Function;
import org.locationtech.jts.algorithm.locate.SimplePointInAreaLocator;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;
import scala.Tuple2;
import vo.av.localfeatures.geometry.Point;
import vo.av.localfeatures.sparktypes.PointFeatures;

public class Clipper {
    public static <P extends Point, F extends PointFeatures> Function<Tuple2<P, F>, Boolean>clip(Polygon clipper){
        return (Function<Tuple2<P, F>, Boolean>) p -> {
            Coordinate pt = new Coordinate(p._1.x(),p._1.y());
            return SimplePointInAreaLocator.containsPointInPolygon(pt, clipper);
        };
    }
}
