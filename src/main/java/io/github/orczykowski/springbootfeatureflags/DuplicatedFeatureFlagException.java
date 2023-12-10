package io.github.orczykowski.springbootfeatureflags;

public class DuplicatedFeatureFlagException extends RuntimeException {
    private static final String MESSAGE_PATTERN = "feature flag definition with name [%s] already exists";

    DuplicatedFeatureFlagException(final FeatureFlagDefinition.FeatureFlagName name) {
        super(MESSAGE_PATTERN.formatted(name.toString()));
    }
}
