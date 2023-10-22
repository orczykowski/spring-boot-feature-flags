package io.github.orczykowski.springbootfeatureflags;

public class ConfigurationFeatureFlagsException extends RuntimeException {
    private static final String MESSAGE_PATTERN = "Invalid feature flags configuration. %s";

    ConfigurationFeatureFlagsException(final String msg) {
        super(MESSAGE_PATTERN.formatted(msg));
    }
}
