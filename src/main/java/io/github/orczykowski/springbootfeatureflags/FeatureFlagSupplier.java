package io.github.orczykowski.springbootfeatureflags;

import java.util.Optional;
import java.util.stream.Stream;


/**
 * interface returns basic information about flags
 */
public interface FeatureFlagSupplier {
    /**
     * Returns a list of enabled feature flags
     *
     * @return stream of Feature Flag Definitions
     */
    Stream<FeatureFlagDefinition> findAllEnabledFeatureFlags();

    /**
     * searches for feature flag definitions by name
     *
     * @param featureFlagName flag name
     * @return may return Feature flag definition or empty
     */
    Optional<FeatureFlagDefinition> findByName(FeatureFlagDefinition.FeatureFlagName featureFlagName);

}
