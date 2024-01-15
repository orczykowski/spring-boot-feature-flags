package io.github.orczykowski.springbootfeatureflags;


/**
 * exception may occur when trying to update no existing feature flag
 */
public class FeatureFlagsNotFoundException extends RuntimeException {
    private static final String MESSAGE_PATTERN = "Feature flag with name [%s] does not exist";

    FeatureFlagsNotFoundException(final FeatureFlagName flagName) {
        super(MESSAGE_PATTERN.formatted(flagName.toString()));
    }
}
