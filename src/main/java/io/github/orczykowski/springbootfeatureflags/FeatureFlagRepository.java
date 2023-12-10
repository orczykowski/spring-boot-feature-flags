package io.github.orczykowski.springbootfeatureflags;

import java.util.stream.Stream;

/**
 * storage feature flag access repository
 */
public interface FeatureFlagRepository extends FeatureFlagSupplier {
    /**
     * removes feature flag by name
     *
     * @param flagName feature flag name
     */
    void removeByName(FeatureFlagDefinition.FeatureFlagName flagName);

    /**
     * store new or update feature flag
     *
     * @param definition feature flag definition
     * @return stored feature flag definition
     */
    FeatureFlagDefinition save(FeatureFlagDefinition definition);

    /**
     * find all feature flags defined in storage
     *
     * @return Stream of feature flag definitions
     */
    Stream<FeatureFlagDefinition> findAll();
}
