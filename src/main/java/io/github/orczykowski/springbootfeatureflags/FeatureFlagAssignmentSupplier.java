package io.github.orczykowski.springbootfeatureflags;

/**
 * Read-only supplier of feature flag user assignments.
 * Provides methods to query which users are assigned to feature flags
 * and whether a specific user is assigned to a given flag.
 */
public interface FeatureFlagAssignmentSupplier {

    /**
     * Returns all users assigned to the given feature flag.
     *
     * @param flagName the feature flag name to look up
     * @return the entitled users for the specified flag
     */
    FeatureFlagEntitledUsers findUsersByFlagName(final FeatureFlagName flagName);

    /**
     * Returns all feature flags assigned to the given user.
     *
     * @param user the user to look up
     * @return the feature flags assigned to the specified user
     */
    FeatureFlags findFlagNamesByUser(final FeatureFlagUser user);

    /**
     * Checks whether a user is assigned to a specific feature flag.
     *
     * @param flagName the feature flag name
     * @param user the user to check
     * @return {@code true} if the user is assigned to the flag, {@code false} otherwise
     */
    boolean isUserAssigned(final FeatureFlagName flagName, final FeatureFlagUser user);
}
