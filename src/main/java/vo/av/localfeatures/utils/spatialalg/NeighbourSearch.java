package vo.av.localfeatures.utils.spatialalg;

import com.github.davidmoten.rtreemulti.Entry;
import com.github.davidmoten.rtreemulti.RTree;
import com.github.davidmoten.rtreemulti.geometry.Rectangle;
import vo.av.localfeatures.geometry.Neighbourhood;
import vo.av.localfeatures.geometry.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Search utility functions.
 */
public class NeighbourSearch {

    /**
     * Search for points within the provided window
     * @param queryPoint query point (3 dim.)
     * @param halfDist querying window size, expressed as a half distance
     * @param envPCloud environment point cloud
     * @return points inside the window
     */
    public static Iterable<Entry<Point, Rectangle>> windowSearch (
            final double[] queryPoint,
            final double halfDist,
            final RTree<Point, Rectangle> envPCloud){
        Rectangle rect = Rectangle.create(
                queryPoint[0]-halfDist,
                queryPoint[1]-halfDist,
                queryPoint[2]-halfDist,
                queryPoint[0]+halfDist,
                queryPoint[1]+halfDist,
                queryPoint[2]+halfDist
        );

        Iterable<Entry<Point, Rectangle>> neighbours
                = envPCloud.search(rect);

        return neighbours;
    }


    /**
     * Search for points within a given radius from the query point, using the window query as the basis
     * @param queryPoint
     * @param radius
     * @param envPCloud
     * @return
     */
    public static Neighbourhood rangeSearch (
            final double[] queryPoint,
            final double radius,
            final RTree<Point, Rectangle> envPCloud){
        Iterable<Entry<Point, Rectangle>> pointsInBox = windowSearch(queryPoint, radius, envPCloud);

        List<Entry<Point, Rectangle>> resultSet = new ArrayList();

        double radius2 = radius * radius;
        double maxDist2 = 0;

        for(Entry<Point, Rectangle> p : pointsInBox){
            double dist2 = 0;
            double[] pCoords = p.value().coords();
            for(int i=0; i<3; i++){
                dist2 += (queryPoint[i] - pCoords[i]) * (queryPoint[i] - pCoords[i]);
            }
            if (dist2 < radius2) {
                resultSet.add(p);
                if(dist2 > maxDist2) maxDist2 = dist2;
            }
        }

        return Neighbourhood.create(resultSet, Math.sqrt(maxDist2));
    }
}
