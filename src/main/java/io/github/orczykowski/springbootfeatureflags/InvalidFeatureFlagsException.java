package io.github.orczykowski.springbootfeatureflags;

public class InvalidFeatureFlagsException extends RuntimeException {

    InvalidFeatureFlagsException(final String msg) {
        super(msg);
    }
}
