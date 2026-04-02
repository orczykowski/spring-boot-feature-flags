package io.github.orczykowski.springbootfeatureflags;

import io.github.orczykowski.springbootfeatureflags.exceptions.FeatureFlagInvalidFeatureFlagsException;

import java.util.Arrays;
import java.util.Objects;

/**
 * Definition of Feature flag
 *
 * @param name    unique name of feature flag, cannot be null
 * @param enabled flag status, cannot be null
 */
public record FeatureFlagDefinition(FeatureFlagName name, FeatureFlagState enabled) {

    /**
     * Constructor
     *
     * @param name    - unique name of feature flag, cannot be null
     * @param enabled - flag status, can take one of the values:
     *                - enabled for everyone (ANYBODY)
     *                - disabled (NOBODY)
     *                - limited to a specific list of user (RESTRICTED)
     *                cannot be null
     */
    public FeatureFlagDefinition(FeatureFlagName name, FeatureFlagState enabled) {
        validate(name, enabled);
        this.name = name;
        this.enabled = enabled;
    }

    public boolean isEnableForALlUser() {
        return enabled.equals(FeatureFlagState.ANYBODY);
    }

    public boolean isRestricted() {
        return FeatureFlagState.RESTRICTED.equals(enabled);
    }

    public boolean isEnable() {
        return !FeatureFlagState.NOBODY.equals(enabled);
    }

    private void validate(final FeatureFlagName name, final FeatureFlagState enabled) {
        if (Objects.isNull(name)) {
            throw new FeatureFlagInvalidFeatureFlagsException("Invalid feature flags definition. Feature flag name cannot be null");
        }
        if (Objects.isNull(enabled)) {
            throw new FeatureFlagInvalidFeatureFlagsException("Invalid feature flags definition. Feature flag enabled value have to be one of %s".formatted(Arrays.stream(FeatureFlagState.values()).toList()));
        }
    }

    FeatureFlagDefinition update(final FeatureFlagState state) {
        return new FeatureFlagDefinition(name, state);
    }
}
