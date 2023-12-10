package io.github.orczykowski.springbootfeatureflags;

/**
 * Exception may occur when trying to create or update feature flag with invalid value
 */
public class InvalidFeatureFlagsException extends RuntimeException {

    InvalidFeatureFlagsException(final String msg) {
        super(msg);
    }
}
