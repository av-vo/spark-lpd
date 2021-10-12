package vo.av.localfeatures.features;

import java.util.List;
import java.util.stream.Stream;

public interface FeatureSet {
    //public FeatureSet build();
    //public void compute();
    //public String toString();

    /**
     * Initiate an empty feature set
     */
    //void init();

    /**
     * Add a feature to the set
     * @param feature
     */
    void add(Feature feature);

    /**
     * Add multiple features to the set
     * @param features
     */
    void add(List<Feature> features);

    /**
     * Add another feature set to the set
     * @param set feature set
     */
    void add(FeatureSet set);

    /**
     * Remove features matching the specified name
     * @param name
     */
    void remove(String name);

    /**
     * Clean the feature set
     */
    void clean();


    /**
     * Get feature stream
     * @return stream of features
     */
    Stream<Feature> stream();

    String toString();
}
