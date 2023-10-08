package com.github.orczykowski.springbootfeatureflags;

import java.util.Objects;
import java.util.Set;

record FeatureFlagDefinition(FeatureFlagName name, FeatureFlagState enabled, Set<User> entitledUsers) {

    FeatureFlagDefinition {
        if (Objects.nonNull(name)) {

        }
        if (Objects.nonNull(enabled)) {

        }
    }

    boolean isEnabled() {
        return !FeatureFlagState.OFF.equals(enabled);
    }

    boolean isDefinedForAllUsers() {
        return FeatureFlagState.ON.equals(enabled);
    }

    boolean isDefinedForUsers() {
        return FeatureFlagState.ON_FOR_USERS.equals(enabled);
    }

    enum FeatureFlagState {
        ON,
        OFF,
        ON_FOR_USERS
    }
}
