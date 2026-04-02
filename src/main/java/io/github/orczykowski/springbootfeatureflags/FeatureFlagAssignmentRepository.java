package io.github.orczykowski.springbootfeatureflags;

/**
 * Repository for managing feature flag user assignments.
 * Extends {@link FeatureFlagAssignmentSupplier} with write operations
 * for assigning and removing users from feature flags.
 */
public interface FeatureFlagAssignmentRepository extends FeatureFlagAssignmentSupplier {

    /**
     * Saves the full set of user assignments for a given feature flag,
     * replacing any existing assignments.
     *
     * @param flagName the feature flag name
     * @param users the entitled users to assign
     */
    void saveAssignments(final FeatureFlagName flagName, final FeatureFlagEntitledUsers users);

    /**
     * Adds a single user assignment to the given feature flag.
     *
     * @param flagName the feature flag name
     * @param user the user to assign
     */
    void addUser(final FeatureFlagName flagName, final FeatureFlagUser user);

    /**
     * Removes a single user assignment from the given feature flag.
     *
     * @param flagName the feature flag name
     * @param user the user to remove
     */
    void removeUser(final FeatureFlagName flagName, final FeatureFlagUser user);

    /**
     * Removes all user assignments for the given feature flag.
     *
     * @param flagName the feature flag name
     */
    void removeAllByFlagName(final FeatureFlagName flagName);
}
