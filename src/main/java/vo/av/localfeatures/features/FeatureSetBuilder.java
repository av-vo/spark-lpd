package vo.av.localfeatures.features;

import vo.av.localfeatures.geometry.Neighbourhood;

import java.util.List;

public interface FeatureSetBuilder {
    //FeatureSet compute(com.github.davidmoten.rtreemulti.geometry.Point queryPoint);

    FeatureSet compute(Neighbourhood neighbours, EigenData eigenData);

    /**
     * Add a feature builder to the set
     * @param featureBuilder
     */
    FeatureSetBuilder add(FeatureBuilder featureBuilder);

    /**
     * Add multiple feature builders to the set
     * @param featureBuilders
     */
    FeatureSetBuilder add(List<FeatureBuilder> featureBuilders);

    /**
     * Check whether the FS contains eigen-based feature
     * @return true if at least one feature is eigen-based
     */
    public boolean containsEigenBasedFeatures();

}
