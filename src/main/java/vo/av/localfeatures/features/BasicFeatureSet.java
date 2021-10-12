package vo.av.localfeatures.features;

import vo.av.localfeatures.geometry.Neighbourhood;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class BasicFeatureSet implements FeatureSet{
    private List<Feature> features;

    private BasicFeatureSet(List<Feature> features){
        this.features = features;
    }

    /**
     * Create an empty feature set
     * @return empty feature set
     */
    public static BasicFeatureSet createEmptySet(){
        return new BasicFeatureSet(new ArrayList());
    }

    /**
     * Create a feature set that has one single feature
     * @param feature
     * @return feature set
     */
    public static BasicFeatureSet createSingletonSet(Feature feature){
        List<Feature> features = new ArrayList();
        features.add(feature);
        return new BasicFeatureSet(features);
    }

    //@Override
    //public void init() {
    //    features = new ArrayList();
    //}

    @Override 
    public void add(Feature feature) {
        features.add(feature);
    }

    @Override
    public void add(List<Feature> features) {
        this.features.addAll(features);
    }

    @Override
    public void add(FeatureSet set) {
        set.stream().forEach(f -> features.add(f));
    }

    @Override
    public void remove(String name) {
        if(features == null) return;
        for(Feature feature : features){
            if(feature.getName().equals(name)){
                features.remove(feature);
            }
        }
    }

    @Override
    public void clean() {
        features = new ArrayList<>();
    }

    @Override
    public Stream<Feature> stream() {
        return features.stream();
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        int n = features.size();
        for (int i=0; i<n; i++){
            builder.append(features.get(i));
            if(i < n-1){
                builder.append(Constants.DELIM);
            }
        }
        return builder.toString();
    }

    public static class Builder implements FeatureSetBuilder{
        private List<Feature> features;
        private List<FeatureBuilder> featureBuilders;

        //List<FeatureBuilder> featureBuilders(){
        //    return this.featureBuilders;
        //}

        @Override
        public boolean containsEigenBasedFeatures(){
            for(FeatureBuilder fBuilder : featureBuilders){
                if(fBuilder instanceof EigenBasedFeatureBuilder){
                    return true;
                }
            }
            return false;
        }

        /**
         * Initiate an empty builder
         */
        public Builder(){
            this.featureBuilders = new ArrayList<>();
            this.features = new ArrayList<>();
        }

        /**
         * Initiate a builder from a given feature set
         * @param features input feature set
         * @return builder based on the input feature set
         */
        public Builder fromFeatureSet(List<Feature> features){
            this.features = new ArrayList<>();
            this.features.addAll(features);
            return this;
        }

        /**
         * Assign feature builders
         * @param featureBuilders
         * @return
         */
        public Builder fromFeatureBuilders(List<FeatureBuilder> featureBuilders){
            this.featureBuilders = featureBuilders;
            return this;
        }

        @Override
        public FeatureSet compute(Neighbourhood neighbours, EigenData eigenData) {
            for(FeatureBuilder fBuilder : featureBuilders){
                // default input: neighbours
                FeatureCompInput input = neighbours;

                // input set as eigenData for eigen-based features
                if(fBuilder instanceof EigenBasedFeatureBuilder){
                    input = eigenData;
                }

                Feature feature = fBuilder.compute(input);
                features.add(feature);
            }
            return new BasicFeatureSet(features);
        }

        @Override
        public FeatureSetBuilder add(FeatureBuilder featureBuilder) {
            this.featureBuilders.add(featureBuilder);
            return this;
        }

        @Override
        public FeatureSetBuilder add(List<FeatureBuilder> featureBuilders) {
            this.featureBuilders.addAll(featureBuilders);
            return this;
        }
    }
}
