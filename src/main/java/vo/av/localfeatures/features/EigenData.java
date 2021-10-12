package vo.av.localfeatures.features;

import com.github.davidmoten.rtreemulti.Entry;
import com.github.davidmoten.rtreemulti.geometry.Rectangle;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;
import vo.av.localfeatures.geometry.Neighbourhood;
import vo.av.localfeatures.geometry.Point;

import java.util.*;

public class EigenData implements FeatureCompInput {

    SortedMap<Double, double[]> data;

    private EigenData(SortedMap<Double, double[]> data){
        this.data = data;
    }

    /**
     * Create unidentified eigen result set
     * @return unidentified eigen result set
     */
    private static EigenData unidentifiedEigenResult(){
        return new EigenData(null);
    }

    /**
     * Check whether the eigen result is defined
     * @return yes if the result is identified
     */
    public boolean isIdentified(){
        return data != null;
    }

    public static class Builder{

        public Builder(){}

        //private double maxDistance = Double.MAX_VALUE;
        //private int maxCount = Integer.MAX_VALUE;
        //private RTree<Point, Rectangle> envPCloud;

        //public Builder(RTree<Point, Rectangle> envPCloud){
        //    this.envPCloud = envPCloud;
        //}

        //public EigenData.Builder maxDistance(double maxDistance){
        //    this.maxDistance = maxDistance;
        //    return this;
        //}

        //public EigenData.Builder maxCount(int maxCount){
        //    this.maxCount = maxCount;
        //    return this;
        //}

        /**
         * Perform eigen decomposition
         * @param input, must be an instance of Neighbourhood
         * @return Map(eigenValue, eigenVector), or NULL upon data insufficiency
         */
        public EigenData decompose(FeatureCompInput input){

            if (!(input instanceof Neighbourhood)){
                System.err.println("Eigen decomposition: input must be of type Neighbourhood");
            }
            Neighbourhood neighbours = (Neighbourhood) input;

            //Iterable<Entry<Point, Rectangle>> neighbours = envPCloud.nearest(
            //        queryPoint,
            //        maxDistance,
            //        maxCount);

            //Iterable<Entry<Point, Rectangle>> neighbours = NeighbourSearch.rangeSearch(
            //        queryPoint.values(),
            //        maxDistance,
            //        envPCloud);

            List<double[]> dataList = new ArrayList();
            for (Entry<Point, Rectangle> n : neighbours.neighbours()){
                Point ptP = n.value();
                double[] point = new double[]{ptP.x(), ptP.y(), ptP.z()};
                dataList.add(point);
            }

            //ref: https://stackoverflow.com/questions/9572795/convert-list-to-array-in-java
            double[][] data = dataList.toArray(new double[0][0]);

            // insufficient data
            //done: should this return NaNs instead?
            //if(data.length<3) return null;
            if(data.length<3) return EigenData.unidentifiedEigenResult();

            RealMatrix matrix = new Array2DRowRealMatrix(data);
            RealMatrix covarianceMatrix = (new Covariance(matrix)).getCovarianceMatrix();

            EigenDecomposition ed = new EigenDecomposition(covarianceMatrix);
            double[] eigenValues = ed.getRealEigenvalues();

            SortedMap<Double, double[]> eigenData = new TreeMap<Double, double[]>() { //todo might be an overkill for only 3 elements
            };
            for(int i=0; i < eigenValues.length; i++){
                eigenData.put(eigenValues[i], ed.getEigenvector(i).toArray());
            }

            return new EigenData(eigenData);
        }
    }
}
