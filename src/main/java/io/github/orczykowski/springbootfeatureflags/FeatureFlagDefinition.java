package io.github.orczykowski.springbootfeatureflags;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

record FeatureFlagDefinition(FeatureFlagName name, FeatureFlagState enabled, Set<User> entitledUsers) {

    public FeatureFlagDefinition(FeatureFlagName name, FeatureFlagState enabled, Set<User> entitledUsers) {
        if (Objects.isNull(name)) {
            throw new ConfigurationFeatureFlagsException("Feature flag name cannot be null");
        }
        if (Objects.isNull(enabled)) {
            final var msg = "feature flag enabled have to be one of %s".formatted(Arrays.stream(FeatureFlagState.values()).toList());
            throw new ConfigurationFeatureFlagsException(msg);
        }
        this.name = name;
        this.enabled = enabled;
        this.entitledUsers = Objects.requireNonNullElseGet(entitledUsers, Set::of);
    }

    boolean isEmpowered() {
        return !FeatureFlagState.OFF.equals(enabled);
    }

    boolean isEnableForUser(final User user) {
        return isEnableForALlUser() || (isDefinedForSpecificUser() && entitledUsers.contains(user));
    }

    boolean isEnableForALlUser() {
        return enabled.equals(FeatureFlagState.ON);
    }

    boolean isDefinedForSpecificUser() {
        return FeatureFlagState.RESTRICTED_FOR_USERS.equals(enabled);
    }

    boolean isEnable() {
        return !FeatureFlagState.OFF.equals(enabled);
    }

    enum FeatureFlagState {
        ON,
        OFF,
        RESTRICTED_FOR_USERS
    }
}
