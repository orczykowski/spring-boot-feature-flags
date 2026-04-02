package io.github.orczykowski.springbootfeatureflags.exceptions;

/**
 * Exception may occur when trying to create Command with invalid param
 */
public class FeatureFlagInvalidCommandException extends RuntimeException {

    public FeatureFlagInvalidCommandException(final String msg) {
        super(msg);
    }
}
