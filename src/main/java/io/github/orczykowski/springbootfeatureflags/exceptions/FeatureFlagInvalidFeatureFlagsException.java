package io.github.orczykowski.springbootfeatureflags.exceptions;

/**
 * Exception may occur when trying to create or update feature flag with invalid value
 */
public class FeatureFlagInvalidFeatureFlagsException extends RuntimeException {

    public FeatureFlagInvalidFeatureFlagsException(final String msg) {
        super(msg);
    }
}
