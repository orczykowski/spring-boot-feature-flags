package io.github.orczykowski.springbootfeatureflags;

/**
 * Exception may occur when trying to create Command with invalid param
 */
public class InvalidCommandException extends RuntimeException {

    InvalidCommandException(final String msg) {
        super(msg);
    }
}
