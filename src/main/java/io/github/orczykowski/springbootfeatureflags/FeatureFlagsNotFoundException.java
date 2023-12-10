package io.github.orczykowski.springbootfeatureflags;

public class FeatureFlagsNotFoundException extends RuntimeException {
    private static final String MESSAGE_PATTERN = "Feature flag with name [%s] does not exist";

    public FeatureFlagsNotFoundException(final FeatureFlagDefinition.FeatureFlagName flagName) {
        super(MESSAGE_PATTERN.formatted(flagName.toString()));
    }
}
