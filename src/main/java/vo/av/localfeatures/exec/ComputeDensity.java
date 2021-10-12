package vo.av.localfeatures.exec;

import com.github.davidmoten.rtreemulti.Entry;
import org.apache.commons.cli.*;
import org.apache.hadoop.util.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import scala.Tuple2;
import vo.av.localfeatures.functions.Aggregater;
import vo.av.localfeatures.functions.Distance;
import vo.av.localfeatures.functions.PointParser;
import vo.av.localfeatures.functions.Quantiser;
import vo.av.localfeatures.geometry.Point;
import vo.av.localfeatures.geometry.PointType1;
import vo.av.localfeatures.sparktypes.IntTuple2;
import vo.av.localfeatures.sparktypes.LPD;
import vo.av.localfeatures.sparktypes.ShortTuple2;
import vo.av.localfeatures.sparktypes.ShortTuple3;
import vo.av.localfeatures.utils.spatialalg.ANN;

import java.util.ArrayList;
import java.util.List;

/**
 * Compute point density
 * Blocked Rtree
 * Kryo serialiser
 */
public class ComputeDensity {

    static Logger LOG = LogManager.getLogger(ComputeDensity.class);

    public static void main(String[] args){

        final CommandLine cmd = parseArgs(args);

        // log user inputs
        LOG.info(StringUtils.arrayToString(args));

        SparkConf conf = new SparkConf();

        conf.setAppName("Compute Point Density");

        conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");

        conf.registerKryoClasses(
                new Class<?>[]{
                        IntTuple2.class,
                        ShortTuple2.class,
                        ShortTuple3.class,
                        Double.class,
                        PointType1.class,
                        Point.class,
                        LPD.class
                }
        );

        // todo - this does not work when set here? temporary fix: set in spark-submit
        // doesn't have much impact on runtime anyway
        conf.set("mapreduce.fileoutputcommitter.algorithm.version", "2");

        JavaSparkContext sc = new JavaSparkContext(conf);

        final String inPath = cmd.getOptionValue("i");
        final String outPath = cmd.getOptionValue("o");

        final String delimiter;
        if(cmd.hasOption("delimiter")){
            delimiter = cmd.getOptionValue("delimiter");
        }else{
            delimiter = ",";
        }

        final int numPartitions = cmd.hasOption("p") ? Integer.parseInt(cmd.getOptionValue("p")) : 10;

        String[] tileSizeStr = cmd.getOptionValues("tile_size");
        final double[] tileSize =new double[]{
                Double.parseDouble(tileSizeStr[0]),
                Double.parseDouble(tileSizeStr[1])
        };

        final int minChildren;
        final int maxChildren;
        final boolean star;
        if(cmd.hasOption("rtree_params")){
            String[] rtreeParamsStr = cmd.getOptionValues("rtree_params");
            minChildren = Integer.parseInt(rtreeParamsStr[0]);
            maxChildren = Integer.parseInt(rtreeParamsStr[1]);
            star = rtreeParamsStr[2].equals("star");
        }
        else{
            minChildren = 0;
            maxChildren = 16;
            star = true;
        }

        double[] localVoxelSize;
        if(cmd.hasOption("local_voxel_size")){
            String[] localVoxelSizeStr = cmd.getOptionValues("local_voxel_size");
            localVoxelSize = new double[]{
                    Double.parseDouble(localVoxelSizeStr[0]),
                    Double.parseDouble(localVoxelSizeStr[1]),
                    Double.parseDouble(localVoxelSizeStr[2]),
            };
        }else{
             localVoxelSize = null; // no local voxelisation
        }

        String[] offsetStr = cmd.getOptionValues("offset");
        final double[] offset = new double[]{
                Double.parseDouble(offsetStr[0]),
                Double.parseDouble(offsetStr[1]),
                0
        };

        double radius = Double.parseDouble(
                cmd.getOptionValue("radius")
        );

        // read text input
        JavaRDD<String> rdd1 = sc.textFile(
                inPath, numPartitions
        );
        LOG.info(String.format("rdd1: %d partitions", rdd1.partitions().size()));

        // transform text to Point
        JavaRDD<Point> rdd2 = rdd1.map(
                PointParser.parseFromTXT(delimiter)
        );
        LOG.info(String.format("rdd2: %d partitions", rdd2.partitions().size()));

        // map points to tiles
        JavaPairRDD<ShortTuple2, Point> rdd3 = rdd2.flatMapToPair(
                (PairFlatMapFunction<Point, ShortTuple2, Point>) point -> {
                    List<Tuple2<ShortTuple2, Point>> result = new ArrayList();

                    double[] coords = new double[]{point.x(), point.y()};

                    int[] gridIdx = Quantiser.realToGrid(
                            coords,
                            offset,
                            tileSize
                    );

                    // core point
                    result.add(new Tuple2(
                            new ShortTuple2(gridIdx[0], gridIdx[1]),
                            point
                    ));

                    int[] lowerBound = new int[2];
                    int[] upperBound = new int[2]; // inclusive
                    //for (int dimIdx=0; dimIdx<2; dimIdx++){ // dimension index
                    //    lowerBound[dimIdx] = gridIdx[dimIdx]-1;
                    //    upperBound[dimIdx] = gridIdx[dimIdx]+1;
                    //}

                    // todo_done: suboptimal - no need to check all 8 neighbours, just look at the relative position of pt with respect to the centre of the cell
                    // worked as expected
                    // but does not have any observable impact on runtime (local test)
                    double[] center = Quantiser.gridToReal(gridIdx, offset, tileSize);
                    for(int dimIdx=0; dimIdx<2; dimIdx++){
                        if(coords[dimIdx] < center[dimIdx]){ // point is on the left of the tile center - no need to check the neighbors on the right hand side of the tile
                            upperBound[dimIdx] = gridIdx[dimIdx];
                        }else{
                            upperBound[dimIdx] = gridIdx[dimIdx]+1;
                        }

                        if(coords[dimIdx] > center[dimIdx]){ // point is on the left of the tile center - no need to check the neighbors on the right hand side of the tile
                            lowerBound[dimIdx] = gridIdx[dimIdx];
                        }else{
                            lowerBound[dimIdx] = gridIdx[dimIdx]-1;
                        }
                    }

                    //int count = 0;
                    for(int xIdx = lowerBound[0]; xIdx<=upperBound[0]; xIdx++){
                        for(int yIdx = lowerBound[1]; yIdx<=upperBound[1]; yIdx++){
                            if (xIdx == gridIdx[0] && yIdx == gridIdx[1]) continue;
                            //count++;
                            double[][] bbox = Quantiser.gridIdx2BBox(
                                    new int[]{xIdx, yIdx},
                                    offset,
                                    tileSize
                            );
                            if(Distance.point2RegionD2(
                                    new double[]{point.x(), point.y()},
                                    bbox[0], bbox[1])
                                    < radius*radius){
                                result.add(
                                        new Tuple2(
                                            new ShortTuple2(xIdx, yIdx),
                                            point.markAsBuffer()
                                        )
                                );
                            }
                        }
                    }
                    //System.out.println(count);
                    return result.iterator();
                }
        );
        LOG.info(String.format("rdd3: %d partitions", rdd3.partitions().size()));

        // TODO_done switch to aggregateByKey
        //JavaPairRDD<ShortTuple2, Iterable<Point>> rdd4 = rdd3.groupByKey();

        // group points by tiles
        JavaPairRDD<ShortTuple2, List<Point>> rdd4 = rdd3.aggregateByKey(
                new ArrayList<Point>(),
                Aggregater.addItemToList(),
                Aggregater.agreegateTwoLists()
        );
        LOG.info(String.format("rdd4: %d partitions", rdd4.partitions().size()));

        // compute features
        JavaPairRDD<Point, LPD> rdd5 = rdd4.flatMapToPair(
                (PairFlatMapFunction<Tuple2<ShortTuple2, List<Point>>, Point, LPD>) tileData -> {
                    List<Entry<Point, com.github.davidmoten.rtreemulti.geometry.Point>> buffer = new ArrayList();
                    for(Point p : tileData._2){
                        Entry<Point, com.github.davidmoten.rtreemulti.geometry.Point> entry = Entry.entry(
                                p,
                                com.github.davidmoten.rtreemulti.geometry.Point.create(p.x(), p.y(), p.z())
                        );
                        buffer.add(entry);
                    }

                    List<Tuple2<Point, LPD>> result;
                    if(localVoxelSize == null){ // no local voxelisation
                        result = ANN.computeSurfaceDensity(buffer, radius,
                                minChildren, maxChildren, star
                        );
                    }else{ // voxelise the points within each tile and index the voxels using a tree
                        result = ANN.computeSurfaceDensity(buffer, radius,
                                minChildren, maxChildren, star,
                                offset, localVoxelSize
                        );
                    }

                    //System.out.printf("%d,%d\n", tileData._1.x(), tileData._1.y());

                    return result.iterator();
                }
        );
        LOG.info(String.format("rdd5: %d partitions", rdd5.partitions().size()));

        // save output to disk
        rdd5.saveAsTextFile(outPath);
        //rdd5.saveAsObjectFile(outPath);

        sc.stop();
        sc.close();
    }

    /**
     * Parse arguments
     * @param args
     * @return
     */
    public static CommandLine parseArgs(String[] args) {
        Options options = new Options();

        Option o;

        o = new Option("i", "input", true, "input point cloud directory");
        options.addOption(o);

        o = new Option("o", "output", true, "output directory");
        options.addOption(o);

        o = new Option("p", "num_partitions", true, "number of partitions");
        options.addOption(o);

        o = new Option("delimiter",  true, "delimiter");
        o.setRequired(false);
        options.addOption(o);

        o = new Option("tile_size", true, "tile size");
        o.setArgs(2);
        options.addOption(o);

        o = new Option("offset", true, "offset");
        o.setArgs(2);
        options.addOption(o);

        o = new Option("radius", true, "radius for neighbour search");
        options.addOption(o);

        o = new Option("rtree_params", true, "rtree parameters");
        o.setArgs(3);
        o.setRequired(false);
        options.addOption(o);

        o = new Option("local_voxel_size", true, "local voxel size");
        o.setArgs(3);
        o.setRequired(false);
        options.addOption(o);

        // debug flag
        options.addOption("d", "debug", false, "switch on DEBUG log level");

        CommandLineParser parser = new PosixParser();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage() + "\n");
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(" ", options, true);
            System.exit(-1);
        }
        if (cmd.hasOption("d")) {
            System.out.println("DEBUG ON");
        }

        return cmd;
    }
}

// todo: instead of moving the points around, it may be good to leave them alone and do a join at the end