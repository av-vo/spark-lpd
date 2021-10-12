package vo.av.localfeatures.functions;

import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;
//import umg.core.lidar.protobuf.laspoint.LASPointProtos;
import vo.av.localfeatures.geometry.Point;
import vo.av.localfeatures.geometry.PointType0;
import vo.av.localfeatures.sparktypes.PointFeatures;
import vo.av.localfeatures.sparktypes.PointFeaturesType3;

public class PointParser {

    // todo make this work for multiple point types
    /**
     * Parse point data in Proto Sequence files
     * @param scale
     * @param offset
     * @return
     */
    /*
    public static Function<Tuple2<IntWritable, BytesWritable>, Point> parseFromSEQ(
            final DMatrix3 scale,
            final DMatrix3 offset
    ){
        return new Function<Tuple2<IntWritable, BytesWritable>, Point>() {
            @Override
            public Point call(Tuple2<IntWritable, BytesWritable> record) throws Exception {

                LASPointProtos.LASPointP pointP
                        = LASPointProtos.LASPointP.parseFrom(record._2.copyBytes());

                DMatrix3 coords = new DMatrix3(
                        pointP.getX(),
                        pointP.getY(),
                        pointP.getZ()
                );
                CommonOps_DDF3.elementMult(coords, scale);
                CommonOps_DDF3.add(coords, offset, coords);

                return PointType0.create(coords.a1, coords.a2, coords.a3);
            }
        };
    }*/

    /*
    public static PairFunction<Tuple2<IntWritable, BytesWritable>, Point, PointFeatures> parseFromSEQ(
            Class<? extends PointFeatures> pointFeaturesClass,
            final DMatrix3 scale,
            final DMatrix3 offset,
            double tScale,
            double tOffset
    ){
        return new PairFunction<Tuple2<IntWritable, BytesWritable>, Point, PointFeatures>() {
            @Override
            public Tuple2<Point, PointFeatures> call(Tuple2<IntWritable, BytesWritable> record) throws Exception {

                LASPointProtos.LASPointP pointP
                        = LASPointProtos.LASPointP.parseFrom(record._2.copyBytes());

                DMatrix3 coords = new DMatrix3(
                        pointP.getX(),
                        pointP.getY(),
                        pointP.getZ()
                );
                CommonOps_DDF3.elementMult(coords, scale);
                CommonOps_DDF3.add(coords, offset, coords);


                //todo do not invite misuse
                PointFeatures features = null;

                if(pointFeaturesClass == PointFeaturesType3.class){
                    double timestamp = pointP.getGpsTimestamp()*tScale + tOffset;
                    features = new PointFeaturesType3(timestamp);
                }

                return new Tuple2(
                        PointType0.create(coords.a1, coords.a2, coords.a3),
                        features
                );
            }
        };
    }*/

    /**
     * Parse points data in text format
     * @param delimiter
     * @return
     */
    public static Function<String, Point> parseFromTXT(
            final String delimiter
    ){
        return new Function<String, Point>() {
            @Override
            public Point call(String s) throws Exception {
                s = s.replaceAll("[\\\\[\\\\](){}]","");
                String[] tokens = s.split(delimiter);

                double x = Double.parseDouble(tokens[0]);
                double y = Double.parseDouble(tokens[1]);
                double z = Double.parseDouble(tokens[2]);

                return PointType0.create(x, y, z);
            }
        };
    }

    public static PairFunction<String, Point, PointFeatures> parseFromTXT(
            Class<? extends Point> pointClass,
            Class<? extends PointFeatures> pointFeaturesClass,
            final String delimiter
    ){
        return new PairFunction<String, Point, PointFeatures>() {
            @Override
            public Tuple2<Point, PointFeatures> call(String s) throws Exception {
                s = s.replaceAll("[\\\\[\\\\](){}]","");
                String[] tokens = s.split(delimiter);

                double x = Double.parseDouble(tokens[0]);
                double y = Double.parseDouble(tokens[1]);
                double z = Double.parseDouble(tokens[2]);

                double timestamp = Double.parseDouble(tokens[3]);

                // todo stop inviting bugs
                Point point = null;
                PointFeatures features = null;

                if(pointClass == PointType0.class)
                    point = PointType0.create(x, y, z);
                if(pointFeaturesClass == PointFeaturesType3.class)
                    features= new PointFeaturesType3(timestamp);

                return new Tuple2(
                        point, features
                );
            }
        };
    }

    public static PairFunction<String, Point, Double> parseFromTXT(
            Class<? extends Point> pointClass,
            int field,
            final String delimiter
    ){
        return new PairFunction<String, Point, Double>() {
            @Override
            public Tuple2<Point, Double> call(String s) throws Exception {
                s = s.replaceAll("[\\\\[\\\\](){}]","");

                String[] tokens = s.split(delimiter);

                double x = Double.parseDouble(tokens[0]);
                double y = Double.parseDouble(tokens[1]);
                double z = Double.parseDouble(tokens[2]);

                double attribute = Double.parseDouble(tokens[field]);

                // todo stop inviting bugs
                Point point = null;

                if(pointClass == PointType0.class)
                    point = PointType0.create(x, y, z);

                return new Tuple2(
                        point, attribute
                );
            }
        };
    }
}
