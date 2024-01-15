package io.github.orczykowski.springbootfeatureflags;

/**
 * Exception may occur when trying to create two feature flags with the same name
 */
public class DuplicatedFeatureFlagException extends RuntimeException {
    private static final String MESSAGE_PATTERN = "feature flag definition with name [%s] already exists";

    DuplicatedFeatureFlagException(final FeatureFlagName name) {
        super(MESSAGE_PATTERN.formatted(name.toString()));
    }
}
