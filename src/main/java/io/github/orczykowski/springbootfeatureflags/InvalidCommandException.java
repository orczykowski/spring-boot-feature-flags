package io.github.orczykowski.springbootfeatureflags;

public class InvalidCommandException extends RuntimeException {

    InvalidCommandException(final String msg) {
        super(msg);
    }
}
