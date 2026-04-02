package io.github.orczykowski.springbootfeatureflags.exceptions;

import io.github.orczykowski.springbootfeatureflags.FeatureFlagName;

/**
 * Exception may occur when trying to create two feature flags with the same name
 */
public class FeatureFlagDuplicatedFeatureFlagException extends RuntimeException {
    private static final String MESSAGE_PATTERN = "feature flag definition with name [%s] already exists";

    public FeatureFlagDuplicatedFeatureFlagException(final FeatureFlagName name) {
        super(MESSAGE_PATTERN.formatted(name.toString()));
    }
}
