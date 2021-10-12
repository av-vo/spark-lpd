package vo.av.localfeatures.utils.spatialalg;

import com.github.davidmoten.rtreemulti.Entry;
import com.github.davidmoten.rtreemulti.RTree;
import com.github.davidmoten.rtreemulti.geometry.Point;
import com.github.davidmoten.rtreemulti.geometry.Rectangle;
import scala.Tuple2;
import vo.av.localfeatures.functions.CoordSystem;
import vo.av.localfeatures.functions.Distance;
import vo.av.localfeatures.functions.LocalFeatures;
import vo.av.localfeatures.functions.Quantiser;
import vo.av.localfeatures.sparktypes.*;
import vo.av.localfeatures.sparktypes.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * All nearest neighbour.
 */
public class ANN {
    /**
     * Rtree window search
     * @param pointList point collection
     * @param radius search radius
     * @param minChildren minimum number of children per node, leave default if <2
     * @param maxChildren maximum number of children per node, leave default if <2
     * @param star true for R*, false for Guttman
     * @param <T> point type
     * @return point, local density index
     */
    public static <T extends vo.av.localfeatures.geometry.Point> List<Tuple2<T, LPD>>
    computeSurfaceDensity(List<Entry<T, Point>> pointList, double radius,
                          int minChildren, int maxChildren, boolean star){

        // construct the tree
        RTree.Builder builder = RTree.dimensions(3);
        if(minChildren > 1)
            builder = builder.minChildren(minChildren);
        if(maxChildren >1)
            builder = builder.maxChildren(maxChildren);
        if(star)
            builder = builder.star();

        RTree<T, Point> tree = builder
                .create(pointList);

        List<Tuple2<T, LPD>> result = new ArrayList();

        int pIdx = 0;

        for(Entry<T, Point> p : pointList){

            if(p.value().isSynthetic()) continue;

            double[] coords = p.geometry().values();
            Rectangle rect = Rectangle.create(coords[0]-radius,
                    coords[1]-radius,
                    coords[2]-radius,
                    coords[0]+radius,
                    coords[1]+radius,
                    coords[2]+radius
            );

            Iterable<Entry<T, Point>> neighbours
                    = tree.search(rect);

            int numNeighbours = 0;
            double actualRadius = -Double.MAX_VALUE;

            for(Entry<T, Point> neighbor : neighbours){
                double dist = neighbor.geometry().distance(p.geometry());
                if(dist < radius){ //only counts points inside the sphere
                    numNeighbours++;
                    if(actualRadius < dist)// update actual radius
                        actualRadius = dist;
                }
            }


            double lpd = numNeighbours / (Math.PI * actualRadius * actualRadius);


            //coords = p.geometry().values();
            result.add(new Tuple2(p.value(), new LPD(lpd)));

            //if(pIdx++ % 1000 == 0)
                //System.out.println(pIdx-1);
        }

        return result;
    }

    public static <T extends vo.av.localfeatures.geometry.Point> List<Tuple2<T, LPD>>
    computeSurfaceDensity(List<Entry<T, Point>> pointList, double radius,
                          int minChildren, int maxChildren, boolean star,
                          double[] offset, double[] scale
            ){

        // voxelise
        HashMap<ShortTuple3, List<T>> voxels = new HashMap();
        for(Entry<T, Point> pt : pointList){
            double[] coords = pt.geometry().values();
            int[] gridCoords = Quantiser.realToGrid(coords, offset, scale);
            ShortTuple3 key = new ShortTuple3(gridCoords[0], gridCoords[1], gridCoords[2]);
            if(!voxels.containsKey(key)){
                voxels.put(key, new ArrayList());
            }
            List<T> voxelPoints = voxels.get(key);
            voxelPoints.add(pt.value());
        }

        // construct the tree
        List<Entry<List<T>, Rectangle>> entries = new ArrayList();
        int voxelIdx = 0;
        for(ShortTuple3 key : voxels.keySet()){
            //System.out.println(voxels.get(key).size());
            double[][] bbox = Quantiser.gridIdx2BBox(
                    new int[]{key.x(), key.y(), key.z()},
                    offset,
                    scale
            );
            //System.out.printf("[%.1f,%.1f,%.1f] [%.1f,%.1f,%.1f]\n",
            //        bbox[0][0],bbox[0][1],bbox[0][2],
            //        bbox[1][0],bbox[1][1],bbox[1][2]
            //        );
            entries.add(Entry.entry(
                    voxels.get(key),
                    Rectangle.create(
                            bbox[0][0],bbox[0][1],bbox[0][2],
                            bbox[1][0],bbox[1][1],bbox[1][2]
                    )));
        }

        // construct the tree
        RTree.Builder builder = RTree.dimensions(3);
        if(minChildren > 1)
            builder = builder.minChildren(minChildren);
        if(maxChildren >1)
            builder = builder.maxChildren(maxChildren);
        if(star)
            builder = builder.star();

        RTree<List<T>, Rectangle> tree = builder
                .create(entries);

        List<Tuple2<T, LPD>> result = new ArrayList();

        for(Entry<List<T>, Rectangle> entry : entries){

            Rectangle rec = entry.geometry();
            List<T> pl = entry.value();

            Iterable<Entry<List<T>, Rectangle>> neighbouringVoxels = tree.search(rec, radius);

            //Rectangle window = Rectangle.create(
            //        rec.min(0) - maxDist,
            //        rec.min(1) - maxDist,
            //        rec.min(2) - maxDist,
            //        rec.max(0) + maxDist,
            //        rec.max(1) + maxDist,
            //        rec.max(2) + maxDist
            //);

            //Iterable<Entry<List<Point>, Rectangle>> neighbouringVoxels = tree.search(window);

            List<T> base = new ArrayList();
            for(Entry<List<T>, Rectangle> neighbouringVoxel : neighbouringVoxels){
                base.addAll(
                        neighbouringVoxel.value()
                );
            }

            //System.out.printf("base size: %d\n", base.size());


            final double radius2 = radius*radius;
            for(T p : pl){

                if(p.isSynthetic()) continue;

                int numNeighbours = 0;
                double dist = -Double.MAX_VALUE;
                for(T neighbourPoint : base){
                    double curDist = Distance.point2pointD2(
                            p.coords(),
                            neighbourPoint.coords()
                    );//   neighbouringVoxel.geometry().distance(p.geometry());
                    if(curDist < radius2){
                        numNeighbours++;
                        if(curDist > dist)
                            dist = curDist;
                    }
                }

                double lpd = numNeighbours / (Math.PI * dist);

                if(Double.isFinite(lpd))
                    result.add(new Tuple2(p, new LPD(lpd)));
                else{
                    result.add(new Tuple2(p, new LPD(1)));
                }
            }
        }
        return result;
    }

    //todo should find a better way to implement this
    // compute features

    /**
     * Compute point features
     * @param <P> output point type
     * @param pointList
     * @param radius
     * @param minChildren
     * @param maxChildren
     * @param star
     * @param offset
     * @param scale
     * @param pointFeaturesClass
     * @return point, feature pair
     */
    public static <P extends vo.av.localfeatures.geometry.Point>
    List<Tuple2<P, PointFeatures>>
    computeFeatures(List<Entry<P, Point>> pointList, double radius,
                    int minChildren, int maxChildren, boolean star,
                    double[] offset, double[] scale,
                    Class<? extends PointFeatures> pointFeaturesClass
    ){

        // voxelise
        HashMap<ShortTuple3, List<P>> voxels = new HashMap();
        for(Entry<P, Point> pt : pointList){
            double[] coords = pt.geometry().values();
            int[] gridCoords = Quantiser.realToGrid(coords, offset, scale);
            ShortTuple3 key = new ShortTuple3(gridCoords[0], gridCoords[1], gridCoords[2]);
            if(!voxels.containsKey(key)){
                voxels.put(key, new ArrayList());
            }
            List<P> voxelPoints = voxels.get(key);
            voxelPoints.add(pt.value());
        }

        // construct the tree
        List<Entry<List<P>, Rectangle>> entries = new ArrayList();
        int voxelIdx = 0;
        for(ShortTuple3 key : voxels.keySet()){
            //System.out.println(voxels.get(key).size());
            double[][] bbox = Quantiser.gridIdx2BBox(
                    new int[]{key.x(), key.y(), key.z()},
                    offset,
                    scale
            );
            //System.out.printf("[%.1f,%.1f,%.1f] [%.1f,%.1f,%.1f]\n",
            //        bbox[0][0],bbox[0][1],bbox[0][2],
            //        bbox[1][0],bbox[1][1],bbox[1][2]
            //        );
            entries.add(Entry.entry(
                    voxels.get(key),
                    Rectangle.create(
                            bbox[0][0],bbox[0][1],bbox[0][2],
                            bbox[1][0],bbox[1][1],bbox[1][2]
                    )));
        }

        // construct the tree
        RTree.Builder builder = RTree.dimensions(3);
        if(minChildren > 1)
            builder = builder.minChildren(minChildren);
        if(maxChildren >1)
            builder = builder.maxChildren(maxChildren);
        if(star)
            builder = builder.star();

        RTree<List<P>, Rectangle> tree = builder
                .create(entries);

        List<Tuple2<P, PointFeatures>> result = new ArrayList();

        for(Entry<List<P>, Rectangle> entry : entries){

            Rectangle rec = entry.geometry();
            List<P> pl = entry.value();

            Iterable<Entry<List<P>, Rectangle>> neighbouringVoxels = tree.search(rec, radius);

            //Rectangle window = Rectangle.create(
            //        rec.min(0) - maxDist,
            //        rec.min(1) - maxDist,
            //        rec.min(2) - maxDist,
            //        rec.max(0) + maxDist,
            //        rec.max(1) + maxDist,
            //        rec.max(2) + maxDist
            //);
            //Iterable<Entry<List<Point>, Rectangle>> neighbouringVoxels = tree.search(window);

            List<P> base = new ArrayList();
            for(Entry<List<P>, Rectangle> neighbouringVoxel : neighbouringVoxels){
                base.addAll(
                        neighbouringVoxel.value()
                );
            }

            //System.out.printf("base size: %d\n", base.size());

            final double radius2 = radius*radius;

            for(P p : pl){

                if(p.isSynthetic()) continue;

                int numNeighbours = 0;
                double dist = -Double.MAX_VALUE;

                List<double[]> sphericalNeighbourhood = new ArrayList();

                for(P neighbourPoint : base){
                    double curDist = Distance.point2pointD2(
                            p.coords(),
                            neighbourPoint.coords()
                    );//   neighbouringVoxel.geometry().distance(p.geometry());

                    if(curDist < radius2){
                        numNeighbours++;
                        if(curDist > dist)
                            dist = curDist;

                        // this only needed when full features are required
                        sphericalNeighbourhood.add(new double[]{
                                neighbourPoint.x(),
                                neighbourPoint.y(),
                                neighbourPoint.z()
                        });
                    }
                }
                if(numNeighbours>2){
                    double lpd = dist == 0 ? 1 : numNeighbours / (Math.PI * dist);

                    double[][] sphericalNeighbourhoodData = new double[sphericalNeighbourhood.size()][3];
                    sphericalNeighbourhood.toArray(sphericalNeighbourhoodData);

                    // todo too much casting
                    LocalFeatures lf = LocalFeatures.compute(sphericalNeighbourhoodData);

                    // this invites bugs
                    PointFeatures pointFeatures = null;

                    // todo more flexibility is needed here, builder pattern may suit?
                    if(pointFeaturesClass == PointFeaturesType1.class){
                        pointFeatures = new PointFeaturesType1(
                                        lpd,
                                        lf.normal(),
                                        CoordSystem.cart2sph(lf.normal())
                                );
                    }else if(pointFeaturesClass == PointFeaturesType2.class){
                        pointFeatures = new PointFeaturesType2(
                                lpd,
                                //lf.normal(),
                                CoordSystem.cart2sph(lf.normal())[1]
                        );
                    }

                    result.add(new Tuple2(p, pointFeatures));
                }
            }
        }
        return result;
    }

}
