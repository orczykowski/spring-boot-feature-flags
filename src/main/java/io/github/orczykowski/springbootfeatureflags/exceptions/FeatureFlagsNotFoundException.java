package io.github.orczykowski.springbootfeatureflags.exceptions;

import io.github.orczykowski.springbootfeatureflags.FeatureFlagName;

/**
 * Exception may occur when trying to update non-existing feature flag
 */
public class FeatureFlagsNotFoundException extends RuntimeException {
    private static final String MESSAGE_PATTERN = "Feature flag with name [%s] does not exist";

    public FeatureFlagsNotFoundException(final FeatureFlagName flagName) {
        super(MESSAGE_PATTERN.formatted(flagName.toString()));
    }
}
