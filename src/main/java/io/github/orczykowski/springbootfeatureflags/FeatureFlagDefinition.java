package io.github.orczykowski.springbootfeatureflags;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

/**
 * Definition of Feature flag
 *
 * @param name          unique name of feature flag, cannot be null
 * @param enabled       flag status, cannot be null
 * @param entitledUsers list of users for whom the flag will be enabled
 */
public record FeatureFlagDefinition(FeatureFlagName name, FeatureFlagState enabled, Set<User> entitledUsers) {

    /**
     * Constructor
     *
     * @param name          - unique name of feature flag, cannot be null
     * @param enabled       - flag status, can take one of the values:
     *                      - enabled for everyone (ANYBODY)
     *                      - disabled (NOBODY)
     *                      - limited to a specific list of user (RESTRICTED)
     *                      cannot be null
     * @param entitledUsers - list of users for whom the flag will be enabled
     */
    public FeatureFlagDefinition(FeatureFlagName name, FeatureFlagState enabled, Set<User> entitledUsers) {
        validate(name, enabled, entitledUsers);

        this.name = name;
        this.enabled = enabled;
        this.entitledUsers = Objects.requireNonNullElseGet(entitledUsers, Set::of);
    }

    boolean isEmpowered() {
        return !FeatureFlagState.NOBODY.equals(enabled);
    }

    boolean isEnableForUser(final User user) {
        return isEnableForALlUser() || (isDefinedForSpecificUser() && entitledUsers.contains(user));
    }


    boolean isEnableForALlUser() {
        return enabled.equals(FeatureFlagState.ANYBODY);
    }

    boolean isDefinedForSpecificUser() {
        return FeatureFlagState.RESTRICTED.equals(enabled);
    }

    boolean isEnable() {
        return !FeatureFlagState.NOBODY.equals(enabled);
    }

    private void validate(final FeatureFlagName name, final FeatureFlagState enabled, final Set<User> entitledUsers) {
        if (Objects.isNull(name)) {
            throw new InvalidFeatureFlagsException("Invalid feature flags definition. Feature flag name cannot be null");
        }
        if (Objects.isNull(enabled)) {
            throw new InvalidFeatureFlagsException("Invalid feature flags definition. Feature flag enabled value have to be one of %s".formatted(Arrays.stream(FeatureFlagState.values()).toList()));
        }
        if (FeatureFlagState.RESTRICTED.equals(enabled) && (Objects.isNull(entitledUsers) || entitledUsers.isEmpty())) {
            throw new InvalidFeatureFlagsException("""
                    Invalid feature flags definition. 
                    Cannot create a feature flag definition RESTRICTED FOR USERS  without providing a list of users or
                    providing empty list""");
        }
    }

    FeatureFlagDefinition update(final FeatureFlagState state, final Set<User> users) {
        return new FeatureFlagDefinition(name, state, users);
    }

}
