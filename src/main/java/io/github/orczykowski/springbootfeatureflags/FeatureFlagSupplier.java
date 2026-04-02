package io.github.orczykowski.springbootfeatureflags;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Read-only supplier of feature flag definitions.
 * Provides methods to look up feature flags by name and to retrieve all enabled flags.
 */
public interface FeatureFlagSupplier {

    /**
     * Returns all enabled feature flag definitions.
     *
     * @return stream of enabled feature flag definitions
     */
    Stream<FeatureFlagDefinition> findAllEnabledFeatureFlags();

    /**
     * Looks up a feature flag definition by name.
     *
     * @param featureFlagName the flag name to search for
     * @return the feature flag definition, or empty if not found
     */
    Optional<FeatureFlagDefinition> findByName(final FeatureFlagName featureFlagName);
}
