package vo.av.localfeatures.features;

import vo.av.localfeatures.geometry.Neighbourhood;

public class SurfaceDensity extends DblFeature {

    /**
     * Static constructor
     * @param value
     * @return surface density
     */
    public static SurfaceDensity create(double value){
        return new SurfaceDensity(value);
    }

    /**
     * Private constructor
     * @param value surface density value
     */
    private SurfaceDensity(double value){this.data = value;}

    @Override
    public String getName() {
        return Constants.SURFACE_DENSITY;
    }

    public String toString(){
        return String.format("%.3f", data);
    }

    public static class Builder implements FeatureBuilder {

        public Builder(){}

        //private double maxDistance = Double.MAX_VALUE;
        //private int maxCount = Integer.MAX_VALUE;
        //private RTree<Point, Rectangle> envPCloud;

        //public Builder(RTree<Point, Rectangle> envPCloud){
        //    this.envPCloud = envPCloud;
        //}

        //public Builder maxDistance(double maxDistance){
        //    this.maxDistance = maxDistance;
        //    return this;
        //}

        //public Builder maxCount(int maxCount){
        //    this.maxCount = maxCount;
        //    return this;
        //}

        /**
         * Compute surface density
         * @param input must be an instance of of Neighbourhood
         * @return surface density index
         */
        public SurfaceDensity compute(FeatureCompInput input){

            // todo casting is expensive, no?
            Neighbourhood neighbours;
            if(input instanceof Neighbourhood){
                neighbours = (Neighbourhood) input;
            } else{
                System.err.println("SurfaceDensity computation: input must be of type Neighbourhood");
                return null;
            }
            //Iterable<Entry<Point, Rectangle>> neighbours = envPCloud.nearest(
            //        queryPoint,
            //        maxDistance,
            //       maxCount);

            //double[] coords = queryPoint.values();
            //Rectangle rect = Rectangle.create(coords[0]-maxDistance,
            //        coords[1]-maxDistance,
            //        coords[2]-maxDistance,
            //        coords[0]+maxDistance,
            //        coords[1]+maxDistance,
            //        coords[2]+maxDistance
            //);

            //Iterable<Entry<Point, Rectangle>> neighbours
            //        = envPCloud.search(rect);

            //Iterable<Entry<Point, Rectangle>> neighbours = NeighbourSearch.windowSearch(
            //        queryPoint.values(),
            //        maxDistance,
            //        envPCloud);

            double lpd = neighbours.size()
                    / (Math.PI * neighbours.maxDistance() * neighbours.maxDistance());

            return SurfaceDensity.create(lpd);

            /*
            Iterator<Entry<Point, Rectangle>> iN = neighbours.iterator();

            int i=1;
            Entry e = iN.next();

            while(iN.hasNext()){
                i++;
                e = iN.next();
            }
            r = e.geometry().distance(queryPoint);

            return SurfaceDensity.create(
                    i/(Math.PI*r*r)
            );*/
        }
    }

}
