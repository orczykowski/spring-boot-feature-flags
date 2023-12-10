package io.github.orczykowski.springbootfeatureflags;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

import static java.util.Optional.ofNullable;

public record FeatureFlagDefinition(FeatureFlagName name, FeatureFlagState enabled, Set<User> entitledUsers) {

    public FeatureFlagDefinition(FeatureFlagName name, FeatureFlagState enabled, Set<User> entitledUsers) {
        validate(name, enabled, entitledUsers);

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

    private void validate(final FeatureFlagName name, final FeatureFlagState enabled, final Set<User> entitledUsers) {
        if (Objects.isNull(name)) {
            throw new InvalidFeatureFlagsException("Invalid feature flags definition. Feature flag name cannot be null");
        }
        if (Objects.isNull(enabled)) {
            throw new InvalidFeatureFlagsException("Invalid feature flags definition. Feature flag enabled value have to be one of %s".formatted(Arrays.stream(FeatureFlagState.values()).toList()));
        }
        if (FeatureFlagState.RESTRICTED_FOR_USERS.equals(enabled) && (Objects.isNull(entitledUsers) || entitledUsers.isEmpty())) {
            throw new InvalidFeatureFlagsException("""
                    Invalid feature flags definition. 
                    Cannot create a feature flag definition RESTRICTED FOR USERS  without providing a list of users or
                    providing empty list""");
        }
    }

    FeatureFlagDefinition update(final FeatureFlagState state, final Set<User> users) {
        return new FeatureFlagDefinition(name, state, users);
    }

    public enum FeatureFlagState {
        ON,
        OFF,
        RESTRICTED_FOR_USERS
    }

    public record FeatureFlagName(String value) {
        private static final int MAX_LENGTH = 120;

        public FeatureFlagName(final String value) {
            validate(value);
            this.value = value.trim();
        }

        private static void validate(final String value) {
            if (Objects.isNull(value) || value.isBlank() || value.length() > MAX_LENGTH) {
                throw new InvalidFeatureFlagsException("Feature flag name can not be null or blank");
            }
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public record User(String id) {

        public User {
            if (Objects.isNull(id) || id.isBlank()) {
                throw invalidIdException();
            }
        }

        public User(final Number id) {
            this(ofNullable(id).map(Object::toString).orElseThrow(User::invalidIdException));
        }

        private static InvalidFeatureFlagsException invalidIdException() {
            return new InvalidFeatureFlagsException("User identifier cannot be null or blank.");
        }

        @Override
        public String toString() {
            return id;
        }
    }
}
