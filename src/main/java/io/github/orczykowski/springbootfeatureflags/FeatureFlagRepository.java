package io.github.orczykowski.springbootfeatureflags;

import java.util.stream.Stream;

/**
 * Repository for managing feature flag definitions.
 * Extends {@link FeatureFlagSupplier} with write operations
 * for creating, updating, and removing feature flags.
 */
public interface FeatureFlagRepository extends FeatureFlagSupplier {

    /**
     * Removes a feature flag definition by name.
     *
     * @param flagName the feature flag name to remove
     */
    void removeByName(final FeatureFlagName flagName);

    /**
     * Saves a new or updates an existing feature flag definition.
     *
     * @param definition the feature flag definition to save
     * @return the stored feature flag definition
     */
    FeatureFlagDefinition save(final FeatureFlagDefinition definition);

    /**
     * Returns all feature flag definitions.
     *
     * @return stream of all feature flag definitions
     */
    Stream<FeatureFlagDefinition> findAll();
}
