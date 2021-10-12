package vo.av.localfeatures.features;

public class NormalVector extends Dbl3Feature{

    public static NormalVector create(double[] data){
        return new NormalVector(data);
    }

    private NormalVector(double[] data){
        this.data = data;
    }

    @Override
    public String getName() {
        return Constants.NORMAL_VECTOR;
    }

    /**
     * Get normal vector from eigen data
     * @param eigenData eigen data
     * @return normal vector
     */
    public static NormalVector getNormalVector(EigenData eigenData){
        return new NormalVector(eigenData.data.get(eigenData.data.firstKey())); //todo to confirm
    }

    public String toString(){
        return String.format(
                "%.3f,%.3f,%.3f",
                data[0],
                data[1],
                data[2]
        );
    }

    public static class Builder extends EigenBasedFeatureBuilder{

        //private SortedMap<Double, double[]> eigenData;

        public Builder(){}

        /**
         * Compute normal vector
         * @param input, must be of type EigenData
         * @return normal vector
         */
        @Override
        public Feature compute(FeatureCompInput input) {
            if(!(input instanceof EigenData)){
                System.err.println("Normal Vector comp: input must be of type EigenData");
            }

            EigenData eigenData = (EigenData) input;

            return NormalVector.getNormalVector(eigenData);
        }
    }
}
