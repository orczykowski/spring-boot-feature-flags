package io.github.orczykowski.springbootfeatureflags;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

import static java.util.Optional.ofNullable;

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

    /**
     * List of possible state of feature flag
     */
    public enum FeatureFlagState {
        /**
         * flag will be enabled for everyone
         */
        ANYBODY,
        /**
         * flag will be disabled
         */
        NOBODY,
        /**
         * flag will be limited to a specific list of user
         */
        RESTRICTED
    }

    /**
     * ValueObject representing feature flag name
     *
     * @param value - unique name of feature flag
     */
    public record FeatureFlagName(String value) {
        private static final int MAX_LENGTH = 120;

        /**
         * Constructor
         *
         * @param value name of feature flag
         */
        public FeatureFlagName(final String value) {
            validate(value);
            this.value = value.trim();
        }

        private static void validate(final String value) {
            if (Objects.isNull(value) || value.isBlank()) {
                throw new InvalidFeatureFlagsException("Feature flag name can not be null or blank");
            }
            if (value.length() > MAX_LENGTH) {
                throw new InvalidFeatureFlagsException("Feature flag name is too long");
            }
        }

        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * ValueObject representing a user/user group from an external system. Used when RESTRICTED is used
     *
     * @param id - unique user ID or unique user group ID. Delivered externally (via external application)
     */
    public record User(String id) {
        /**
         * Constructor
         *
         * @param id unique user ID or unique user group ID
         */
        public User {
            if (Objects.isNull(id) || id.isBlank()) {
                throw invalidIdException();
            }
        }

        /**
         * Constructor
         *
         * @param id unique user ID or unique user group ID
         */
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
